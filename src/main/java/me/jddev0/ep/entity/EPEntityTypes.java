package me.jddev0.ep.entity;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class EPEntityTypes {
    private EPEntityTypes() {}

    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityType<T> registerEntity(String name, EntityType<? extends T> entityType) {
        return (EntityType<T>)Registry.register(Registries.ENTITY_TYPE, EPAPI.id(name), entityType);
    }

    public static final EntityType<MinecartBatteryBox> BATTERY_BOX_MINECART = registerEntity(
            "battery_box_minecart", EntityType.Builder.<MinecartBatteryBox>create(MinecartBatteryBox::new, SpawnGroup.MISC).
                    dimensions(0.98F, 0.7F).
                    maxTrackingRange(8).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, EPAPI.id("battery_box_minecart")))
    );
    public static final EntityType<MinecartAdvancedBatteryBox> ADVANCED_BATTERY_BOX_MINECART = registerEntity(
            "advanced_battery_box_minecart", EntityType.Builder.<MinecartAdvancedBatteryBox>create(MinecartAdvancedBatteryBox::new, SpawnGroup.MISC).
                    dimensions(0.98F, 0.7F).
                    maxTrackingRange(8).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, EPAPI.id("advanced_battery_box_minecart")))
    );


    public static void register() {

    }
}
