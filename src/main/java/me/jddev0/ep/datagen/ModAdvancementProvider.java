package me.jddev0.ep.datagen;

import me.jddev0.ep.datagen.adavancement.ModBasicsAdvancements;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModAdvancementProvider {
    public static ForgeAdvancementProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                                  ExistingFileHelper existingFileHelper) {
        return new ForgeAdvancementProvider(output, lookupProvider, existingFileHelper, List.of(
                new ModBasicsAdvancements()
        ));
    }
}
