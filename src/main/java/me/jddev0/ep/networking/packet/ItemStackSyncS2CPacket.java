package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.inventory.ItemStackPacketUpdate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public record ItemStackSyncS2CPacket(int slot, ItemStack itemStack, BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ItemStackSyncS2CPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("item_stack_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackSyncS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(ItemStackSyncS2CPacket::write, ItemStackSyncS2CPacket::new);

    public ItemStackSyncS2CPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer), buffer.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, itemStack);
        buffer.writeBlockPos(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(ItemStackSyncS2CPacket data, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if(context.client().level == null)
                return;

            BlockEntity blockEntity = context.client().level.getBlockEntity(data.pos);
            if(blockEntity instanceof ItemStackPacketUpdate itemStackStorage) {
                itemStackStorage.setItemStack(data.slot, data.itemStack);
            }
        });
    }
}
