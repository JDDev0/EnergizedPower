package me.jddev0.ep.fluid;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public class EnergizedPowerFluidStorage implements IEnergizedPowerFluidStorage {
    final NonNullList<FluidStack> fluidStacks;
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
        this.capacities = capacities;
    }

    @Override
    public void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        if(getTankCount() == 1) {
            FluidStack fluid = getFluid(0);
            CompoundTag tag = new CompoundTag();
            if(!fluid.isEmpty()) {
                tag.put("Fluid", fluid.save(lookupProvider));
            }
            nbt.put("fluid", tag);
        }else {
            for(int i = 0;i < getTankCount();i++) {
                FluidStack fluid = getFluid(i);
                Tag tag = new CompoundTag();
                if(!fluid.isEmpty()) {
                    tag = fluid.save(lookupProvider);
                }
                nbt.put("fluid." + i, tag);
            }
        }
    }

    @Override
    public void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        if(getTankCount() == 1) {
            CompoundTag tag = nbt.getCompound("fluid");
            FluidStack fluid = FluidStack.parseOptional(lookupProvider, tag.getCompound("Fluid"));
            setFluid(0, fluid);
        }else {
            for(int i = 0;i < getTankCount();i++) {
                FluidStack fluid = FluidStack.parseOptional(lookupProvider, nbt.getCompound("fluid." + i));
                setFluid(i, fluid);
            }
        }
    }

    protected void onContentsChanged() {}

    @Override
    public int getTanks() {
        return capacities.length;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return fluidStacks.get(tank);
    }

    @Override
    public final int getTankCapacity(int tank) {
        return capacities[tank];
    }

    @Override
    public final void setTankCapacity(int tank, int capacity) {
        //Does nothing (capacity is final)
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if(resource.isEmpty())
            return 0;

        int amountLeft = resource.getAmount();
        int filled = 0;

        for(int i = 0;i < getTanks();i++) {
            if(!isFluidValid(i, resource))
                continue;

            FluidStack fluid = getFluidInTank(i);
            int capacity = getTankCapacity(i);

            if(action.simulate()) {
                int fluidAmountToAdd = 0;

                if(fluid.isEmpty())
                    fluidAmountToAdd = Math.min(capacity, amountLeft);
                else if(FluidStack.isSameFluidSameComponents(fluid, resource))
                    fluidAmountToAdd = Math.min(capacity - fluid.getAmount(), amountLeft);

                filled += fluidAmountToAdd;
                amountLeft -= fluidAmountToAdd;

                continue;
            }

            if(fluid.isEmpty()) {
                int fluidAmountToAdd = Math.min(capacity, amountLeft);

                fluid = new FluidStack(resource.getFluidHolder(), fluidAmountToAdd, resource.getComponentsPatch());
                fluidStacks.set(i, fluid);

                filled += fluidAmountToAdd;
                amountLeft -= fluidAmountToAdd;

                continue;
            }

            if(FluidStack.isSameFluidSameComponents(fluid, resource)) {
                int fluidAmountToAdd = Math.min(capacity - fluid.getAmount(), amountLeft);

                fluid.grow(fluidAmountToAdd);
                filled += fluidAmountToAdd;
                amountLeft -= fluidAmountToAdd;
            }
        }

        if(!action.simulate() && filled > 0)
            onContentsChanged();

        return filled;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        for(int i = 0;i < getTanks();i++) {
            FluidStack fluid = getFluidInTank(i);
            if(!fluid.isEmpty())
                return drain(new FluidStack(fluid.getFluidHolder(), maxDrain, fluid.getComponentsPatch()), action);
        }

        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if(resource.isEmpty())
            return FluidStack.EMPTY;

        int drainingLeft = resource.getAmount();
        int drained = 0;

        for(int i = 0;i < getTanks();i++) {
            FluidStack fluid = getFluidInTank(i);

            if(!FluidStack.isSameFluidSameComponents(fluid, resource))
                continue;

            int fluidAmountToDrain = Math.min(fluid.getAmount(), drainingLeft);
            if(!action.simulate()) {
                if(fluidAmountToDrain < fluid.getAmount())
                    fluid.shrink(fluidAmountToDrain);
                else
                    fluidStacks.set(i, FluidStack.EMPTY);
            }

            drained += fluidAmountToDrain;
            drainingLeft -= fluidAmountToDrain;
        }

        if(!action.simulate() && drained > 0)
            onContentsChanged();

        return new FluidStack(resource.getFluidHolder(), drained, resource.getComponentsPatch());
    }

    @Override
    public FluidStack getFluid(int tank) {
        return fluidStacks.get(tank);
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStacks.set(tank, fluidStack);
    }
}
