package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;

public record EnergySyncS2CPacket(long energy, long capacity, BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<EnergySyncS2CPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("energy_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnergySyncS2CPacket> PACKET_CODEC =
            StreamCodec.ofMember(EnergySyncS2CPacket::write, EnergySyncS2CPacket::new);

    public EnergySyncS2CPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readLong(), buffer.readLong(), buffer.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeLong(energy);
        buffer.writeLong(capacity);
        buffer.writeBlockPos(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(EnergySyncS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().level == null)
                return;

            BlockEntity blockEntity = context.client().level.getBlockEntity(data.pos);
            if(blockEntity instanceof EnergyStoragePacketUpdate energyStorage) {
                energyStorage.setCapacity(data.capacity);
                energyStorage.setEnergy(data.energy);
            }
        });
    }
}
