package me.jddev0.ep.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface IEnergizedPowerFluidStorage extends SlottedStorage<FluidVariant> {
    default int getTankCount() {
        return getSlotCount();
    }
    default long getAmount(int tank) {
        return getSlot(tank).getAmount();
    }
    long getTankCapacity(int tank);
    void setTankCapacity(int tank, long capacity);
    boolean isValid(int index, FluidVariant resource);

    void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider);
    void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider);

    default FluidVariant getResource(int tank) {
        return getSlot(tank).getResource();
    }

    default FluidStack getFluid(int tank) {
        SingleSlotStorage<FluidVariant> slotStorage = getSlot(tank);
        return new FluidStack(slotStorage.getResource(), slotStorage.getAmount());
    }
    void setFluid(int tank, FluidStack fluidStack);

    default int size() {
        return getTankCount();
    }
}
