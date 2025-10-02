package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.SetCurrentRecipeIdPacketUpdate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public record SetCurrentRecipeIdC2SPacket(BlockPos pos, Identifier recipeId) implements CustomPayload {
    public static final CustomPayload.Id<SetCurrentRecipeIdC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("set_current_recipe_id"));
    public static final PacketCodec<RegistryByteBuf, SetCurrentRecipeIdC2SPacket> PACKET_CODEC =
            PacketCodec.of(SetCurrentRecipeIdC2SPacket::write, SetCurrentRecipeIdC2SPacket::new);

    public SetCurrentRecipeIdC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean()?buffer.readIdentifier():null);
    }

     public void write(RegistryByteBuf buffer) {
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
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetCurrentRecipeIdC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof SetCurrentRecipeIdPacketUpdate setCurrentRecipeIdPacketUpdate))
                return;

            setCurrentRecipeIdPacketUpdate.setRecipeId(data.recipeId);
        });
    }
}
