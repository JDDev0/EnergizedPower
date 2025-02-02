package me.jddev0.ep.datagen;

import me.jddev0.ep.datagen.advancement.ModAdvancedAdvancements;
import me.jddev0.ep.datagen.advancement.ModBasicsAdvancements;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModAdvancementProvider {
    public static AdvancementProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        return new AdvancementProvider(output, lookupProvider, List.of(
                new ModBasicsAdvancements(),
                new ModAdvancedAdvancements()
        ));
    }
}
