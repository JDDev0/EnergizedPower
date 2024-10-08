package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public record PopEnergizedPowerBookFromLecternC2SPacket(BlockPos pos) implements IEnergizedPowerPacket {
    public static final Identifier ID = EPAPI.id("pop_energized_power_book_from_lectern");

    public PopEnergizedPowerBookFromLecternC2SPacket(PacketByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static void receive(PopEnergizedPowerBookFromLecternC2SPacket data, MinecraftServer server, ServerPlayerEntity player,
                               ServerPlayNetworkHandler handler, PacketSender responseSender) {
        player.server.execute(() -> {
            if(!player.canModifyBlocks())
                return;

            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof LecternBlockEntity lecternBlockEntity))
                return;

            ItemStack itemStack = lecternBlockEntity.getBook();

            lecternBlockEntity.setBook(ItemStack.EMPTY);
            LecternBlock.setHasBook(player, player.getWorld(), data.pos, player.getWorld().getBlockState(data.pos), false);
            if(!player.getInventory().insertStack(itemStack))
                player.dropItem(itemStack, false);
        });
    }
}
