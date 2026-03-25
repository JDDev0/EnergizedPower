package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.SetCurrentRecipeIdPacketUpdate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record SetCurrentRecipeIdC2SPacket(BlockPos pos, Identifier recipeId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetCurrentRecipeIdC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("set_current_recipe_id"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetCurrentRecipeIdC2SPacket> PACKET_CODEC =
            StreamCodec.ofMember(SetCurrentRecipeIdC2SPacket::write, SetCurrentRecipeIdC2SPacket::new);

    public SetCurrentRecipeIdC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean()?buffer.readIdentifier():null);
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
         if(recipeId == null) {
             buffer.writeBoolean(false);
         }else {
             buffer.writeBoolean(true);
             buffer.writeIdentifier(recipeId);
         }
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(SetCurrentRecipeIdC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof SetCurrentRecipeIdPacketUpdate setCurrentRecipeIdPacketUpdate))
                return;

            setCurrentRecipeIdPacketUpdate.setRecipeId(data.recipeId);
        });
    }
}
