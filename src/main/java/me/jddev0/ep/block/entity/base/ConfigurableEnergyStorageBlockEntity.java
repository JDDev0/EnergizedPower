package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.machine.configuration.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigurableEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage>
        extends MenuEnergyStorageBlockEntity<E>
        implements RedstoneModeUpdate, IRedstoneModeHandler {
    protected @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    public ConfigurableEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                String machineName,
                                                long baseEnergyCapacity, long baseEnergyTransferRate) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
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
}