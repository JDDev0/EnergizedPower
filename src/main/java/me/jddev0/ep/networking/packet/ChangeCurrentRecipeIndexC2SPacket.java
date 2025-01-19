package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.ChangeCurrentRecipeIndexPacketUpdate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record ChangeCurrentRecipeIndexC2SPacket(BlockPos pos, boolean downUp) implements CustomPayload {
    public static final CustomPayload.Id<ChangeCurrentRecipeIndexC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("change_current_recipe_index"));
    public static final PacketCodec<RegistryByteBuf, ChangeCurrentRecipeIndexC2SPacket> PACKET_CODEC =
            PacketCodec.of(ChangeCurrentRecipeIndexC2SPacket::write, ChangeCurrentRecipeIndexC2SPacket::new);

    public ChangeCurrentRecipeIndexC2SPacket(RegistryByteBuf buffer) {
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

    public static void receive(ChangeCurrentRecipeIndexC2SPacket data, ServerPlayNetworking.Context context) {
        context.player().server.execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof ChangeCurrentRecipeIndexPacketUpdate changeCurrentRecipeIndexPacketUpdate))
                return;

            changeCurrentRecipeIndexPacketUpdate.changeRecipeIndex(data.downUp);
        });
    }
}
