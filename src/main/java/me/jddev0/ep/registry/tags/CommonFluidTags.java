package me.jddev0.ep.registry.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public final class CommonFluidTags {
    private CommonFluidTags() {}

    public static final TagKey<Fluid> DIRTY_WATER = TagKey.create(Registries.FLUID,
            ResourceLocation.fromNamespaceAndPath("c", "dirty_water"));
}
