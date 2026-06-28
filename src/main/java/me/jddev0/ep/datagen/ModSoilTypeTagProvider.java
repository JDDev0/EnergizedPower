package me.jddev0.ep.datagen;

import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.EPSoilTypeTags;
import me.jddev0.ep.soil.EPSoilTypes;
import me.jddev0.ep.soil.SoilType;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModSoilTypeTagProvider extends FabricTagsProvider<SoilType> {
    public ModSoilTypeTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, EPRegistries.SOIL_TYPE, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        builder(EPSoilTypeTags.FLOWERS).
                add(EPSoilTypes.DIRT,
                        EPSoilTypes.COARSE_DIRT,
                        EPSoilTypes.GRASS,
                        EPSoilTypes.PODZOL);

        builder(EPSoilTypeTags.MUSHROOMS).
                add(EPSoilTypes.DIRT,
                        EPSoilTypes.COARSE_DIRT,
                        EPSoilTypes.GRASS,
                        EPSoilTypes.MYCELIUM,
                        EPSoilTypes.PODZOL,
                        EPSoilTypes.STONE);

        builder(EPSoilTypeTags.CROPS).
                add(EPSoilTypes.FARMLAND).
                add(EPSoilTypes.DIRT);

        builder(EPSoilTypeTags.WATER_CROPS).
                add(EPSoilTypes.DIRT,
                        EPSoilTypes.CLAY,
                        EPSoilTypes.SAND,
                        EPSoilTypes.GRAVEL);

        builder(EPSoilTypeTags.DESERT_CROPS).
                add(EPSoilTypes.SAND);

        builder(EPSoilTypeTags.NETHER_FLOWERS).
                add(EPSoilTypes.NETHERRACK,
                        EPSoilTypes.NYLIUM);

        builder(EPSoilTypeTags.NETHER_CROPS).
                add(EPSoilTypes.SOUL_SAND);

        builder(EPSoilTypeTags.END_CROPS).
                add(EPSoilTypes.END_STONE);

        builder(EPSoilTypeTags.SUGAR_CANE_CROPS).
                add(EPSoilTypes.DIRT,
                        EPSoilTypes.COARSE_DIRT,
                        EPSoilTypes.GRASS,
                        EPSoilTypes.PODZOL,
                        EPSoilTypes.MUD,
                        EPSoilTypes.MOSS,
                        EPSoilTypes.GRAVEL);
    }
}
