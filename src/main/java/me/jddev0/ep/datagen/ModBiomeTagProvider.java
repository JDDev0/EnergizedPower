package me.jddev0.ep.datagen;

import me.jddev0.ep.registry.tags.EnergizedPowerBiomeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class ModBiomeTagProvider extends FabricTagProvider.DynamicRegistryTagProvider<Biome> {
    public ModBiomeTagProvider(FabricDataGenerator output) {
        super(output, Registry.BIOME_KEY);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(EnergizedPowerBiomeTags.HAS_STRUCTURE_FACTORY_1).
                add(BiomeKeys.FOREST,
                        BiomeKeys.FLOWER_FOREST);

        getOrCreateTagBuilder(EnergizedPowerBiomeTags.HAS_STRUCTURE_SMALL_SOLAR_FARM).
                add(BiomeKeys.DESERT,
                        BiomeKeys.BADLANDS,
                        BiomeKeys.SAVANNA);
    }
}
