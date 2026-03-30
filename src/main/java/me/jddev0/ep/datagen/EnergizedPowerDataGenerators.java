package me.jddev0.ep.datagen;

import me.jddev0.ep.datagen.loot.ModBlockLootTables;
import me.jddev0.ep.paintings.EPPaintingVariants;
import me.jddev0.ep.villager.EPTradeSets;
import me.jddev0.ep.villager.EPVillagerTrades;
import me.jddev0.ep.world.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

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
        pack.addProvider(ModVillagerTradeTagProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
        registryBuilder.add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
        registryBuilder.add(Registries.TEMPLATE_POOL, ModTemplatePools::bootstrap);
        registryBuilder.add(Registries.STRUCTURE, ModStructures::bootstrap);
        registryBuilder.add(Registries.STRUCTURE_SET, ModStructureSets::bootstrap);
        registryBuilder.add(Registries.PAINTING_VARIANT, EPPaintingVariants::bootstrap);
        registryBuilder.add(Registries.VILLAGER_TRADE, EPVillagerTrades::bootstrap);
        registryBuilder.add(Registries.TRADE_SET, EPTradeSets::bootstrap);
    }
}
