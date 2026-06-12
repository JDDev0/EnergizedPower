package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.datagen.recipe.ModRecipeGenerator;
import me.jddev0.ep.datagen.lang.ModLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = EPAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class EnergizedPowerDataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        CompletableFuture<HolderLookup.Provider> lookupProvider =
                generator.addProvider(event.includeServer(), new ModRegistriesProvider(output, event.getLookupProvider())).
                        getRegistryProvider();

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModBookPageContentProvider(output, lookupProvider, existingFileHelper));

        //Languages with separate localization
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "de_de"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "en_us"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "es_es"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "es_mx"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "it_it"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "ja_jp"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "pt_br"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "ru_ru"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "tr_tr"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "zh_cn"));

        //Languages with copied translations
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "de_at", "lang/de_de.json"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "de_ch", "lang/de_de.json"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "es_ar", "lang/es_mx.json"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "es_cl", "lang/es_mx.json"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "es_ec", "lang/es_mx.json"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "es_uy", "lang/es_mx.json"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(output, "es_ve", "lang/es_mx.json"));

        generator.addProvider(event.includeServer(), new ModRecipeGenerator(output, lookupProvider));
        generator.addProvider(event.includeServer(), ModLootTableProvider.create(output, lookupProvider));
        generator.addProvider(event.includeServer(), ModAdvancementProvider.create(output, lookupProvider, existingFileHelper));

        ModBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(),
                new ModBlockTagProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModItemTagProvider(output, lookupProvider,
                blockTagProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new ModPoiTypeTagProvider(output, lookupProvider,
                existingFileHelper));
        generator.addProvider(event.includeServer(), new ModBiomeTagProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModPaintingVariantTagProvider(output, lookupProvider,
                existingFileHelper));
        generator.addProvider(event.includeServer(), new ModSoilTypeTagProvider(output, lookupProvider, existingFileHelper));
    }
}
