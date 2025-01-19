package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.CurrentRecipePacketUpdate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class SyncCurrentRecipeS2CPacket<R extends Recipe<?>> implements CustomPayload {
    public static final Id<SyncCurrentRecipeS2CPacket<?>> ID =
            new Id<>(EPAPI.id("sync_current_recipe"));
    public static final PacketCodec<RegistryByteBuf, SyncCurrentRecipeS2CPacket<?>> PACKET_CODEC =
            PacketCodec.of(SyncCurrentRecipeS2CPacket::write, SyncCurrentRecipeS2CPacket::new);

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
    public SyncCurrentRecipeS2CPacket(RegistryByteBuf buffer) {
        pos = buffer.readBlockPos();

        Identifier recipeSerializerId = buffer.readIdentifier();
        recipeSerializer = (RecipeSerializer<R>)Registries.RECIPE_SERIALIZER.get(recipeSerializerId);
        if(recipeSerializer == null)
            throw new IllegalArgumentException("Recipe Serializer \"" + recipeSerializerId + "\" does not exist!");

        currentRecipe = buffer.readBoolean()?new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, buffer.readIdentifier()),
                recipeSerializer.packetCodec().decode(buffer)):null;
    }

     public void write(RegistryByteBuf buffer) {
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

            buffer.writeIdentifier(currentRecipe.id().getValue());
            recipeSerializer.packetCodec().encode(buffer, currentRecipe.value());
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @SuppressWarnings("unchecked")
    public static <R extends Recipe<?>> void receive(SyncCurrentRecipeS2CPacket<R> data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().world == null)
                return;

            BlockEntity blockEntity = context.client().world.getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof CurrentRecipePacketUpdate) {
                CurrentRecipePacketUpdate<R> currentRecipePacketUpdate = (CurrentRecipePacketUpdate<R>)blockEntity;
                currentRecipePacketUpdate.setCurrentRecipe(data.currentRecipe);
            }
        });
    }
}
