package me.jddev0.ep.paintings;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public final class EPPaintingVariants {
    private EPPaintingVariants() {}

    public static final ResourceKey<PaintingVariant> GEAR = registerKey("gear");
    public static final ResourceKey<PaintingVariant> FACTORY = registerKey("factory");


    public static void bootstrap(BootstrapContext<PaintingVariant> context) {
        register(context, GEAR, 2, 2);
        register(context, FACTORY, 2, 2);
    }

    public static ResourceKey<PaintingVariant> registerKey(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT,
                EPAPI.id(name));
    }

    private static void register(BootstrapContext<PaintingVariant> context, ResourceKey<PaintingVariant> key,
                                 int width, int height) {
        context.register(key, new PaintingVariant(width, height, key.location()));
    }
}