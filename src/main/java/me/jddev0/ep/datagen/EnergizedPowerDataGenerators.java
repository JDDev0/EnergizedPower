package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = EnergizedPowerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EnergizedPowerDataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new ModRecipeProvider(output, lookupProvider));
        //TODO enable: generator.addProvider(event.includeServer(), ModLootTableProvider.create(output, lookupProvider));

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, existingFileHelper));
        //TODO add custom generator for book pages (Builder and Provider base class)

        ModBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(),
                new ModBlockTagProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModItemTagProvider(output, lookupProvider,
                blockTagProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new ModPoiTypeTagProvider(output, lookupProvider,
                existingFileHelper));
        generator.addProvider(event.includeServer(), new ModPaintingVariantTagProvider(output, lookupProvider,
                existingFileHelper));
    }
}
