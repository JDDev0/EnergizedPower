package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.StoneSolidifierBlockEntity;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SyncStoneSolidifierCurrentRecipeS2CPacket {
    private final BlockPos pos;
    private final RecipeHolder<StoneSolidifierRecipe> currentRecipe;

    public SyncStoneSolidifierCurrentRecipeS2CPacket(BlockPos pos, @Nullable RecipeHolder<StoneSolidifierRecipe> currentRecipe) {
        this.pos = pos;
        this.currentRecipe = currentRecipe;
    }

    public SyncStoneSolidifierCurrentRecipeS2CPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        currentRecipe = buffer.readBoolean()?new RecipeHolder<>(buffer.readResourceLocation(),
                StoneSolidifierRecipe.Serializer.INSTANCE.fromNetwork(buffer)):null;
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeResourceLocation(currentRecipe.id());
            StoneSolidifierRecipe.Serializer.INSTANCE.toNetwork(buffer, currentRecipe.value());
        }
    }

    public boolean handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            //BlockEntity
            if(blockEntity instanceof StoneSolidifierBlockEntity stoneSolidifierBlockEntity) {
                stoneSolidifierBlockEntity.setCurrentRecipe(currentRecipe);
            }
        });

        return true;
    }
}
