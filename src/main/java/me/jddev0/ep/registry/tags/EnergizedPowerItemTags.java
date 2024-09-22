package me.jddev0.ep.registry.tags;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class EnergizedPowerItemTags {
    private EnergizedPowerItemTags() {}

    public static final TagKey<Item> RAW_METAL_PRESS_MOLDS = TagKey.of(Registry.ITEM_KEY, Identifier.of(EnergizedPowerMod.MODID, "metal_press/raw_press_molds"));

    public static final TagKey<Item> METAL_PRESS_MOLDS = TagKey.of(Registry.ITEM_KEY, Identifier.of(EnergizedPowerMod.MODID, "metal_press/press_molds"));
}