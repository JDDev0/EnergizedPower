package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.StoneSolidifierBlockEntity;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;

public class SyncStoneSolidifierCurrentRecipeS2CPacket {
    private SyncStoneSolidifierCurrentRecipeS2CPacket() {}

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        RecipeEntry<StoneSolidifierRecipe> currentRecipe = buf.readBoolean()?new RecipeEntry<>(buf.readIdentifier(),
                StoneSolidifierRecipe.Serializer.INSTANCE.read(buf)):null;

        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(pos);

            //BlockEntity
            if(blockEntity instanceof StoneSolidifierBlockEntity stoneSolidifierBlockEntity) {
                stoneSolidifierBlockEntity.setCurrentRecipe(currentRecipe);
            }
        });
    }
}
