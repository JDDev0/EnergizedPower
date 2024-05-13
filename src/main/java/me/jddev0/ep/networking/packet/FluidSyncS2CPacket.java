package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record FluidSyncS2CPacket(int tank, FluidStack fluidStack, long capacity, BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<FluidSyncS2CPacket> ID =
            new CustomPayload.Id<>(new Identifier(EnergizedPowerMod.MODID, "fluid_sync"));
    public static final PacketCodec<RegistryByteBuf, FluidSyncS2CPacket> PACKET_CODEC =
            PacketCodec.of(FluidSyncS2CPacket::write, FluidSyncS2CPacket::new);

    public FluidSyncS2CPacket(RegistryByteBuf buffer) {
        this(buffer.readInt(), FluidStack.PACKET_CODEC.decode(buffer), buffer.readLong(), buffer.readBlockPos());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeInt(tank);
        FluidStack.PACKET_CODEC.encode(buffer, fluidStack);
        buffer.writeLong(capacity);
        buffer.writeBlockPos(pos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(FluidSyncS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().world == null)
                return;

            BlockEntity blockEntity = context.client().world.getBlockEntity(data.pos);
            if(blockEntity instanceof FluidStoragePacketUpdate) {
                FluidStoragePacketUpdate fluidStorage = (FluidStoragePacketUpdate)blockEntity;
                fluidStorage.setTankCapacity(data.tank, data.capacity);
                fluidStorage.setFluid(data.tank, data.fluidStack);
            }
        });
    }
}
