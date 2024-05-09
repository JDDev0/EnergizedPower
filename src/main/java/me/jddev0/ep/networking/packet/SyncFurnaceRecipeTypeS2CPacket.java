package me.jddev0.ep.networking.packet;

import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

public class SyncFurnaceRecipeTypeS2CPacket {
    private SyncFurnaceRecipeTypeS2CPacket() {}

    @SuppressWarnings("unchecked")
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        RecipeType<? extends AbstractCookingRecipe> recipeType;
        if(buf.readBoolean())
            recipeType = (RecipeType<? extends AbstractCookingRecipe>)Registries.RECIPE_TYPE.
                    getOrEmpty(buf.readIdentifier()).orElse(RecipeType.SMELTING);
        else
            recipeType = RecipeType.SMELTING;
        BlockPos pos = buf.readBlockPos();

        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(pos);
            if(blockEntity instanceof FurnaceRecipeTypePacketUpdate recipeTypePacketUpdate)
                recipeTypePacketUpdate.setRecipeType(recipeType);
        });
    }
}
