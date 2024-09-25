package me.jddev0.ep.entity;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModEntityTypes {
    private ModEntityTypes() {}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, EPAPI.MOD_ID);

    public static final Supplier<EntityType<MinecartBatteryBox>> BATTERY_BOX_MINECART =
            ENTITY_TYPES.register("battery_box_minecart",
                    () -> EntityType.Builder.<MinecartBatteryBox>of(MinecartBatteryBox::new, MobCategory.MISC).
                            sized(.98f, .7f).
                            clientTrackingRange(8).
                            build(EPAPI.id("battery_box_minecart").
                                    toString()));
    public static final Supplier<EntityType<MinecartAdvancedBatteryBox>> ADVANCED_BATTERY_BOX_MINECART =
            ENTITY_TYPES.register("advanced_battery_box_minecart",
                    () -> EntityType.Builder.<MinecartAdvancedBatteryBox>of(MinecartAdvancedBatteryBox::new, MobCategory.MISC).
                            sized(.98f, .7f).
                            clientTrackingRange(8).
                            build(EPAPI.id("advanced_battery_box_minecart").
                                    toString()));


    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
