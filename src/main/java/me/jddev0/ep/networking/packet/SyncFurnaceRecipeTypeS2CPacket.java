package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public final class SyncFurnaceRecipeTypeS2CPacket implements IEnergizedPowerPacket {
    public static final Identifier ID = EPAPI.id("sync_furnace_recipe_type");

    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final BlockPos pos;

    public SyncFurnaceRecipeTypeS2CPacket(RecipeType<? extends AbstractCookingRecipe> recipeType, BlockPos pos) {
        this.recipeType = recipeType;
        this.pos = pos;
    }

    @SuppressWarnings("unchecked")
    public SyncFurnaceRecipeTypeS2CPacket(PacketByteBuf buffer) {
        if(buffer.readBoolean()) {
            this.recipeType = (RecipeType<? extends AbstractCookingRecipe>)Registry.RECIPE_TYPE.
                    getOrEmpty(buffer.readIdentifier()).orElse(RecipeType.SMELTING);
        }else {
            this.recipeType = RecipeType.SMELTING;
        }

        this.pos = buffer.readBlockPos();
    }

    public void write(PacketByteBuf buffer) {
        Identifier recipeTypeKey = Registry.RECIPE_TYPE.getId(recipeType);
        if(recipeTypeKey == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);
            buffer.writeIdentifier(recipeTypeKey);
        }
        buffer.writeBlockPos(pos);
    }

    @Override
    @NotNull
    public Identifier getId() {
        return ID;
    }

    public static void receive(SyncFurnaceRecipeTypeS2CPacket data, MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketSender responseSender) {
        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(data.pos);
            if(blockEntity instanceof FurnaceRecipeTypePacketUpdate recipeTypePacketUpdate)
                recipeTypePacketUpdate.setRecipeType(data.recipeType);
        });
    }
}
