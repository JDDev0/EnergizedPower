package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.StoneSolidifierBlockEntity;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class SyncStoneSolidifierCurrentRecipeS2CPacket implements CustomPayload {
    public static final CustomPayload.Id<SyncStoneSolidifierCurrentRecipeS2CPacket> ID =
            new CustomPayload.Id<>(new Identifier(EnergizedPowerMod.MODID, "sync_stone_solidifier_current_recipe"));
    public static final PacketCodec<RegistryByteBuf, SyncStoneSolidifierCurrentRecipeS2CPacket> PACKET_CODEC =
            PacketCodec.of(SyncStoneSolidifierCurrentRecipeS2CPacket::write, SyncStoneSolidifierCurrentRecipeS2CPacket::new);

    private final BlockPos pos;
    private final RecipeEntry<StoneSolidifierRecipe> currentRecipe;

    public SyncStoneSolidifierCurrentRecipeS2CPacket(BlockPos pos, @Nullable RecipeEntry<StoneSolidifierRecipe> currentRecipe) {
        this.pos = pos;
        this.currentRecipe = currentRecipe;
    }

    public SyncStoneSolidifierCurrentRecipeS2CPacket(RegistryByteBuf buffer) {
        pos = buffer.readBlockPos();

        currentRecipe = buffer.readBoolean()?new RecipeEntry<>(buffer.readIdentifier(),
                StoneSolidifierRecipe.Serializer.INSTANCE.packetCodec().decode(buffer)):null;
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);

        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeIdentifier(currentRecipe.id());
            StoneSolidifierRecipe.Serializer.INSTANCE.packetCodec().encode(buffer, currentRecipe.value());
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SyncStoneSolidifierCurrentRecipeS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().world == null)
                return;

            BlockEntity blockEntity = context.client().world.getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof StoneSolidifierBlockEntity stoneSolidifierBlockEntity) {
                stoneSolidifierBlockEntity.setCurrentRecipe(data.currentRecipe);
            }
        });
    }
}
