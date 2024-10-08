package me.jddev0.ep.paintings;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class EPPaintings {
    private EPPaintings() {}

    public static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, EPAPI.MOD_ID);

    public static final RegistryObject<PaintingVariant> GEAR = PAINTINGS.register("gear", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> FACTORY = PAINTINGS.register("factory", () -> new PaintingVariant(32, 32));

    public static void register(IEventBus modEventBus) {
        PAINTINGS.register(modEventBus);
    }
}
