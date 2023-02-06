package me.jddev0.ep.registry.tags;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class CommonItemTags {
    private CommonItemTags() {}

    public static final TagKey<Item> SHEARS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "shears"));
}
