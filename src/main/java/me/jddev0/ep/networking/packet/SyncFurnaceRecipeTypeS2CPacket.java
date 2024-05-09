package me.jddev0.ep.networking.packet;

import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

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
            RecipeType<?> recipeType = ForgeRegistries.RECIPE_TYPES.getValue(buffer.readResourceLocation());

            this.recipeType = recipeType == null?RecipeType.SMELTING:
                    (RecipeType<? extends AbstractCookingRecipe>)recipeType;
        }else {
            this.recipeType = RecipeType.SMELTING;
        }

        this.pos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        ResourceLocation recipeTypeKey = ForgeRegistries.RECIPE_TYPES.getKey(recipeType);
        if(recipeTypeKey == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);
            buffer.writeResourceLocation(recipeTypeKey);
        }
        buffer.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            if(blockEntity instanceof FurnaceRecipeTypePacketUpdate recipeTypePacketUpdate)
                recipeTypePacketUpdate.setRecipeType(recipeType);
        });

        return true;
    }
}
