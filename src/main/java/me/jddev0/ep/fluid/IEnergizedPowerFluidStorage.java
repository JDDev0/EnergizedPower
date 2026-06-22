package me.jddev0.ep.fluid;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public interface IEnergizedPowerFluidStorage extends ResourceHandler<FluidResource> {
    default int getTankCount() {
        return size();
    }
    int getTankCapacity(int tank);
    void setTankCapacity(int tank, int capacity);

    void serialize(ValueOutput view);
    void deserialize(ValueInput view);

    default FluidStack getFluid(int tank) {
        return getResource(tank).toStack(getAmountAsInt(tank));
    }
    void setFluid(int slot, FluidStack itemStack);
}
