package me.jddev0.ep.inventory;

import net.minecraft.item.ItemStack;

/**
 * Used for ItemStackSyncS2CPacket
 */
public interface ItemStackPacketUpdate {
    void setItemStack(int slot, ItemStack itemStack);
}
