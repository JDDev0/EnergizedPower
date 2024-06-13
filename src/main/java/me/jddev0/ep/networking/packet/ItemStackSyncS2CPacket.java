package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.inventory.ItemStackPacketUpdate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record ItemStackSyncS2CPacket(int slot, ItemStack itemStack, BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<ItemStackSyncS2CPacket> ID =
            new CustomPayload.Id<>(Identifier.of(EnergizedPowerMod.MODID, "item_stack_sync"));
    public static final PacketCodec<RegistryByteBuf, ItemStackSyncS2CPacket> PACKET_CODEC =
            PacketCodec.of(ItemStackSyncS2CPacket::write, ItemStackSyncS2CPacket::new);

    public ItemStackSyncS2CPacket(RegistryByteBuf buffer) {
        this(buffer.readInt(), ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer), buffer.readBlockPos());
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeInt(slot);
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, itemStack);
        buffer.writeBlockPos(pos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(ItemStackSyncS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().world == null)
                return;

            BlockEntity blockEntity = context.client().world.getBlockEntity(data.pos);
            if(blockEntity instanceof ItemStackPacketUpdate itemStackStorage) {
                itemStackStorage.setItemStack(data.slot, data.itemStack);
            }
        });
    }
}
