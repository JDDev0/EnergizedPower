package me.jddev0.ep.entity;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntityTypes {
    private ModEntityTypes() {}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EnergizedPowerMod.MODID);

    public static final RegistryObject<EntityType<MinecartBatteryBox>> BATTERY_BOX_MINECART =
            ENTITY_TYPES.register("battery_box_minecart",
                    () -> EntityType.Builder.<MinecartBatteryBox>of(MinecartBatteryBox::new, MobCategory.MISC).
                            sized(0.98F, 0.7F).
                            clientTrackingRange(8).
                            build(new ResourceLocation(EnergizedPowerMod.MODID, "battery_box_minecart").
                                    toString()));


    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
