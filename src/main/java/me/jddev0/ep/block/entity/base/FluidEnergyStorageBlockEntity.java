package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public abstract class FluidEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, F extends Storage<FluidVariant>>
        extends EnergyStorageBlockEntity<E>
        implements FluidStoragePacketUpdate {
    protected final FluidStorageMethods<F> fluidStorageMethods;

    public final F fluidStorage;

    protected final long baseTankCapacity;

    public FluidEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                         long baseEnergyCapacity, long baseEnergyTransferRate,
                                         FluidStorageMethods<F> fluidStorageMethods, long baseTankCapacity) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate);

        this.fluidStorageMethods = fluidStorageMethods;
        this.baseTankCapacity = baseTankCapacity;

        fluidStorage = initFluidStorage();
    }

    protected abstract F initFluidStorage();

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        fluidStorageMethods.saveFluidStorage(fluidStorage, nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        fluidStorageMethods.loadFluidStorage(fluidStorage, nbt);
    }

    protected final void syncFluidToPlayer(PlayerEntity player) {
        fluidStorageMethods.syncFluidToPlayer(fluidStorage, player, pos);
    }

    protected final void syncFluidToPlayers(int distance) {
        if(world != null && !world.isClient())
            fluidStorageMethods.syncFluidToPlayers(fluidStorage, world, pos, distance);
    }

    public FluidStack getFluid(int tank) {
        return fluidStorageMethods.getFluid(fluidStorage, tank);
    }

    public long getTankCapacity(int tank) {
        return fluidStorageMethods.getTankCapacity(fluidStorage, tank);
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorageMethods.setFluid(fluidStorage, tank, fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, long capacity) {
        fluidStorageMethods.setTankCapacity(fluidStorage, tank, capacity);
    }
}
