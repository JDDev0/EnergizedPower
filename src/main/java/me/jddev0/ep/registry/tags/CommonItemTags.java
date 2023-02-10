package me.jddev0.ep.registry.tags;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class CommonItemTags {
    private CommonItemTags() {}

    public static final TagKey<Item> SHEARS = TagKey.of(Registry.ITEM_KEY, new Identifier("c", "shears"));
}
