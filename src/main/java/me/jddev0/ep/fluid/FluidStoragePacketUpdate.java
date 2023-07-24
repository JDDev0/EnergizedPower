package me.jddev0.ep.fluid;

/**
 * Used for FluidSyncS2CPacket
 */
public interface FluidStoragePacketUpdate {
    void setFluid(FluidStack fluidStack);
    void setTankCapacity(long capacity);
}
