package me.jddev0.ep.fluid;

import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Used for FluidSyncS2CPacket
 */
public interface FluidStoragePacketUpdate {
    void setFluid(FluidStack fluidStack);
    void setTankCapacity(int capacity);
}
