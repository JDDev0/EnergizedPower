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

    public static final TagKey<Item> METAL_PRESS_MOLDS_TIER_BASIC = TagKey.create(Registries.ITEM,
            EPAPI.id("metal_press/press_molds/tier/basic"));
    public static final TagKey<Item> METAL_PRESS_MOLDS_TIER_HARDENED = TagKey.create(Registries.ITEM,
            EPAPI.id("metal_press/press_molds/tier/hardened"));
    public static final TagKey<Item> METAL_PRESS_MOLDS_TIER_ADVANCED = TagKey.create(Registries.ITEM,
            EPAPI.id("metal_press/press_molds/tier/advanced"));
    public static final TagKey<Item> METAL_PRESS_MOLDS_TIER_ELITE = TagKey.create(Registries.ITEM,
            EPAPI.id("metal_press/press_molds/tier/elite"));

    public static final TagKey<Item> METAL_PRESS_MOLDS_GEAR = TagKey.create(Registries.ITEM,
            EPAPI.id("metal_press/press_molds/gear"));
    public static final TagKey<Item> METAL_PRESS_MOLDS_ROD = TagKey.create(Registries.ITEM,
            EPAPI.id("metal_press/press_molds/rod"));
    public static final TagKey<Item> METAL_PRESS_MOLDS_WIRE = TagKey.create(Registries.ITEM,
            EPAPI.id("metal_press/press_molds/wire"));
}