package me.jddev0.ep.registry.tags;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class CommonItemTags {
    private CommonItemTags() {}

    public static final Tag<Item> SHEARS = TagFactory.ITEM.create(new Identifier("c", "shears"));
}
