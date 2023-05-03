package me.jddev0.ep.networking;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModMessages {
    private ModMessages() {}

    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(EnergizedPowerMod.MODID, "messages")).
                networkProtocolVersion(() -> "1.0").
                clientAcceptedVersions(v -> true).
                serverAcceptedVersions(v -> true).
                simpleChannel();

        INSTANCE = net;

        net.messageBuilder(EnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).
                decoder(EnergySyncS2CPacket::new).
                encoder(EnergySyncS2CPacket::toBytes).
                consumer(EnergySyncS2CPacket::handle).
                add();

        net.messageBuilder(OpenEnergizedPowerBookS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).
                decoder(OpenEnergizedPowerBookS2CPacket::new).
                encoder(OpenEnergizedPowerBookS2CPacket::toBytes).
                consumer(OpenEnergizedPowerBookS2CPacket::handle).
                add();

        net.messageBuilder(PopEnergizedPowerBookFromLecternC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(PopEnergizedPowerBookFromLecternC2SPacket::new).
                encoder(PopEnergizedPowerBookFromLecternC2SPacket::toBytes).
                consumer(PopEnergizedPowerBookFromLecternC2SPacket::handle).
                add();

        net.messageBuilder(SetAutoCrafterPatternInputSlotsC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetAutoCrafterPatternInputSlotsC2SPacket::new).
                encoder(SetAutoCrafterPatternInputSlotsC2SPacket::toBytes).
                consumer(SetAutoCrafterPatternInputSlotsC2SPacket::handle).
                add();

        net.messageBuilder(SetWeatherFromWeatherControllerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetWeatherFromWeatherControllerC2SPacket::new).
                encoder(SetWeatherFromWeatherControllerC2SPacket::toBytes).
                consumer(SetWeatherFromWeatherControllerC2SPacket::handle).
                add();

        net.messageBuilder(SetTimeFromTimeControllerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetTimeFromTimeControllerC2SPacket::new).
                encoder(SetTimeFromTimeControllerC2SPacket::toBytes).
                consumer(SetTimeFromTimeControllerC2SPacket::handle).
                add();

        net.messageBuilder(SetAutoCrafterCheckboxC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetAutoCrafterCheckboxC2SPacket::new).
                encoder(SetAutoCrafterCheckboxC2SPacket::toBytes).
                consumer(SetAutoCrafterCheckboxC2SPacket::handle).
                add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
