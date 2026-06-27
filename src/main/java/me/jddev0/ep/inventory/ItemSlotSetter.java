package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

@FunctionalInterface
public interface ItemSlotSetter {
    void set(int index, ItemVariant resource, int amount);
}
