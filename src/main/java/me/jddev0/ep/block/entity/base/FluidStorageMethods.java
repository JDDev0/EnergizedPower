package me.jddev0.ep.block.entity.base;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public interface FluidStorageMethods<F extends IFluidHandler> {
    void saveFluidStorage(@NotNull F fluidStorage, @NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries);

    void loadFluidStorage(@NotNull F fluidStorage, @NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries);

    FluidStack getFluid(F fluidStorage, int tank);

    int getTankCapacity(F fluidStorage, int tank);

    void setFluid(F fluidStorage, int tank, FluidStack fluidStack);

    void setTankCapacity(F fluidStorage, int tank, int capacity);
}
