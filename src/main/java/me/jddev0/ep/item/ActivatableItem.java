package me.jddev0.ep.item;

import net.minecraft.item.ItemStack;

public interface ActivatableItem {
    boolean isActive(ItemStack itemStack);
}
