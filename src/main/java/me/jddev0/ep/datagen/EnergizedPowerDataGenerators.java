package me.jddev0.ep.datagen;

import me.jddev0.ep.datagen.lang.ModLanguageProvider;
import me.jddev0.ep.datagen.loot.ModBlockLootTables;
import me.jddev0.ep.datagen.recipe.ModRecipeGenerator;
import me.jddev0.ep.paintings.EPPaintingVariants;
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

        //Languages with separate localization
        pack.addProvider(ModLanguageProvider.create("de_de"));
        pack.addProvider(ModLanguageProvider.create("en_us"));
        pack.addProvider(ModLanguageProvider.create("es_es"));
        pack.addProvider(ModLanguageProvider.create("es_mx"));
        pack.addProvider(ModLanguageProvider.create("it_it"));
        pack.addProvider(ModLanguageProvider.create("ja_jp"));
        pack.addProvider(ModLanguageProvider.create("pt_br"));
        pack.addProvider(ModLanguageProvider.create("ru_ru"));
        pack.addProvider(ModLanguageProvider.create("tr_tr"));
        pack.addProvider(ModLanguageProvider.create("zh_cn"));

        //Languages with copied translations
        pack.addProvider(ModLanguageProvider.create("de_at", "lang/de_de.json"));
        pack.addProvider(ModLanguageProvider.create("de_ch", "lang/de_de.json"));
        pack.addProvider(ModLanguageProvider.create("es_ar", "lang/es_mx.json"));
        pack.addProvider(ModLanguageProvider.create("es_cl", "lang/es_mx.json"));
        pack.addProvider(ModLanguageProvider.create("es_ec", "lang/es_mx.json"));
        pack.addProvider(ModLanguageProvider.create("es_uy", "lang/es_mx.json"));
        pack.addProvider(ModLanguageProvider.create("es_ve", "lang/es_mx.json"));

        pack.addProvider(ModRecipeGenerator::new);
        pack.addProvider(ModBlockLootTables::new);
        ModAdvancementProvider.create(pack);

        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModItemTagProvider::new);
        pack.addProvider(ModPoiTypeTagProvider::new);
        pack.addProvider(ModBiomeTagProvider::new);
        pack.addProvider(ModPaintingVariantTagProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
        registryBuilder.add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
        registryBuilder.add(Registries.TEMPLATE_POOL, ModTemplatePools::bootstrap);
        registryBuilder.add(Registries.STRUCTURE, ModStructures::bootstrap);
        registryBuilder.add(Registries.STRUCTURE_SET, ModStructureSets::bootstrap);
        registryBuilder.add(Registries.PAINTING_VARIANT, EPPaintingVariants::bootstrap);
    }
}
