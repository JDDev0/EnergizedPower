package me.jddev0.ep.paintings;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class EPPaintings {
    private EPPaintings() {}

    public static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(BuiltInRegistries.PAINTING_VARIANT, EPAPI.MOD_ID);

    public static final Supplier<PaintingVariant> GEAR = PAINTINGS.register("gear", () -> new PaintingVariant(32, 32));
    public static final Supplier<PaintingVariant> FACTORY = PAINTINGS.register("factory", () -> new PaintingVariant(32, 32));

    public static void register(IEventBus modEventBus) {
        PAINTINGS.register(modEventBus);
    }
}
