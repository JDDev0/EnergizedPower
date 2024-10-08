package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
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

public final class SetAdvancedAutoCrafterPatternInputSlotsC2SPacket implements IEnergizedPowerPacket {
    public static final Identifier ID = EPAPI.id("set_advanced_auto_crafter_pattern_input_slots");

    private final BlockPos pos;
    private final List<ItemStack> itemStacks;
    private final Identifier recipeId;

    public SetAdvancedAutoCrafterPatternInputSlotsC2SPacket(BlockPos pos, List<ItemStack> itemStacks, Identifier recipeId) {
        this.pos = pos;

        this.itemStacks = new ArrayList<>(itemStacks);

        while(this.itemStacks.size() < 9)
            this.itemStacks.add(ItemStack.EMPTY);

        this.recipeId = recipeId;
    }

    public SetAdvancedAutoCrafterPatternInputSlotsC2SPacket(PacketByteBuf buffer) {
        pos = buffer.readBlockPos();

        itemStacks = new ArrayList<>(9);
        for(int i = 0;i < 9;i++)
            itemStacks.add(buffer.readItemStack());

        recipeId = buffer.readIdentifier();
    }

    public void write(final PacketByteBuf buffer) {
        buffer.writeBlockPos(pos);

        for(ItemStack itemStack:itemStacks)
            buffer.writeItemStack(itemStack);

        buffer.writeIdentifier(recipeId);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static void receive(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket data, MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketSender responseSender) {
        player.server.execute(() -> {
            if(!player.canModifyBlocks())
                return;

            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AdvancedAutoCrafterBlockEntity advancedAutoCrafterBlockEntity))
                return;

            ScreenHandler menu = player.currentScreenHandler;

            if(!(menu instanceof AdvancedAutoCrafterMenu advancedAutoCrafterMenu))
                return;

            for(int i = 0;i < data.itemStacks.size();i++)
                advancedAutoCrafterMenu.getPatternSlots()[advancedAutoCrafterMenu.getRecipeIndex()].setStack(i, data.itemStacks.get(i));

            advancedAutoCrafterBlockEntity.setRecipeIdForSetRecipe(data.recipeId);

            advancedAutoCrafterBlockEntity.resetProgressAndMarkAsChanged(advancedAutoCrafterMenu.getRecipeIndex());
        });
    }
}
