package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.CheckboxUpdate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record SetCheckboxC2SPacket(BlockPos pos, int checkboxId, boolean checked) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetCheckboxC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("set_checkbox"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetCheckboxC2SPacket> PACKET_CODEC =
            StreamCodec.ofMember(SetCheckboxC2SPacket::write, SetCheckboxC2SPacket::new);

    public SetCheckboxC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readBoolean());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(checkboxId);
        buffer.writeBoolean(checked);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(SetCheckboxC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof CheckboxUpdate checkboxUpdate))
                return;

            checkboxUpdate.setCheckbox(data.checkboxId, data.checked);
        });
    }
}
