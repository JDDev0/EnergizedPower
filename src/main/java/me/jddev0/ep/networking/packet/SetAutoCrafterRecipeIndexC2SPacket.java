package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AbstractAutoCrafterBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

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
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(SetAutoCrafterRecipeIndexC2SPacket data, ServerPlayNetworking.Context context) {
        context.player().server.execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AbstractAutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            autoCrafterBlockEntity.setCurrentRecipeIndex(data.recipeIndex);
        });
    }
}
