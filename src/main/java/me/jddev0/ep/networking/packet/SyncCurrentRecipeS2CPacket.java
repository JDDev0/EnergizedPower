package me.jddev0.ep.networking.packet;

import me.jddev0.ep.recipe.CurrentRecipePacketUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.jetbrains.annotations.Nullable;

public final class SyncCurrentRecipeS2CPacket<R extends Recipe<?>> {
    private final BlockPos pos;
    private final RecipeSerializer<R> recipeSerializer;
    private final RecipeHolder<R> currentRecipe;

    public SyncCurrentRecipeS2CPacket(BlockPos pos, RecipeSerializer<R> recipeSerializerId,
                                      @Nullable RecipeHolder<R> currentRecipe) {
        this.pos = pos;
        this.recipeSerializer = recipeSerializerId;
        this.currentRecipe = currentRecipe;
    }

    @SuppressWarnings("unchecked")
    public SyncCurrentRecipeS2CPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        ResourceLocation recipeSerializerId = buffer.readResourceLocation();
        recipeSerializer = (RecipeSerializer<R>)BuiltInRegistries.RECIPE_SERIALIZER.get(recipeSerializerId);
        if(recipeSerializer == null)
            throw new IllegalArgumentException("Recipe Serializer \"" + recipeSerializerId + "\" does not exist!");

        currentRecipe = buffer.readBoolean()?new RecipeHolder<>(buffer.readResourceLocation(),
                recipeSerializer.fromNetwork(buffer)):null;
    }

     public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        ResourceLocation recipeSerializerId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipeSerializer);
        if(recipeSerializerId == null)
            throw new IllegalArgumentException("The recipe serializer \"" + recipeSerializer.getClass().getCanonicalName() +
                    "\" is not registered!");

        buffer.writeResourceLocation(recipeSerializerId);
        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeResourceLocation(currentRecipe.id());
            recipeSerializer.toNetwork(buffer, currentRecipe.value());
        }
    }

    @SuppressWarnings("unchecked")
    public boolean handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            //BlockEntity
            if(blockEntity instanceof CurrentRecipePacketUpdate) {
                CurrentRecipePacketUpdate<R> currentRecipePacketUpdate = (CurrentRecipePacketUpdate<R>)blockEntity;
                currentRecipePacketUpdate.setCurrentRecipe(currentRecipe);
            }
        });

        return true;
    }
}
