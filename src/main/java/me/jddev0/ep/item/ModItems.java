package me.jddev0.ep.item;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EnergizedPowerMod.MODID);

    public static final RegistryObject<Item> ENERGIZED_COPPER_INGOT = ITEMS.register("energized_copper_ingot",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SILICON = ITEMS.register("silicon",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BASIC_SOLAR_CELL = ITEMS.register("basic_solar_cell",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ADVANCED_SOLAR_CELL = ITEMS.register("advanced_solar_cell",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ENERGIZED_POWER_BOOK = ITEMS.register("energized_power_book",
            () -> new EnergizedPowerBookItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
