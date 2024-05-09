package me.jddev0.ep.networking.packet;

import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;

public final class SyncFurnaceRecipeTypeS2CPacket {
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final BlockPos pos;

    public SyncFurnaceRecipeTypeS2CPacket(RecipeType<? extends AbstractCookingRecipe> recipeType, BlockPos pos) {
        this.recipeType = recipeType;
        this.pos = pos;
    }

    @SuppressWarnings("unchecked")
    public SyncFurnaceRecipeTypeS2CPacket(FriendlyByteBuf buffer) {
        if(buffer.readBoolean()) {
            this.recipeType = (RecipeType<? extends AbstractCookingRecipe>)BuiltInRegistries.RECIPE_TYPE.
                    getOptional(buffer.readResourceLocation()).orElse(RecipeType.SMELTING);
        }else {
            this.recipeType = RecipeType.SMELTING;
        }

        this.pos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        ResourceLocation recipeTypeKey = BuiltInRegistries.RECIPE_TYPE.getKey(recipeType);
        if(recipeTypeKey == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);
            buffer.writeResourceLocation(recipeTypeKey);
        }
        buffer.writeBlockPos(pos);
    }

    public boolean handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            if(blockEntity instanceof FurnaceRecipeTypePacketUpdate recipeTypePacketUpdate)
                recipeTypePacketUpdate.setRecipeType(recipeType);
        });

        return true;
    }
}
