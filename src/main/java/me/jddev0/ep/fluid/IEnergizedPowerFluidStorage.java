package me.jddev0.ep.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

    void serialize(ValueOutput view);
    void deserialize(ValueInput view);

    default FluidStack getFluid(int tank) {
        SingleSlotStorage<FluidVariant> slotStorage = getSlot(tank);
        return new FluidStack(slotStorage.getResource(), slotStorage.getAmount());
    }
    void setFluid(int tank, FluidStack fluidStack);

    default int size() {
        return getTankCount();
    }
}
