package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.CheckboxUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetCheckboxC2SPacket(BlockPos pos, int checkboxId, boolean checked) implements CustomPacketPayload {
    public static final Type<SetCheckboxC2SPacket> ID =
            new Type<>(EPAPI.id("set_checkbox"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetCheckboxC2SPacket> STREAM_CODEC =
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
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SetCheckboxC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof CheckboxUpdate checkboxUpdate))
                return;

            checkboxUpdate.setCheckbox(data.checkboxId, data.checked);
        });
    }
}
