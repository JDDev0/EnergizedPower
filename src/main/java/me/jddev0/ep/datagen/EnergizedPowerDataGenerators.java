package me.jddev0.ep.datagen;

import me.jddev0.ep.datagen.loot.ModBlockLootTables;
import me.jddev0.ep.paintings.EPPaintingVariants;
import me.jddev0.ep.world.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class EnergizedPowerDataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModRegistriesProvider::new);

        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModBookPageContentProvider::new);

        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModBlockLootTables::new);
        ModAdvancementProvider.create(pack);

        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModItemTagProvider::new);
        pack.addProvider(ModPoiTypeTagProvider::new);
        pack.addProvider(ModBiomeTagProvider::new);
        pack.addProvider(ModPaintingVariantTagProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.TEMPLATE_POOL, ModTemplatePools::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.STRUCTURE, ModStructures::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.STRUCTURE_SET, ModStructureSets::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.PAINTING_VARIANT, EPPaintingVariants::bootstrap);
    }
}
