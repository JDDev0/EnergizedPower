package me.jddev0.ep.util;

import me.jddev0.ep.inventory.SingleItemStackHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public final class InventoryUtils {
    private InventoryUtils() {}

    public static boolean canInsertItemIntoSlot(Inventory inventory, int slot, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getStack(slot);

        return inventoryItemStack.isEmpty() || (ItemStack.areItemsAndComponentsEqual(inventoryItemStack, itemStack) &&
                inventoryItemStack.getMaxCount() >= inventoryItemStack.getCount() + itemStack.getCount());
    }

    public static int getRedstoneSignalFromItemStackHandler(SingleItemStackHandler itemHandler) {
        boolean isEmptyFlag = itemHandler.isEmpty();

        long count = itemHandler.getAmount();
        long capacity = itemHandler.getCapacity();

        return Math.min(MathHelper.floor((double)count / (double)capacity * 14.d) + (isEmptyFlag?0:1), 15);
    }
}
