package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SyncFiltrationPlantCurrentRecipeS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "sync_filtration_plant_current_recipe");

    private final BlockPos pos;
    private final RecipeHolder<FiltrationPlantRecipe> currentRecipe;

    public SyncFiltrationPlantCurrentRecipeS2CPacket(BlockPos pos, @Nullable RecipeHolder<FiltrationPlantRecipe> currentRecipe) {
        this.pos = pos;
        this.currentRecipe = currentRecipe;
    }

    public SyncFiltrationPlantCurrentRecipeS2CPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        currentRecipe = buffer.readBoolean()?new RecipeHolder<>(buffer.readResourceLocation(),
                FiltrationPlantRecipe.Serializer.INSTANCE.fromNetwork(buffer)):null;
    }

    @Override
    public void write(final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeResourceLocation(currentRecipe.id());
            FiltrationPlantRecipe.Serializer.INSTANCE.toNetwork(buffer, currentRecipe.value());
        }
    }

    @Override
    @NotNull
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(final SyncFiltrationPlantCurrentRecipeS2CPacket data, final PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            if(context.level().isEmpty())
                return;

            BlockEntity blockEntity = context.level().get().getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof FiltrationPlantBlockEntity filtrationPlantBlockEntity) {
                filtrationPlantBlockEntity.setCurrentRecipe(data.currentRecipe);
            }
        });
    }
}
