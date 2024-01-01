package me.jddev0.ep.networking.packet;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.inventory.ItemStackPacketUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ItemStackSyncS2CPacket(int slot, ItemStack itemStack, BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "item_stack_sync");

    public ItemStackSyncS2CPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readItem(), buffer.readBlockPos());
    }

    @Override
    public void write(final FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeItem(itemStack);
        buffer.writeBlockPos(pos);
    }

    @Override
    @NotNull
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(final ItemStackSyncS2CPacket data, final PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            if(context.level().isEmpty())
                return;

            BlockEntity blockEntity = context.level().get().getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof ItemStackPacketUpdate) {
                ItemStackPacketUpdate itemStackStorage = (ItemStackPacketUpdate)blockEntity;
                itemStackStorage.setItemStack(data.slot, data.itemStack);
            }
        });
    }
}
