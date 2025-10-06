package me.jddev0.ep.fluid;

import net.minecraft.core.NonNullList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;

public class EnergizedPowerFluidStorage implements ResourceHandler<FluidResource> {
    private final NonNullList<FluidStack> fluidStacks;
    private final List<SnapshotJournal<FluidStack>> fluidSnapshots;
    private final int[] capacities;

    public EnergizedPowerFluidStorage(int[] capacities) {
        this.fluidStacks = NonNullList.withSize(capacities.length, FluidStack.EMPTY);
        this.fluidSnapshots = new ArrayList<>(capacities.length);
        for(int i = 0;i < capacities.length;i++) {
            final int index = i;
            fluidSnapshots.add(new SnapshotJournal<>() {
                @Override
                protected FluidStack createSnapshot() {
                    return fluidStacks.get(index).copy();
                }

                @Override
                protected void revertToSnapshot(FluidStack snapshot) {
                    fluidStacks.set(index, snapshot);
                }

                @Override
                protected void onRootCommit(FluidStack originalState) {
                   onFinalCommit();
                }
            });
        }

        this.capacities = capacities;
    }

    protected void onFinalCommit() {}

    @Override
    public int size() {
        return capacities.length;
    }

    @Override
    public FluidResource getResource(int tank) {
        return FluidResource.of(fluidStacks.get(tank));
    }

    public FluidStack getFluid(int tank) {
        return fluidStacks.get(tank).copy();
    }

    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStacks.set(tank, fluidStack);
    }

    @Override
    public long getAmountAsLong(int tank) {
        return fluidStacks.get(tank).getAmount();
    }

    @Override
    public long getCapacityAsLong(int tank, FluidResource resource) {
        return capacities[tank];
    }

    public int getCapacity(int tank) {
        return capacities[tank];
    }

    @Override
    public boolean isValid(int tank, FluidResource resource) {
        return true;
    }

    @Override
    public int insert(int index, FluidResource resource, int maxAmount, TransactionContext transaction) {
        int currentAmount = fluidStacks.get(index).getAmount();

        if((currentAmount == 0 || resource.matches(fluidStacks.get(index))) && isValid(index, resource)) {
            int inserted = Math.min(maxAmount, getCapacity(index) - currentAmount);
            if(inserted > 0) {
                fluidSnapshots.get(index).updateSnapshots(transaction);
                fluidStacks.set(index, resource.toStack(currentAmount + inserted));
                return inserted;
            }
        }

        return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int maxAmount, TransactionContext transaction) {
        int currentAmount = fluidStacks.get(index).getAmount();

        if(resource.matches(fluidStacks.get(index))) {
            int extracted = Math.min(maxAmount, currentAmount);
            if(extracted > 0) {
                fluidSnapshots.get(index).updateSnapshots(transaction);
                fluidStacks.set(index, resource.toStack(currentAmount - extracted));
                return extracted;
            }
        }

        return 0;
    }
}
