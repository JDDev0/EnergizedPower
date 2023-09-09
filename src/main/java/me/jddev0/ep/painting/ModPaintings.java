package me.jddev0.ep.painting;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ModPaintings {
    private ModPaintings() {}

    public static PaintingVariant registerPainting(String name, PaintingVariant paintingVariant) {
        return Registry.register(Registry.PAINTING_VARIANT, new Identifier(EnergizedPowerMod.MODID, name), paintingVariant);
    }

    public static final PaintingVariant GEAR = registerPainting("gear", new PaintingVariant(32, 32));

    public static void register() {

    }
}
