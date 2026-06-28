package me.jddev0.ep.soil;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.registry.EPRegistries;
import net.minecraft.tags.TagKey;

public class EPSoilTypeTags {
    private EPSoilTypeTags() {}

    public static final TagKey<SoilType> FLOWERS = TagKey.create(EPRegistries.SOIL_TYPE, EPAPI.id("flowers"));
    public static final TagKey<SoilType> MUSHROOMS = TagKey.create(EPRegistries.SOIL_TYPE, EPAPI.id("mushrooms"));
    public static final TagKey<SoilType> CROPS = TagKey.create(EPRegistries.SOIL_TYPE, EPAPI.id("crops"));
    public static final TagKey<SoilType> WATER_CROPS = TagKey.create(EPRegistries.SOIL_TYPE, EPAPI.id("water_crops"));
    public static final TagKey<SoilType> DESERT_CROPS = TagKey.create(EPRegistries.SOIL_TYPE, EPAPI.id("desert_crops"));
    public static final TagKey<SoilType> NETHER_FLOWERS = TagKey.create(EPRegistries.SOIL_TYPE, EPAPI.id("nether_flowers"));
    public static final TagKey<SoilType> NETHER_CROPS = TagKey.create(EPRegistries.SOIL_TYPE, EPAPI.id("nether_crops"));
    public static final TagKey<SoilType> END_CROPS = TagKey.create(EPRegistries.SOIL_TYPE, EPAPI.id("end_crops"));

    public static final TagKey<SoilType> SUGAR_CANE_CROPS = TagKey.create(EPRegistries.SOIL_TYPE, EPAPI.id("sugar_cane_crops"));
}
