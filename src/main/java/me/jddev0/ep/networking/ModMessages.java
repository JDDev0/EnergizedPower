package me.jddev0.ep.networking;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.packet.EnergySync2SCPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
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

        net.messageBuilder(EnergySync2SCPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).
                decoder(EnergySync2SCPacket::new).
                encoder(EnergySync2SCPacket::toBytes).
                consumerMainThread(EnergySync2SCPacket::handle).
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
