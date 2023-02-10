package me.jddev0.ep.networking.packet;

import me.jddev0.ep.energy.EnergyStorageMenuPacketUpdate;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public final class EnergySyncS2CPacket {
    private EnergySyncS2CPacket() {}

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if(client.world == null)
            return;

        long energy = buf.readLong();
        long capacity = buf.readLong();
        BlockEntity blockEntity = client.world.getBlockEntity(buf.readBlockPos());

        if(blockEntity instanceof EnergyStoragePacketUpdate energyStorage) {
            if(client.player.currentScreenHandler instanceof EnergyStorageMenuPacketUpdate energyStorageMenu) {
                if(energyStorageMenu.getBlockEntity() != blockEntity)
                    return;

                energyStorageMenu.setEnergy(energy);
                energyStorageMenu.setCapacity(capacity);
            }else {
                energyStorage.setEnergy(energy);
                energyStorage.setCapacity(capacity);
            }
        }
    }
}
