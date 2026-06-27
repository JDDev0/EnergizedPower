package me.jddev0.ep.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class InfinityFluidStorage extends SingleFluidStorage implements IEnergizedPowerFluidStorage {
    public InfinityFluidStorage() {
        amount = Long.MAX_VALUE;
    }

    @Override
    public void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        FluidStack fluid = getFluid(0);
        CompoundTag tag = new CompoundTag();
        if(!fluid.isEmpty()) {
            tag.put("Fluid", fluid.toNBT(new CompoundTag(), lookupProvider));
        }
        nbt.put("fluid", tag);
    }

    @Override
    public void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        CompoundTag tag = nbt.getCompound("fluid");
        FluidStack fluid = FluidStack.fromNbt(tag.getCompound("Fluid"), lookupProvider);
        setFluid(0, fluid);
    }

    public void setFluid(FluidStack fluidStack) {
        variant = fluidStack.getFluidVariant();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return Long.MAX_VALUE;
    }

    @Override
    public final long getTankCapacity(int tank) {
        return Long.MAX_VALUE;
    }

    @Override
    public final void setTankCapacity(int tank, long capacity) {}

    @Override
    public boolean isValid(int index, FluidVariant resource) {
        return true;
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        variant = fluidStack.getFluidVariant();
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
