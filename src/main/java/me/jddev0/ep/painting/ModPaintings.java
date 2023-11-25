package me.jddev0.ep.painting;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModPaintings {
    private ModPaintings() {}

    public static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(BuiltInRegistries.PAINTING_VARIANT, EnergizedPowerMod.MODID);

    public static final Supplier<PaintingVariant> GEAR = PAINTINGS.register("gear", () -> new PaintingVariant(32, 32));
    public static final Supplier<PaintingVariant> FACTORY = PAINTINGS.register("factory", () -> new PaintingVariant(32, 32));

    public static void register(IEventBus modEventBus) {
        PAINTINGS.register(modEventBus);
    }
}
