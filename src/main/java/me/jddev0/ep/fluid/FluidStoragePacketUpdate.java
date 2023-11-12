package me.jddev0.ep.fluid;

/**
 * Used for FluidSyncS2CPacket
 */
public interface FluidStoragePacketUpdate {
    void setFluid(int tank, FluidStack fluidStack);
    void setTankCapacity(int tank, long capacity);
}
