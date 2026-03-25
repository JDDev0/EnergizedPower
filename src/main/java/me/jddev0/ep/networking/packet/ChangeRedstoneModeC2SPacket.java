package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record ChangeRedstoneModeC2SPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeRedstoneModeC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("change_redstone_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeRedstoneModeC2SPacket> PACKET_CODEC =
            StreamCodec.ofMember(ChangeRedstoneModeC2SPacket::write, ChangeRedstoneModeC2SPacket::new);

    public ChangeRedstoneModeC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(ChangeRedstoneModeC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof RedstoneModeUpdate redstoneModeUpdate))
                return;

            redstoneModeUpdate.setNextRedstoneMode();
        });
    }
}
