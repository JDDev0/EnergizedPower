package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.block.entity.MachineConfiguratorConfigurable;
import me.jddev0.ep.component.MachineConfigurationComponent;
import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.machine.configuration.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public abstract class ConfigurableEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage>
        extends MenuEnergyStorageBlockEntity<E>
        implements RedstoneModeUpdate, IRedstoneModeHandler,
        MachineConfiguratorConfigurable {
    protected @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    public ConfigurableEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                String machineName,
                                                long baseEnergyCapacity, long baseEnergyTransferRate) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("configuration.redstone_mode", redstoneMode.ordinal());
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        redstoneMode = RedstoneMode.fromIndex(view.getIntOr("configuration.redstone_mode", 0));
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
    public boolean onApplyMachineConfiguration(@NotNull MachineConfigurationComponent machineConfiguration) {
        if(Arrays.stream(getAvailableRedstoneModes()).
                noneMatch(redstoneMode -> redstoneMode == machineConfiguration.getRedstoneMode()))
            return false;

        if(machineConfiguration.getComparatorMode() != ComparatorMode.ENERGY)
            return false;

        //IO configuration is not supported: Invalid if present
        if(!machineConfiguration.getIOConfigurations().isEmpty())
            return false;

        boolean configChanged = redstoneMode != machineConfiguration.getRedstoneMode();
        if(configChanged) {
            this.redstoneMode = machineConfiguration.getRedstoneMode();
            setChanged();
        }

        return true;
    }

    @Override
    public @NotNull MachineConfigurationComponent onStoreMachineConfiguration() {
        return new MachineConfigurationComponent(redstoneMode, ComparatorMode.ENERGY, Map.of());
    }
}