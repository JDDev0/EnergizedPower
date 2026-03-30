package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public record OpenEnergizedPowerBookS2CPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenEnergizedPowerBookS2CPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("open_energized_power_book"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenEnergizedPowerBookS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(OpenEnergizedPowerBookS2CPacket::write, OpenEnergizedPowerBookS2CPacket::new);

    public OpenEnergizedPowerBookS2CPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(OpenEnergizedPowerBookS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().level == null)
                return;

            BlockEntity blockEntity = context.client().level.getBlockEntity(data.pos);

            if(blockEntity instanceof LecternBlockEntity lecternBlockEntity) {
                showBookViewScreen(lecternBlockEntity);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    private static void showBookViewScreen(LecternBlockEntity lecternBlockEntity) {
        Minecraft.getInstance().setScreen(new EnergizedPowerBookScreen(lecternBlockEntity));
    }
}
