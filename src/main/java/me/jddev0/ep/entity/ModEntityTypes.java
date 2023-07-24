package me.jddev0.ep.entity;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ModEntityTypes {
    private ModEntityTypes() {}

    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityType<T> registerEntity(String name, EntityType<? extends T> entityType) {
        return (EntityType<T>) Registry.register(Registry.ENTITY_TYPE, new Identifier(EnergizedPowerMod.MODID, name), entityType);
    }

    public static final EntityType<MinecartBatteryBox> BATTERY_BOX_MINECART = registerEntity(
            "battery_box_minecart", FabricEntityTypeBuilder.<MinecartBatteryBox>create(SpawnGroup.MISC,
                            MinecartBatteryBox::new).
                    dimensions(EntityDimensions.fixed(0.98F, 0.7F)).
                    trackRangeChunks(8).build()
    );
    public static final EntityType<MinecartAdvancedBatteryBox> ADVANCED_BATTERY_BOX_MINECART = registerEntity(
            "advanced_battery_box_minecart", FabricEntityTypeBuilder.<MinecartAdvancedBatteryBox>create(SpawnGroup.MISC,
                            MinecartAdvancedBatteryBox::new).
                    dimensions(EntityDimensions.fixed(0.98F, 0.7F)).
                    trackRangeChunks(8).build()
    );


    public static void register() {

    }
}
