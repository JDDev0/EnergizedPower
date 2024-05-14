package me.jddev0.ep.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface EnergizedPowerServerPacketHandler<P extends IEnergizedPowerPacket> {
    void receive(P data, MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketSender responseSender);
}
