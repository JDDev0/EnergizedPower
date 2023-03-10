package me.jddev0.ep.item;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    private ModItems() {}
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EnergizedPowerMod.MODID);

    public static final RegistryObject<Item> ENERGIZED_COPPER_INGOT = ITEMS.register("energized_copper_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ENERGIZED_GOLD_INGOT = ITEMS.register("energized_gold_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SILICON = ITEMS.register("silicon",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SAWDUST = ITEMS.register("sawdust",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BASIC_SOLAR_CELL = ITEMS.register("basic_solar_cell",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ADVANCED_SOLAR_CELL = ITEMS.register("advanced_solar_cell",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ENERGIZED_POWER_BOOK = ITEMS.register("energized_power_book",
            () -> new EnergizedPowerBookItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CABLE_INSULATOR = ITEMS.register("cable_insulator",
            () -> new CableInsulatorItem(new Item.Properties()));

    public static final RegistryObject<Item> SAW_BLADE = ITEMS.register("saw_blade",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> INVENTORY_COAL_ENGINE = ITEMS.register("inventory_coal_engine",
            () -> new InventoryCoalEngine(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BATTERY_1 = ITEMS.register("battery_1",
            () -> new BatteryItem(BatteryItem.Tier.BATTERY_1));
    public static final RegistryObject<Item> BATTERY_2 = ITEMS.register("battery_2",
            () -> new BatteryItem(BatteryItem.Tier.BATTERY_2));
    public static final RegistryObject<Item> BATTERY_3 = ITEMS.register("battery_3",
            () -> new BatteryItem(BatteryItem.Tier.BATTERY_3));
    public static final RegistryObject<Item> BATTERY_4 = ITEMS.register("battery_4",
            () -> new BatteryItem(BatteryItem.Tier.BATTERY_4));
    public static final RegistryObject<Item> BATTERY_5 = ITEMS.register("battery_5",
            () -> new BatteryItem(BatteryItem.Tier.BATTERY_5));
    public static final RegistryObject<Item> BATTERY_6 = ITEMS.register("battery_6",
            () -> new BatteryItem(BatteryItem.Tier.BATTERY_6));
    public static final RegistryObject<Item> BATTERY_7 = ITEMS.register("battery_7",
            () -> new BatteryItem(BatteryItem.Tier.BATTERY_7));
    public static final RegistryObject<Item> BATTERY_8 = ITEMS.register("battery_8",
            () -> new BatteryItem(BatteryItem.Tier.BATTERY_8));

    public static final RegistryObject<Item> ENERGY_ANALYZER = ITEMS.register("energy_analyzer",
            () -> new EnergyAnalyzerItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
