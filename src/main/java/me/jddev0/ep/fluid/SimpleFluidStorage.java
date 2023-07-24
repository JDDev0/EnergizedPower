package me.jddev0.ep.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.nbt.NbtCompound;

public class SimpleFluidStorage extends SingleFluidStorage implements IEnergizedPowerFluidStorage {
    public final long capacity;

    public SimpleFluidStorage(long capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean isEmpty() {
        return isResourceBlank();
    }

    @Override
    public FluidStack getFluid() {
        return new FluidStack(variant, amount);
    }

    @Override
    public void setFluid(FluidStack fluidStack) {
        variant = fluidStack.getFluidVariant();
        amount = fluidStack.getDropletsAmount();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return capacity;
    }

    @Override
    public NbtCompound toNBT(NbtCompound nbt) {
        return getFluid().toNBT(nbt);
    }

    @Override
    public void fromNBT(NbtCompound nbt) {
        FluidStack fluidStack = FluidStack.fromNbt(nbt);

        variant = fluidStack.getFluidVariant();
        amount = fluidStack.getDropletsAmount();
    }
}
