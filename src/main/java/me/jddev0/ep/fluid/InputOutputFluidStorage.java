package me.jddev0.ep.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class InputOutputFluidStorage implements IFluidHandler {
    private final EnergizedPowerFluidStorage handler;
    private final BiPredicate<Integer, FluidStack> canInput;
    private final Predicate<Integer> canOutput;

    /**
     * In NeoForge 1.21.1, fluid storages cannot be limited to filter certain tanks.
     * This class must modify the internal data of the handler directly in order to function correctly (=> Only EnergizedPowerFluidStorage is supported).
     */
    public InputOutputFluidStorage(EnergizedPowerFluidStorage handler, BiPredicate<Integer, FluidStack> canInput, Predicate<Integer> canOutput) {
        this.handler = handler;
        this.canInput = canInput;
        this.canOutput = canOutput;
    }

    @Override
    public int getTanks() {
        return handler.getTanks();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return handler.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return handler.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return handler.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if(resource.isEmpty())
            return 0;

        int amountLeft = resource.getAmount();
        int filled = 0;

        for(int i = 0;i < getTanks();i++) {
            if(!isFluidValid(i, resource) || !canInput.test(i, resource))
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
                handler.fluidStacks.set(i, fluid);

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
            handler.onContentsChanged();

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

            if(!FluidStack.isSameFluidSameComponents(fluid, resource) || !canOutput.test(i))
                continue;

            int fluidAmountToDrain = Math.min(fluid.getAmount(), drainingLeft);
            if(!action.simulate()) {
                if(fluidAmountToDrain < fluid.getAmount())
                    fluid.shrink(fluidAmountToDrain);
                else
                    handler.fluidStacks.set(i, FluidStack.EMPTY);
            }

            drained += fluidAmountToDrain;
            drainingLeft -= fluidAmountToDrain;
        }

        if(!action.simulate() && drained > 0)
            handler.onContentsChanged();

        return new FluidStack(resource.getFluidHolder(), drained, resource.getComponentsPatch());
    }
}
