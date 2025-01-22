package me.jddev0.ep.registry.tags;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class EnergizedPowerItemTags {
    private EnergizedPowerItemTags() {}

    public static final TagKey<Item> RAW_METAL_PRESS_MOLDS = TagKey.create(Registries.ITEM,
            EPAPI.id("metal_press/raw_press_molds"));

    public static final TagKey<Item> METAL_PRESS_MOLDS = TagKey.create(Registries.ITEM,
            EPAPI.id("metal_press/press_molds"));
}