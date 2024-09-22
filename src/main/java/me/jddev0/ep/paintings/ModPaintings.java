package me.jddev0.ep.paintings;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModPaintings {
    private ModPaintings() {}

    public static PaintingVariant registerPainting(String name, PaintingVariant paintingVariant) {
        return Registry.register(Registries.PAINTING_VARIANT, new Identifier(EnergizedPowerMod.MODID, name), paintingVariant);
    }

    public static final PaintingVariant GEAR = registerPainting("gear", new PaintingVariant(32, 32));
    public static final PaintingVariant FACTORY = registerPainting("factory", new PaintingVariant(32, 32));

    public static void register() {

    }
}
