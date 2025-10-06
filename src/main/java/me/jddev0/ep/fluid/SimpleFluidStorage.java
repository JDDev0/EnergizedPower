package me.jddev0.ep.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SimpleFluidStorage extends SnapshotJournal<FluidStack> implements ResourceHandler<FluidResource> {
    protected FluidStack fluid = FluidStack.EMPTY;
    public final int capacity;

    public SimpleFluidStorage(int capacity) {
        this.capacity = capacity;
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
        return fluid.copy();
    }

    public boolean isEmpty() {
        return getResource(0).isEmpty();
    }

    public void setFluid(FluidStack fluidStack) {
        this.fluid = fluidStack.copy();
    }

    @Override
    public long getAmountAsLong(int tank) {
        return fluid.getAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return true;
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
        int currentAmount = fluid.getAmount();

        if((currentAmount == 0 || resource.matches(fluid)) && isValid(index, resource)) {
            int inserted = Math.min(maxAmount, capacity - currentAmount);
            if(inserted > 0) {
                updateSnapshots(transaction);
                fluid = resource.toStack(currentAmount + inserted);
                return inserted;
            }
        }

        return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int maxAmount, TransactionContext transaction) {
        int currentAmount = fluid.getAmount();

        if(resource.matches(fluid)) {
            int extracted = Math.min(maxAmount, currentAmount);
            if(extracted > 0) {
                updateSnapshots(transaction);
                fluid = resource.toStack(currentAmount - extracted);
                return extracted;
            }
        }

        return 0;
    }
}
