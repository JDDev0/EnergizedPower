package me.jddev0.ep.fluid;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

import java.util.function.Supplier;

public final class EPFluidTypes {
    private EPFluidTypes() {}

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, EPAPI.MOD_ID);

    public static final Supplier<EnergizedPowerFluidType> DIRTY_WATER_FLUID_TYPE = FLUID_TYPES.register("dirty_water",
            () -> new EnergizedPowerFluidType(FluidType.Properties.create().density(1200).viscosity(1200).canExtinguish(true),
                    Identifier.withDefaultNamespace("block/water_still"), Identifier.withDefaultNamespace("block/water_flow"),
                    null, 0xC86F3900, new Vector3f(100.f / 255.f, 50.f / 255.f, 0.f)));

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }
}
