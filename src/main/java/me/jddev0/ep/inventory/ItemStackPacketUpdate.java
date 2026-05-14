package me.jddev0.ep.inventory;

import net.minecraft.world.item.ItemStack;

/**
 * Used for ItemStackSyncS2CPacket
 */
public interface ItemStackPacketUpdate {
    void setItemStack(int slot, ItemStack itemStack);
}
