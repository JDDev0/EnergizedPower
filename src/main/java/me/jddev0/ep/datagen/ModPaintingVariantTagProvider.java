package me.jddev0.ep.datagen;

import me.jddev0.ep.paintings.ModPaintings;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.PaintingVariantTags;

import java.util.concurrent.CompletableFuture;

public class ModPaintingVariantTagProvider extends FabricTagProvider<PaintingVariant> {
    public ModPaintingVariantTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(output, RegistryKeys.PAINTING_VARIANT, lookupProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookupProvider) {
        getOrCreateTagBuilder(PaintingVariantTags.PLACEABLE).
                add(ModPaintings.GEAR,
                        ModPaintings.FACTORY);
    }
}
