package me.jddev0.ep.world.gen;

import me.jddev0.ep.world.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.level.levelgen.GenerationStep;

public class ModOreGeneration {
    private ModOreGeneration() {}

    public static void register() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.TIN_ORE_KEY);
    }
}
