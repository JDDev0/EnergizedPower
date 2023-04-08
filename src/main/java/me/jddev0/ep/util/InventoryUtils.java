package me.jddev0.ep.util;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public final class InventoryUtils {
    private InventoryUtils() {}

    public static int getRedstoneSignalFromItemStackHandler(ItemStackHandler itemHandler) {
        float fullnessPercentSum = 0;
        boolean isEmptyFlag = true;

        int size = itemHandler.getSlots();
        for(int i = 0;i < size;i++) {
            ItemStack item = itemHandler.getStackInSlot(i);
            if(!item.isEmpty()) {
                fullnessPercentSum += (float)item.getCount() / Math.min(item.getMaxStackSize(), itemHandler.getSlotLimit(i));
                isEmptyFlag = false;
            }
        }

        return Math.max(Mth.floor(fullnessPercentSum / size * 14.f) + (isEmptyFlag?0:1), 15);
    }
}
