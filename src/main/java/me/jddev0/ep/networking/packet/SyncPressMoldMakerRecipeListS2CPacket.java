package me.jddev0.ep.networking.packet;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.PressMoldMakerBlockEntity;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SyncPressMoldMakerRecipeListS2CPacket implements CustomPayload {
    public static final CustomPayload.Id<SyncPressMoldMakerRecipeListS2CPacket> ID =
            new CustomPayload.Id<>(new Identifier(EnergizedPowerMod.MODID, "sync_press_mold_maker_recipe_list"));
    public static final PacketCodec<RegistryByteBuf, SyncPressMoldMakerRecipeListS2CPacket> PACKET_CODEC =
            PacketCodec.of(SyncPressMoldMakerRecipeListS2CPacket::write, SyncPressMoldMakerRecipeListS2CPacket::new);

    private final BlockPos pos;
    private final List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> recipeList;

    public SyncPressMoldMakerRecipeListS2CPacket(BlockPos pos, List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> recipeList) {
        this.pos = pos;
        this.recipeList = recipeList;
    }

    public SyncPressMoldMakerRecipeListS2CPacket(RegistryByteBuf buffer) {
        pos = buffer.readBlockPos();

        int size = buffer.readInt();
        recipeList = IntStream.range(0, size).mapToObj(i -> Pair.of(new RecipeEntry<>(buffer.readIdentifier(),
                        PressMoldMakerRecipe.Serializer.INSTANCE.packetCodec().decode(buffer)), buffer.readBoolean())).
                collect(Collectors.toList());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);

        buffer.writeInt(recipeList.size());
        recipeList.forEach(entry -> {
            buffer.writeIdentifier(entry.getFirst().id());
            PressMoldMakerRecipe.Serializer.INSTANCE.packetCodec().encode(buffer, entry.getFirst().value());
            buffer.writeBoolean(entry.getSecond());
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SyncPressMoldMakerRecipeListS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().world == null)
                return;

            BlockEntity blockEntity = context.client().world.getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity) {
                pressMoldMakerBlockEntity.setRecipeList(data.recipeList);
            }
        });
    }
}
