package me.jddev0.ep.worldgen;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModOreGeneration {
    private ModOreGeneration() {}

    public static final RegistryKey<PlacedFeature> TIN_ORE_KEY = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE, Identifier.of(EnergizedPowerMod.MODID, "tin_ore")
    );

    public static void register() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, TIN_ORE_KEY);
    }
}
