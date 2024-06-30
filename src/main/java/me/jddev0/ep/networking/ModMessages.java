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

        net.messageBuilder(SyncPressMoldMakerRecipeListS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).
                decoder(SyncPressMoldMakerRecipeListS2CPacket::new).
                encoder(SyncPressMoldMakerRecipeListS2CPacket::toBytes).
                consumerMainThread(SyncPressMoldMakerRecipeListS2CPacket::handle).
                add();

        net.messageBuilder(SyncCurrentRecipeS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).
                decoder(SyncCurrentRecipeS2CPacket::new).
                encoder(SyncCurrentRecipeS2CPacket::toBytes).
                consumerMainThread(SyncCurrentRecipeS2CPacket::handle).
                add();

        net.messageBuilder(SyncFurnaceRecipeTypeS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).
                decoder(SyncFurnaceRecipeTypeS2CPacket::new).
                encoder(SyncFurnaceRecipeTypeS2CPacket::toBytes).
                consumerMainThread(SyncFurnaceRecipeTypeS2CPacket::handle).
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

        net.messageBuilder(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::new).
                encoder(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::toBytes).
                consumerMainThread(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::handle).
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

        net.messageBuilder(UseTeleporterC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(UseTeleporterC2SPacket::new).
                encoder(UseTeleporterC2SPacket::toBytes).
                consumerMainThread(UseTeleporterC2SPacket::handle).
                add();

        net.messageBuilder(SetCheckboxC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetCheckboxC2SPacket::new).
                encoder(SetCheckboxC2SPacket::toBytes).
                consumerMainThread(SetCheckboxC2SPacket::handle).
                add();

        net.messageBuilder(SetAdvancedAutoCrafterRecipeIndexC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetAdvancedAutoCrafterRecipeIndexC2SPacket::new).
                encoder(SetAdvancedAutoCrafterRecipeIndexC2SPacket::toBytes).
                consumerMainThread(SetAdvancedAutoCrafterRecipeIndexC2SPacket::handle).
                add();

        net.messageBuilder(CycleAutoCrafterRecipeOutputC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(CycleAutoCrafterRecipeOutputC2SPacket::new).
                encoder(CycleAutoCrafterRecipeOutputC2SPacket::toBytes).
                consumerMainThread(CycleAutoCrafterRecipeOutputC2SPacket::handle).
                add();

        net.messageBuilder(CycleAdvancedAutoCrafterRecipeOutputC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(CycleAdvancedAutoCrafterRecipeOutputC2SPacket::new).
                encoder(CycleAdvancedAutoCrafterRecipeOutputC2SPacket::toBytes).
                consumerMainThread(CycleAdvancedAutoCrafterRecipeOutputC2SPacket::handle).
                add();

        net.messageBuilder(CraftPressMoldMakerRecipeC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(CraftPressMoldMakerRecipeC2SPacket::new).
                encoder(CraftPressMoldMakerRecipeC2SPacket::toBytes).
                consumerMainThread(CraftPressMoldMakerRecipeC2SPacket::handle).
                add();

        net.messageBuilder(ChangeCurrentRecipeIndexC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(ChangeCurrentRecipeIndexC2SPacket::new).
                encoder(ChangeCurrentRecipeIndexC2SPacket::toBytes).
                consumerMainThread(ChangeCurrentRecipeIndexC2SPacket::handle).
                add();

        net.messageBuilder(ChangeRedstoneModeC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(ChangeRedstoneModeC2SPacket::new).
                encoder(ChangeRedstoneModeC2SPacket::toBytes).
                consumerMainThread(ChangeRedstoneModeC2SPacket::handle).
                add();

        net.messageBuilder(SetFluidTankFilterC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetFluidTankFilterC2SPacket::new).
                encoder(SetFluidTankFilterC2SPacket::toBytes).
                consumerMainThread(SetFluidTankFilterC2SPacket::handle).
                add();

        net.messageBuilder(ChangeComparatorModeC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(ChangeComparatorModeC2SPacket::new).
                encoder(ChangeComparatorModeC2SPacket::toBytes).
                consumerMainThread(ChangeComparatorModeC2SPacket::handle).
                add();

        net.messageBuilder(SetCurrentRecipeIdC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetCurrentRecipeIdC2SPacket::new).
                encoder(SetCurrentRecipeIdC2SPacket::toBytes).
                consumerMainThread(SetCurrentRecipeIdC2SPacket::handle).
                add();

        net.messageBuilder(SetCreativeFluidTankFluidStackC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).
                decoder(SetCreativeFluidTankFluidStackC2SPacket::new).
                encoder(SetCreativeFluidTankFluidStackC2SPacket::toBytes).
                consumerMainThread(SetCreativeFluidTankFluidStackC2SPacket::handle).
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
