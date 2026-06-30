package me.jddev0.ep.fluid;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
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

    public static final Supplier<EnergizedPowerFluidType> LIQUID_XP_FLUID_TYPE = FLUID_TYPES.register("liquid_xp",
            () -> new EnergizedPowerFluidType(FluidType.Properties.create().lightLevel(10).density(3000).viscosity(6000).canExtinguish(false).canSwim(false).canDrown(false),
                    EPAPI.id("block/liquid_xp_still"), EPAPI.id("block/liquid_xp_flow"),
                    null, 0x00000000, new Vector3f(100.f / 255.f, 50.f / 255.f, 0.f)) {
                @Override
                public double motionScale(Entity entity) {
                    return 0.0023333333333333335;
                }
            });

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }
}
