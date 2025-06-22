package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigurableUpgradableInventoryEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends SimpleInventory>
        extends UpgradableInventoryEnergyStorageBlockEntity<E, I>
        implements RedstoneModeUpdate, IRedstoneModeHandler, ComparatorModeUpdate, IComparatorModeHandler {
    protected @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    protected @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    public ConfigurableUpgradableInventoryEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                                   String machineName,
                                                                   long baseEnergyCapacity, long baseEnergyTransferRate,
                                                                   int slotCount,
                                                                   UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate, slotCount, upgradeModifierSlots);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        view.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        view.putInt("configuration.comparator_mode", comparatorMode.ordinal());
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        redstoneMode = RedstoneMode.fromIndex(view.getInt("configuration.redstone_mode", 0));
        comparatorMode = ComparatorMode.fromIndex(view.getInt("configuration.comparator_mode", 0));
    }

    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> ScreenHandler.calculateComparatorOutput(itemHandler);
            case FLUID -> 0;
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
    }

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        markDirty();
    }

    @Override
    @NotNull
    public RedstoneMode @NotNull [] getAvailableRedstoneModes() {
        return RedstoneMode.values();
    }

    @Override
    @NotNull
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public boolean setRedstoneMode(@NotNull RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;
        markDirty();

        return true;
    }

    @Override
    public void setNextComparatorMode() {
        do {
            comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        }while(comparatorMode == ComparatorMode.FLUID);
        markDirty();
    }

    @Override
    @NotNull
    public ComparatorMode @NotNull [] getAvailableComparatorModes() {
        return new ComparatorMode[] {
                ComparatorMode.ENERGY,
                ComparatorMode.ITEM
        };
    }

    @Override
    @NotNull
    public ComparatorMode getComparatorMode() {
        return comparatorMode;
    }

    @Override
    public boolean setComparatorMode(@NotNull ComparatorMode comparatorMode) {
        if(comparatorMode == ComparatorMode.FLUID)
            return false;

        this.comparatorMode = comparatorMode;
        markDirty();

        return true;
    }
}