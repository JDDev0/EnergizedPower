package me.jddev0.ep.networking;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class ModMessages {
    public static Identifier ENERGY_SYNC_ID = new Identifier(EnergizedPowerMod.MODID, "energy_sync");
    public static Identifier FLUID_SYNC_ID = new Identifier(EnergizedPowerMod.MODID, "fluid_sync");
    public static Identifier ITEM_STACK_SYNC_ID = new Identifier(EnergizedPowerMod.MODID, "item_stack_sync");
    public static Identifier OPEN_ENERGIZED_POWER_BOOK_ID = new Identifier(EnergizedPowerMod.MODID, "open_energized_power_book");
    public static Identifier SYNC_PRESS_MOLD_MAKER_RECIPE_LIST_ID = new Identifier(EnergizedPowerMod.MODID, "sync_press_mold_maker_recipe_list");
    public static Identifier SYNC_STONE_SOLIDIFIER_CURRENT_RECIPE_ID = new Identifier(EnergizedPowerMod.MODID, "sync_stone_solidifier_current_recipe");
    public static Identifier SYNC_FILTRATION_PLANT_CURRENT_RECIPE_ID = new Identifier(EnergizedPowerMod.MODID, "sync_filtration_plant_current_recipe");
    public static Identifier SYNC_FURNACE_RECIPE_TYPE = new Identifier(EnergizedPowerMod.MODID, "sync_furnace_recipe_type");
    public static Identifier POP_ENERGIZED_POWER_BOOK_FROM_LECTERN_ID = new Identifier(EnergizedPowerMod.MODID, "pop_energized_power_book_from_lectern");
    public static Identifier SET_AUTO_CRAFTER_PATTERN_INPUT_SLOTS_ID = new Identifier(EnergizedPowerMod.MODID, "set_auto_crafter_pattern_input_slots");
    public static Identifier SET_ADVANCED_AUTO_CRAFTER_PATTERN_INPUT_SLOTS_ID = new Identifier(EnergizedPowerMod.MODID, "set_advanced_auto_crafter_pattern_input_slots");
    public static Identifier SET_WEATHER_FROM_WEATHER_CONTROLLER_ID = new Identifier(EnergizedPowerMod.MODID, "set_weather_from_weather_controller");
    public static Identifier SET_TIME_FROM_TIME_CONTROLLER_ID = new Identifier(EnergizedPowerMod.MODID, "set_time_from_time_controller");
    public static Identifier USE_TELEPORTER_ID = new Identifier(EnergizedPowerMod.MODID, "use_teleporter");
    public static Identifier SET_CHECKBOX_ID = new Identifier(EnergizedPowerMod.MODID, "set_checkbox");
    public static Identifier SET_ADVANCED_AUTO_CRAFTER_RECIPE_INDEX_ID = new Identifier(EnergizedPowerMod.MODID, "set_advanced_auto_crafter_recipe_index");
    public static Identifier CYCLE_AUTO_CRAFTER_RECIPE_OUTPUT_ID = new Identifier(EnergizedPowerMod.MODID, "cycle_auto_crafter_recipe_output");
    public static Identifier CYCLE_ADVANCED_AUTO_CRAFTER_RECIPE_OUTPUT_ID = new Identifier(EnergizedPowerMod.MODID, "cycle_advanced_auto_crafter_recipe_output");
    public static Identifier CRAFT_PRESS_MOLD_MAKER_RECIPE_ID = new Identifier(EnergizedPowerMod.MODID, "craft_press_mold_maker_recipe");
    public static Identifier CHANGE_STONE_SOLIDIFIER_RECIPE_INDEX_ID = new Identifier(EnergizedPowerMod.MODID, "change_stone_solidifer_recipe_index");
    public static Identifier CHANGE_REDSTONE_MODE_ID = new Identifier(EnergizedPowerMod.MODID, "change_redstone_mode");
    public static Identifier SET_FLUID_TANK_FILTER_ID = new Identifier(EnergizedPowerMod.MODID, "set_fluid_tank_filter");
    public static Identifier CHANGE_FILTRATION_PLANT_RECIPE_INDEX_ID = new Identifier(EnergizedPowerMod.MODID, "change_filtration_plant_recipe_index");
    public static Identifier CHANGE_COMPARATOR_MODE_ID = new Identifier(EnergizedPowerMod.MODID, "change_comparator_mode");

    private ModMessages() {}

    public static void registerPacketsS2C() {
        ClientPlayNetworking.registerGlobalReceiver(ENERGY_SYNC_ID, EnergySyncS2CPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(FLUID_SYNC_ID, FluidSyncS2CPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(ITEM_STACK_SYNC_ID, ItemStackSyncS2CPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(OPEN_ENERGIZED_POWER_BOOK_ID, OpenEnergizedPowerBookS2CPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(SYNC_PRESS_MOLD_MAKER_RECIPE_LIST_ID, SyncPressMoldMakerRecipeListS2CPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(SYNC_STONE_SOLIDIFIER_CURRENT_RECIPE_ID, SyncStoneSolidifierCurrentRecipeS2CPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(SYNC_FILTRATION_PLANT_CURRENT_RECIPE_ID, SyncFiltrationPlantCurrentRecipeS2CPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(SYNC_FURNACE_RECIPE_TYPE, SyncFurnaceRecipeTypeS2CPacket::receive);
    }

    public static void registerPacketsC2S() {
        ServerPlayNetworking.registerGlobalReceiver(POP_ENERGIZED_POWER_BOOK_FROM_LECTERN_ID, PopEnergizedPowerBookFromLecternC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_AUTO_CRAFTER_PATTERN_INPUT_SLOTS_ID, SetAutoCrafterPatternInputSlotsC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_ADVANCED_AUTO_CRAFTER_PATTERN_INPUT_SLOTS_ID, SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_WEATHER_FROM_WEATHER_CONTROLLER_ID, SetWeatherFromWeatherControllerC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_TIME_FROM_TIME_CONTROLLER_ID, SetTimeFromTimeControllerC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(USE_TELEPORTER_ID, UseTeleporterC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_CHECKBOX_ID, SetCheckboxC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_ADVANCED_AUTO_CRAFTER_RECIPE_INDEX_ID, SetAdvancedAutoCrafterRecipeIndexC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(CYCLE_AUTO_CRAFTER_RECIPE_OUTPUT_ID, CycleAutoCrafterRecipeOutputC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(CYCLE_ADVANCED_AUTO_CRAFTER_RECIPE_OUTPUT_ID, CycleAdvancedAutoCrafterRecipeOutputC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(CRAFT_PRESS_MOLD_MAKER_RECIPE_ID, CraftPressMoldMakerRecipeC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(CHANGE_STONE_SOLIDIFIER_RECIPE_INDEX_ID, ChangeStoneSolidifierRecipeIndexC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(CHANGE_REDSTONE_MODE_ID, ChangeRedstoneModeC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(SET_FLUID_TANK_FILTER_ID, SetFluidTankFilterC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(CHANGE_FILTRATION_PLANT_RECIPE_INDEX_ID, ChangeFiltrationPlantRecipeIndexC2SPacket::receive);

        ServerPlayNetworking.registerGlobalReceiver(CHANGE_COMPARATOR_MODE_ID, ChangeComparatorModeC2SPacket::receive);
    }

    public static void broadcastServerPacket(MinecraftServer server, Identifier channelName, PacketByteBuf buf) {
        for(ServerPlayerEntity player:PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, channelName, buf);
        }
    }

    public static void sendServerPacketToPlayersWithinXBlocks(BlockPos pos, ServerWorld dimension, double distance, Identifier channelName, PacketByteBuf buf) {
        for(ServerPlayerEntity player:PlayerLookup.around(dimension, pos, distance)) {
            ServerPlayNetworking.send(player, channelName, buf);
        }
    }

    public static void sendServerPacketToPlayer(ServerPlayerEntity player, Identifier channelName, PacketByteBuf buf) {
        ServerPlayNetworking.send(player, channelName, buf);
    }
}
