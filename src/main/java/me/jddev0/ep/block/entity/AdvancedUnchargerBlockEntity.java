package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AdvancedUnchargerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ExtractOnlyEnergyStorage;
import me.jddev0.ep.inventory.data.ComparatorModeValueContainerData;
import me.jddev0.ep.inventory.data.EnergyValueContainerData;
import me.jddev0.ep.inventory.data.RedstoneModeValueContainerData;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AdvancedUnchargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AdvancedUnchargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<ExtractOnlyEnergyStorage, ItemStackHandler> {
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i < 0 || i > 2)
            return false;

        ItemStack stack = itemHandler.getStackInSlot(i);
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage == null || !energyStorage.canExtract())
            return true;

        return energyStorage.extractEnergy(AdvancedUnchargerBlockEntity.this.energyStorage.getMaxExtract() / 3, true) == 0;
    });

    private int[] energyProductionLeft = new int[] {
            -1, -1, -1
    };

    public AdvancedUnchargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_UNCHARGER_ENTITY.get(), blockPos, blockState,

                "advanced_uncharger",

                ModConfigs.COMMON_ADVANCED_UNCHARGER_CAPACITY_PER_SLOT.getValue() * 3,
                ModConfigs.COMMON_ADVANCED_UNCHARGER_TRANSFER_RATE_PER_SLOT.getValue() * 3,

                3,

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
                if(slot >= 0 && slot < 3) {
                    IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                    return energyStorage != null && energyStorage.canExtract();
                }

                return super.isItemValid(slot, stack);
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.isSameItem(stack, itemStack) ||
                            (!ItemStack.isSameItemSameComponents(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(stack.getCapability(Capabilities.EnergyStorage.ITEM) != null && itemStack.getCapability(Capabilities.EnergyStorage.ITEM) != null))))
                        resetProgress(slot);
                }

                super.setStackInSlot(slot, stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new EnergyValueContainerData(this::getEnergyProductionPerTickSum, value -> {}),
                new EnergyValueContainerData(() -> energyProductionLeft[0], value -> {}),
                new EnergyValueContainerData(() -> energyProductionLeft[1], value -> {}),
                new EnergyValueContainerData(() -> energyProductionLeft[2], value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new AdvancedUnchargerMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        for(int i = 0;i < 3;i++)
            nbt.put("recipe.energy_production_left." + i, IntTag.valueOf(energyProductionLeft[i]));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        for(int i = 0;i < 3;i++)
            energyProductionLeft[i] = nbt.getInt("recipe.energy_production_left." + i);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AdvancedUnchargerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(blockEntity.redstoneMode.isActive(state.getValue(AdvancedUnchargerBlock.POWERED)))
            tickRecipe(level, blockPos, state, blockEntity);

        transferEnergy(level, blockPos, state, blockEntity);
    }

    protected final int getEnergyProductionPerTickSum() {
        final int maxExtractPerSlot = Math.max(0, (int)Math.min(this.energyStorage.getMaxExtract() / 3.,
                Math.ceil((this.energyStorage.getMaxEnergyStored() - this.energyStorage.getEnergy()) / 3.)));

        int energyProductionSum = -1;

        for(int i = 0;i < 3;i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if(energyStorage == null || !energyStorage.canExtract())
                continue;

            int energyProduction = energyStorage.extractEnergy(Math.max(0, Math.min(maxExtractPerSlot,
                    this.energyStorage.getCapacity() - this.energyStorage.getEnergy())), true);

            if(energyProductionSum == -1)
                energyProductionSum = energyProduction;
            else
                energyProductionSum += energyProduction;

            if(energyProductionSum < 0)
                energyProductionSum = Integer.MAX_VALUE;
        }

        return energyProductionSum;
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, AdvancedUnchargerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        final int maxExtractPerSlot = Math.max(0, (int)Math.min(blockEntity.energyStorage.getMaxExtract() / 3.,
                Math.ceil((blockEntity.energyStorage.getMaxEnergyStored() - blockEntity.energyStorage.getEnergy()) / 3.)));

        for(int i = 0;i < 3;i++) {
            if(blockEntity.hasRecipe(i)) {
                ItemStack stack = blockEntity.itemHandler.getStackInSlot(i);

                IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                if(energyStorage == null || !energyStorage.canExtract())
                    continue;

                blockEntity.energyProductionLeft[i] = energyStorage.getEnergyStored();

                int energyProductionPerTick = energyStorage.extractEnergy(Math.max(0, Math.min(maxExtractPerSlot,
                        blockEntity.energyStorage.getCapacity() - blockEntity.energyStorage.getEnergy())), false);

                if(blockEntity.energyProductionLeft[i] < 0 || energyProductionPerTick < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(i);
                    setChanged(level, blockPos, state);

                    continue;
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() + energyProductionPerTick);
                blockEntity.energyProductionLeft[i] -= energyProductionPerTick;

                if(blockEntity.energyProductionLeft[i] <= 0)
                    blockEntity.resetProgress(i);

                setChanged(level, blockPos, state);
            }else {
                blockEntity.resetProgress(i);
                setChanged(level, blockPos, state);
            }
        }
    }

    private static void transferEnergy(Level level, BlockPos blockPos, BlockState state, AdvancedUnchargerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        List<IEnergyStorage> consumerItems = new ArrayList<>();
        List<Integer> consumerEnergyValues = new ArrayList<>();
        int consumptionSum = 0;
        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);

            IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, testPos,
                    level.getBlockState(testPos), testBlockEntity, direction.getOpposite());
            if(energyStorage == null || !energyStorage.canReceive())
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

    private void resetProgress(int index) {
        energyProductionLeft[index] = -1;
    }

    private boolean hasRecipe(int index) {
        ItemStack stack = itemHandler.getStackInSlot(index);
        return stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
    }

    @Override
    protected void updateUpgradeModules() {
        for(int i = 0;i < 3;i++)
            resetProgress(i);

        super.updateUpgradeModules();
    }
}