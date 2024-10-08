package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.SetCurrentRecipeIdPacketUpdate;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public record SetCurrentRecipeIdC2SPacket(BlockPos pos, Identifier recipeId) implements IEnergizedPowerPacket {
    public static final Identifier ID = EPAPI.id("set_current_recipe_id");

    public SetCurrentRecipeIdC2SPacket(PacketByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean()?buffer.readIdentifier():null);
    }

     public void write(PacketByteBuf buffer) {
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
    public Identifier getId() {
        return ID;
    }

    public static void receive(SetCurrentRecipeIdC2SPacket data, MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketSender responseSender) {
        server.execute(() -> {
            if(!player.canModifyBlocks())
                return;

            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof SetCurrentRecipeIdPacketUpdate setCurrentRecipeIdPacketUpdate))
                return;

            setCurrentRecipeIdPacketUpdate.setRecipeId(data.recipeId);
        });
    }
}
