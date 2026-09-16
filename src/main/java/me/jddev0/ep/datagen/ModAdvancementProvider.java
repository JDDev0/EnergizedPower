package me.jddev0.ep.datagen;

import me.jddev0.ep.datagen.advancement.ModAdvancedAdvancements;
import me.jddev0.ep.datagen.advancement.ModBasicsAdvancements;
import me.jddev0.ep.datagen.advancement.ModEliteAdvancements;
import net.minecraft.data.advancements.AdvancementProvider;

import java.util.List;

public class ModAdvancementProvider {
    public static AdvancementProvider create() {
        return new AdvancementProvider(List.of(
                ModBasicsAdvancements::new,
                ModAdvancedAdvancements::new,
                ModEliteAdvancements::new
        ));
    }
}
