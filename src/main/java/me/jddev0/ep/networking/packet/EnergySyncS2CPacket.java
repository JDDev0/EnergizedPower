package me.jddev0.ep.networking.packet;

import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public final class EnergySyncS2CPacket {
    private EnergySyncS2CPacket() {}

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        long energy = buf.readLong();
        long capacity = buf.readLong();
        BlockPos pos = buf.readBlockPos();

        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(pos);
            if(blockEntity instanceof EnergyStoragePacketUpdate energyStorage) {
                energyStorage.setEnergy(energy);
                energyStorage.setCapacity(capacity);
            }
        });
    }
}
