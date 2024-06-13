package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.AdvancedAutoCrafterBlockEntity;
import me.jddev0.ep.screen.AdvancedAutoCrafterMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record CycleAdvancedAutoCrafterRecipeOutputC2SPacket(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<CycleAdvancedAutoCrafterRecipeOutputC2SPacket> ID =
            new CustomPayload.Id<>(Identifier.of(EnergizedPowerMod.MODID, "cycle_advanced_auto_crafter_recipe_output"));
    public static final PacketCodec<RegistryByteBuf, CycleAdvancedAutoCrafterRecipeOutputC2SPacket> PACKET_CODEC =
            PacketCodec.of(CycleAdvancedAutoCrafterRecipeOutputC2SPacket::write, CycleAdvancedAutoCrafterRecipeOutputC2SPacket::new);

    public CycleAdvancedAutoCrafterRecipeOutputC2SPacket(RegistryByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CycleAdvancedAutoCrafterRecipeOutputC2SPacket data, ServerPlayNetworking.Context context) {
        context.player().server.execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AdvancedAutoCrafterBlockEntity advancedAutoCrafterBlockEntity))
                return;

            ScreenHandler menu = context.player().currentScreenHandler;

            if(!(menu instanceof AdvancedAutoCrafterMenu advancedAutoCrafterMenu))
                return;

            advancedAutoCrafterBlockEntity.cycleRecipe();

            advancedAutoCrafterBlockEntity.resetProgressAndMarkAsChanged(advancedAutoCrafterMenu.getRecipeIndex());
        });
    }
}
