package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record ChangeFiltrationPlantRecipeIndexC2SPacket(BlockPos pos, boolean downUp) implements CustomPayload {
    public static final CustomPayload.Id<ChangeFiltrationPlantRecipeIndexC2SPacket> ID =
            new CustomPayload.Id<>(new Identifier(EnergizedPowerMod.MODID, "change_filtration_plant_recipe_index"));
    public static final PacketCodec<RegistryByteBuf, ChangeFiltrationPlantRecipeIndexC2SPacket> PACKET_CODEC =
            PacketCodec.of(ChangeFiltrationPlantRecipeIndexC2SPacket::write, ChangeFiltrationPlantRecipeIndexC2SPacket::new);

    public ChangeFiltrationPlantRecipeIndexC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(downUp);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(ChangeFiltrationPlantRecipeIndexC2SPacket data, ServerPlayNetworking.Context context) {
        context.player().server.execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof FiltrationPlantBlockEntity filtrationPlantBlockEntity))
                return;

            filtrationPlantBlockEntity.changeRecipeIndex(data.downUp);
        });
    }
}
