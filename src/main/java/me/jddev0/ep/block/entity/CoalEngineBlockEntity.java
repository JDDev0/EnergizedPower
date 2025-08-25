package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ExtractOnlyEnergyStorage;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.CoalEngineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CoalEngineBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<ExtractOnlyEnergyStorage, ItemStackHandler> {
    public static final float ENERGY_PRODUCTION_MULTIPLIER = ModConfigs.COMMON_COAL_ENGINE_ENERGY_PRODUCTION_MULTIPLIER.getValue();

    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
                if(i != 0)
                    return false;

                //Do not allow extraction of fuel items, allow for non fuel items (Bucket of Lava -> Empty Bucket)
                ItemStack item = itemHandler.getStackInSlot(i);
                return ForgeHooks.getBurnTime(item, null) <= 0;
            }));

    private int progress;
    private int maxProgress;
    private int energyProductionLeft = -1;
    private boolean hasEnoughCapacityForProduction;

    private int timeoutOffState;

    public CoalEngineBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.COAL_ENGINE_ENTITY.get(), blockPos, blockState,

                "coal_engine",

                ModConfigs.COMMON_COAL_ENGINE_CAPACITY.getValue(),
                ModConfigs.COMMON_COAL_ENGINE_TRANSFER_RATE.getValue(),

                1,

                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected ExtractOnlyEnergyStorage initEnergyStorage() {
        return new ExtractOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            public int getMaxExtract() {
                return Math.max(1, (int)Math.ceil(maxExtract * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }

            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if(slot == 0)
                    return ForgeHooks.getBurnTime(stack, null) > 0;

                return super.isItemValid(slot, stack);
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new EnergyValueContainerData(() -> maxProgress > 0 || hasRecipe(this)?getEnergyProductionPerTick():-1, value -> {}),
                new EnergyValueContainerData(() -> energyProductionLeft, value -> {}),
                new BooleanValueContainerData(() -> hasEnoughCapacityForProduction, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new CoalEngineMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.put("recipe.progress", IntTag.valueOf(progress));
        nbt.put("recipe.max_progress", IntTag.valueOf(maxProgress));
        nbt.put("recipe.energy_production_left", IntTag.valueOf(energyProductionLeft));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        energyProductionLeft = nbt.getInt("recipe.energy_production_left");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, CoalEngineBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(blockEntity.redstoneMode.isActive(state.getValue(BlockStateProperties.POWERED)))
            tickRecipe(level, blockPos, state, blockEntity);

        transferEnergy(level, blockPos, state, blockEntity);
    }
    
    protected final int getEnergyProductionPerTick() {
        //TODO improve (alternate values +/- 1 per x recipes instead of changing last energy production tick)
        int energyProductionPerTick = (int)Math.ceil((float)energyProductionLeft / (maxProgress - progress));
        if(progress == maxProgress - 1)
            energyProductionPerTick = energyProductionLeft;

        return energyProductionPerTick;
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, CoalEngineBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(blockEntity.timeoutOffState > 0) {
            blockEntity.timeoutOffState--;

            if(blockEntity.timeoutOffState == 0 && level.getBlockState(blockPos).hasProperty(BlockStateProperties.LIT) &&
                    level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                level.setBlock(blockPos, state.setValue(BlockStateProperties.LIT, false), 3);
            }
        }

        if(blockEntity.maxProgress > 0 || hasRecipe(blockEntity)) {
            SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
            for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
                inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

            ItemStack item = inventory.getItem(0);

            int energyProduction = ForgeHooks.getBurnTime(item, null);
            energyProduction = (int)(energyProduction * ENERGY_PRODUCTION_MULTIPLIER);
            if(blockEntity.progress == 0)
                blockEntity.energyProductionLeft = energyProduction;

            //Change max progress if item would output more than max extract
            if(blockEntity.maxProgress == 0) {
                if(energyProduction / 100 <= blockEntity.energyStorage.getMaxExtract())
                    blockEntity.maxProgress = 100;
                else
                    blockEntity.maxProgress = (int)Math.ceil((float)energyProduction / blockEntity.energyStorage.getMaxExtract());
            }

            int energyProductionPerTick = blockEntity.getEnergyProductionPerTick();
            if(energyProductionPerTick <= blockEntity.energyStorage.getCapacity() - blockEntity.energyStorage.getEnergy()) {
                if(blockEntity.progress == 0) {
                    //Remove item instantly else the item could be removed before finished and energy was cheated

                    if(item.hasCraftingRemainingItem())
                        blockEntity.itemHandler.setStackInSlot(0, item.getCraftingRemainingItem());
                    else
                        blockEntity.itemHandler.extractItem(0, 1, false);
                }

                blockEntity.hasEnoughCapacityForProduction = true;
                blockEntity.timeoutOffState = 0;
                if(level.getBlockState(blockPos).hasProperty(BlockStateProperties.LIT) &&
                        !level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                    level.setBlock(blockPos, state.setValue(BlockStateProperties.LIT, true), 3);
                }

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyProductionLeft < 0 ||
                        energyProductionPerTick < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    setChanged(level, blockPos, state);

                    return;
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() + energyProductionPerTick);
                blockEntity.energyProductionLeft -= energyProductionPerTick;

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    blockEntity.resetProgress(blockPos, state);

                setChanged(level, blockPos, state);
            }else {
                blockEntity.hasEnoughCapacityForProduction = false;
                if(blockEntity.timeoutOffState == 0) {
                    blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
                }
                setChanged(level, blockPos, state);
            }
        }else {
            blockEntity.resetProgress(blockPos, state);
            if(blockEntity.timeoutOffState == 0) {
                blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
            }
            setChanged(level, blockPos, state);
        }
    }

    private static void transferEnergy(Level level, BlockPos blockPos, BlockState state, CoalEngineBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        List<IEnergyStorage> consumerItems = new ArrayList<>();
        List<Integer> consumerEnergyValues = new ArrayList<>();
        int consumptionSum = 0;
        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            LazyOptional<IEnergyStorage> energyStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
            if(!energyStorageLazyOptional.isPresent())
                continue;

            IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
            if(!energyStorage.canReceive())
                continue;

            int received = energyStorage.receiveEnergy(Math.min(blockEntity.energyStorage.getMaxExtract(), blockEntity.energyStorage.getEnergy()), true);
            if(received <= 0)
                continue;

            consumptionSum += received;
            consumerItems.add(energyStorage);
            consumerEnergyValues.add(received);
        }

        List<Integer> consumerEnergyDistributed = new ArrayList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0);

        int consumptionLeft = Math.min(blockEntity.energyStorage.getMaxExtract(), Math.min(blockEntity.energyStorage.getEnergy(), consumptionSum));
        blockEntity.energyStorage.extractEnergy(consumptionLeft, false);

        int divisor = consumerItems.size();
        outer:
        while(consumptionLeft > 0) {
            int consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < consumerEnergyValues.size();i++) {
                int consumptionDistributed = consumerEnergyDistributed.get(i);
                int consumptionOfConsumerLeft = consumerEnergyValues.get(i) - consumptionDistributed;

                int consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                consumerEnergyDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < consumerItems.size();i++) {
            int energy = consumerEnergyDistributed.get(i);
            if(energy > 0)
                consumerItems.get(i).receiveEnergy(energy, false);
        }
    }

    private void resetProgress(BlockPos blockPos, BlockState state) {
        progress = 0;
        maxProgress = 0;
        energyProductionLeft = -1;
        hasEnoughCapacityForProduction = false;
    }

    private static boolean hasRecipe(CoalEngineBlockEntity blockEntity) {
        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        ItemStack item = inventory.getItem(0);

        if(ForgeHooks.getBurnTime(item, null) <= 0)
            return false;

        return !item.hasCraftingRemainingItem() || item.getCount() == 1;
    }
}