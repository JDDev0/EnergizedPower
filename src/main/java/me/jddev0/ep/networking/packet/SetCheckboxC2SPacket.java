package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.CheckboxUpdate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record SetCheckboxC2SPacket(BlockPos pos, int checkboxId, boolean checked) implements CustomPayload {
    public static final CustomPayload.Id<SetCheckboxC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("set_checkbox"));
    public static final PacketCodec<RegistryByteBuf, SetCheckboxC2SPacket> PACKET_CODEC =
            PacketCodec.of(SetCheckboxC2SPacket::write, SetCheckboxC2SPacket::new);

    public SetCheckboxC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readBoolean());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(checkboxId);
        buffer.writeBoolean(checked);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetCheckboxC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof CheckboxUpdate checkboxUpdate))
                return;

            checkboxUpdate.setCheckbox(data.checkboxId, data.checked);
        });
    }
}
