package me.jddev0.ep.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

public record CreativeTabEntriesHelper(FabricItemGroupEntries entries) {
    public void accept(ItemConvertible item) {
        entries.add(item);
    }

    public void accept(ItemStack item) {
        entries.add(item);
    }
}
