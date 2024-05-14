package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.inventory.ItemStackPacketUpdate;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record ItemStackSyncS2CPacket(int slot, ItemStack itemStack, BlockPos pos) implements IEnergizedPowerPacket {
    public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "item_stack_sync");

    public ItemStackSyncS2CPacket(PacketByteBuf buffer) {
        this(buffer.readInt(), buffer.readItemStack(), buffer.readBlockPos());
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeItemStack(itemStack);
        buffer.writeBlockPos(pos);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static void receive(ItemStackSyncS2CPacket data, MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketSender responseSender) {
        client.execute(() -> {
            if(client.world == null)
                return;

            BlockEntity blockEntity = client.world.getBlockEntity(data.pos);
            if(blockEntity instanceof ItemStackPacketUpdate itemStackStorage) {
                itemStackStorage.setItemStack(data.slot, data.itemStack);
            }
        });
    }
}
