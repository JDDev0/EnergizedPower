package me.jddev0.ep.datagen;

import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.EPSoilTypeTags;
import me.jddev0.ep.soil.EPSoilTypes;
import me.jddev0.ep.soil.SoilType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModSoilTypeTagProvider extends FabricTagProvider<SoilType> {
    public ModSoilTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, EPRegistries.SOIL_TYPE, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(EPSoilTypeTags.FLOWERS).
                add(EPSoilTypes.DIRT,
                        EPSoilTypes.COARSE_DIRT,
                        EPSoilTypes.GRASS,
                        EPSoilTypes.PODZOL);

        tag(EPSoilTypeTags.MUSHROOMS).
                add(EPSoilTypes.DIRT,
                        EPSoilTypes.COARSE_DIRT,
                        EPSoilTypes.GRASS,
                        EPSoilTypes.MYCELIUM,
                        EPSoilTypes.PODZOL,
                        EPSoilTypes.STONE);

        tag(EPSoilTypeTags.CROPS).
                add(EPSoilTypes.FARMLAND).
                add(EPSoilTypes.DIRT);

        tag(EPSoilTypeTags.WATER_CROPS).
                add(EPSoilTypes.DIRT,
                        EPSoilTypes.SAND,
                        EPSoilTypes.GRAVEL);

        tag(EPSoilTypeTags.DESERT_CROPS).
                add(EPSoilTypes.SAND);

        tag(EPSoilTypeTags.NETHER_CROPS).
                add(EPSoilTypes.SOUL_SAND);

        tag(EPSoilTypeTags.END_CROPS).
                add(EPSoilTypes.END_STONE);

        tag(EPSoilTypeTags.SUGAR_CANE_CROPS).
                add(EPSoilTypes.DIRT,
                        EPSoilTypes.COARSE_DIRT,
                        EPSoilTypes.GRASS,
                        EPSoilTypes.PODZOL,
                        EPSoilTypes.MUD,
                        EPSoilTypes.MOSS,
                        EPSoilTypes.GRAVEL);
    }
}
