package me.jddev0.ep.registry;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.soil.SoilType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = EPAPI.MOD_ID)
public final class EPRegistries {
    private EPRegistries() {}

    public static final ResourceKey<Registry<SoilType>> SOIL_TYPE = ResourceKey.createRegistryKey(EPAPI.id("soil_type"));

    @SubscribeEvent
    public static void onRegisterDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(SOIL_TYPE, SoilType.CODEC, SoilType.CODEC);
    }
}
