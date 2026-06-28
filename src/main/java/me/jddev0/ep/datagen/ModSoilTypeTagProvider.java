package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.EPSoilTypeTags;
import me.jddev0.ep.soil.EPSoilTypes;
import me.jddev0.ep.soil.SoilType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;

import java.util.concurrent.CompletableFuture;

public class ModSoilTypeTagProvider extends KeyTagProvider<SoilType> {
    public ModSoilTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, EPRegistries.SOIL_TYPE, lookupProvider, EPAPI.MOD_ID);
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
                add(EPSoilTypes.FARMLAND,
                        EPSoilTypes.DIRT);

        tag(EPSoilTypeTags.WATER_CROPS).
                add(EPSoilTypes.DIRT,
                        EPSoilTypes.CLAY,
                        EPSoilTypes.SAND,
                        EPSoilTypes.GRAVEL);

        tag(EPSoilTypeTags.DESERT_CROPS).
                add(EPSoilTypes.SAND);

        tag(EPSoilTypeTags.NETHER_FLOWERS).
                add(EPSoilTypes.NETHERRACK,
                        EPSoilTypes.NYLIUM);

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
