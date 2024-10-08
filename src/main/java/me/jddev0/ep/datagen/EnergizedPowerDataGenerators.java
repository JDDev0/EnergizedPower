package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EPAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EnergizedPowerDataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModBookPageContentProvider(generator, existingFileHelper));

        generator.addProvider(event.includeServer(), new ModRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new ModLootTableProvider(generator));
        generator.addProvider(event.includeServer(), new ModAdvancementProvider(generator, existingFileHelper));

        ModBlockTagProvider blockTagProvider = new ModBlockTagProvider(generator, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagProvider);
        generator.addProvider(event.includeServer(), new ModItemTagProvider(generator, blockTagProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModPoiTypeTagProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModBiomeTagProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModPaintingVariantTagProvider(generator, existingFileHelper));
    }
}
