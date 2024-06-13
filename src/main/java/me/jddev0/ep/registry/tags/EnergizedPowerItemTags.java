package me.jddev0.ep.registry.tags;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class EnergizedPowerItemTags {
    private EnergizedPowerItemTags() {}

    public static final TagKey<Item> METAL_PRESS_MOLDS = TagKey.of(RegistryKeys.ITEM, Identifier.of(EnergizedPowerMod.MODID, "metal_press/press_molds"));
}