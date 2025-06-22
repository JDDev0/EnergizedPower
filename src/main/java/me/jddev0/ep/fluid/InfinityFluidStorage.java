package me.jddev0.ep.fluid;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class InfinityFluidStorage extends SingleFluidStorage {
    public static final Codec<FluidStack> CODEC = FluidStack.CODEC_MILLIBUCKETS;

    public InfinityFluidStorage() {
        amount = Long.MAX_VALUE;
    }

    public boolean isEmpty() {
        return isResourceBlank();
    }

    public FluidStack getFluid() {
        return new FluidStack(variant, Long.MAX_VALUE);
    }

    public void setFluid(FluidStack fluidStack) {
        variant = fluidStack.getFluidVariant();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return Long.MAX_VALUE;
    }

    public void setFluid(FluidStack fluidStack, TransactionContext transaction) {
        FluidVariant fluidVariant = fluidStack.getFluidVariant();

        updateSnapshots(transaction);

        variant = fluidVariant;
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        return maxAmount;
    }

    @Override
    public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        return !extractedVariant.isBlank() && extractedVariant.isOf(variant.getFluid()) &&
                extractedVariant.componentsMatch(variant.getComponents())?maxAmount:0;
    }
}
