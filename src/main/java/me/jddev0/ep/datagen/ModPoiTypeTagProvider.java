package me.jddev0.ep.datagen;

import me.jddev0.ep.villager.EPPoiTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.concurrent.CompletableFuture;

public class ModPoiTypeTagProvider extends FabricTagsProvider<PoiType> {
    public ModPoiTypeTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.POINT_OF_INTEREST_TYPE, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        builder(PoiTypeTags.ACQUIRABLE_JOB_SITE).
                add(EPPoiTypes.ELECTRICIAN_KEY);
    }
}
