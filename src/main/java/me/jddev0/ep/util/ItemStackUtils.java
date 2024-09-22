package me.jddev0.ep.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

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

            if(!inserted) {
                ItemStack itemStackCopy = itemStack.copy();
                itemStackCopy.setCount(amountLeft);
                combinedItemStacks.add(itemStackCopy);
            }
        }

        return combinedItemStacks;
    }
    
    public static ItemStack fromJson(JsonObject json) {
        try {
            Item item = Registry.ITEM.get(new Identifier(JsonHelper.getString(json, "item")));
            int count = json.has("count")?JsonHelper.getByte(json, "count"):1;

            ItemStack itemStack = new ItemStack(item, count);

            if(json.has("tag")) {
                JsonElement tagJson = json.get("tag");
                if(tagJson.isJsonObject()) {
                    NbtCompound nbt = StringNbtReader.parse(tagJson.toString());
                    itemStack.setNbt(nbt);
                }else if(tagJson.isJsonPrimitive() && tagJson.getAsJsonPrimitive().isString()) {
                    NbtCompound nbt = StringNbtReader.parse(tagJson.getAsString());
                    itemStack.setNbt(nbt);
                }else {
                    throw new JsonSyntaxException("Invalid ItemStack nbt data (Expected json object or string)");
                }
            }

            return itemStack;
        }catch(CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid ItemStack json representation", e);
        }
    }

    public static JsonElement toJson(ItemStack item) {
        JsonObject itemJson = new JsonObject();

        itemJson.addProperty("item", Registry.ITEM.getId(item.getItem()).toString());

        if(item.getCount() != 1)
            itemJson.addProperty("count", item.getCount());

        if(item.hasNbt())
            itemJson.addProperty("tag", item.getNbt().toString());

        return itemJson;
    }
}
