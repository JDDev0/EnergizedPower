package me.jddev0.ep.fluid;

import net.minecraft.nbt.NbtCompound;

public interface IEnergizedPowerFluidStorage {
    boolean isEmpty();

    FluidStack getFluid();
    void setFluid(FluidStack fluidStack);

    NbtCompound toNBT(NbtCompound nbt);
    void fromNBT(NbtCompound nbt);
}
