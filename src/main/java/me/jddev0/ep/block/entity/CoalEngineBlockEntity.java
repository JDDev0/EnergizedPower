package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.CoalEngineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoalEngineBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler> {
    public static final float ENERGY_PRODUCTION_MULTIPLIER = ModConfigs.COMMON_COAL_ENGINE_ENERGY_PRODUCTION_MULTIPLIER.getValue();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i != 0)
            return false;

        //Do not allow extraction of fuel items, allow for non fuel items (Bucket of Lava -> Empty Bucket)
        ItemStack item = itemHandler.getStackInSlot(i);
        return item.getBurnTime(null) <= 0;
    });

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

                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ENERGY_PRODUCTION,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, 0, baseEnergyTransferRate) {
            @Override
            public int getMaxExtract() {
                return Math.max(1, (int)Math.ceil(maxExtract * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                if(slot == 0)
                    return stack.getBurnTime(null) > 0;

                return super.isValid(slot, stack);
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

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.put("recipe.progress", IntTag.valueOf(progress));
        nbt.put("recipe.max_progress", IntTag.valueOf(maxProgress));
        nbt.put("recipe.energy_production_left", IntTag.valueOf(energyProductionLeft));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        energyProductionLeft = nbt.getInt("recipe.energy_production_left");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, CoalEngineBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(blockEntity.redstoneMode.isActive(state.getValue(BlockStateProperties.POWERED))) {
            blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));
            tickRecipe(level, blockPos, state, blockEntity);
            blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
        }

        blockEntity.pushEnergyToOutputs(Direction.values());
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

            if(blockEntity.timeoutOffState == 0 && level.getBlockState(blockPos).hasProperty(EPBlockStateProperties.WORKING) &&
                    level.getBlockState(blockPos).getValue(EPBlockStateProperties.WORKING)) {
                level.setBlock(blockPos, state.setValue(EPBlockStateProperties.WORKING, false), 3);
            }
        }

        if(blockEntity.maxProgress > 0 || hasRecipe(blockEntity)) {
            SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
            for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
                inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

            ItemStack item = inventory.getItem(0);

            int energyProduction = item.getBurnTime(null);
            energyProduction = (int)(energyProduction * ENERGY_PRODUCTION_MULTIPLIER *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_PRODUCTION));
            if(blockEntity.progress == 0)
                blockEntity.energyProductionLeft = energyProduction;

            //Change max progress if item would output more than max extract
            if(blockEntity.maxProgress == 0) {
                if(energyProduction / 100 <= blockEntity.limitingEnergyStorage.getMaxExtract())
                    blockEntity.maxProgress = 100;
                else
                    blockEntity.maxProgress = (int)Math.ceil((float)energyProduction / blockEntity.limitingEnergyStorage.getMaxExtract());
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
                if(level.getBlockState(blockPos).hasProperty(EPBlockStateProperties.WORKING) &&
                        !level.getBlockState(blockPos).getValue(EPBlockStateProperties.WORKING)) {
                    level.setBlock(blockPos, state.setValue(EPBlockStateProperties.WORKING, true), 3);
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

        if(item.getBurnTime(null) <= 0)
            return false;

        return !item.hasCraftingRemainingItem() || item.getCount() == 1;
    }
}