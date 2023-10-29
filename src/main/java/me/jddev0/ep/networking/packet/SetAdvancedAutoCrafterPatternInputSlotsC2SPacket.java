package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.AdvancedAutoCrafterBlockEntity;
import me.jddev0.ep.screen.AdvancedAutoCrafterMenu;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SetAdvancedAutoCrafterPatternInputSlotsC2SPacket {
    private SetAdvancedAutoCrafterPatternInputSlotsC2SPacket() {}

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        List<ItemStack> itemStacks = new ArrayList<>(9);
        for(int i = 0;i < 9;i++)
            itemStacks.add(buf.readItemStack());
        Identifier recipeId = buf.readIdentifier();

        server.execute(() -> {
            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof AdvancedAutoCrafterBlockEntity advancedAutoCrafterBlockEntity))
                return;

            ScreenHandler menu = player.currentScreenHandler;

            if(!(menu instanceof AdvancedAutoCrafterMenu advancedAutoCrafterMenu))
                return;

            for(int i = 0;i < itemStacks.size();i++)
                advancedAutoCrafterMenu.getPatternSlots()[advancedAutoCrafterMenu.getRecipeIndex()].setStack(i, itemStacks.get(i));

            advancedAutoCrafterBlockEntity.setRecipeIdForSetRecipe(recipeId);

            advancedAutoCrafterBlockEntity.resetProgressAndMarkAsChanged(advancedAutoCrafterMenu.getRecipeIndex());
        });
    }
}
