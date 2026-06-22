package me.jddev0.ep.fluid;

import net.minecraft.core.NonNullList;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class EnergizedPowerFluidStorage implements IEnergizedPowerFluidStorage {
    private final NonNullList<FluidStack> fluidStacks;
    private final List<SnapshotJournal<FluidStack>> fluidSnapshots;
    private final int[] capacities;

    /**
     * Constructor for a single tank
     */
    public EnergizedPowerFluidStorage(int capacity) {
        this(1, capacity);
    }

    /**
     * Constructor for multiple tanks with the same capacity
     */
    public EnergizedPowerFluidStorage(int size, int capacity) {
        this(IntStream.range(0, size).map(i -> capacity).toArray());
    }

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
    public void serialize(ValueOutput view) {
        if(getTankCount() == 1) {
            FluidStack fluid = getFluid(0);
            view.child("fluid").storeNullable("Fluid", FluidStack.CODEC, fluid.isEmpty()?null:fluid);
        }else {
            for(int i = 0;i < getTankCount();i++) {
                FluidStack fluid = getFluid(i);
                view.storeNullable("fluid." + i, FluidStack.CODEC, fluid.isEmpty()?null:fluid);
            }
        }
    }

    @Override
    public void deserialize(ValueInput view) {
        if(getTankCount() == 1) {
            FluidStack fluid = view.child("fluid").flatMap(subView -> subView.read("Fluid", FluidStack.CODEC)).
                    orElse(FluidStack.EMPTY);
            setFluid(0, fluid);
        }else {
            for(int i = 0;i < getTankCount();i++) {
                FluidStack fluid = view.read("fluid." + i, FluidStack.CODEC).
                        orElse(FluidStack.EMPTY);
                setFluid(i, fluid);
            }
        }
    }

    @Override
    public int size() {
        return capacities.length;
    }

    @Override
    public FluidResource getResource(int tank) {
        return FluidResource.of(fluidStacks.get(tank));
    }

    @Override
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

    @Override
    public int getTankCapacity(int tank) {
        return capacities[tank];
    }

    @Override
    public void setTankCapacity(int tank, int capacity) {
        //Does nothing (capacity is final)
    }

    @Override
    public boolean isValid(int tank, FluidResource resource) {
        return true;
    }

    @Override
    public int insert(int index, FluidResource resource, int maxAmount, TransactionContext transaction) {
        int currentAmount = fluidStacks.get(index).getAmount();

        if((currentAmount == 0 || resource.matches(fluidStacks.get(index))) && isValid(index, resource)) {
            int inserted = Math.min(maxAmount, getTankCapacity(index) - currentAmount);
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
