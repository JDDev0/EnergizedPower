package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.UnchargerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.UnchargerMenu;
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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnchargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler> {
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i != 0)
            return false;

        ItemStack stack = itemHandler.getStackInSlot(i);
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage == null || !energyStorage.canExtract())
            return true;

        return energyStorage.extractEnergy(UnchargerBlockEntity.this.limitingEnergyStorage.getMaxExtract(), true) == 0;
    });

    private int energyProductionLeft = -1;

    public UnchargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.UNCHARGER_ENTITY.get(), blockPos, blockState,

                "uncharger",

                ModConfigs.COMMON_UNCHARGER_CAPACITY.getValue(),
                ModConfigs.COMMON_UNCHARGER_TRANSFER_RATE.getValue(),

                1,

                UpgradeModuleModifier.ENERGY_CAPACITY,
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
                if(slot == 0) {
                    IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                    return energyStorage != null && energyStorage.canExtract();
                }

                return super.isValid(slot, stack);
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.isSameItem(stack, itemStack) ||
                            (!ItemStack.isSameItemSameComponents(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(stack.getCapability(Capabilities.EnergyStorage.ITEM) != null && itemStack.getCapability(Capabilities.EnergyStorage.ITEM) != null))))
                        resetProgress();
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
                new EnergyValueContainerData(() -> hasRecipe()?getEnergyProductionPerTick():-1, value -> {}),
                new EnergyValueContainerData(() -> energyProductionLeft, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new UnchargerMenu(id, inventory, this, upgradeModuleInventory, this.data);
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

        nbt.put("recipe.energy_production_left", IntTag.valueOf(energyProductionLeft));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        energyProductionLeft = nbt.getInt("recipe.energy_production_left");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, UnchargerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(blockEntity.redstoneMode.isActive(state.getValue(UnchargerBlock.POWERED))) {
            blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));
            tickRecipe(level, blockPos, state, blockEntity);
            blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
        }

        blockEntity.pushEnergyToOutputs(Direction.values());
    }
    
    protected final int getEnergyProductionPerTick() {
        ItemStack stack = itemHandler.getStackInSlot(0);

        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage == null || !energyStorage.canExtract())
            return -1;

        return energyStorage.extractEnergy(Math.max(0, Math.min(this.limitingEnergyStorage.getMaxExtract(),
                this.energyStorage.getCapacity() - this.energyStorage.getEnergy())), true);
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, UnchargerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStackInSlot(0);

            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if(energyStorage == null || !energyStorage.canExtract())
                return;

            blockEntity.energyProductionLeft = energyStorage.getEnergyStored();

            int energyProductionPerTick = energyStorage.extractEnergy(Math.max(0, Math.min(blockEntity.limitingEnergyStorage.getMaxExtract(),
                    blockEntity.energyStorage.getCapacity() - blockEntity.energyStorage.getEnergy())), false);

            if(blockEntity.energyProductionLeft < 0 || energyProductionPerTick < 0) {
                //Reset progress for invalid values

                blockEntity.resetProgress();
                setChanged(level, blockPos, state);

                return;
            }

            blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() + energyProductionPerTick);
            blockEntity.energyProductionLeft -= energyProductionPerTick;

            if(blockEntity.energyProductionLeft <= 0)
                blockEntity.resetProgress();

            setChanged(level, blockPos, state);
        }else {
            blockEntity.resetProgress();
            setChanged(level, blockPos, state);
        }
    }

    private void resetProgress() {
        energyProductionLeft = -1;
    }

    private boolean hasRecipe() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        return stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
    }
}