package me.jddev0.ep.machine.configuration;

/**
 * Used for IOConfigurationSyncS2CPacket
 */
public interface IOConfigurationUpdate {
    void setIOConfiguration(SlotType slotType, IOConfiguration ioConfiguration);
}
