package me.jddev0.ep.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface IEnergizedPowerPacket {
    void write(PacketByteBuf buffer);

    Identifier getId();
}
