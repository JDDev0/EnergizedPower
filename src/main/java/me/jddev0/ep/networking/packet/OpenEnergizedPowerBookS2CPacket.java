package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record OpenEnergizedPowerBookS2CPacket(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<OpenEnergizedPowerBookS2CPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("open_energized_power_book"));
    public static final PacketCodec<RegistryByteBuf, OpenEnergizedPowerBookS2CPacket> PACKET_CODEC =
            PacketCodec.of(OpenEnergizedPowerBookS2CPacket::write, OpenEnergizedPowerBookS2CPacket::new);

    public OpenEnergizedPowerBookS2CPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(OpenEnergizedPowerBookS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().world == null)
                return;

            BlockEntity blockEntity = context.client().world.getBlockEntity(data.pos);

            if(blockEntity instanceof LecternBlockEntity lecternBlockEntity) {
                showBookViewScreen(lecternBlockEntity);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    private static void showBookViewScreen(LecternBlockEntity lecternBlockEntity) {
        MinecraftClient.getInstance().setScreen(new EnergizedPowerBookScreen(lecternBlockEntity));
    }
}
