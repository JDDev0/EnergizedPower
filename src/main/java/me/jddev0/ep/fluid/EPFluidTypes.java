package me.jddev0.ep.fluid;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

public final class EPFluidTypes {
    private EPFluidTypes() {}

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, EPAPI.MOD_ID);

    public static RegistryObject<FluidType> DIRTY_WATER_FLUID_TYPE = FLUID_TYPES.register("dirty_water",
            () -> new EnergizedPowerFluidType(FluidType.Properties.create().density(1200).viscosity(1200).canExtinguish(true),
                    new ResourceLocation("block/water_still"), new ResourceLocation("block/water_flow"),
                    null, 0xC86F3900, new Vector3f(100.f / 255.f, 50.f / 255.f, 0.f)));

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }
}
