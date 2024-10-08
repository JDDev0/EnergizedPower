package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public final class SyncFurnaceRecipeTypeS2CPacket implements CustomPayload {
    public static final Id<SyncFurnaceRecipeTypeS2CPacket> ID =
            new Id<>(EPAPI.id("sync_furnace_recipe_type"));
    public static final PacketCodec<RegistryByteBuf, SyncFurnaceRecipeTypeS2CPacket> PACKET_CODEC =
            PacketCodec.of(SyncFurnaceRecipeTypeS2CPacket::write, SyncFurnaceRecipeTypeS2CPacket::new);

    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final BlockPos pos;

    public SyncFurnaceRecipeTypeS2CPacket(RecipeType<? extends AbstractCookingRecipe> recipeType, BlockPos pos) {
        this.recipeType = recipeType;
        this.pos = pos;
    }

    @SuppressWarnings("unchecked")
    public SyncFurnaceRecipeTypeS2CPacket(RegistryByteBuf buffer) {
        if(buffer.readBoolean()) {
            this.recipeType = (RecipeType<? extends AbstractCookingRecipe>)Registries.RECIPE_TYPE.
                    getOrEmpty(buffer.readIdentifier()).orElse(RecipeType.SMELTING);
        }else {
            this.recipeType = RecipeType.SMELTING;
        }

        this.pos = buffer.readBlockPos();
    }

    public void write(RegistryByteBuf buffer) {
        Identifier recipeTypeKey = Registries.RECIPE_TYPE.getId(recipeType);
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
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SyncFurnaceRecipeTypeS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().world == null)
                return;

            BlockEntity blockEntity = context.client().world.getBlockEntity(data.pos);
            if(blockEntity instanceof FurnaceRecipeTypePacketUpdate recipeTypePacketUpdate)
                recipeTypePacketUpdate.setRecipeType(data.recipeType);
        });
    }
}
