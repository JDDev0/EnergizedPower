package me.jddev0.ep.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

public final class PopEnergizedPowerBookFromLecternC2SPacket {
    private PopEnergizedPowerBookFromLecternC2SPacket() {}

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();

        server.execute(() -> {
            if(!player.canModifyBlocks())
                return;

            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);

            if(blockEntity instanceof LecternBlockEntity lecternBlockEntity) {
                ItemStack itemStack = lecternBlockEntity.getBook();

                lecternBlockEntity.setBook(ItemStack.EMPTY);
                LecternBlock.setHasBook(player.getWorld(), pos, player.getWorld().getBlockState(pos), false);
                if(!player.getInventory().insertStack(itemStack))
                    player.dropItem(itemStack, false);
            }
        });
    }
}
