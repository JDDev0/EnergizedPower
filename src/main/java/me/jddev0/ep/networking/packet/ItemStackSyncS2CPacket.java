package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.inventory.ItemStackPacketUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ItemStackSyncS2CPacket(int slot, ItemStack itemStack, BlockPos pos) implements CustomPacketPayload {
    public static final Type<ItemStackSyncS2CPacket> ID =
            new Type<>(ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "item_stack_sync"));
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
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(ItemStackSyncS2CPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof ItemStackPacketUpdate) {
                ItemStackPacketUpdate itemStackStorage = (ItemStackPacketUpdate)blockEntity;
                itemStackStorage.setItemStack(data.slot, data.itemStack);
            }
        });
    }
}
