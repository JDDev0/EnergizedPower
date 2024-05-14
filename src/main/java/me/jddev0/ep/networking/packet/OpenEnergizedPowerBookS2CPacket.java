package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OpenEnergizedPowerBookS2CPacket(BlockPos pos) implements IEnergizedPowerPacket {
    public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "open_energized_power_book");

    public OpenEnergizedPowerBookS2CPacket(PacketByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static void receive(OpenEnergizedPowerBookS2CPacket data, MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketSender responseSender) {
        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(data.pos);

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
