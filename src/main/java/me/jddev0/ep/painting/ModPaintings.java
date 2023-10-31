package me.jddev0.ep.painting;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public final class ModPaintings {
    private ModPaintings() {}

    public static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, EnergizedPowerMod.MODID);

    public static final RegistryObject<PaintingVariant> GEAR = PAINTINGS.register("gear", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> FACTORY = PAINTINGS.register("factory", () -> new PaintingVariant(32, 32));

    public static void register(IEventBus modEventBus) {
        PAINTINGS.register(modEventBus);
    }
}
