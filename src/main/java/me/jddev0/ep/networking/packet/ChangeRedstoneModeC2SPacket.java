package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record ChangeRedstoneModeC2SPacket(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<ChangeRedstoneModeC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("change_redstone_mode"));
    public static final PacketCodec<RegistryByteBuf, ChangeRedstoneModeC2SPacket> PACKET_CODEC =
            PacketCodec.of(ChangeRedstoneModeC2SPacket::write, ChangeRedstoneModeC2SPacket::new);

    public ChangeRedstoneModeC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(ChangeRedstoneModeC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof RedstoneModeUpdate redstoneModeUpdate))
                return;

            redstoneModeUpdate.setNextRedstoneMode();
        });
    }
}
