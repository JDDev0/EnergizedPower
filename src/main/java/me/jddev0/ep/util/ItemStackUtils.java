package me.jddev0.ep.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

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
    
    public static ItemStack fromJson(JsonElement json) {
        try {
            NbtCompound nbt = StringNbtReader.parse(json.toString());
            Item item = Registries.ITEM.get(new Identifier(nbt.getString("item")));
            int count = nbt.contains("count", NbtElement.NUMBER_TYPE)?nbt.getByte("count"):1;

            ItemStack itemStack = new ItemStack(item, count);
            if(nbt.contains("tag", NbtElement.COMPOUND_TYPE))
                itemStack.setNbt(nbt.getCompound("tag"));

            return itemStack;
        } catch(CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid ItemStack json representation", e);
        }
    }
}
