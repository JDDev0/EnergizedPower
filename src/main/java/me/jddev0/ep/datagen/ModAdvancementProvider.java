package me.jddev0.ep.datagen;

import me.jddev0.ep.datagen.advancement.ModBasicsAdvancements;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ModAdvancementProvider {
    public static void create(FabricDataGenerator.Pack pack) {
        pack.addProvider(ModBasicsAdvancements::new);
    }
}
