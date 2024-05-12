package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageMultiTankMethods implements FluidStorageMethods<EnergizedPowerFluidStorage> {
    public static final FluidStorageMultiTankMethods INSTANCE = new FluidStorageMultiTankMethods();

    private FluidStorageMultiTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull EnergizedPowerFluidStorage fluidStorage, @NotNull CompoundTag nbt,
                                  @NotNull HolderLookup.Provider registries) {
        for(int i = 0;i < fluidStorage.getTanks();i++)
            nbt.put("fluid." + i, fluidStorage.getFluid(i).saveOptional(registries));
    }

    @Override
    public void loadFluidStorage(@NotNull EnergizedPowerFluidStorage fluidStorage, @NotNull CompoundTag nbt,
                                  @NotNull HolderLookup.Provider registries) {
        for(int i = 0;i < fluidStorage.getTanks();i++)
            fluidStorage.setFluid(i, FluidStack.parseOptional(registries, nbt.getCompound("fluid." + i)));
    }

    @Override
    public FluidStack getFluid(EnergizedPowerFluidStorage fluidStorage, int tank) {
        return fluidStorage.getFluid(tank);
    }

    @Override
    public int getTankCapacity(EnergizedPowerFluidStorage fluidStorage, int tank) {
        return fluidStorage.getCapacity(tank);
    }

    @Override
    public void setFluid(EnergizedPowerFluidStorage fluidStorage, int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(tank, fluidStack);
    }

    @Override
    public void setTankCapacity(EnergizedPowerFluidStorage fluidStorage, int tank, int capacity) {
        fluidStorage.setCapacity(tank, capacity);
    }
}
