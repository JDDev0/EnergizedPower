package me.jddev0.ep.registry.tags;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public final class EnergizedPowerBiomeTags {
    private EnergizedPowerBiomeTags() {}

    public static final TagKey<Biome> HAS_STRUCTURE_FACTORY_1 = TagKey.of(RegistryKeys.BIOME,
            EPAPI.id("has_structure/factory_1"));

    public static final TagKey<Biome> HAS_STRUCTURE_SMALL_SOLAR_FARM = TagKey.of(RegistryKeys.BIOME,
            EPAPI.id("has_structure/small_solar_farm"));
}