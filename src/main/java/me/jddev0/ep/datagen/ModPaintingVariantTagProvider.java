package me.jddev0.ep.datagen;

import me.jddev0.ep.paintings.ModPaintings;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.tag.PaintingVariantTags;
import net.minecraft.util.registry.Registry;

public class ModPaintingVariantTagProvider extends FabricTagProvider<PaintingVariant> {
    public ModPaintingVariantTagProvider(FabricDataGenerator output) {
        super(output, Registry.PAINTING_VARIANT);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(PaintingVariantTags.PLACEABLE).
                add(ModPaintings.GEAR,
                        ModPaintings.FACTORY);
    }
}
