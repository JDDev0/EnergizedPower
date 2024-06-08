package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigurableUpgradableFluidEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, F extends IFluidHandler>
        extends UpgradableFluidEnergyStorageBlockEntity<E, F>
        implements RedstoneModeUpdate, IRedstoneModeHandler, ComparatorModeUpdate, IComparatorModeHandler {
    protected @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    protected @NotNull ComparatorMode comparatorMode = ComparatorMode.FLUID;

    public ConfigurableUpgradableFluidEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                               String machineName,
                                                               int baseEnergyCapacity, int baseEnergyTransferRate,
                                                               FluidStorageMethods<F> fluidStorageMethods, int baseTankCapacity,
                                                               UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate,
                fluidStorageMethods, baseTankCapacity, upgradeModifierSlots);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = nbt.contains("configuration.comparator_mode")?
                ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode")):ComparatorMode.FLUID;
    }

    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> 0;
            case FLUID -> FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
    }

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        setChanged();
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
        setChanged();

        return true;
    }

    @Override
    public void setNextComparatorMode() {
        do {
            comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        }while(comparatorMode == ComparatorMode.ITEM);
        setChanged();
    }

    @Override
    @NotNull
    public ComparatorMode @NotNull [] getAvailableComparatorModes() {
        return new ComparatorMode[] {
                ComparatorMode.ENERGY,
                ComparatorMode.FLUID
        };
    }

    @Override
    @NotNull
    public ComparatorMode getComparatorMode() {
        return comparatorMode;
    }

    @Override
    public boolean setComparatorMode(@NotNull ComparatorMode comparatorMode) {
        if(comparatorMode == ComparatorMode.ITEM)
            return false;

        this.comparatorMode = comparatorMode;
        setChanged();

        return true;
    }
}