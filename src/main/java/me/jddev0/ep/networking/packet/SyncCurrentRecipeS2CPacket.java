package me.jddev0.ep.networking.packet;

import me.jddev0.ep.recipe.CurrentRecipePacketUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class SyncCurrentRecipeS2CPacket<R extends Recipe<?>> {
    private final BlockPos pos;
    private final RecipeSerializer<R> recipeSerializer;
    private final R currentRecipe;

    public SyncCurrentRecipeS2CPacket(BlockPos pos, RecipeSerializer<R> recipeSerializerId,
                                      @Nullable R currentRecipe) {
        this.pos = pos;
        this.recipeSerializer = recipeSerializerId;
        this.currentRecipe = currentRecipe;
    }

    @SuppressWarnings("unchecked")
    public SyncCurrentRecipeS2CPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        ResourceLocation recipeSerializerId = buffer.readResourceLocation();
        recipeSerializer = (RecipeSerializer<R>)ForgeRegistries.RECIPE_SERIALIZERS.getValue(recipeSerializerId);
        if(recipeSerializer == null)
            throw new IllegalArgumentException("Recipe Serializer \"" + recipeSerializerId + "\" does not exist!");

        currentRecipe = buffer.readBoolean()?
                recipeSerializer.fromNetwork(buffer.readResourceLocation(), buffer):null;
    }

     public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        ResourceLocation recipeSerializerId = ForgeRegistries.RECIPE_SERIALIZERS.getKey(recipeSerializer);
        if(recipeSerializerId == null)
            throw new IllegalArgumentException("The recipe serializer \"" + recipeSerializer.getClass().getCanonicalName() +
                    "\" is not registered!");

        buffer.writeResourceLocation(recipeSerializerId);
        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeResourceLocation(currentRecipe.getId());
            recipeSerializer.toNetwork(buffer, currentRecipe);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
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
