package me.jddev0.ep.item;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.energy.InfinityEnergyStorage;
import me.jddev0.ep.machine.tier.BatteryTier;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import team.reborn.energy.api.EnergyStorage;

public final class EPItems {
    private EPItems() {}

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, EPAPI.id(name), item);
    }

    public static final Item ENERGIZED_COPPER_INGOT = registerItem("energized_copper_ingot",
            new Item(new FabricItemSettings()));
    public static final Item ENERGIZED_GOLD_INGOT = registerItem("energized_gold_ingot",
            new Item(new FabricItemSettings()));

    public static final Item ENERGIZED_COPPER_PLATE = registerItem("energized_copper_plate",
            new Item(new FabricItemSettings()));
    public static final Item ENERGIZED_GOLD_PLATE = registerItem("energized_gold_plate",
            new Item(new FabricItemSettings()));

    public static final Item ENERGIZED_COPPER_WIRE = registerItem("energized_copper_wire",
            new Item(new FabricItemSettings()));
    public static final Item ENERGIZED_GOLD_WIRE = registerItem("energized_gold_wire",
            new Item(new FabricItemSettings()));

    public static final Item SILICON = registerItem("silicon",
            new Item(new FabricItemSettings()));

    public static final Item STONE_PEBBLE = registerItem("stone_pebble",
            new Item(new Item.Settings()));

    public static final Item RAW_TIN = registerItem("raw_tin",
            new Item(new FabricItemSettings()));

    public static final Item TIN_DUST = registerItem("tin_dust",
            new Item(new FabricItemSettings()));
    public static final Item COPPER_DUST = registerItem("copper_dust",
            new Item(new FabricItemSettings()));
    public static final Item IRON_DUST = registerItem("iron_dust",
            new Item(new FabricItemSettings()));
    public static final Item GOLD_DUST = registerItem("gold_dust",
            new Item(new FabricItemSettings()));

    public static final Item TIN_NUGGET = registerItem("tin_nugget",
            new Item(new FabricItemSettings()));

    public static final Item TIN_INGOT = registerItem("tin_ingot",
            new Item(new FabricItemSettings()));

    public static final Item TIN_PLATE = registerItem("tin_plate",
            new Item(new FabricItemSettings()));
    public static final Item COPPER_PLATE = registerItem("copper_plate",
            new Item(new FabricItemSettings()));
    public static final Item IRON_PLATE = registerItem("iron_plate",
            new Item(new FabricItemSettings()));
    public static final Item GOLD_PLATE = registerItem("gold_plate",
            new Item(new FabricItemSettings()));

    public static final Item STEEL_INGOT = registerItem("steel_ingot",
            new Item(new Item.Settings()));

    public static final Item REDSTONE_ALLOY_INGOT = registerItem("redstone_alloy_ingot",
            new Item(new Item.Settings()));

    public static final Item ADVANCED_ALLOY_INGOT = registerItem("advanced_alloy_ingot",
            new Item(new Item.Settings()));

    public static final Item ADVANCED_ALLOY_PLATE = registerItem("advanced_alloy_plate",
            new Item(new Item.Settings()));

    public static final Item IRON_GEAR = registerItem("iron_gear",
            new Item(new FabricItemSettings()));

    public static final Item IRON_ROD = registerItem("iron_rod",
            new Item(new FabricItemSettings()));

    public static final Item TIN_WIRE = registerItem("tin_wire",
            new Item(new FabricItemSettings()));
    public static final Item COPPER_WIRE = registerItem("copper_wire",
            new Item(new FabricItemSettings()));
    public static final Item GOLD_WIRE = registerItem("gold_wire",
            new Item(new FabricItemSettings()));

    public static final Item SAWDUST = registerItem("sawdust",
            new Item(new FabricItemSettings()));

    public static final Item CHARCOAL_DUST = registerItem("charcoal_dust",
            new Item(new FabricItemSettings()));

    public static final Item BASIC_FERTILIZER = registerItem("basic_fertilizer",
            new Item(new FabricItemSettings()));

    public static final Item GOOD_FERTILIZER = registerItem("good_fertilizer",
            new Item(new FabricItemSettings()));

    public static final Item ADVANCED_FERTILIZER = registerItem("advanced_fertilizer",
            new Item(new FabricItemSettings()));

    public static final Item RAW_GEAR_PRESS_MOLD = registerItem("raw_gear_press_mold",
            new Item(new FabricItemSettings()));
    public static final Item RAW_ROD_PRESS_MOLD = registerItem("raw_rod_press_mold",
            new Item(new FabricItemSettings()));
    public static final Item RAW_WIRE_PRESS_MOLD = registerItem("raw_wire_press_mold",
            new Item(new FabricItemSettings()));

    public static final Item GEAR_PRESS_MOLD = registerItem("gear_press_mold",
            new Item(new FabricItemSettings().maxDamage(2000)));
    public static final Item ROD_PRESS_MOLD = registerItem("rod_press_mold",
            new Item(new FabricItemSettings().maxDamage(2000)));
    public static final Item WIRE_PRESS_MOLD = registerItem("wire_press_mold",
            new Item(new FabricItemSettings().maxDamage(2000)));

    public static final Item BASIC_SOLAR_CELL = registerItem("basic_solar_cell",
            new Item(new FabricItemSettings()));
    public static final Item ADVANCED_SOLAR_CELL = registerItem("advanced_solar_cell",
            new Item(new FabricItemSettings()));
    public static final Item REINFORCED_ADVANCED_SOLAR_CELL = registerItem("reinforced_advanced_solar_cell",
            new Item(new FabricItemSettings()));

    public static final Item BASIC_CIRCUIT = registerItem("basic_circuit",
            new Item(new FabricItemSettings()));
    public static final Item ADVANCED_CIRCUIT = registerItem("advanced_circuit",
            new Item(new FabricItemSettings()));
    public static final Item PROCESSING_UNIT = registerItem("processing_unit",
            new Item(new FabricItemSettings()));

    public static final Item TELEPORTER_MATRIX = registerItem("teleporter_matrix",
            new TeleporterMatrixItem(new FabricItemSettings()));
    public static final Item TELEPORTER_PROCESSING_UNIT = registerItem("teleporter_processing_unit",
            new Item(new FabricItemSettings()));

    public static Item BASIC_UPGRADE_MODULE = registerItem("basic_upgrade_module",
            new Item(new FabricItemSettings()));
    public static Item ADVANCED_UPGRADE_MODULE = registerItem("advanced_upgrade_module",
            new Item(new FabricItemSettings()));
    public static Item REINFORCED_ADVANCED_UPGRADE_MODULE = registerItem("reinforced_advanced_upgrade_module",
            new Item(new FabricItemSettings()));

    public static Item SPEED_UPGRADE_MODULE_1 = registerItem("speed_upgrade_module_1",
            new SpeedUpgradeModuleItem(new FabricItemSettings(), 1));
    public static Item SPEED_UPGRADE_MODULE_2 = registerItem("speed_upgrade_module_2",
            new SpeedUpgradeModuleItem(new FabricItemSettings(), 2));
    public static Item SPEED_UPGRADE_MODULE_3 = registerItem("speed_upgrade_module_3",
            new SpeedUpgradeModuleItem(new FabricItemSettings(), 3));
    public static Item SPEED_UPGRADE_MODULE_4 = registerItem("speed_upgrade_module_4",
            new SpeedUpgradeModuleItem(new FabricItemSettings(), 4));
    public static Item SPEED_UPGRADE_MODULE_5 = registerItem("speed_upgrade_module_5",
            new SpeedUpgradeModuleItem(new FabricItemSettings(), 5));

    public static Item ENERGY_EFFICIENCY_UPGRADE_MODULE_1 = registerItem("energy_efficiency_upgrade_module_1",
            new EnergyEfficiencyUpgradeModuleItem(new FabricItemSettings(), 1));
    public static Item ENERGY_EFFICIENCY_UPGRADE_MODULE_2 = registerItem("energy_efficiency_upgrade_module_2",
            new EnergyEfficiencyUpgradeModuleItem(new FabricItemSettings(), 2));
    public static Item ENERGY_EFFICIENCY_UPGRADE_MODULE_3 = registerItem("energy_efficiency_upgrade_module_3",
            new EnergyEfficiencyUpgradeModuleItem(new FabricItemSettings(), 3));
    public static Item ENERGY_EFFICIENCY_UPGRADE_MODULE_4 = registerItem("energy_efficiency_upgrade_module_4",
            new EnergyEfficiencyUpgradeModuleItem(new FabricItemSettings(), 4));
    public static Item ENERGY_EFFICIENCY_UPGRADE_MODULE_5 = registerItem("energy_efficiency_upgrade_module_5",
            new EnergyEfficiencyUpgradeModuleItem(new FabricItemSettings(), 5));

    public static Item ENERGY_CAPACITY_UPGRADE_MODULE_1 = registerItem("energy_capacity_upgrade_module_1",
            new EnergyCapacityUpgradeModuleItem(new FabricItemSettings(), 1));
    public static Item ENERGY_CAPACITY_UPGRADE_MODULE_2 = registerItem("energy_capacity_upgrade_module_2",
            new EnergyCapacityUpgradeModuleItem(new FabricItemSettings(), 2));
    public static Item ENERGY_CAPACITY_UPGRADE_MODULE_3 = registerItem("energy_capacity_upgrade_module_3",
            new EnergyCapacityUpgradeModuleItem(new FabricItemSettings(), 3));
    public static Item ENERGY_CAPACITY_UPGRADE_MODULE_4 = registerItem("energy_capacity_upgrade_module_4",
            new EnergyCapacityUpgradeModuleItem(new FabricItemSettings(), 4));
    public static Item ENERGY_CAPACITY_UPGRADE_MODULE_5 = registerItem("energy_capacity_upgrade_module_5",
            new EnergyCapacityUpgradeModuleItem(new FabricItemSettings(), 5));

    public static Item DURATION_UPGRADE_MODULE_1 = registerItem("duration_upgrade_module_1",
            new DurationUpgradeModuleItem(new FabricItemSettings(), 1));
    public static Item DURATION_UPGRADE_MODULE_2 = registerItem("duration_upgrade_module_2",
            new DurationUpgradeModuleItem(new FabricItemSettings(), 2));
    public static Item DURATION_UPGRADE_MODULE_3 = registerItem("duration_upgrade_module_3",
            new DurationUpgradeModuleItem(new FabricItemSettings(), 3));
    public static Item DURATION_UPGRADE_MODULE_4 = registerItem("duration_upgrade_module_4",
            new DurationUpgradeModuleItem(new FabricItemSettings(), 4));
    public static Item DURATION_UPGRADE_MODULE_5 = registerItem("duration_upgrade_module_5",
            new DurationUpgradeModuleItem(new FabricItemSettings(), 5));
    public static Item DURATION_UPGRADE_MODULE_6 = registerItem("duration_upgrade_module_6",
            new DurationUpgradeModuleItem(new FabricItemSettings(), 6));

    public static Item RANGE_UPGRADE_MODULE_1 = registerItem("range_upgrade_module_1",
            new RangeUpgradeModuleItem(new FabricItemSettings(), 1));
    public static Item RANGE_UPGRADE_MODULE_2 = registerItem("range_upgrade_module_2",
            new RangeUpgradeModuleItem(new FabricItemSettings(), 2));
    public static Item RANGE_UPGRADE_MODULE_3 = registerItem("range_upgrade_module_3",
            new RangeUpgradeModuleItem(new FabricItemSettings(), 3));

    public static final Item EXTRACTION_DEPTH_UPGRADE_MODULE_1 = registerItem("extraction_depth_upgrade_module_1",
            new ExtractionDepthUpgradeModuleItem(new FabricItemSettings(), 1));
    public static final Item EXTRACTION_DEPTH_UPGRADE_MODULE_2 = registerItem("extraction_depth_upgrade_module_2",
            new ExtractionDepthUpgradeModuleItem(new FabricItemSettings(), 2));
    public static final Item EXTRACTION_DEPTH_UPGRADE_MODULE_3 = registerItem("extraction_depth_upgrade_module_3",
            new ExtractionDepthUpgradeModuleItem(new FabricItemSettings(), 3));
    public static final Item EXTRACTION_DEPTH_UPGRADE_MODULE_4 = registerItem("extraction_depth_upgrade_module_4",
            new ExtractionDepthUpgradeModuleItem(new FabricItemSettings(), 4));
    public static final Item EXTRACTION_DEPTH_UPGRADE_MODULE_5 = registerItem("extraction_depth_upgrade_module_5",
            new ExtractionDepthUpgradeModuleItem(new FabricItemSettings(), 5));

    public static final Item EXTRACTION_RANGE_UPGRADE_MODULE_1 = registerItem("extraction_range_upgrade_module_1",
            new ExtractionRangeUpgradeModuleItem(new FabricItemSettings(), 1));
    public static final Item EXTRACTION_RANGE_UPGRADE_MODULE_2 = registerItem("extraction_range_upgrade_module_2",
            new ExtractionRangeUpgradeModuleItem(new FabricItemSettings(), 2));
    public static final Item EXTRACTION_RANGE_UPGRADE_MODULE_3 = registerItem("extraction_range_upgrade_module_3",
            new ExtractionRangeUpgradeModuleItem(new FabricItemSettings(), 3));
    public static final Item EXTRACTION_RANGE_UPGRADE_MODULE_4 = registerItem("extraction_range_upgrade_module_4",
            new ExtractionRangeUpgradeModuleItem(new FabricItemSettings(), 4));
    public static final Item EXTRACTION_RANGE_UPGRADE_MODULE_5 = registerItem("extraction_range_upgrade_module_5",
            new ExtractionRangeUpgradeModuleItem(new FabricItemSettings(), 5));

    public static final Item BLAST_FURNACE_UPGRADE_MODULE = registerItem("blast_furnace_upgrade_module",
            new FurnaceModeUpgradeModuleItem(new FabricItemSettings(), 1));
    public static final Item SMOKER_UPGRADE_MODULE = registerItem("smoker_upgrade_module",
            new FurnaceModeUpgradeModuleItem(new FabricItemSettings(), 2));

    public static final Item MOON_LIGHT_UPGRADE_MODULE_1 = registerItem("moon_light_upgrade_module_1",
            new MoonLightUpgradeModuleItem(new FabricItemSettings(), 1));
    public static final Item MOON_LIGHT_UPGRADE_MODULE_2 = registerItem("moon_light_upgrade_module_2",
            new MoonLightUpgradeModuleItem(new FabricItemSettings(), 2));
    public static final Item MOON_LIGHT_UPGRADE_MODULE_3 = registerItem("moon_light_upgrade_module_3",
            new MoonLightUpgradeModuleItem(new FabricItemSettings(), 3));

    public static final Item ENERGIZED_POWER_BOOK = registerItem("energized_power_book",
            new EnergizedPowerBookItem(new FabricItemSettings().maxCount(1)));

    public static final Item CABLE_INSULATOR = registerItem("cable_insulator",
            new CableInsulatorItem(new FabricItemSettings()));

    public static final Item CHARCOAL_FILTER = registerItem("charcoal_filter",
            new Item(new FabricItemSettings().maxDamage(200).maxDamageIfAbsent(200)));

    public static final Item SAW_BLADE = registerItem("saw_blade",
            new Item(new FabricItemSettings()));

    public static final Item CRYSTAL_MATRIX = registerItem("crystal_matrix",
            new Item(new FabricItemSettings()));

    public static final Item ENERGIZED_CRYSTAL_MATRIX = registerItem("energized_crystal_matrix",
            new Item(new FabricItemSettings()));

    public static final Item INVENTORY_COAL_ENGINE = registerItem("inventory_coal_engine",
            new InventoryCoalEngineItem(new FabricItemSettings().maxCount(1)));

    public static final Item INVENTORY_CHARGER = registerItem("inventory_charger",
            new InventoryChargerItem(new FabricItemSettings().maxCount(1)));

    public static final Item INVENTORY_TELEPORTER = registerItem("inventory_teleporter",
            new InventoryTeleporterItem(new FabricItemSettings().maxCount(1)));

    public static final Item BATTERY_1 = registerItem("battery_1",
            new BatteryItem(BatteryTier.BATTERY_1));
    public static final Item BATTERY_2 = registerItem("battery_2",
            new BatteryItem(BatteryTier.BATTERY_2));
    public static final Item BATTERY_3 = registerItem("battery_3",
            new BatteryItem(BatteryTier.BATTERY_3));
    public static final Item BATTERY_4 = registerItem("battery_4",
            new BatteryItem(BatteryTier.BATTERY_4));
    public static final Item BATTERY_5 = registerItem("battery_5",
            new BatteryItem(BatteryTier.BATTERY_5));
    public static final Item BATTERY_6 = registerItem("battery_6",
            new BatteryItem(BatteryTier.BATTERY_6));
    public static final Item BATTERY_7 = registerItem("battery_7",
            new BatteryItem(BatteryTier.BATTERY_7));
    public static final Item BATTERY_8 = registerItem("battery_8",
            new BatteryItem(BatteryTier.BATTERY_8));
    public static final Item CREATIVE_BATTERY = registerItem("creative_battery",
            new CreativeBatteryItem(new FabricItemSettings().maxCount(1)));
    //Register energy storage for creative battery
    static {
        EnergyStorage.ITEM.registerFallback((stack, ctx) -> {
            if(stack.getItem() instanceof CreativeBatteryItem)
                return new InfinityEnergyStorage();

            return null;
        });
    }

    public static final Item ENERGY_ANALYZER = registerItem("energy_analyzer",
            new EnergyAnalyzerItem(new FabricItemSettings().maxCount(1)));

    public static final Item FLUID_ANALYZER = registerItem("fluid_analyzer",
            new FluidAnalyzerItem(new FabricItemSettings().maxCount(1)));

    public static final Item WOODEN_HAMMER = registerItem("wooden_hammer",
            new HammerItem(ToolMaterials.WOOD, new FabricItemSettings()));
    public static final Item STONE_HAMMER = registerItem("stone_hammer",
            new HammerItem(ToolMaterials.STONE, new FabricItemSettings()));
    public static final Item IRON_HAMMER = registerItem("iron_hammer",
            new HammerItem(ToolMaterials.IRON, new FabricItemSettings()));
    public static final Item GOLDEN_HAMMER = registerItem("golden_hammer",
            new HammerItem(ToolMaterials.GOLD, new FabricItemSettings()));
    public static final Item DIAMOND_HAMMER = registerItem("diamond_hammer",
            new HammerItem(ToolMaterials.DIAMOND, new FabricItemSettings()));
    public static final Item NETHERITE_HAMMER = registerItem("netherite_hammer",
            new HammerItem(ToolMaterials.NETHERITE, new FabricItemSettings().fireproof()));

    public static final Item CUTTER = registerItem("cutter",
            new CutterItem(ToolMaterials.IRON, new FabricItemSettings()));

    public static final Item WRENCH = registerItem("wrench",
            new WrenchItem(new FabricItemSettings().maxCount(1)));

    public static final Item BATTERY_BOX_MINECART = registerItem("battery_box_minecart",
            new BatteryBoxMinecartItem(new FabricItemSettings().maxCount(1)));
    public static final Item ADVANCED_BATTERY_BOX_MINECART = registerItem("advanced_battery_box_minecart",
            new AdvancedBatteryBoxMinecartItem(new FabricItemSettings().maxCount(1)));

    public static void register() {

    }
}
