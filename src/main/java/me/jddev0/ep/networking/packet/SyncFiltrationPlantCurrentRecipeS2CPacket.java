package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.jetbrains.annotations.Nullable;

public class SyncFiltrationPlantCurrentRecipeS2CPacket {
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

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeResourceLocation(currentRecipe.id());
            FiltrationPlantRecipe.Serializer.INSTANCE.toNetwork(buffer, currentRecipe.value());
        }
    }

    public boolean handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            //BlockEntity
            if(blockEntity instanceof FiltrationPlantBlockEntity filtrationPlantBlockEntity) {
                filtrationPlantBlockEntity.setCurrentRecipe(currentRecipe);
            }
        });

        return true;
    }
}
