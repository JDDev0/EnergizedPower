package me.jddev0.ep.datagen;

import me.jddev0.ep.registry.tags.EnergizedPowerBiomeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagProvider extends FabricTagProvider<Biome> {
    public ModBiomeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(output, RegistryKeys.BIOME, lookupProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookupProvider) {
        getOrCreateTagBuilder(EnergizedPowerBiomeTags.HAS_STRUCTURE_FACTORY_1).
                add(BiomeKeys.FOREST,
                        BiomeKeys.FLOWER_FOREST);

        getOrCreateTagBuilder(EnergizedPowerBiomeTags.HAS_STRUCTURE_SMALL_SOLAR_FARM).
                add(BiomeKeys.DESERT,
                        BiomeKeys.BADLANDS,
                        BiomeKeys.SAVANNA);
    }
}
