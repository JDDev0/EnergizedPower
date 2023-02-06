package me.jddev0.ep.networking;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.networking.packet.OpenEnergizedPowerBookS2CPacket;
import me.jddev0.ep.networking.packet.PopEnergizedPowerBookFromLecternC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class ModMessages {
    public static Identifier ENERGY_SYNC_ID = new Identifier(EnergizedPowerMod.MODID, "energy_sync");
    public static Identifier OPEN_ENERGIZED_POWER_BOOK_ID = new Identifier(EnergizedPowerMod.MODID, "open_energized_power_book");
    public static Identifier POP_ENERGIZED_POWER_BOOK_FROM_LECTERN_ID = new Identifier(EnergizedPowerMod.MODID, "pop_energized_power_book_from_lectern");

    private ModMessages() {}

    public static void registerPacketsS2C() {
        ClientPlayNetworking.registerGlobalReceiver(ENERGY_SYNC_ID, EnergySyncS2CPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(OPEN_ENERGIZED_POWER_BOOK_ID, OpenEnergizedPowerBookS2CPacket::receive);
    }

    public static void registerPacketsC2S() {
        ServerPlayNetworking.registerGlobalReceiver(POP_ENERGIZED_POWER_BOOK_FROM_LECTERN_ID, PopEnergizedPowerBookFromLecternC2SPacket::receive);
    }

    public static void broadcastServerPacket(MinecraftServer server, Identifier channelName, PacketByteBuf buf) {
        for(ServerPlayerEntity player:PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, channelName, buf);
        }
    }
}
