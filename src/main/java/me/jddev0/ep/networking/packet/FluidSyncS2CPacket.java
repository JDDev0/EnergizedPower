package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record FluidSyncS2CPacket(int tank, FluidStack fluidStack, int capacity, BlockPos pos) implements CustomPacketPayload {
    public static final Type<FluidSyncS2CPacket> ID =
            new Type<>(EPAPI.id("fluid_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidSyncS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(FluidSyncS2CPacket::write, FluidSyncS2CPacket::new);

    public FluidSyncS2CPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt(), FluidStack.OPTIONAL_STREAM_CODEC.decode(buffer), buffer.readInt(), buffer.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(tank);
        FluidStack.OPTIONAL_STREAM_CODEC.encode(buffer, fluidStack);
        buffer.writeInt(capacity);
        buffer.writeBlockPos(pos);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(FluidSyncS2CPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof FluidStoragePacketUpdate) {
                FluidStoragePacketUpdate fluidStorage = (FluidStoragePacketUpdate)blockEntity;
                fluidStorage.setTankCapacity(data.tank, data.capacity);
                fluidStorage.setFluid(data.tank, data.fluidStack);
            }
        });
    }
}
