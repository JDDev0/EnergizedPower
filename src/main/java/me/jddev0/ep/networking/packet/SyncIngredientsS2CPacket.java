package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SyncIngredientsS2CPacket implements CustomPayload {
    public static final Id<SyncIngredientsS2CPacket> ID =
            new Id<>(EPAPI.id("sync_ingredients"));
    public static final PacketCodec<RegistryByteBuf, SyncIngredientsS2CPacket> PACKET_CODEC =
            PacketCodec.of(SyncIngredientsS2CPacket::write, SyncIngredientsS2CPacket::new);

    private final BlockPos pos;
    private final int index;
    private final List<Ingredient> ingredientList;

    public SyncIngredientsS2CPacket(BlockPos pos, int index, List<Ingredient> ingredientList) {
        this.pos = pos;
        this.index = index;
        this.ingredientList = new ArrayList<>(ingredientList);
    }

    public SyncIngredientsS2CPacket(RegistryByteBuf buffer) {
        pos = buffer.readBlockPos();

        index = buffer.readInt();

        int len = buffer.readInt();
        ArrayList<Ingredient> ingredientList = new ArrayList<>(len);
        for(int i = 0;i < len;i++)
            ingredientList.add(Ingredient.PACKET_CODEC.decode(buffer));

        this.ingredientList = ingredientList;
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);

        buffer.writeInt(index);

        buffer.writeInt(ingredientList.size());

        for(Ingredient ingredient: ingredientList)
            Ingredient.PACKET_CODEC.encode(buffer, ingredient);
    }

    @Override
    @NotNull
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SyncIngredientsS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().world == null)
                return;

            BlockEntity blockEntity = context.client().world.getBlockEntity(data.pos);
            if(blockEntity instanceof IngredientPacketUpdate ingredientPacketUpdate)
                ingredientPacketUpdate.setIngredients(data.index, new ArrayList<>(data.ingredientList));
        });
    }
}
