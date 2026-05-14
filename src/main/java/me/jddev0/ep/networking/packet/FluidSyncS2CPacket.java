package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;

public record FluidSyncS2CPacket(int tank, FluidStack fluidStack, long capacity, BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FluidSyncS2CPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("fluid_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidSyncS2CPacket> PACKET_CODEC =
            StreamCodec.ofMember(FluidSyncS2CPacket::write, FluidSyncS2CPacket::new);

    public FluidSyncS2CPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt(), FluidStack.PACKET_CODEC.decode(buffer), buffer.readLong(), buffer.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(tank);
        FluidStack.PACKET_CODEC.encode(buffer, fluidStack);
        buffer.writeLong(capacity);
        buffer.writeBlockPos(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(FluidSyncS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().level == null)
                return;

            BlockEntity blockEntity = context.client().level.getBlockEntity(data.pos);
            if(blockEntity instanceof FluidStoragePacketUpdate) {
                FluidStoragePacketUpdate fluidStorage = (FluidStoragePacketUpdate)blockEntity;
                fluidStorage.setTankCapacity(data.tank, data.capacity);
                fluidStorage.setFluid(data.tank, data.fluidStack);
            }
        });
    }
}
