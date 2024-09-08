package me.jddev0.ep.datagen;

import me.jddev0.ep.datagen.adavancement.ModBasicsAdvancements;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModAdvancementProvider {
    public static AdvancementProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                           ExistingFileHelper existingFileHelper) {
        return new AdvancementProvider(output, lookupProvider, existingFileHelper, List.of(
                new ModBasicsAdvancements()
        ));
    }
}
