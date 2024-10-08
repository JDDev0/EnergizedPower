package me.jddev0.ep.paintings;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class EPPaintings {
    private EPPaintings() {}

    public static PaintingVariant registerPainting(String name, PaintingVariant paintingVariant) {
        return Registry.register(Registries.PAINTING_VARIANT, EPAPI.id(name), paintingVariant);
    }

    public static final PaintingVariant GEAR = registerPainting("gear", new PaintingVariant(32, 32));
    public static final PaintingVariant FACTORY = registerPainting("factory", new PaintingVariant(32, 32));

    public static void register() {

    }
}
