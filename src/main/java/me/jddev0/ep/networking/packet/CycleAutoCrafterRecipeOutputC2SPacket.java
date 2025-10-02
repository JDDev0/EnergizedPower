package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.screen.AutoCrafterMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record CycleAutoCrafterRecipeOutputC2SPacket(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<CycleAutoCrafterRecipeOutputC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("cycle_auto_crafter_recipe_output"));
    public static final PacketCodec<RegistryByteBuf, CycleAutoCrafterRecipeOutputC2SPacket> PACKET_CODEC =
            PacketCodec.of(CycleAutoCrafterRecipeOutputC2SPacket::write, CycleAutoCrafterRecipeOutputC2SPacket::new);

    public CycleAutoCrafterRecipeOutputC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CycleAutoCrafterRecipeOutputC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            ScreenHandler menu = context.player().currentScreenHandler;

            if(!(menu instanceof AutoCrafterMenu))
                return;

            autoCrafterBlockEntity.cycleRecipe();

            autoCrafterBlockEntity.resetProgressAndMarkAsChanged();
        });
    }
}
