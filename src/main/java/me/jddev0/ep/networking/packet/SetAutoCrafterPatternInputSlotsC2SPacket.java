package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.screen.AutoCrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.List;

public class SetAutoCrafterPatternInputSlotsC2SPacket {
    private final BlockPos pos;
    private final List<ItemStack> itemStacks;
    private final ResourceLocation recipeId;

    public SetAutoCrafterPatternInputSlotsC2SPacket(BlockPos pos, List<ItemStack> itemStacks, ResourceLocation recipeId) {
        this.pos = pos;

        this.itemStacks = new ArrayList<>(itemStacks);

        while(this.itemStacks.size() < 9)
            this.itemStacks.add(ItemStack.EMPTY);

        this.recipeId = recipeId;
    }

    public SetAutoCrafterPatternInputSlotsC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        itemStacks = new ArrayList<>(9);
        for(int i = 0;i < 9;i++)
            itemStacks.add(buffer.readItem());

        recipeId = buffer.readResourceLocation();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        for(ItemStack itemStack:itemStacks)
            buffer.writeItemStack(itemStack, false);

        buffer.writeResourceLocation(recipeId);
    }

    public boolean handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            AbstractContainerMenu menu = context.getSender().containerMenu;

            if(!(menu instanceof AutoCrafterMenu autoCrafterMenu))
                return;

            for(int i = 0;i < itemStacks.size();i++)
                autoCrafterMenu.getPatternSlots().setItem(i, itemStacks.get(i));

            autoCrafterBlockEntity.setRecipeIdForSetRecipe(recipeId);

            autoCrafterBlockEntity.resetProgressAndMarkAsChanged();
        });

        return true;
    }
}
