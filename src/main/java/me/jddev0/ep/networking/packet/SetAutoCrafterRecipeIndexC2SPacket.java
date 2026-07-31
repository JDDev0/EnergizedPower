package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AbstractAutoCrafterBlockEntity;
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

public record SetAutoCrafterRecipeIndexC2SPacket(BlockPos pos, int recipeIndex) implements CustomPacketPayload {
    public static final Type<SetAutoCrafterRecipeIndexC2SPacket> ID =
            new Type<>(EPAPI.id("set_auto_crafter_recipe_index"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetAutoCrafterRecipeIndexC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetAutoCrafterRecipeIndexC2SPacket::write, SetAutoCrafterRecipeIndexC2SPacket::new);

    public SetAutoCrafterRecipeIndexC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(recipeIndex);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SetAutoCrafterRecipeIndexC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AbstractAutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            autoCrafterBlockEntity.setCurrentRecipeIndex(data.recipeIndex);
        });
    }
}
