package me.jddev0.ep.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

@FunctionalInterface
public interface EnergizedPowerClientPacketHandler<P extends IEnergizedPowerPacket> {
    void receive(P data, MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketSender responseSender);
}
