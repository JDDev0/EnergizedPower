package me.jddev0.ep.networking.packet;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.entity.PressMoldMakerBlockEntity;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SyncPressMoldMakerRecipeListS2CPacket {
    private final BlockPos pos;
    private final List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList;

    public SyncPressMoldMakerRecipeListS2CPacket(BlockPos pos, List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList) {
        this.pos = pos;
        this.recipeList = recipeList;
    }

    public SyncPressMoldMakerRecipeListS2CPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        int size = buffer.readInt();
        recipeList = IntStream.range(0, size).mapToObj(i -> Pair.of(new RecipeHolder<>(buffer.readResourceLocation(),
                PressMoldMakerRecipe.Serializer.INSTANCE.fromNetwork(buffer)), buffer.readBoolean())).
                collect(Collectors.toList());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        buffer.writeInt(recipeList.size());
        recipeList.forEach(entry -> {
            buffer.writeResourceLocation(entry.getFirst().id());
            PressMoldMakerRecipe.Serializer.INSTANCE.toNetwork(buffer, entry.getFirst().value());
            buffer.writeBoolean(entry.getSecond());
        });
    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            //BlockEntity
            if(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity) {
                pressMoldMakerBlockEntity.setRecipeList(recipeList);
            }
        });

        return true;
    }
}
