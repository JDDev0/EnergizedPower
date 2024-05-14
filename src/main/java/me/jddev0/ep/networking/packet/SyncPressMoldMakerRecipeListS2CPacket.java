package me.jddev0.ep.networking.packet;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.PressMoldMakerBlockEntity;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SyncPressMoldMakerRecipeListS2CPacket implements IEnergizedPowerPacket {
    public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "sync_press_mold_maker_recipe_list");

    private final BlockPos pos;
    private final List<Pair<PressMoldMakerRecipe, Boolean>> recipeList;

    public SyncPressMoldMakerRecipeListS2CPacket(BlockPos pos, List<Pair<PressMoldMakerRecipe, Boolean>> recipeList) {
        this.pos = pos;
        this.recipeList = recipeList;
    }

    public SyncPressMoldMakerRecipeListS2CPacket(PacketByteBuf buffer) {
        pos = buffer.readBlockPos();

        int size = buffer.readInt();
        recipeList = IntStream.range(0, size).mapToObj(i -> Pair.of(PressMoldMakerRecipe.Serializer.INSTANCE.read(buffer.readIdentifier(), buffer),
                buffer.readBoolean())).
                collect(Collectors.toList());
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeBlockPos(pos);

        buffer.writeInt(recipeList.size());
        recipeList.forEach(entry -> {
            buffer.writeIdentifier(entry.getFirst().getId());
            PressMoldMakerRecipe.Serializer.INSTANCE.write(buffer, entry.getFirst());
            buffer.writeBoolean(entry.getSecond());
        });
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static void receive(SyncPressMoldMakerRecipeListS2CPacket data, MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketSender responseSender) {
        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity) {
                pressMoldMakerBlockEntity.setRecipeList(data.recipeList);
            }
        });
    }
}
