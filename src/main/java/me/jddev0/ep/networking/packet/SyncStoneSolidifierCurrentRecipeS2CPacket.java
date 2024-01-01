package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.StoneSolidifierBlockEntity;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SyncStoneSolidifierCurrentRecipeS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "sync_stone_solidifier_current_recipe");

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

    @Override
    public void write(final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeResourceLocation(currentRecipe.id());
            StoneSolidifierRecipe.Serializer.INSTANCE.toNetwork(buffer, currentRecipe.value());
        }
    }

    @Override
    @NotNull
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(final SyncStoneSolidifierCurrentRecipeS2CPacket data, final PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            if(context.level().isEmpty())
                return;

            BlockEntity blockEntity = context.level().get().getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof StoneSolidifierBlockEntity stoneSolidifierBlockEntity) {
                stoneSolidifierBlockEntity.setCurrentRecipe(data.currentRecipe);
            }
        });
    }
}
