package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AdvancedAutoCrafterBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record SetAdvancedAutoCrafterRecipeIndexC2SPacket(BlockPos pos, int recipeIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetAdvancedAutoCrafterRecipeIndexC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("set_advanced_auto_crafter_recipe_index"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetAdvancedAutoCrafterRecipeIndexC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetAdvancedAutoCrafterRecipeIndexC2SPacket::write, SetAdvancedAutoCrafterRecipeIndexC2SPacket::new);

    public SetAdvancedAutoCrafterRecipeIndexC2SPacket(RegistryFriendlyByteBuf buffer) {
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

    public static void receive(SetAdvancedAutoCrafterRecipeIndexC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AdvancedAutoCrafterBlockEntity advancedAutoCrafterBlockEntity))
                return;

            advancedAutoCrafterBlockEntity.setCurrentRecipeIndex(data.recipeIndex);
        });
    }
}
