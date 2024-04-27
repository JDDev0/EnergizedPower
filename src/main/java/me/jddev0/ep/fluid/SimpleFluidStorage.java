package me.jddev0.ep.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public class SimpleFluidStorage extends SingleFluidStorage {
    public final long capacity;

    public SimpleFluidStorage(long capacity) {
        this.capacity = capacity;
    }

    public boolean isEmpty() {
        return isResourceBlank();
    }

    public FluidStack getFluid() {
        return new FluidStack(variant, amount);
    }

    public void setFluid(FluidStack fluidStack) {
        variant = fluidStack.getFluidVariant();
        amount = fluidStack.getDropletsAmount();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return capacity;
    }

    public NbtCompound toNBT(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        if(isResourceBlank())
            return nbt;

        nbt.put("Fluid", getFluid().toNBT(new NbtCompound(), registries));
        return nbt;
    }

    public void fromNBT(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        FluidStack fluidStack = FluidStack.fromNbt(nbt.getCompound("Fluid"), registries);

        variant = fluidStack.getFluidVariant();
        amount = fluidStack.getDropletsAmount();
    }
}
