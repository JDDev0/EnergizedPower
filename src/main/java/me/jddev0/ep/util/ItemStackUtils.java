package me.jddev0.ep.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedList;
import java.util.List;

public final class ItemStackUtils {
    private ItemStackUtils() {}

    public static ItemStack copyWithCount(ItemStack itemStack, int count) {
        if(itemStack.isEmpty())
            return ItemStack.EMPTY;

        itemStack = itemStack.copy();
        itemStack.setCount(count);
        return itemStack;
    }

    public static List<ItemStack> combineItemStacks(List<ItemStack> itemStacks) {
        List<ItemStack> combinedItemStacks = new LinkedList<>();
        for(ItemStack itemStack:itemStacks) {
            boolean inserted = false;
            int amountLeft = itemStack.getCount();
            for(ItemStack combinedItemStack:combinedItemStacks) {
                if(ItemStack.isSame(itemStack, combinedItemStack) && ItemStack.tagMatches(itemStack, combinedItemStack) &&
                        combinedItemStack.getMaxStackSize() > combinedItemStack.getCount()) {
                    int amount = Math.min(amountLeft, combinedItemStack.getMaxStackSize() - combinedItemStack.getCount());
                    amountLeft -= amount;

                    combinedItemStack.grow(amount);

                    if(amountLeft == 0) {
                        inserted = true;
                        break;
                    }
                }
            }

            if(!inserted) {
                ItemStack itemStackCopy = itemStack.copy();
                itemStackCopy.setCount(amountLeft);
                combinedItemStacks.add(itemStackCopy);
            }
        }

        return combinedItemStacks;
    }

    public static JsonElement toJson(ItemStack item) {
        JsonObject itemJson = new JsonObject();

        itemJson.addProperty("item", ForgeRegistries.ITEMS.getKey(item.getItem()).toString());

        if(item.getCount() != 1)
            itemJson.addProperty("count", item.getCount());

        if(item.hasTag())
            itemJson.addProperty("nbt", item.getTag().toString());

        return itemJson;
    }
}
