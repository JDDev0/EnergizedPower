package me.jddev0.ep.datagen;

import me.jddev0.ep.villager.EPVillager;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.concurrent.CompletableFuture;

public class ModPoiTypeTagProvider extends FabricTagProvider<PointOfInterestType> {
    public ModPoiTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(output, RegistryKeys.POINT_OF_INTEREST_TYPE, lookupProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookupProvider) {
        getOrCreateTagBuilder(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE).
                add(EPVillager.BASIC_MACHINE_FRAME_POI);
    }
}
