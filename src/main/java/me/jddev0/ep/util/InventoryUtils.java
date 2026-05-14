package me.jddev0.ep.util;

import me.jddev0.ep.inventory.SingleItemStackHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public final class InventoryUtils {
    private InventoryUtils() {}

    public static boolean canInsertItemIntoSlot(Container inventory, int slot, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getItem(slot);

        return inventoryItemStack.isEmpty() || (ItemStack.isSameItemSameComponents(inventoryItemStack, itemStack) &&
                inventoryItemStack.getMaxStackSize() >= inventoryItemStack.getCount() + itemStack.getCount());
    }

    public static int getRedstoneSignalFromItemStackHandler(SingleItemStackHandler itemHandler) {
        boolean isEmptyFlag = itemHandler.isEmpty();

        long count = itemHandler.getAmount();
        long capacity = itemHandler.getCapacity();

        return Math.min(Mth.floor((double)count / (double)capacity * 14.d) + (isEmptyFlag?0:1), 15);
    }
}
