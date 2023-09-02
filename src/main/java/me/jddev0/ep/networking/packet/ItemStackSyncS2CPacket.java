package me.jddev0.ep.networking.packet;

import me.jddev0.ep.inventory.ItemStackPacketUpdate;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class ItemStackSyncS2CPacket {
    public ItemStackSyncS2CPacket() {}

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int slot = buf.readInt();
        ItemStack itemStack = buf.readItemStack();
        BlockPos pos = buf.readBlockPos();

        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(pos);
            if(blockEntity instanceof ItemStackPacketUpdate itemStackStorage) {
                itemStackStorage.setItemStack(slot, itemStack);
            }
        });
    }
}
