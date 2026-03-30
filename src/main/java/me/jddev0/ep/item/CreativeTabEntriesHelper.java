package me.jddev0.ep.item;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record CreativeTabEntriesHelper(FabricCreativeModeTabOutput entries) {
    public void accept(ItemLike item) {
        entries.accept(item);
    }

    public void accept(ItemStack item) {
        entries.accept(item);
    }
}
