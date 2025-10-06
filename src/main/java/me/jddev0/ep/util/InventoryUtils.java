package me.jddev0.ep.util;

import me.jddev0.ep.inventory.SingleItemStackHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public final class InventoryUtils {
    private InventoryUtils() {}

    public static boolean canInsertItemIntoSlot(Container inventory, int slot, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getItem(slot);

        return inventoryItemStack.isEmpty() || (ItemStack.isSameItemSameComponents(inventoryItemStack, itemStack) &&
                inventoryItemStack.getMaxStackSize() >= inventoryItemStack.getCount() + itemStack.getCount());
    }

    public static int getRedstoneSignalFromItemStackHandler(ResourceHandler<ItemResource> itemHandler) {
        double fullnessPercentSum = 0;
        boolean isEmptyFlag = true;

        int size = itemHandler.size();
        for(int i = 0;i < size;i++) {
            if(!itemHandler.getResource(i).isEmpty()) {
                fullnessPercentSum += (double)itemHandler.getAmountAsLong(i) / Math.min(itemHandler.getResource(i).getMaxStackSize(), itemHandler.getCapacityAsLong(i, ItemResource.EMPTY));
                isEmptyFlag = false;
            }
        }

        return Math.min(Mth.floor(fullnessPercentSum / size * 14.f) + (isEmptyFlag?0:1), 15);
    }

    public static int getRedstoneSignalFromItemStackHandler(SingleItemStackHandler itemHandler) {
        boolean isEmptyFlag = itemHandler.getCount() == 0 && itemHandler.getResource().isEmpty();

        int count = itemHandler.getCount();
        int capacity = itemHandler.size() * itemHandler.getResource().getMaxStackSize();

        return Math.min(Mth.floor((double)count / (double)capacity * 14.d) + (isEmptyFlag?0:1), 15);
    }
}
