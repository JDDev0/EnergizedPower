package me.jddev0.ep.fluid;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class InfinityFluidStorage extends SnapshotJournal<FluidStack> implements IEnergizedPowerFluidStorage {
    protected FluidStack fluid;

    public InfinityFluidStorage() {
        fluid = FluidStack.EMPTY;
    }

    @Override
    public void serialize(ValueOutput view) {
        FluidStack fluid = getFluid(0);
        view.child("fluid").storeNullable("Fluid", FluidStack.CODEC, fluid.isEmpty()?null:fluid);
    }

    @Override
    public void deserialize(ValueInput view) {
        FluidStack fluid = view.child("fluid").flatMap(subView -> subView.read("Fluid", FluidStack.CODEC)).
                orElse(FluidStack.EMPTY);
        setFluid(0, fluid);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int tank) {
        return FluidResource.of(fluid);
    }

    @Override
    public long getAmountAsLong(int tank) {
        return fluid.isEmpty()?0:Integer.MAX_VALUE;
    }

    @Override
    public long getCapacityAsLong(int tank, FluidResource resource) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setTankCapacity(int tank, int capacity) {}

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return true;
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
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
