package me.jddev0.ep.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public final class InventoryUtils {
    private InventoryUtils() {}

    public static boolean canInsertItemIntoSlot(Inventory inventory, int slot, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getStack(slot);

        return inventoryItemStack.isEmpty() || (ItemStack.canCombine(inventoryItemStack, itemStack) &&
                inventoryItemStack.getMaxCount() >= inventoryItemStack.getCount() + itemStack.getCount());
    }
}
