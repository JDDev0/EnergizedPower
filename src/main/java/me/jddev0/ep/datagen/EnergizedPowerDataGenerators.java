package me.jddev0.ep.datagen;

import me.jddev0.ep.datagen.loot.ModBlockLootTables;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class EnergizedPowerDataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator pack) {
        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModBlockLootTables::new);
        ModAdvancementProvider.create(pack);

        pack.addProvider(ModModelProvider::new);

        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModItemTagProvider::new);
        pack.addProvider(ModPoiTypeTagProvider::new);
        pack.addProvider(ModPaintingVariantTagProvider::new);
    }
}
