package me.jddev0.ep.item;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    private ModItems() {}
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EnergizedPowerMod.MODID);

    public static final RegistryObject<Item> ENERGIZED_COPPER_INGOT = ITEMS.register("energized_copper_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> ENERGIZED_GOLD_INGOT = ITEMS.register("energized_gold_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> ENERGIZED_COPPER_PLATE = ITEMS.register("energized_copper_plate",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> ENERGIZED_GOLD_PLATE = ITEMS.register("energized_gold_plate",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> ENERGIZED_COPPER_WIRE = ITEMS.register("energized_copper_wire",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> ENERGIZED_GOLD_WIRE = ITEMS.register("energized_gold_wire",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> SILICON = ITEMS.register("silicon",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> RAW_TIN = ITEMS.register("raw_tin",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> TIN_DUST = ITEMS.register("tin_dust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> COPPER_DUST = ITEMS.register("copper_dust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> IRON_DUST = ITEMS.register("iron_dust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> GOLD_DUST = ITEMS.register("gold_dust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> TIN_NUGGET = ITEMS.register("tin_nugget",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> TIN_PLATE = ITEMS.register("tin_plate",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> COPPER_PLATE = ITEMS.register("copper_plate",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> IRON_PLATE = ITEMS.register("iron_plate",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> GOLD_PLATE = ITEMS.register("gold_plate",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> IRON_GEAR = ITEMS.register("iron_gear",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> IRON_ROD = ITEMS.register("iron_rod",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> TIN_WIRE = ITEMS.register("tin_wire",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> COPPER_WIRE = ITEMS.register("copper_wire",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> GOLD_WIRE = ITEMS.register("gold_wire",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> SAWDUST = ITEMS.register("sawdust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> CHARCOAL_DUST = ITEMS.register("charcoal_dust",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> BASIC_FERTILIZER = ITEMS.register("basic_fertilizer",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> GOOD_FERTILIZER = ITEMS.register("good_fertilizer",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> ADVANCED_FERTILIZER = ITEMS.register("advanced_fertilizer",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> RAW_GEAR_PRESS_MOLD = ITEMS.register("raw_gear_press_mold",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> RAW_ROD_PRESS_MOLD = ITEMS.register("raw_rod_press_mold",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> RAW_WIRE_PRESS_MOLD = ITEMS.register("raw_wire_press_mold",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> GEAR_PRESS_MOLD = ITEMS.register("gear_press_mold",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).durability(2000)));
    public static final RegistryObject<Item> ROD_PRESS_MOLD = ITEMS.register("rod_press_mold",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).durability(2000)));
    public static final RegistryObject<Item> WIRE_PRESS_MOLD = ITEMS.register("wire_press_mold",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).durability(2000)));

    public static final RegistryObject<Item> BASIC_SOLAR_CELL = ITEMS.register("basic_solar_cell",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> ADVANCED_SOLAR_CELL = ITEMS.register("advanced_solar_cell",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> REINFORCED_ADVANCED_SOLAR_CELL = ITEMS.register("reinforced_advanced_solar_cell",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> BASIC_CIRCUIT = ITEMS.register("basic_circuit",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> ADVANCED_CIRCUIT = ITEMS.register("advanced_circuit",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> PROCESSING_UNIT = ITEMS.register("processing_unit",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> TELEPORTER_MATRIX = ITEMS.register("teleporter_matrix",
            () -> new TeleporterMatrixItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> TELEPORTER_PROCESSING_UNIT = ITEMS.register("teleporter_processing_unit",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> BASIC_UPGRADE_MODULE = ITEMS.register("basic_upgrade_module",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> ADVANCED_UPGRADE_MODULE = ITEMS.register("advanced_upgrade_module",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> REINFORCED_ADVANCED_UPGRADE_MODULE = ITEMS.register("reinforced_advanced_upgrade_module",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> SPEED_UPGRADE_MODULE_1 = ITEMS.register("speed_upgrade_module_1",
            () -> new SpeedUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 1));
    public static final RegistryObject<Item> SPEED_UPGRADE_MODULE_2 = ITEMS.register("speed_upgrade_module_2",
            () -> new SpeedUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 2));
    public static final RegistryObject<Item> SPEED_UPGRADE_MODULE_3 = ITEMS.register("speed_upgrade_module_3",
            () -> new SpeedUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 3));
    public static final RegistryObject<Item> SPEED_UPGRADE_MODULE_4 = ITEMS.register("speed_upgrade_module_4",
            () -> new SpeedUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 4));
    public static final RegistryObject<Item> SPEED_UPGRADE_MODULE_5 = ITEMS.register("speed_upgrade_module_5",
            () -> new SpeedUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 5));

    public static final RegistryObject<Item> ENERGY_EFFICIENCY_UPGRADE_MODULE_1 = ITEMS.register("energy_efficiency_upgrade_module_1",
            () -> new EnergyEfficiencyUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 1));
    public static final RegistryObject<Item> ENERGY_EFFICIENCY_UPGRADE_MODULE_2 = ITEMS.register("energy_efficiency_upgrade_module_2",
            () -> new EnergyEfficiencyUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 2));
    public static final RegistryObject<Item> ENERGY_EFFICIENCY_UPGRADE_MODULE_3 = ITEMS.register("energy_efficiency_upgrade_module_3",
            () -> new EnergyEfficiencyUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 3));
    public static final RegistryObject<Item> ENERGY_EFFICIENCY_UPGRADE_MODULE_4 = ITEMS.register("energy_efficiency_upgrade_module_4",
            () -> new EnergyEfficiencyUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 4));
    public static final RegistryObject<Item> ENERGY_EFFICIENCY_UPGRADE_MODULE_5 = ITEMS.register("energy_efficiency_upgrade_module_5",
            () -> new EnergyEfficiencyUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 5));

    public static final RegistryObject<Item> ENERGY_CAPACITY_UPGRADE_MODULE_1 = ITEMS.register("energy_capacity_upgrade_module_1",
            () -> new EnergyCapacityUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 1));
    public static final RegistryObject<Item> ENERGY_CAPACITY_UPGRADE_MODULE_2 = ITEMS.register("energy_capacity_upgrade_module_2",
            () -> new EnergyCapacityUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 2));
    public static final RegistryObject<Item> ENERGY_CAPACITY_UPGRADE_MODULE_3 = ITEMS.register("energy_capacity_upgrade_module_3",
            () -> new EnergyCapacityUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 3));
    public static final RegistryObject<Item> ENERGY_CAPACITY_UPGRADE_MODULE_4 = ITEMS.register("energy_capacity_upgrade_module_4",
            () -> new EnergyCapacityUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 4));
    public static final RegistryObject<Item> ENERGY_CAPACITY_UPGRADE_MODULE_5 = ITEMS.register("energy_capacity_upgrade_module_5",
            () -> new EnergyCapacityUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 5));

    public static final RegistryObject<Item> DURATION_UPGRADE_MODULE_1 = ITEMS.register("duration_upgrade_module_1",
            () -> new DurationUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 1));
    public static final RegistryObject<Item> DURATION_UPGRADE_MODULE_2 = ITEMS.register("duration_upgrade_module_2",
            () -> new DurationUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 2));
    public static final RegistryObject<Item> DURATION_UPGRADE_MODULE_3 = ITEMS.register("duration_upgrade_module_3",
            () -> new DurationUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 3));
    public static final RegistryObject<Item> DURATION_UPGRADE_MODULE_4 = ITEMS.register("duration_upgrade_module_4",
            () -> new DurationUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 4));
    public static final RegistryObject<Item> DURATION_UPGRADE_MODULE_5 = ITEMS.register("duration_upgrade_module_5",
            () -> new DurationUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 5));

    public static final RegistryObject<Item> RANGE_UPGRADE_MODULE_1 = ITEMS.register("range_upgrade_module_1",
            () -> new RangeUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 1));
    public static final RegistryObject<Item> RANGE_UPGRADE_MODULE_2 = ITEMS.register("range_upgrade_module_2",
            () -> new RangeUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 2));
    public static final RegistryObject<Item> RANGE_UPGRADE_MODULE_3 = ITEMS.register("range_upgrade_module_3",
            () -> new RangeUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 3));

    public static final RegistryObject<Item> BLAST_FURNACE_UPGRADE_MODULE = ITEMS.register("blast_furnace_upgrade_module",
            () -> new FurnaceModeUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 1));
    public static final RegistryObject<Item> SMOKER_UPGRADE_MODULE = ITEMS.register("smoker_upgrade_module",
            () -> new FurnaceModeUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 2));

    public static final RegistryObject<Item> MOON_LIGHT_UPGRADE_MODULE_1 = ITEMS.register("moon_light_upgrade_module_1",
            () -> new MoonLightUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 1));
    public static final RegistryObject<Item> MOON_LIGHT_UPGRADE_MODULE_2 = ITEMS.register("moon_light_upgrade_module_2",
            () -> new MoonLightUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 2));
    public static final RegistryObject<Item> MOON_LIGHT_UPGRADE_MODULE_3 = ITEMS.register("moon_light_upgrade_module_3",
            () -> new MoonLightUpgradeModuleItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB), 3));

    public static final RegistryObject<Item> ENERGIZED_POWER_BOOK = ITEMS.register("energized_power_book",
            () -> new EnergizedPowerBookItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).stacksTo(1)));

    public static final RegistryObject<Item> CABLE_INSULATOR = ITEMS.register("cable_insulator",
            () -> new CableInsulatorItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> CHARCOAL_FILTER = ITEMS.register("charcoal_filter",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).durability(200).defaultDurability(200)));

    public static final RegistryObject<Item> SAW_BLADE = ITEMS.register("saw_blade",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> CRYSTAL_MATRIX = ITEMS.register("crystal_matrix",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> ENERGIZED_CRYSTAL_MATRIX = ITEMS.register("energized_crystal_matrix",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> INVENTORY_COAL_ENGINE = ITEMS.register("inventory_coal_engine",
            () -> new InventoryCoalEngineItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).stacksTo(1)));

    public static final RegistryObject<Item> INVENTORY_CHARGER = ITEMS.register("inventory_charger",
            () -> new InventoryChargerItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).stacksTo(1)));

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
    public static final RegistryObject<Item> CREATIVE_BATTERY = ITEMS.register("creative_battery",
            () -> new CreativeBatteryItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).stacksTo(1)));

    public static final RegistryObject<Item> ENERGY_ANALYZER = ITEMS.register("energy_analyzer",
            () -> new EnergyAnalyzerItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).stacksTo(1)));

    public static final RegistryObject<Item> FLUID_ANALYZER = ITEMS.register("fluid_analyzer",
            () -> new FluidAnalyzerItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).stacksTo(1)));

    public static final RegistryObject<Item> WOODEN_HAMMER = ITEMS.register("wooden_hammer",
            () -> new HammerItem(Tiers.WOOD, new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> STONE_HAMMER = ITEMS.register("stone_hammer",
            () -> new HammerItem(Tiers.STONE, new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> IRON_HAMMER = ITEMS.register("iron_hammer",
            () -> new HammerItem(Tiers.IRON, new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> GOLDEN_HAMMER = ITEMS.register("golden_hammer",
            () -> new HammerItem(Tiers.GOLD, new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> DIAMOND_HAMMER = ITEMS.register("diamond_hammer",
            () -> new HammerItem(Tiers.DIAMOND, new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final RegistryObject<Item> NETHERITE_HAMMER = ITEMS.register("netherite_hammer",
            () -> new HammerItem(Tiers.NETHERITE, new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).fireResistant()));

    public static final RegistryObject<Item> CUTTER = ITEMS.register("cutter",
            () -> new CutterItem(Tiers.IRON, new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench",
            () -> new WrenchItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).stacksTo(1)));

    public static final RegistryObject<Item> BATTERY_BOX_MINECART = ITEMS.register("battery_box_minecart",
            () -> new BatteryBoxMinecartItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).stacksTo(1)));
    public static final RegistryObject<Item> ADVANCED_BATTERY_BOX_MINECART = ITEMS.register("advanced_battery_box_minecart",
            () -> new AdvancedBatteryBoxMinecartItem(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).stacksTo(1)));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
