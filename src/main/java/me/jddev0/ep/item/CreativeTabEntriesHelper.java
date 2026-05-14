package me.jddev0.ep.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record CreativeTabEntriesHelper(FabricItemGroupEntries entries) {
    public void accept(ItemLike item) {
        entries.accept(item);
    }

    public void accept(ItemStack item) {
        entries.accept(item);
    }
}
