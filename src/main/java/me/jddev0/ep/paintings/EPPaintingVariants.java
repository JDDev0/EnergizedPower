package me.jddev0.ep.paintings;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

import java.util.Optional;

public final class EPPaintingVariants {
    private EPPaintingVariants() {}

    public static final RegistryKey<PaintingVariant> GEAR = registerKey("gear");
    public static final RegistryKey<PaintingVariant> FACTORY = registerKey("factory");

    public static void bootstrap(Registerable<PaintingVariant> context) {
        register(context, GEAR, 2, 2);
        register(context, FACTORY, 2, 2);
    }

    public static RegistryKey<PaintingVariant> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PAINTING_VARIANT,
                EPAPI.id(name));
    }

    private static void register(Registerable<PaintingVariant> context, RegistryKey<PaintingVariant> key,
                                 int width, int height) {
        context.register(key, new PaintingVariant(width, height, key.getValue(),
                Optional.of(Text.translatable("painting.energizedpower." + key.getValue().getPath() + ".title")),
                Optional.of(Text.translatable("painting.energizedpower." + key.getValue().getPath() + ".author"))));
    }
}
