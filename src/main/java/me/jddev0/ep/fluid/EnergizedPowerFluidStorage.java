package me.jddev0.ep.fluid;

import net.minecraft.core.NonNullList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class EnergizedPowerFluidStorage implements IFluidHandler {
    private final NonNullList<FluidStack> fluidStacks;
    private final int[] capacities;

    public EnergizedPowerFluidStorage(int[] capacities) {
        this.fluidStacks = NonNullList.withSize(capacities.length, FluidStack.EMPTY);
        this.capacities = capacities;
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
    public int getTankCapacity(int tank) {
        return capacities[tank];
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

    public FluidStack getFluid(int tank) {
        return fluidStacks.get(tank);
    }

    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStacks.set(tank, fluidStack);
    }

    public int getCapacity(int tank) {
        return capacities[tank];
    }

    public void setCapacity(int tank, int capacity) {
        capacities[tank] = capacity;
    }
}
