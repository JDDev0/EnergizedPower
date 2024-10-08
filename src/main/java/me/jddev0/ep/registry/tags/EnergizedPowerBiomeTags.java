package me.jddev0.ep.registry.tags;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public final class EnergizedPowerBiomeTags {
    private EnergizedPowerBiomeTags() {}

    public static final TagKey<Biome> HAS_STRUCTURE_FACTORY_1 = TagKey.of(Registry.BIOME_KEY,
            EPAPI.id("has_structure/factory_1"));

    public static final TagKey<Biome> HAS_STRUCTURE_SMALL_SOLAR_FARM = TagKey.of(Registry.BIOME_KEY,
            EPAPI.id("has_structure/small_solar_farm"));
}