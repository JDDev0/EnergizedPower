package me.jddev0.ep.networking.packet;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.entity.PressMoldMakerBlockEntity;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SyncPressMoldMakerRecipeListS2CPacket {
    private SyncPressMoldMakerRecipeListS2CPacket() {}

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        int size = buf.readInt();
        List<Pair<PressMoldMakerRecipe, Boolean>> recipeList = IntStream.range(0, size).mapToObj(i -> Pair.of(
                PressMoldMakerRecipe.Serializer.INSTANCE.read(buf.readIdentifier(), buf), buf.readBoolean())).
                collect(Collectors.toList());

        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(pos);

            //BlockEntity
            if(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity) {
                pressMoldMakerBlockEntity.setRecipeList(recipeList);
            }
        });
    }
}
