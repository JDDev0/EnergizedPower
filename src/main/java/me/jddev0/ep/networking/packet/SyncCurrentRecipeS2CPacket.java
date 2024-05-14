package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.recipe.CurrentRecipePacketUpdate;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class SyncCurrentRecipeS2CPacket<R extends Recipe<?>> implements IEnergizedPowerPacket {
    public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "sync_current_recipe");

    private final BlockPos pos;
    private final RecipeSerializer<R> recipeSerializer;
    private final RecipeEntry<R> currentRecipe;

    public SyncCurrentRecipeS2CPacket(BlockPos pos, RecipeSerializer<R> recipeSerializerId,
                                      @Nullable RecipeEntry<R> currentRecipe) {
        this.pos = pos;
        this.recipeSerializer = recipeSerializerId;
        this.currentRecipe = currentRecipe;
    }

    @SuppressWarnings("unchecked")
    public SyncCurrentRecipeS2CPacket(PacketByteBuf buffer) {
        pos = buffer.readBlockPos();

        Identifier recipeSerializerId = buffer.readIdentifier();
        recipeSerializer = (RecipeSerializer<R>)Registries.RECIPE_SERIALIZER.get(recipeSerializerId);
        if(recipeSerializer == null)
            throw new IllegalArgumentException("Recipe Serializer \"" + recipeSerializerId + "\" does not exist!");

        currentRecipe = buffer.readBoolean()?new RecipeEntry<>(buffer.readIdentifier(),
                recipeSerializer.read(buffer)):null;
    }

     public void write(PacketByteBuf buffer) {
        buffer.writeBlockPos(pos);

        Identifier recipeSerializerId = Registries.RECIPE_SERIALIZER.getId(recipeSerializer);
        if(recipeSerializerId == null)
            throw new IllegalArgumentException("The recipe serializer \"" + recipeSerializer.getClass().getCanonicalName() +
                    "\" is not registered!");

        buffer.writeIdentifier(recipeSerializerId);
        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeIdentifier(currentRecipe.id());
            recipeSerializer.write(buffer, currentRecipe.value());
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @SuppressWarnings("unchecked")
    public static <R extends Recipe<?>> void receive(SyncCurrentRecipeS2CPacket<R> data, MinecraftClient client, ClientPlayNetworkHandler handler,
                                                     PacketSender responseSender) {
        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof CurrentRecipePacketUpdate) {
                CurrentRecipePacketUpdate<R> currentRecipePacketUpdate = (CurrentRecipePacketUpdate<R>)blockEntity;
                currentRecipePacketUpdate.setCurrentRecipe(data.currentRecipe);
            }
        });
    }
}
