package me.jddev0.ep.networking;

import me.jddev0.ep.networking.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

public final class ModMessages {
    private ModMessages() {}

    public static void registerPacketsS2C() {
        registerClientPacketReceiver(EnergySyncS2CPacket.ID,
                EnergySyncS2CPacket::new, EnergySyncS2CPacket::receive);

        registerClientPacketReceiver(FluidSyncS2CPacket.ID,
                FluidSyncS2CPacket::new, FluidSyncS2CPacket::receive);

        registerClientPacketReceiver(ItemStackSyncS2CPacket.ID,
                ItemStackSyncS2CPacket::new, ItemStackSyncS2CPacket::receive);

        registerClientPacketReceiver(OpenEnergizedPowerBookS2CPacket.ID,
                OpenEnergizedPowerBookS2CPacket::new, OpenEnergizedPowerBookS2CPacket::receive);

        registerClientPacketReceiver(SyncPressMoldMakerRecipeListS2CPacket.ID,
                SyncPressMoldMakerRecipeListS2CPacket::new, SyncPressMoldMakerRecipeListS2CPacket::receive);

        registerClientPacketReceiver(SyncCurrentRecipeS2CPacket.ID,
                SyncCurrentRecipeS2CPacket::new, SyncCurrentRecipeS2CPacket::receive);

        registerClientPacketReceiver(SyncFurnaceRecipeTypeS2CPacket.ID,
                SyncFurnaceRecipeTypeS2CPacket::new, SyncFurnaceRecipeTypeS2CPacket::receive);
    }

    public static void registerPacketsC2S() {
        registerServerPacketReceiver(PopEnergizedPowerBookFromLecternC2SPacket.ID,
                PopEnergizedPowerBookFromLecternC2SPacket::new, PopEnergizedPowerBookFromLecternC2SPacket::receive);

        registerServerPacketReceiver(SetAutoCrafterPatternInputSlotsC2SPacket.ID,
                SetAutoCrafterPatternInputSlotsC2SPacket::new, SetAutoCrafterPatternInputSlotsC2SPacket::receive);

        registerServerPacketReceiver(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket.ID,
                SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::new, SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::receive);

        registerServerPacketReceiver(SetWeatherFromWeatherControllerC2SPacket.ID,
                SetWeatherFromWeatherControllerC2SPacket::new, SetWeatherFromWeatherControllerC2SPacket::receive);

        registerServerPacketReceiver(SetTimeFromTimeControllerC2SPacket.ID,
                SetTimeFromTimeControllerC2SPacket::new, SetTimeFromTimeControllerC2SPacket::receive);

        registerServerPacketReceiver(UseTeleporterC2SPacket.ID,
                UseTeleporterC2SPacket::new, UseTeleporterC2SPacket::receive);

        registerServerPacketReceiver(SetCheckboxC2SPacket.ID,
                SetCheckboxC2SPacket::new, SetCheckboxC2SPacket::receive);

        registerServerPacketReceiver(SetAdvancedAutoCrafterRecipeIndexC2SPacket.ID,
                SetAdvancedAutoCrafterRecipeIndexC2SPacket::new, SetAdvancedAutoCrafterRecipeIndexC2SPacket::receive);

        registerServerPacketReceiver(CycleAutoCrafterRecipeOutputC2SPacket.ID,
                CycleAutoCrafterRecipeOutputC2SPacket::new, CycleAutoCrafterRecipeOutputC2SPacket::receive);

        registerServerPacketReceiver(CycleAdvancedAutoCrafterRecipeOutputC2SPacket.ID,
                CycleAdvancedAutoCrafterRecipeOutputC2SPacket::new, CycleAdvancedAutoCrafterRecipeOutputC2SPacket::receive);

        registerServerPacketReceiver(CraftPressMoldMakerRecipeC2SPacket.ID,
                CraftPressMoldMakerRecipeC2SPacket::new, CraftPressMoldMakerRecipeC2SPacket::receive);

        registerServerPacketReceiver(ChangeCurrentRecipeIndexC2SPacket.ID,
                ChangeCurrentRecipeIndexC2SPacket::new, ChangeCurrentRecipeIndexC2SPacket::receive);

        registerServerPacketReceiver(ChangeRedstoneModeC2SPacket.ID,
                ChangeRedstoneModeC2SPacket::new, ChangeRedstoneModeC2SPacket::receive);

        registerServerPacketReceiver(SetFluidTankFilterC2SPacket.ID,
                SetFluidTankFilterC2SPacket::new, SetFluidTankFilterC2SPacket::receive);

        registerServerPacketReceiver(ChangeComparatorModeC2SPacket.ID,
                ChangeComparatorModeC2SPacket::new, ChangeComparatorModeC2SPacket::receive);

        registerServerPacketReceiver(SetCurrentRecipeIdC2SPacket.ID,
                SetCurrentRecipeIdC2SPacket::new, SetCurrentRecipeIdC2SPacket::receive);
    }

    public static <P extends IEnergizedPowerPacket> void registerClientPacketReceiver(
            Identifier packetId, Function<PacketByteBuf, P> readFunction,
            EnergizedPowerClientPacketHandler<P> packetHandler) {
        ClientPlayNetworking.registerGlobalReceiver(packetId, (client, handler, buf, responseSender) ->
                packetHandler.receive(readFunction.apply(buf), client, handler, responseSender));
    }

    public static <P extends IEnergizedPowerPacket> void registerServerPacketReceiver(
            Identifier packetId, Function<PacketByteBuf, P> readFunction,
            EnergizedPowerServerPacketHandler<P> packetHandler) {
        ServerPlayNetworking.registerGlobalReceiver(packetId, (server, player, handler, buf, responseSender) ->
                packetHandler.receive(readFunction.apply(buf), server, player, handler, responseSender));
    }

    public static void sendClientPacketToServer(IEnergizedPowerPacket packet) {
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);

        ClientPlayNetworking.send(packet.getId(), buf);
    }

    public static void broadcastServerPacket(MinecraftServer server, IEnergizedPowerPacket packet) {
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);

        for(ServerPlayerEntity player:PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, packet.getId(), buf);
        }
    }

    public static void sendServerPacketToPlayersWithinXBlocks(BlockPos pos, ServerWorld dimension, double distance, IEnergizedPowerPacket packet) {
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);

        for(ServerPlayerEntity player:PlayerLookup.around(dimension, pos, distance)) {
            ServerPlayNetworking.send(player, packet.getId(), buf);
        }
    }

    public static void sendServerPacketToPlayer(ServerPlayerEntity player, IEnergizedPowerPacket packet) {
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);

        ServerPlayNetworking.send(player, packet.getId(), buf);
    }
}
