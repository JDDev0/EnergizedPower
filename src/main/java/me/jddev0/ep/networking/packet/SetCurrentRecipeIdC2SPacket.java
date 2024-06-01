package me.jddev0.ep.networking.packet;

import me.jddev0.ep.recipe.SetCurrentRecipeIdPacketUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetCurrentRecipeIdC2SPacket(BlockPos pos, ResourceLocation recipeId) {
    public SetCurrentRecipeIdC2SPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean()?buffer.readResourceLocation():null);
    }

     public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
         if(recipeId == null) {
             buffer.writeBoolean(false);
         }else {
             buffer.writeBoolean(true);
             buffer.writeResourceLocation(recipeId);
         }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = context.getSender().level;
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof SetCurrentRecipeIdPacketUpdate setCurrentRecipeIdPacketUpdate))
                return;

            setCurrentRecipeIdPacketUpdate.setRecipeId(recipeId);
        });

        return true;
    }
}
