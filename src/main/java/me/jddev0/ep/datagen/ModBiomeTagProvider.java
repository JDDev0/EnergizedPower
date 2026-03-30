package me.jddev0.ep.datagen;

import me.jddev0.ep.registry.tags.EnergizedPowerBiomeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagProvider extends FabricTagsProvider<Biome> {
    public ModBiomeTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.BIOME, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        builder(EnergizedPowerBiomeTags.HAS_STRUCTURE_FACTORY_1).
                add(Biomes.FOREST,
                        Biomes.FLOWER_FOREST);

        builder(EnergizedPowerBiomeTags.HAS_STRUCTURE_SMALL_SOLAR_FARM).
                add(Biomes.DESERT,
                        Biomes.BADLANDS,
                        Biomes.SAVANNA);
    }
}
