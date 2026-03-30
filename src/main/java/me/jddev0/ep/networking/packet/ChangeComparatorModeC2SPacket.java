package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record ChangeComparatorModeC2SPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeComparatorModeC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("change_comparator_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeComparatorModeC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(ChangeComparatorModeC2SPacket::write, ChangeComparatorModeC2SPacket::new);

    public ChangeComparatorModeC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(ChangeComparatorModeC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof ComparatorModeUpdate comparatorModeUpdate))
                return;

            comparatorModeUpdate.setNextComparatorMode();
        });
    }
}
