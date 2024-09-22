package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.registry.tags.EnergizedPowerBiomeTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBiomeTagProvider extends BiomeTagsProvider {
    public ModBiomeTagProvider(DataGenerator output, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, EnergizedPowerMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(EnergizedPowerBiomeTags.HAS_STRUCTURE_FACTORY_1).
                add(Biomes.FOREST,
                        Biomes.FLOWER_FOREST);

        tag(EnergizedPowerBiomeTags.HAS_STRUCTURE_SMALL_SOLAR_FARM).
                add(Biomes.DESERT,
                        Biomes.BADLANDS,
                        Biomes.SAVANNA);
    }
}
