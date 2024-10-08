package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record EnergySyncS2CPacket(long energy, long capacity, BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<EnergySyncS2CPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("energy_sync"));
    public static final PacketCodec<RegistryByteBuf, EnergySyncS2CPacket> PACKET_CODEC =
            PacketCodec.of(EnergySyncS2CPacket::write, EnergySyncS2CPacket::new);

    public EnergySyncS2CPacket(RegistryByteBuf buffer) {
        this(buffer.readLong(), buffer.readLong(), buffer.readBlockPos());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeLong(energy);
        buffer.writeLong(capacity);
        buffer.writeBlockPos(pos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(EnergySyncS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().world == null)
                return;

            BlockEntity blockEntity = context.client().world.getBlockEntity(data.pos);
            if(blockEntity instanceof EnergyStoragePacketUpdate energyStorage) {
                energyStorage.setCapacity(data.capacity);
                energyStorage.setEnergy(data.energy);
            }
        });
    }
}
