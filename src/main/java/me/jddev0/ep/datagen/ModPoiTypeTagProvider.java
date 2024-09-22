package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.villager.ModVillager;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.tags.PoiTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ModPoiTypeTagProvider extends PoiTypeTagsProvider {
    public ModPoiTypeTagProvider(DataGenerator output, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, EnergizedPowerMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).
                add(Objects.requireNonNull(ModVillager.BASIC_MACHINE_FRAME_POI.getKey()));
    }
}
