package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.paintings.EPPaintingVariants;
import me.jddev0.ep.world.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModRegistriesProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().
            add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap).
            add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap).
            add(Registries.TEMPLATE_POOL, ModTemplatePools::bootstrap).
            add(Registries.STRUCTURE, ModStructures::bootstrap).
            add(Registries.STRUCTURE_SET, ModStructureSets::bootstrap).
            add(Registries.PAINTING_VARIANT, EPPaintingVariants::bootstrap).
            add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);

    public ModRegistriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, BUILDER, Set.of(EPAPI.MOD_ID));
    }
}
