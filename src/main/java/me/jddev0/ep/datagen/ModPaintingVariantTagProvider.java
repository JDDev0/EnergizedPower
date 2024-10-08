package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.paintings.EPPaintingVariants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.tags.PaintingVariantTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModPaintingVariantTagProvider extends PaintingVariantTagsProvider {
    public ModPaintingVariantTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                         @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EPAPI.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(PaintingVariantTags.PLACEABLE).
                add(EPPaintingVariants.GEAR,
                        EPPaintingVariants.FACTORY);
    }
}
