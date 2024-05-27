package me.jddev0.ep.networking;

import me.jddev0.ep.networking.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class ModMessages {
    private ModMessages() {}

    public static void registerTypedPayloads() {
        //Server -> Client
        PayloadTypeRegistry.playS2C().register(EnergySyncS2CPacket.ID, EnergySyncS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(FluidSyncS2CPacket.ID, FluidSyncS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ItemStackSyncS2CPacket.ID, ItemStackSyncS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(OpenEnergizedPowerBookS2CPacket.ID, OpenEnergizedPowerBookS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncPressMoldMakerRecipeListS2CPacket.ID, SyncPressMoldMakerRecipeListS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncCurrentRecipeS2CPacket.ID, SyncCurrentRecipeS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncFurnaceRecipeTypeS2CPacket.ID, SyncFurnaceRecipeTypeS2CPacket.PACKET_CODEC);

        //Client -> Server
        PayloadTypeRegistry.playC2S().register(PopEnergizedPowerBookFromLecternC2SPacket.ID, PopEnergizedPowerBookFromLecternC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SetAutoCrafterPatternInputSlotsC2SPacket.ID, SetAutoCrafterPatternInputSlotsC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket.ID, SetAdvancedAutoCrafterPatternInputSlotsC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SetWeatherFromWeatherControllerC2SPacket.ID, SetWeatherFromWeatherControllerC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SetTimeFromTimeControllerC2SPacket.ID, SetTimeFromTimeControllerC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(UseTeleporterC2SPacket.ID, UseTeleporterC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SetCheckboxC2SPacket.ID, SetCheckboxC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SetAdvancedAutoCrafterRecipeIndexC2SPacket.ID, SetAdvancedAutoCrafterRecipeIndexC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(CycleAutoCrafterRecipeOutputC2SPacket.ID, CycleAutoCrafterRecipeOutputC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(CycleAdvancedAutoCrafterRecipeOutputC2SPacket.ID, CycleAdvancedAutoCrafterRecipeOutputC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(CraftPressMoldMakerRecipeC2SPacket.ID, CraftPressMoldMakerRecipeC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(ChangeCurrentRecipeIndexC2SPacket.ID, ChangeCurrentRecipeIndexC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(ChangeRedstoneModeC2SPacket.ID, ChangeRedstoneModeC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SetFluidTankFilterC2SPacket.ID, SetFluidTankFilterC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(ChangeComparatorModeC2SPacket.ID, ChangeComparatorModeC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SetCurrentRecipeIdC2SPacket.ID, SetCurrentRecipeIdC2SPacket.PACKET_CODEC);
    }

    public static void registerPacketsS2C() {
        ClientPlayNetworking.registerGlobalReceiver(EnergySyncS2CPacket.ID, EnergySyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(FluidSyncS2CPacket.ID, FluidSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ItemStackSyncS2CPacket.ID, ItemStackSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(OpenEnergizedPowerBookS2CPacket.ID, OpenEnergizedPowerBookS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SyncPressMoldMakerRecipeListS2CPacket.ID, SyncPressMoldMakerRecipeListS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SyncCurrentRecipeS2CPacket.ID, SyncCurrentRecipeS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SyncFurnaceRecipeTypeS2CPacket.ID, SyncFurnaceRecipeTypeS2CPacket::receive);
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
    }

    public static void sendClientPacketToServer(CustomPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    public static void broadcastServerPacket(MinecraftServer server, CustomPayload payload) {
        for(ServerPlayerEntity player:PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendServerPacketToPlayersWithinXBlocks(BlockPos pos, ServerWorld dimension, double distance, CustomPayload payload) {
        for(ServerPlayerEntity player:PlayerLookup.around(dimension, pos, distance)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendServerPacketToPlayer(ServerPlayerEntity player, CustomPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
