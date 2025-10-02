package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.PressMoldMakerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record CraftPressMoldMakerRecipeC2SPacket(BlockPos pos, Identifier recipeId) implements CustomPayload {
    public static final CustomPayload.Id<CraftPressMoldMakerRecipeC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("craft_press_mold_maker_recipe"));
    public static final PacketCodec<RegistryByteBuf, CraftPressMoldMakerRecipeC2SPacket> PACKET_CODEC =
            PacketCodec.of(CraftPressMoldMakerRecipeC2SPacket::write, CraftPressMoldMakerRecipeC2SPacket::new);

    public CraftPressMoldMakerRecipeC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readIdentifier());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeIdentifier(recipeId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CraftPressMoldMakerRecipeC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity))
                return;

            pressMoldMakerBlockEntity.craftItem(data.recipeId);
        });
    }
}
