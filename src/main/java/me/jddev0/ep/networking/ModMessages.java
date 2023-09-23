package me.jddev0.ep.networking;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.packet.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.*;

public final class ModMessages {
    private ModMessages() {}

    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = ChannelBuilder.named(new ResourceLocation(EnergizedPowerMod.MODID, "messages")).
                networkProtocolVersion(1).
                simpleChannel();

        INSTANCE = net;

        //Server -> Client
        net.messageBuilder(EnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).
                decoder(EnergySyncS2CPacket::new).
                encoder(EnergySyncS2CPacket::toBytes).
                consumerMainThread(EnergySyncS2CPacket::handle).
                add();

        net.messageBuilder(FluidSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).
                decoder(FluidSyncS2CPacket::new).
                encoder(FluidSyncS2CPacket::toBytes).
                consumerMainThread(FluidSyncS2CPacket::handle).
                add();

        net.messageBuilder(ItemStackSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).
                decoder(ItemStackSyncS2CPacket::new).
                encoder(ItemStackSyncS2CPacket::toBytes).
                consumerMainThread(ItemStackSyncS2CPacket::handle).
                add();

        net.messageBuilder(OpenEnergizedPowerBookS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).
                decoder(OpenEnergizedPowerBookS2CPacket::new).
                encoder(OpenEnergizedPowerBookS2CPacket::toBytes).
                consumerMainThread(OpenEnergizedPowerBookS2CPacket::handle).
                add();

        //Client -> Server
        net.messageBuilder(PopEnergizedPowerBookFromLecternC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(PopEnergizedPowerBookFromLecternC2SPacket::new).
                encoder(PopEnergizedPowerBookFromLecternC2SPacket::toBytes).
                consumerMainThread(PopEnergizedPowerBookFromLecternC2SPacket::handle).
                add();

        net.messageBuilder(SetAutoCrafterPatternInputSlotsC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetAutoCrafterPatternInputSlotsC2SPacket::new).
                encoder(SetAutoCrafterPatternInputSlotsC2SPacket::toBytes).
                consumerMainThread(SetAutoCrafterPatternInputSlotsC2SPacket::handle).
                add();

        net.messageBuilder(SetWeatherFromWeatherControllerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetWeatherFromWeatherControllerC2SPacket::new).
                encoder(SetWeatherFromWeatherControllerC2SPacket::toBytes).
                consumerMainThread(SetWeatherFromWeatherControllerC2SPacket::handle).
                add();

        net.messageBuilder(SetTimeFromTimeControllerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetTimeFromTimeControllerC2SPacket::new).
                encoder(SetTimeFromTimeControllerC2SPacket::toBytes).
                consumerMainThread(SetTimeFromTimeControllerC2SPacket::handle).
                add();

        net.messageBuilder(SetAutoCrafterCheckboxC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetAutoCrafterCheckboxC2SPacket::new).
                encoder(SetAutoCrafterCheckboxC2SPacket::toBytes).
                consumerMainThread(SetAutoCrafterCheckboxC2SPacket::handle).
                add();

        net.messageBuilder(SetBlockPlacerCheckboxC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetBlockPlacerCheckboxC2SPacket::new).
                encoder(SetBlockPlacerCheckboxC2SPacket::toBytes).
                consumerMainThread(SetBlockPlacerCheckboxC2SPacket::handle).
                add();

        net.messageBuilder(SetItemConveyorBeltSorterCheckboxC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetItemConveyorBeltSorterCheckboxC2SPacket::new).
                encoder(SetItemConveyorBeltSorterCheckboxC2SPacket::toBytes).
                consumerMainThread(SetItemConveyorBeltSorterCheckboxC2SPacket::handle).
                add();

        net.messageBuilder(CycleAutoCrafterRecipeOutputC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(CycleAutoCrafterRecipeOutputC2SPacket::new).
                encoder(CycleAutoCrafterRecipeOutputC2SPacket::toBytes).
                consumerMainThread(CycleAutoCrafterRecipeOutputC2SPacket::handle).
                add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.send(message, PacketDistributor.SERVER.noArg());
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
    }

    public static <MSG> void sendToPlayerNear(MSG message, PacketDistributor.TargetPoint targetPoint) {
        INSTANCE.send(message, PacketDistributor.NEAR.with(targetPoint));
    }

    public static <MSG> void sendToPlayersWithinXBlocks(MSG message, BlockPos pos, ResourceKey<Level> dimension, int distance) {
        INSTANCE.send(message, PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), distance, dimension)));
    }

    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(message, PacketDistributor.ALL.noArg());
    }
}
