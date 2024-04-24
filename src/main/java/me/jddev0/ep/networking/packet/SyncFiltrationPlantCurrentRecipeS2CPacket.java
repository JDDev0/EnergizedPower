package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class SyncFiltrationPlantCurrentRecipeS2CPacket implements CustomPayload {
    public static final CustomPayload.Id<SyncFiltrationPlantCurrentRecipeS2CPacket> ID =
            new CustomPayload.Id<>(new Identifier(EnergizedPowerMod.MODID, "sync_filtration_plant_current_recipe"));
    public static final PacketCodec<RegistryByteBuf, SyncFiltrationPlantCurrentRecipeS2CPacket> PACKET_CODEC =
            PacketCodec.of(SyncFiltrationPlantCurrentRecipeS2CPacket::write, SyncFiltrationPlantCurrentRecipeS2CPacket::new);

    private final BlockPos pos;
    private final RecipeEntry<FiltrationPlantRecipe> currentRecipe;

    public SyncFiltrationPlantCurrentRecipeS2CPacket(BlockPos pos, @Nullable RecipeEntry<FiltrationPlantRecipe> currentRecipe) {
        this.pos = pos;
        this.currentRecipe = currentRecipe;
    }

    public SyncFiltrationPlantCurrentRecipeS2CPacket(RegistryByteBuf buffer) {
        pos = buffer.readBlockPos();

        currentRecipe = buffer.readBoolean()?new RecipeEntry<>(buffer.readIdentifier(),
                FiltrationPlantRecipe.Serializer.INSTANCE.packetCodec().decode(buffer)):null;
    }

    public void write(final RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);

        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeIdentifier(currentRecipe.id());
            FiltrationPlantRecipe.Serializer.INSTANCE.packetCodec().encode(buffer, currentRecipe.value());
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SyncFiltrationPlantCurrentRecipeS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().world == null)
                return;

            BlockEntity blockEntity = context.client().world.getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof FiltrationPlantBlockEntity filtrationPlantBlockEntity) {
                filtrationPlantBlockEntity.setCurrentRecipe(data.currentRecipe);
            }
        });
    }
}
