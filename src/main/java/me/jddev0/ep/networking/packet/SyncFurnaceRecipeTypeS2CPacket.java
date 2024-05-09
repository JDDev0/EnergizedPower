package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public final class SyncFurnaceRecipeTypeS2CPacket implements CustomPacketPayload {
    public static final Type<SyncFurnaceRecipeTypeS2CPacket> ID =
            new Type<>(new ResourceLocation(EnergizedPowerMod.MODID, "sync_furnace_recipe_type"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncFurnaceRecipeTypeS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(SyncFurnaceRecipeTypeS2CPacket::write, SyncFurnaceRecipeTypeS2CPacket::new);

    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final BlockPos pos;

    public SyncFurnaceRecipeTypeS2CPacket(RecipeType<? extends AbstractCookingRecipe> recipeType, BlockPos pos) {
        this.recipeType = recipeType;
        this.pos = pos;
    }

    @SuppressWarnings("unchecked")
    public SyncFurnaceRecipeTypeS2CPacket(RegistryFriendlyByteBuf buffer) {
        if(buffer.readBoolean()) {
            this.recipeType = (RecipeType<? extends AbstractCookingRecipe>)BuiltInRegistries.RECIPE_TYPE.
                    getOptional(buffer.readResourceLocation()).orElse(RecipeType.SMELTING);
        }else {
            this.recipeType = RecipeType.SMELTING;
        }

        this.pos = buffer.readBlockPos();
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        ResourceLocation recipeTypeKey = BuiltInRegistries.RECIPE_TYPE.getKey(recipeType);
        if(recipeTypeKey == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);
            buffer.writeResourceLocation(recipeTypeKey);
        }
        buffer.writeBlockPos(pos);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SyncFurnaceRecipeTypeS2CPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(data.pos);

            if(blockEntity instanceof FurnaceRecipeTypePacketUpdate recipeTypePacketUpdate)
                recipeTypePacketUpdate.setRecipeType(data.recipeType);
        });
    }
}
