package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record ChangeComparatorModeC2SPacket(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<ChangeComparatorModeC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("change_comparator_mode"));
    public static final PacketCodec<RegistryByteBuf, ChangeComparatorModeC2SPacket> PACKET_CODEC =
            PacketCodec.of(ChangeComparatorModeC2SPacket::write, ChangeComparatorModeC2SPacket::new);

    public ChangeComparatorModeC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(ChangeComparatorModeC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof ComparatorModeUpdate comparatorModeUpdate))
                return;

            comparatorModeUpdate.setNextComparatorMode();
        });
    }
}
