package me.jddev0.ep.machine.configuration;

import net.minecraft.server.level.ServerPlayer;

/**
 * Used for SetIOConfigurationC2SPacket
 */
public interface SetIOConfigurationUpdate {
    void setIOConfigurationByPlayer(SlotType slotType, RelativeDirection direction, int slotGroupId, ServerPlayer player);
}
