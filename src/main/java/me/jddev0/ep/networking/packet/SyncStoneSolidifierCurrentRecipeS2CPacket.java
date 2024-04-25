package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.StoneSolidifierBlockEntity;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SyncStoneSolidifierCurrentRecipeS2CPacket implements CustomPacketPayload {
    public static final Type<SyncStoneSolidifierCurrentRecipeS2CPacket> ID =
            new Type<>(new ResourceLocation(EnergizedPowerMod.MODID, "sync_stone_solidifier_current_recipe"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncStoneSolidifierCurrentRecipeS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(SyncStoneSolidifierCurrentRecipeS2CPacket::write, SyncStoneSolidifierCurrentRecipeS2CPacket::new);

    private final BlockPos pos;
    private final RecipeHolder<StoneSolidifierRecipe> currentRecipe;

    public SyncStoneSolidifierCurrentRecipeS2CPacket(BlockPos pos, @Nullable RecipeHolder<StoneSolidifierRecipe> currentRecipe) {
        this.pos = pos;
        this.currentRecipe = currentRecipe;
    }

    public SyncStoneSolidifierCurrentRecipeS2CPacket(RegistryFriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        currentRecipe = buffer.readBoolean()?new RecipeHolder<>(buffer.readResourceLocation(),
                StoneSolidifierRecipe.Serializer.INSTANCE.streamCodec().decode(buffer)):null;
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeResourceLocation(currentRecipe.id());
            StoneSolidifierRecipe.Serializer.INSTANCE.streamCodec().encode(buffer, currentRecipe.value());
        }
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SyncStoneSolidifierCurrentRecipeS2CPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof StoneSolidifierBlockEntity stoneSolidifierBlockEntity) {
                stoneSolidifierBlockEntity.setCurrentRecipe(data.currentRecipe);
            }
        });
    }
}
