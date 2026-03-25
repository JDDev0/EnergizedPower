package me.jddev0.ep.entity;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class EPEntityTypes {
    private EPEntityTypes() {}

    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityType<T> registerEntity(String name, EntityType<? extends T> entityType) {
        return (EntityType<T>)Registry.register(BuiltInRegistries.ENTITY_TYPE, EPAPI.id(name), entityType);
    }

    public static final EntityType<MinecartBatteryBox> BATTERY_BOX_MINECART = registerEntity(
            "battery_box_minecart", EntityType.Builder.<MinecartBatteryBox>of(MinecartBatteryBox::new, MobCategory.MISC).
                    sized(0.98F, 0.7F).
                    clientTrackingRange(8).build(ResourceKey.create(Registries.ENTITY_TYPE, EPAPI.id("battery_box_minecart")))
    );
    public static final EntityType<MinecartAdvancedBatteryBox> ADVANCED_BATTERY_BOX_MINECART = registerEntity(
            "advanced_battery_box_minecart", EntityType.Builder.<MinecartAdvancedBatteryBox>of(MinecartAdvancedBatteryBox::new, MobCategory.MISC).
                    sized(0.98F, 0.7F).
                    clientTrackingRange(8).build(ResourceKey.create(Registries.ENTITY_TYPE, EPAPI.id("advanced_battery_box_minecart")))
    );


    public static void register() {

    }
}
