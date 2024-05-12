package me.jddev0.ep.block.entity.base;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageSingleTankMethods implements FluidStorageMethods<FluidTank> {
    public static final FluidStorageSingleTankMethods INSTANCE = new FluidStorageSingleTankMethods();

    private FluidStorageSingleTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull FluidTank fluidStorage, @NotNull CompoundTag nbt,
                                  @NotNull HolderLookup.Provider registries) {
        nbt.put("fluid", fluidStorage.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    public void loadFluidStorage(@NotNull FluidTank fluidStorage, @NotNull CompoundTag nbt,
                                  @NotNull HolderLookup.Provider registries) {
        fluidStorage.readFromNBT(registries, nbt.getCompound("fluid"));
    }

    @Override
    public FluidStack getFluid(FluidTank fluidStorage, int tank) {
        return fluidStorage.getFluid();
    }

    @Override
    public int getTankCapacity(FluidTank fluidStorage, int tank) {
        return fluidStorage.getCapacity();
    }

    @Override
    public void setFluid(FluidTank fluidStorage, int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(FluidTank fluidStorage, int tank, int capacity) {
        fluidStorage.setCapacity(capacity);
    }
}
