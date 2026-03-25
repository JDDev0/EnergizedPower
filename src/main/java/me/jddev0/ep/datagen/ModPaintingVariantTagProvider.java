package me.jddev0.ep.datagen;

import me.jddev0.ep.paintings.EPPaintingVariants;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;

import java.util.concurrent.CompletableFuture;

public class ModPaintingVariantTagProvider extends FabricTagProvider<PaintingVariant> {
    public ModPaintingVariantTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.PAINTING_VARIANT, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        builder(PaintingVariantTags.PLACEABLE).
                add(EPPaintingVariants.GEAR,
                        EPPaintingVariants.FACTORY);
    }
}
