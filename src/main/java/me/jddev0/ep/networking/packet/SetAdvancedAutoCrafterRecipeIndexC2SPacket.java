package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AdvancedAutoCrafterBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record SetAdvancedAutoCrafterRecipeIndexC2SPacket(BlockPos pos, int recipeIndex) implements CustomPayload {
    public static final CustomPayload.Id<SetAdvancedAutoCrafterRecipeIndexC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("set_advanced_auto_crafter_recipe_index"));
    public static final PacketCodec<RegistryByteBuf, SetAdvancedAutoCrafterRecipeIndexC2SPacket> PACKET_CODEC =
            PacketCodec.of(SetAdvancedAutoCrafterRecipeIndexC2SPacket::write, SetAdvancedAutoCrafterRecipeIndexC2SPacket::new);

    public SetAdvancedAutoCrafterRecipeIndexC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(recipeIndex);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetAdvancedAutoCrafterRecipeIndexC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AdvancedAutoCrafterBlockEntity advancedAutoCrafterBlockEntity))
                return;

            advancedAutoCrafterBlockEntity.setCurrentRecipeIndex(data.recipeIndex);
        });
    }
}
