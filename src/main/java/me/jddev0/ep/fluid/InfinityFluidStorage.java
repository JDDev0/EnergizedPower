package me.jddev0.ep.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class InfinityFluidStorage extends SnapshotJournal<FluidStack> implements ResourceHandler<FluidResource> {
    protected FluidStack fluid;

    public InfinityFluidStorage() {
        fluid = FluidStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int tank) {
        return FluidResource.of(fluid);
    }

    public FluidStack getFluid() {
        return fluid.copyWithAmount(Integer.MAX_VALUE);
    }

    @Override
    public long getAmountAsLong(int tank) {
        return fluid.isEmpty()?0:Integer.MAX_VALUE;
    }

    @Override
    public long getCapacityAsLong(int tank, FluidResource resource) {
        return Integer.MAX_VALUE;
    }

    public int getCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return true;
    }

    public void setFluid(FluidStack fluidStack) {
        if(fluidStack.isEmpty())
            this.fluid = FluidStack.EMPTY;
        else
            this.fluid = fluidStack.copyWithAmount(Integer.MAX_VALUE);
    }

    public void setFluid(FluidStack fluidStack, TransactionContext transaction) {
        updateSnapshots(transaction);

        fluid = fluidStack.copyWithAmount(Integer.MAX_VALUE);
    }

    @Override
    protected FluidStack createSnapshot() {
        return fluid.copy();
    }

    @Override
    protected void revertToSnapshot(FluidStack snapshot) {
        fluid = snapshot;
    }

    protected void onFinalCommit() {}

    @Override
    protected void onRootCommit(FluidStack originalState) {
        onFinalCommit();
    }

    @Override
    public int insert(int index, FluidResource resource, int maxAmount, TransactionContext transaction) {
        return maxAmount;
    }

    @Override
    public int extract(int index, FluidResource resource, int maxAmount, TransactionContext transaction) {
        return !resource.isEmpty() && resource.matches(fluid)?maxAmount:0;
    }
}
