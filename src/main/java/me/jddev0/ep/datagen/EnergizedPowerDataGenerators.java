package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.datagen.lang.ModLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = EPAPI.MOD_ID)
public class EnergizedPowerDataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        CompletableFuture<HolderLookup.Provider> lookupProvider =
                generator.addProvider(true, new ModRegistriesProvider(output, event.getLookupProvider())).
                        getRegistryProvider();

        generator.addProvider(true, new ModModelProvider(output));
        generator.addProvider(true, new ModBookPageContentProvider(output, lookupProvider));

        //Languages with separate localization
        generator.addProvider(true, new ModLanguageProvider(output, "de_de"));
        generator.addProvider(true, new ModLanguageProvider(output, "en_us"));
        generator.addProvider(true, new ModLanguageProvider(output, "es_es"));
        generator.addProvider(true, new ModLanguageProvider(output, "es_mx"));
        generator.addProvider(true, new ModLanguageProvider(output, "it_it"));
        generator.addProvider(true, new ModLanguageProvider(output, "ja_jp"));
        generator.addProvider(true, new ModLanguageProvider(output, "pt_br"));
        generator.addProvider(true, new ModLanguageProvider(output, "ru_ru"));
        generator.addProvider(true, new ModLanguageProvider(output, "tr_tr"));
        generator.addProvider(true, new ModLanguageProvider(output, "zh_cn"));

        //Languages with copied translations
        generator.addProvider(true, new ModLanguageProvider(output, "de_at", "lang/de_de.json"));
        generator.addProvider(true, new ModLanguageProvider(output, "de_ch", "lang/de_de.json"));
        generator.addProvider(true, new ModLanguageProvider(output, "es_ar", "lang/es_mx.json"));
        generator.addProvider(true, new ModLanguageProvider(output, "es_cl", "lang/es_mx.json"));
        generator.addProvider(true, new ModLanguageProvider(output, "es_ec", "lang/es_mx.json"));
        generator.addProvider(true, new ModLanguageProvider(output, "es_uy", "lang/es_mx.json"));
        generator.addProvider(true, new ModLanguageProvider(output, "es_ve", "lang/es_mx.json"));

        generator.addProvider(true, new ModRecipeProvider(output, lookupProvider));
        generator.addProvider(true, ModLootTableProvider.create(output, lookupProvider));
        generator.addProvider(true, ModAdvancementProvider.create(output, lookupProvider));

        generator.addProvider(true, new ModBlockTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModItemTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModFluidTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModPoiTypeTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModBiomeTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModPaintingVariantTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModVillagerTradeTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModSoilTypeTagProvider(output, lookupProvider));
    }
}
