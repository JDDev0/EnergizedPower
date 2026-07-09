package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.XPStorageBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record InsertExtractXPFromXPStorageC2SPacket(BlockPos pos, int levels) implements CustomPacketPayload {
    public static final Type<InsertExtractXPFromXPStorageC2SPacket> ID =
            new Type<>(EPAPI.id("insert_extract_xp_from_xp_storage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InsertExtractXPFromXPStorageC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(InsertExtractXPFromXPStorageC2SPacket::write, InsertExtractXPFromXPStorageC2SPacket::new);


    public InsertExtractXPFromXPStorageC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(levels);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(InsertExtractXPFromXPStorageC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            ServerLevel level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof XPStorageBlockEntity xpStorageBlockEntity))
                return;

            xpStorageBlockEntity.onInsertExtractXP(data.levels, context.player());
        });
    }
}
