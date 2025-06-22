package me.jddev0.ep.fluid;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;

public class SimpleFluidStorage extends SingleFluidStorage {
    public static final Codec<FluidStack> CODEC = FluidStack.CODEC_MILLIBUCKETS;

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
}
