package me.jddev0.ep.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class EnergizedPowerDataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator pack) {
        pack.addProvider(ModRecipeProvider::new);
        //TODO enable: pack.addProvider(ModBlockLootTables::new);

        pack.addProvider(ModModelProvider::new);

        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModItemTagProvider::new);
    }
}
