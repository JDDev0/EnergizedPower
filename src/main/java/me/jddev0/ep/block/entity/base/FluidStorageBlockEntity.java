package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

public abstract class FluidStorageBlockEntity<F extends Storage<FluidVariant>>
        extends BlockEntity
        implements FluidStoragePacketUpdate {
    protected final FluidStorageMethods<F> fluidStorageMethods;

    public final F fluidStorage;

    protected final long baseTankCapacity;

    public FluidStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                   FluidStorageMethods<F> fluidStorageMethods, long baseTankCapacity) {
        super(type, blockPos, blockState);

        this.fluidStorageMethods = fluidStorageMethods;
        this.baseTankCapacity = baseTankCapacity;

        fluidStorage = initFluidStorage();
    }

    protected abstract F initFluidStorage();

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        fluidStorageMethods.saveFluidStorage(fluidStorage, view);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        fluidStorageMethods.loadFluidStorage(fluidStorage, view);
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
