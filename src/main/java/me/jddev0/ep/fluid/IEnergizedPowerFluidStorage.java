package me.jddev0.ep.fluid;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface IEnergizedPowerFluidStorage extends IFluidHandler {
    default int getTankCount() {
        return getTanks();
    }
    default int getAmount(int tank) {
        return getFluid(tank).getAmount();
    }
    int getTankCapacity(int tank);
    void setTankCapacity(int tank, int capacity);

    void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider);
    void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider);

    FluidStack getFluid(int tank);
    void setFluid(int tank, FluidStack fluidStack);

    default int size() {
        return getTankCount();
    }
}
