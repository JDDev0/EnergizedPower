package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.CreativeFluidTankBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public record SetCreativeFluidTankFluidStackC2SPacket(BlockPos pos, FluidStack fluidStack) implements CustomPayload {
    public static final CustomPayload.Id<SetCreativeFluidTankFluidStackC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("set_creative_fluid_tank_fluid_stack"));
    public static final PacketCodec<RegistryByteBuf, SetCreativeFluidTankFluidStackC2SPacket> PACKET_CODEC =
            PacketCodec.of(SetCreativeFluidTankFluidStackC2SPacket::write, SetCreativeFluidTankFluidStackC2SPacket::new);

    public SetCreativeFluidTankFluidStackC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos(), FluidStack.PACKET_CODEC.decode(buffer));
    }

     public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
        FluidStack.PACKET_CODEC.encode(buffer, fluidStack);
    }

    @Override
    @NotNull
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetCreativeFluidTankFluidStackC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof CreativeFluidTankBlockEntity creativeFluidTankBlockEntity))
                return;

            creativeFluidTankBlockEntity.setFluidStack(data.fluidStack);
        });
    }
}
