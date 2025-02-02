package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = EPAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
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

        generator.addProvider(true, new ModRecipeProvider(output, lookupProvider));
        generator.addProvider(true, ModLootTableProvider.create(output, lookupProvider));
        generator.addProvider(true, ModAdvancementProvider.create(output, lookupProvider));

        ModBlockTagProvider blockTagProvider = generator.addProvider(true,
                new ModBlockTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModItemTagProvider(output, lookupProvider,
                blockTagProvider.contentsGetter()));
        generator.addProvider(true, new ModPoiTypeTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModBiomeTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModPaintingVariantTagProvider(output, lookupProvider));
    }
}
