package me.jddev0.ep.datagen;

import me.jddev0.ep.datagen.advancement.ModAdvancedAdvancements;
import me.jddev0.ep.datagen.advancement.ModBasicsAdvancements;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ModAdvancementProvider {
    public static void create(FabricDataGenerator pack) {
        pack.addProvider(ModBasicsAdvancements::new);
        pack.addProvider(ModAdvancedAdvancements::new);
    }
}
