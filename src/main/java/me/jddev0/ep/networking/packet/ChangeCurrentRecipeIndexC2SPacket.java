package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.ChangeCurrentRecipeIndexPacketUpdate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record ChangeCurrentRecipeIndexC2SPacket(BlockPos pos, boolean downUp) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeCurrentRecipeIndexC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("change_current_recipe_index"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeCurrentRecipeIndexC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(ChangeCurrentRecipeIndexC2SPacket::write, ChangeCurrentRecipeIndexC2SPacket::new);

    public ChangeCurrentRecipeIndexC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(downUp);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(ChangeCurrentRecipeIndexC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof ChangeCurrentRecipeIndexPacketUpdate changeCurrentRecipeIndexPacketUpdate))
                return;

            changeCurrentRecipeIndexPacketUpdate.changeRecipeIndex(data.downUp);
        });
    }
}
