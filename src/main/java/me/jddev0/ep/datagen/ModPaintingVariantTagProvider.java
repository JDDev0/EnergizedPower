package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.registry.paintings.EnergizedPowerPaintingVariants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModPaintingVariantTagProvider extends PaintingVariantTagsProvider {
    public ModPaintingVariantTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                         @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnergizedPowerMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(PaintingVariantTags.PLACEABLE).
                add(EnergizedPowerPaintingVariants.GEAR,
                        EnergizedPowerPaintingVariants.FACTORY);
    }
}
