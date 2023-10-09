package me.jddev0.ep.registry.tags;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class EnergizedPowerItemTags {
    private EnergizedPowerItemTags() {}

    public static final TagKey<Item> METAL_PRESS_MOLDS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(EnergizedPowerMod.MODID, "metal_press/press_molds"));
}