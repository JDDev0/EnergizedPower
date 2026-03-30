package me.jddev0.ep.networking;

import me.jddev0.ep.networking.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class ModMessages {
    private ModMessages() {}

    public static void registerTypedPayloads() {
        //Server -> Client
        PayloadTypeRegistry.clientboundPlay().register(EnergySyncS2CPacket.ID, EnergySyncS2CPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(FluidSyncS2CPacket.ID, FluidSyncS2CPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ItemStackSyncS2CPacket.ID, ItemStackSyncS2CPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenEnergizedPowerBookS2CPacket.ID, OpenEnergizedPowerBookS2CPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncPressMoldMakerRecipeListS2CPacket.ID, SyncPressMoldMakerRecipeListS2CPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncCurrentRecipeS2CPacket.ID, SyncCurrentRecipeS2CPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncIngredientsS2CPacket.ID, SyncIngredientsS2CPacket.STREAM_CODEC);

        //Client -> Server
        PayloadTypeRegistry.serverboundPlay().register(PopEnergizedPowerBookFromLecternC2SPacket.ID, PopEnergizedPowerBookFromLecternC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetAutoCrafterPatternInputSlotsC2SPacket.ID, SetAutoCrafterPatternInputSlotsC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket.ID, SetAdvancedAutoCrafterPatternInputSlotsC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetWeatherFromWeatherControllerC2SPacket.ID, SetWeatherFromWeatherControllerC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetTimeFromTimeControllerC2SPacket.ID, SetTimeFromTimeControllerC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(UseTeleporterC2SPacket.ID, UseTeleporterC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetCheckboxC2SPacket.ID, SetCheckboxC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetAdvancedAutoCrafterRecipeIndexC2SPacket.ID, SetAdvancedAutoCrafterRecipeIndexC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(CycleAutoCrafterRecipeOutputC2SPacket.ID, CycleAutoCrafterRecipeOutputC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(CycleAdvancedAutoCrafterRecipeOutputC2SPacket.ID, CycleAdvancedAutoCrafterRecipeOutputC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(CraftPressMoldMakerRecipeC2SPacket.ID, CraftPressMoldMakerRecipeC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ChangeCurrentRecipeIndexC2SPacket.ID, ChangeCurrentRecipeIndexC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ChangeRedstoneModeC2SPacket.ID, ChangeRedstoneModeC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetFluidTankFilterC2SPacket.ID, SetFluidTankFilterC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ChangeComparatorModeC2SPacket.ID, ChangeComparatorModeC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetCurrentRecipeIdC2SPacket.ID, SetCurrentRecipeIdC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetCreativeFluidTankFluidStackC2SPacket.ID, SetCreativeFluidTankFluidStackC2SPacket.STREAM_CODEC);
    }

    public static void registerPacketsS2C() {
        ClientPlayNetworking.registerGlobalReceiver(EnergySyncS2CPacket.ID, EnergySyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(FluidSyncS2CPacket.ID, FluidSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ItemStackSyncS2CPacket.ID, ItemStackSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(OpenEnergizedPowerBookS2CPacket.ID, OpenEnergizedPowerBookS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SyncPressMoldMakerRecipeListS2CPacket.ID, SyncPressMoldMakerRecipeListS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SyncCurrentRecipeS2CPacket.ID, SyncCurrentRecipeS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SyncIngredientsS2CPacket.ID, SyncIngredientsS2CPacket::receive);
    }

    public static void registerPacketsC2S() {
        ServerPlayNetworking.registerGlobalReceiver(PopEnergizedPowerBookFromLecternC2SPacket.ID, PopEnergizedPowerBookFromLecternC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SetAutoCrafterPatternInputSlotsC2SPacket.ID, SetAutoCrafterPatternInputSlotsC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket.ID, SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SetWeatherFromWeatherControllerC2SPacket.ID, SetWeatherFromWeatherControllerC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SetTimeFromTimeControllerC2SPacket.ID, SetTimeFromTimeControllerC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(UseTeleporterC2SPacket.ID, UseTeleporterC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SetCheckboxC2SPacket.ID, SetCheckboxC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SetAdvancedAutoCrafterRecipeIndexC2SPacket.ID, SetAdvancedAutoCrafterRecipeIndexC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CycleAutoCrafterRecipeOutputC2SPacket.ID, CycleAutoCrafterRecipeOutputC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CycleAdvancedAutoCrafterRecipeOutputC2SPacket.ID, CycleAdvancedAutoCrafterRecipeOutputC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CraftPressMoldMakerRecipeC2SPacket.ID, CraftPressMoldMakerRecipeC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ChangeCurrentRecipeIndexC2SPacket.ID, ChangeCurrentRecipeIndexC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ChangeRedstoneModeC2SPacket.ID, ChangeRedstoneModeC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SetFluidTankFilterC2SPacket.ID, SetFluidTankFilterC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ChangeComparatorModeC2SPacket.ID, ChangeComparatorModeC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SetCurrentRecipeIdC2SPacket.ID, SetCurrentRecipeIdC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SetCreativeFluidTankFluidStackC2SPacket.ID, SetCreativeFluidTankFluidStackC2SPacket::receive);
    }

    public static void sendClientPacketToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    public static void broadcastServerPacket(MinecraftServer server, CustomPacketPayload payload) {
        for(ServerPlayer player:PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendServerPacketToPlayersWithinXBlocks(BlockPos pos, ServerLevel dimension, double distance, CustomPacketPayload payload) {
        for(ServerPlayer player:PlayerLookup.around(dimension, pos, distance)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendServerPacketToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
