package me.jddev0.ep.networking;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class ModMessages {
    public static Identifier ENERGY_SYNC_ID = new Identifier(EnergizedPowerMod.MODID, "energy_sync");
    public static Identifier FLUID_SYNC_ID = new Identifier(EnergizedPowerMod.MODID, "fluid_sync");
    public static Identifier OPEN_ENERGIZED_POWER_BOOK_ID = new Identifier(EnergizedPowerMod.MODID, "open_energized_power_book");
    public static Identifier POP_ENERGIZED_POWER_BOOK_FROM_LECTERN_ID = new Identifier(EnergizedPowerMod.MODID, "pop_energized_power_book_from_lectern");
    public static Identifier SET_AUTO_CRAFTER_PATTERN_INPUT_SLOTS_ID = new Identifier(EnergizedPowerMod.MODID, "set_auto_crafter_pattern_input_slots");
    public static Identifier SET_WEATHER_FROM_WEATHER_CONTROLLER_ID = new Identifier(EnergizedPowerMod.MODID, "set_weather_from_weather_controller");
    public static Identifier SET_TIME_FROM_TIME_CONTROLLER_ID = new Identifier(EnergizedPowerMod.MODID, "set_time_from_time_controller");
    public static Identifier SET_AUTO_CRAFTER_CHECKBOX_ID = new Identifier(EnergizedPowerMod.MODID, "set_auto_crafter_checkbox");
    public static Identifier SET_BLOCK_PLACER_CHECKBOX_ID = new Identifier(EnergizedPowerMod.MODID, "set_block_placer_checkbox");
    public static Identifier CYCLE_AUTO_CRAFTER_RECIPE_OUTPUT_ID = new Identifier(EnergizedPowerMod.MODID, "cycle_auto_crafter_recipe_output");

    private ModMessages() {}

    public static void registerPacketsS2C() {
        ClientPlayNetworking.registerGlobalReceiver(ENERGY_SYNC_ID, EnergySyncS2CPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(FLUID_SYNC_ID, FluidSyncS2CPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(OPEN_ENERGIZED_POWER_BOOK_ID, OpenEnergizedPowerBookS2CPacket::receive);
    }

    public static void registerPacketsC2S() {
        ServerPlayNetworking.registerGlobalReceiver(POP_ENERGIZED_POWER_BOOK_FROM_LECTERN_ID, PopEnergizedPowerBookFromLecternC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_AUTO_CRAFTER_PATTERN_INPUT_SLOTS_ID, SetAutoCrafterPatternInputSlotsC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_WEATHER_FROM_WEATHER_CONTROLLER_ID, SetWeatherFromWeatherControllerC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_TIME_FROM_TIME_CONTROLLER_ID, SetTimeFromTimeControllerC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_AUTO_CRAFTER_CHECKBOX_ID, SetAutoCrafterCheckboxC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_BLOCK_PLACER_CHECKBOX_ID, SetBlockPlacerCheckboxC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(CYCLE_AUTO_CRAFTER_RECIPE_OUTPUT_ID, CycleAutoCrafterRecipeOutputC2SPacket::receive);
    }

    public static void broadcastServerPacket(MinecraftServer server, Identifier channelName, PacketByteBuf buf) {
        for(ServerPlayerEntity player:PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, channelName, buf);
        }
    }

    public static void sendServerPacketToPlayer(ServerPlayerEntity player, Identifier channelName, PacketByteBuf buf) {
        ServerPlayNetworking.send(player, channelName, buf);
    }
}
