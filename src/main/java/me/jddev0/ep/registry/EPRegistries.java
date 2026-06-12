package me.jddev0.ep.registry;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.soil.SoilType;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class EPRegistries {
    private EPRegistries() {}

    public static final ResourceKey<Registry<SoilType>> SOIL_TYPE = ResourceKey.createRegistryKey(EPAPI.id("soil_type"));

    public static void register() {
        DynamicRegistries.registerSynced(SOIL_TYPE, SoilType.DIRECT_CODEC, SoilType.DIRECT_CODEC);
    }
}
