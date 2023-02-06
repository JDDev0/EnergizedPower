package me.jddev0.ep.networking.packet;

import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public final class OpenEnergizedPowerBookS2CPacket {
    private OpenEnergizedPowerBookS2CPacket() {}

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockEntity blockEntity = client.world.getBlockEntity(buf.readBlockPos());

        if(blockEntity instanceof LecternBlockEntity lecternBlockEntity) {
            showBookViewScreen(client, lecternBlockEntity);
        }
    }

    @Environment(EnvType.CLIENT)
    private static void showBookViewScreen(MinecraftClient client, LecternBlockEntity lecternBlockEntity) {
        client.execute(() -> client.setScreen(new EnergizedPowerBookScreen(lecternBlockEntity)));
    }
}
