package me.jddev0.ep.registry.tags;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class EnergizedPowerBiomeTags {
    private EnergizedPowerBiomeTags() {}

    public static final TagKey<Biome> HAS_STRUCTURE_FACTORY_1 = TagKey.create(Registries.BIOME,
            EPAPI.id("has_structure/factory_1"));

    public static final TagKey<Biome> HAS_STRUCTURE_SMALL_SOLAR_FARM = TagKey.create(Registries.BIOME,
            EPAPI.id("has_structure/small_solar_farm"));
}