package me.jddev0.ep.villager;

import com.google.common.collect.ImmutableSet;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EPPoiTypes {
    private EPPoiTypes() {}

    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, EPAPI.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> ELECTRICIAN = POI_TYPES.register("electrician",
            () -> new PoiType(ImmutableSet.of(EPBlocks.BASIC_MACHINE_FRAME.get().defaultBlockState()), 1, 1));

    public static final ResourceKey<PoiType> ELECTRICIAN_KEY = poiKey("electrician");

    private static ResourceKey<PoiType> poiKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, EPAPI.id(name));
    }

    public static void register(IEventBus modEventBus) {
        POI_TYPES.register(modEventBus);
    }
}
