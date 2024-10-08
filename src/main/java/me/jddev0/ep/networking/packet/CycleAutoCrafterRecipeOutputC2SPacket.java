package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.screen.AutoCrafterMenu;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record CycleAutoCrafterRecipeOutputC2SPacket(BlockPos pos) implements IEnergizedPowerPacket {
    public static final Identifier ID = EPAPI.id("cycle_auto_crafter_recipe_output");

    public CycleAutoCrafterRecipeOutputC2SPacket(PacketByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static void receive(CycleAutoCrafterRecipeOutputC2SPacket data, MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketSender responseSender) {
        player.server.execute(() -> {
            if(!player.canModifyBlocks())
                return;

            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            ScreenHandler menu = player.currentScreenHandler;

            if(!(menu instanceof AutoCrafterMenu))
                return;

            autoCrafterBlockEntity.cycleRecipe();

            autoCrafterBlockEntity.resetProgressAndMarkAsChanged();
        });
    }
}
