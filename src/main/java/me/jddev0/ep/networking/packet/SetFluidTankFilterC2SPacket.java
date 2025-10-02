package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record SetFluidTankFilterC2SPacket(BlockPos pos, FluidStack fluidFilter) implements CustomPayload {
    public static final CustomPayload.Id<SetFluidTankFilterC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("set_fluid_tank_filter"));
    public static final PacketCodec<RegistryByteBuf, SetFluidTankFilterC2SPacket> PACKET_CODEC =
            PacketCodec.of(SetFluidTankFilterC2SPacket::write, SetFluidTankFilterC2SPacket::new);

    public SetFluidTankFilterC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos(), FluidStack.PACKET_CODEC.decode(buffer));
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
        FluidStack.PACKET_CODEC.encode(buffer, fluidFilter);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetFluidTankFilterC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity))
                return;

            fluidTankBlockEntity.setFluidFilter(data.fluidFilter, context.player().getRegistryManager());
        });
    }
}
