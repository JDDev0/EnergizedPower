package me.jddev0.ep.networking;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.packet.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public final class ModMessages {
    private ModMessages() {}

    public static void register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(EnergizedPowerMod.MODID).
                versioned("1.0");

        //Server -> Client
        registrar.play(EnergySyncS2CPacket.ID, EnergySyncS2CPacket::new, handler -> handler.
                client(EnergySyncS2CPacket::handle));

        registrar.play(FluidSyncS2CPacket.ID, FluidSyncS2CPacket::new, handler -> handler.
                client(FluidSyncS2CPacket::handle));

        registrar.play(ItemStackSyncS2CPacket.ID, ItemStackSyncS2CPacket::new, handler -> handler.
                client(ItemStackSyncS2CPacket::handle));

        registrar.play(OpenEnergizedPowerBookS2CPacket.ID, OpenEnergizedPowerBookS2CPacket::new, handler -> handler.
                client(OpenEnergizedPowerBookS2CPacket::handle));

        registrar.play(SyncPressMoldMakerRecipeListS2CPacket.ID, SyncPressMoldMakerRecipeListS2CPacket::new, handler -> handler.
                client(SyncPressMoldMakerRecipeListS2CPacket::handle));

        registrar.play(SyncStoneSolidifierCurrentRecipeS2CPacket.ID, SyncStoneSolidifierCurrentRecipeS2CPacket::new, handler -> handler.
                client(SyncStoneSolidifierCurrentRecipeS2CPacket::handle));

        //Client -> Server
        registrar.play(PopEnergizedPowerBookFromLecternC2SPacket.ID, PopEnergizedPowerBookFromLecternC2SPacket::new, handler -> handler.
                server(PopEnergizedPowerBookFromLecternC2SPacket::handle));

        registrar.play(SetAutoCrafterPatternInputSlotsC2SPacket.ID, SetAutoCrafterPatternInputSlotsC2SPacket::new, handler -> handler.
                server(SetAutoCrafterPatternInputSlotsC2SPacket::handle));

        registrar.play(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket.ID, SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::new, handler -> handler.
                server(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::handle));

        registrar.play(SetWeatherFromWeatherControllerC2SPacket.ID, SetWeatherFromWeatherControllerC2SPacket::new, handler -> handler.
                server(SetWeatherFromWeatherControllerC2SPacket::handle));

        registrar.play(SetTimeFromTimeControllerC2SPacket.ID, SetTimeFromTimeControllerC2SPacket::new, handler -> handler.
                server(SetTimeFromTimeControllerC2SPacket::handle));

        registrar.play(UseTeleporterC2SPacket.ID, UseTeleporterC2SPacket::new, handler -> handler.
                server(UseTeleporterC2SPacket::handle));

        registrar.play(SetAutoCrafterCheckboxC2SPacket.ID, SetAutoCrafterCheckboxC2SPacket::new, handler -> handler.
                server(SetAutoCrafterCheckboxC2SPacket::handle));

        registrar.play(SetAdvancedAutoCrafterRecipeIndexC2SPacket.ID, SetAdvancedAutoCrafterRecipeIndexC2SPacket::new, handler -> handler.
                server(SetAdvancedAutoCrafterRecipeIndexC2SPacket::handle));

        registrar.play(SetAdvancedAutoCrafterCheckboxC2SPacket.ID, SetAdvancedAutoCrafterCheckboxC2SPacket::new, handler -> handler.
                server(SetAdvancedAutoCrafterCheckboxC2SPacket::handle));

        registrar.play(SetBlockPlacerCheckboxC2SPacket.ID, SetBlockPlacerCheckboxC2SPacket::new, handler -> handler.
                server(SetBlockPlacerCheckboxC2SPacket::handle));

        registrar.play(SetItemConveyorBeltSorterCheckboxC2SPacket.ID, SetItemConveyorBeltSorterCheckboxC2SPacket::new, handler -> handler.
                server(SetItemConveyorBeltSorterCheckboxC2SPacket::handle));

        registrar.play(CycleAutoCrafterRecipeOutputC2SPacket.ID, CycleAutoCrafterRecipeOutputC2SPacket::new, handler -> handler.
                server(CycleAutoCrafterRecipeOutputC2SPacket::handle));

        registrar.play(CycleAdvancedAutoCrafterRecipeOutputC2SPacket.ID, CycleAdvancedAutoCrafterRecipeOutputC2SPacket::new, handler -> handler.
                server(CycleAdvancedAutoCrafterRecipeOutputC2SPacket::handle));

        registrar.play(CraftPressMoldMakerRecipeC2SPacket.ID, CraftPressMoldMakerRecipeC2SPacket::new, handler -> handler.
                server(CraftPressMoldMakerRecipeC2SPacket::handle));

        registrar.play(ChangeStoneSolidifierRecipeIndexC2SPacket.ID, ChangeStoneSolidifierRecipeIndexC2SPacket::new, handler -> handler.
                server(ChangeStoneSolidifierRecipeIndexC2SPacket::handle));

        registrar.play(ChangeRedstoneModeC2SPacket.ID, ChangeRedstoneModeC2SPacket::new, handler -> handler.
                server(ChangeRedstoneModeC2SPacket::handle));

        registrar.play(SetFluidTankCheckboxC2SPacket.ID, SetFluidTankCheckboxC2SPacket::new, handler -> handler.
                server(SetFluidTankCheckboxC2SPacket::handle));

        registrar.play(SetFluidTankFilterC2SPacket.ID, SetFluidTankFilterC2SPacket::new, handler -> handler.
                server(SetFluidTankFilterC2SPacket::handle));
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.SERVER.noArg().send(message);
    }

    public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(message);
    }

    public static void sendToPlayerNear(CustomPacketPayload message, PacketDistributor.TargetPoint targetPoint) {
        PacketDistributor.NEAR.with(targetPoint).send(message);
    }

    public static void sendToPlayersWithinXBlocks(CustomPacketPayload message, BlockPos pos, ResourceKey<Level> dimension, int distance) {
        PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), distance, dimension)).send(message);
    }

    public static void sendToAllPlayers(CustomPacketPayload message) {
        PacketDistributor.ALL.noArg().send(message);
    }
}
