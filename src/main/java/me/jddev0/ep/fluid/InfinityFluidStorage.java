package me.jddev0.ep.fluid;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class InfinityFluidStorage implements IEnergizedPowerFluidStorage {
    protected FluidStack fluid;

    public InfinityFluidStorage() {
        fluid = FluidStack.EMPTY;
    }

    @Override
    public void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        FluidStack fluid = getFluid(0);
        CompoundTag tag = new CompoundTag();
        if(!fluid.isEmpty()) {
            tag.put("Fluid", fluid.save(lookupProvider));
        }
        nbt.put("fluid", tag);
    }

    @Override
    public void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        CompoundTag tag = nbt.getCompound("fluid");
        FluidStack fluid = FluidStack.parseOptional(lookupProvider, tag.getCompound("Fluid"));
        setFluid(0, fluid);
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

    @Override
    public final void setTankCapacity(int tank, int capacity) {}

    @Override
    public FluidStack getFluid(int tank) {
        return fluid;
    }

    public int getCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack fluidStack) {
        return true;
    }

    public void setFluid(int tank, FluidStack fluid) {
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
