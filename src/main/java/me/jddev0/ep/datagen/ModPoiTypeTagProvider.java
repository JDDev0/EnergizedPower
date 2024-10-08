package me.jddev0.ep.datagen;

import me.jddev0.ep.villager.EPVillager;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tag.PointOfInterestTypeTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

public class ModPoiTypeTagProvider extends FabricTagProvider<PointOfInterestType> {
    public ModPoiTypeTagProvider(FabricDataGenerator output) {
        super(output, Registry.POINT_OF_INTEREST_TYPE);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE).
                add(EPVillager.BASIC_MACHINE_FRAME_POI);
    }
}
