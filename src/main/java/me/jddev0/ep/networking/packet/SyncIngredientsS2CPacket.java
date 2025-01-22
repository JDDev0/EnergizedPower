package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SyncIngredientsS2CPacket implements CustomPacketPayload {
    public static final Type<SyncIngredientsS2CPacket> ID =
            new Type<>(EPAPI.id("sync_ingredients"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncIngredientsS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(SyncIngredientsS2CPacket::write, SyncIngredientsS2CPacket::new);

    private final BlockPos pos;
    private final int index;
    private final List<Ingredient> ingredientList;

    public SyncIngredientsS2CPacket(BlockPos pos, int index, List<Ingredient> ingredientList) {
        this.pos = pos;
        this.index = index;
        this.ingredientList = new ArrayList<>(ingredientList);
    }

    public SyncIngredientsS2CPacket(RegistryFriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        index = buffer.readInt();

        int len = buffer.readInt();
        ArrayList<Ingredient> ingredientList = new ArrayList<>(len);
        for(int i = 0;i < len;i++)
            ingredientList.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));

        this.ingredientList = ingredientList;
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        buffer.writeInt(index);

        buffer.writeInt(ingredientList.size());

        for(Ingredient ingredient: ingredientList)
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SyncIngredientsS2CPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(data.pos);

            if(blockEntity instanceof IngredientPacketUpdate ingredientPacketUpdate)
                ingredientPacketUpdate.setIngredients(data.index, new ArrayList<>(data.ingredientList));
        });
    }
}
