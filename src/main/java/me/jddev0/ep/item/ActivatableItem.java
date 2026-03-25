package me.jddev0.ep.item;

import net.minecraft.world.item.ItemStack;

public interface ActivatableItem {
    boolean isActive(ItemStack itemStack);
}
