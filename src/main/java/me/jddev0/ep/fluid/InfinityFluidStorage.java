package me.jddev0.ep.fluid;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class InfinityFluidStorage implements IFluidHandler {
    protected FluidStack fluid;

    public InfinityFluidStorage() {
        fluid = FluidStack.EMPTY;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return fluid;
    }

    @Override
    public int getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public int getCapacity() {
        return Integer.MAX_VALUE;
    }

    public void readFromNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        this.fluid = FluidStack.parseOptional(lookupProvider, nbt.getCompound("Fluid"));
    }

    public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        if(!this.fluid.isEmpty())
            nbt.put("Fluid", this.fluid.save(lookupProvider));

        return nbt;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack fluidStack) {
        return true;
    }

    public void setFluid(FluidStack fluid) {
        if(fluid.isEmpty())
            this.fluid = FluidStack.EMPTY;
        else
            this.fluid = fluid.copyWithAmount(Integer.MAX_VALUE);

        onChange();
    }

    protected void onChange() {}

    @Override
    public int fill(FluidStack fluidStack, FluidAction fluidAction) {
        return fluidStack.getAmount();
    }

    @Override
    public FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
        return !fluidStack.isEmpty() && FluidStack.isSameFluidSameComponents(fluidStack, this.fluid)?
                drain(fluidStack.getAmount(), fluidAction):FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int amount, FluidAction fluidAction) {
        return fluid.copyWithAmount(amount);
    }
}
