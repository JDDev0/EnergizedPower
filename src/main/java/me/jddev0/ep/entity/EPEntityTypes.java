package me.jddev0.ep.entity;

import me.jddev0.ep.api.EPAPI;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class EPEntityTypes {
    private EPEntityTypes() {}

    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityType<T> registerEntity(String name, EntityType<? extends T> entityType) {
        return (EntityType<T>)Registry.register(BuiltInRegistries.ENTITY_TYPE, EPAPI.id(name), entityType);
    }

    public static final EntityType<MinecartBatteryBox> BATTERY_BOX_MINECART = registerEntity(
            "battery_box_minecart", FabricEntityTypeBuilder.<MinecartBatteryBox>create(MobCategory.MISC,
                            MinecartBatteryBox::new).
                    dimensions(EntityDimensions.fixed(0.98F, 0.7F)).
                    trackRangeChunks(8).build()
    );
    public static final EntityType<MinecartAdvancedBatteryBox> ADVANCED_BATTERY_BOX_MINECART = registerEntity(
            "advanced_battery_box_minecart", FabricEntityTypeBuilder.<MinecartAdvancedBatteryBox>create(MobCategory.MISC,
                            MinecartAdvancedBatteryBox::new).
                    dimensions(EntityDimensions.fixed(0.98F, 0.7F)).
                    trackRangeChunks(8).build()
    );


    public static void register() {

    }
}
