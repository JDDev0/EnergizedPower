package me.jddev0.ep.registry.paintings;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public final class ModPaintingVariants {
    private ModPaintingVariants() {}

    public static final ResourceKey<PaintingVariant> GEAR = ResourceKey.create(Registries.PAINTING_VARIANT,
            EPAPI.id("gear"));

    public static final ResourceKey<PaintingVariant> FACTORY = ResourceKey.create(Registries.PAINTING_VARIANT,
            EPAPI.id("factory"));
}