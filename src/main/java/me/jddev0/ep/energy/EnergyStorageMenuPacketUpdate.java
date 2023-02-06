package me.jddev0.ep.energy;

import net.minecraft.block.entity.BlockEntity;

/**
 * Used for EnergySyncS2CPacket
 */
public interface EnergyStorageMenuPacketUpdate extends EnergyStoragePacketUpdate {
    BlockEntity getBlockEntity();
}
