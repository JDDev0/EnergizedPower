package me.jddev0.ep.util;

import net.minecraft.item.ItemStack;

import java.util.LinkedList;
import java.util.List;

public final class ItemStackUtils {
    private ItemStackUtils() {}

    public static List<ItemStack> combineItemStacks(List<ItemStack> itemStacks) {
        List<ItemStack> combinedItemStacks = new LinkedList<>();
        for(ItemStack itemStack:itemStacks) {
            boolean inserted = false;
            int amountLeft = itemStack.getCount();
            for(ItemStack combinedItemStack:combinedItemStacks) {
                if(ItemStack.areItemsEqual(itemStack, combinedItemStack) && ItemStack.areNbtEqual(itemStack, combinedItemStack) &&
                        combinedItemStack.getMaxCount() > combinedItemStack.getCount()) {
                    int amount = Math.min(amountLeft, combinedItemStack.getMaxCount() - combinedItemStack.getCount());
                    amountLeft -= amount;

                    combinedItemStack.increment(amount);

                    if(amountLeft == 0) {
                        inserted = true;
                        break;
                    }
                }
            }

            if(!inserted)
                combinedItemStacks.add(itemStack.copyWithCount(amountLeft));
        }

        return combinedItemStacks;
    }
}
