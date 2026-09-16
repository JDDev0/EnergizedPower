package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.paintings.EPPaintingVariants;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.EPSoilTypes;
import me.jddev0.ep.villager.EPTradeSets;
import me.jddev0.ep.villager.EPVillagerTrades;
import me.jddev0.ep.world.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ModRegistriesProvider {
    private ModRegistriesProvider() {}

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().
            add(Registries.FEATURE, ModConfiguredFeatures::bootstrap).
            add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap).
            add(Registries.TEMPLATE_POOL, ModTemplatePools::bootstrap).
            add(Registries.STRUCTURE, ModStructures::bootstrap).
            add(Registries.STRUCTURE_SET, ModStructureSets::bootstrap).
            add(Registries.PAINTING_VARIANT, EPPaintingVariants::bootstrap).
            add(Registries.VILLAGER_TRADE, EPVillagerTrades::bootstrap).
            add(Registries.TRADE_SET, EPTradeSets::bootstrap).
            add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap).
            add(EPRegistries.SOIL_TYPE, EPSoilTypes::bootstrap);

    public static DatapackBuiltinEntriesProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        return DatapackBuiltinEntriesProvider.forWorldLayer(output, "Energized Power", registries, BUILDER, Set.of(EPAPI.MOD_ID));
    }
}
