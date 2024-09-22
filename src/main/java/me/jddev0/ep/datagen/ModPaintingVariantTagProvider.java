package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.registry.paintings.EnergizedPowerPaintingVariants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModPaintingVariantTagProvider extends PaintingVariantTagsProvider {
    public ModPaintingVariantTagProvider(DataGenerator output, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, EnergizedPowerMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(PaintingVariantTags.PLACEABLE).
                add(EnergizedPowerPaintingVariants.GEAR,
                        EnergizedPowerPaintingVariants.FACTORY);
    }
}
