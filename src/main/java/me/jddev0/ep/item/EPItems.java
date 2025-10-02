package me.jddev0.ep.item;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.energy.InfinityEnergyStorage;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItemStorage;
import me.jddev0.ep.machine.tier.BatteryTier;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.Function;

public final class EPItems {
    private EPItems() {}

    public static Item registerItem(String name) {
        return registerItem(name, Item::new, new Item.Settings());
    }

    public static Item registerItem(String name, Item.Settings props) {
        return registerItem(name, Item::new, props);
    }

    public static Item registerItem(String name, Function<Item.Settings, Item> factory) {
        return registerItem(name, factory, new Item.Settings());
    }

    public static  <T extends Item> T registerItem(String name, Function<Item.Settings, T> factory, Item.Settings props) {
        Identifier itemId = EPAPI.id(name);
        return Registry.register(Registries.ITEM, itemId, factory.apply(props.
                registryKey(RegistryKey.of(RegistryKeys.ITEM, itemId))));
    }

    public static final Item ENERGIZED_COPPER_INGOT = registerItem("energized_copper_ingot");
    public static final Item ENERGIZED_GOLD_INGOT = registerItem("energized_gold_ingot");

    public static final Item ENERGIZED_COPPER_PLATE = registerItem("energized_copper_plate");
    public static final Item ENERGIZED_GOLD_PLATE = registerItem("energized_gold_plate");

    public static final Item ENERGIZED_COPPER_WIRE = registerItem("energized_copper_wire");
    public static final Item ENERGIZED_GOLD_WIRE = registerItem("energized_gold_wire");

    public static final Item SILICON = registerItem("silicon");

    public static final Item STONE_PEBBLE = registerItem("stone_pebble");

    public static final Item RAW_TIN = registerItem("raw_tin");

    public static final Item TIN_DUST = registerItem("tin_dust");
    public static final Item COPPER_DUST = registerItem("copper_dust");
    public static final Item IRON_DUST = registerItem("iron_dust");
    public static final Item GOLD_DUST = registerItem("gold_dust");

    public static final Item TIN_NUGGET = registerItem("tin_nugget");

    public static final Item TIN_INGOT = registerItem("tin_ingot");

    public static final Item TIN_PLATE = registerItem("tin_plate");
    public static final Item COPPER_PLATE = registerItem("copper_plate");
    public static final Item IRON_PLATE = registerItem("iron_plate");
    public static final Item GOLD_PLATE = registerItem("gold_plate");

    public static final Item STEEL_INGOT = registerItem("steel_ingot");

    public static final Item REDSTONE_ALLOY_INGOT = registerItem("redstone_alloy_ingot");

    public static final Item ADVANCED_ALLOY_INGOT = registerItem("advanced_alloy_ingot");

    public static final Item ADVANCED_ALLOY_PLATE = registerItem("advanced_alloy_plate");

    public static final Item IRON_GEAR = registerItem("iron_gear");

    public static final Item IRON_ROD = registerItem("iron_rod");

    public static final Item TIN_WIRE = registerItem("tin_wire");
    public static final Item COPPER_WIRE = registerItem("copper_wire");
    public static final Item GOLD_WIRE = registerItem("gold_wire");

    public static final Item SAWDUST = registerItem("sawdust");

    public static final Item CHARCOAL_DUST = registerItem("charcoal_dust");

    public static final Item BASIC_FERTILIZER = registerItem("basic_fertilizer");

    public static final Item GOOD_FERTILIZER = registerItem("good_fertilizer");

    public static final Item ADVANCED_FERTILIZER = registerItem("advanced_fertilizer");

    public static final Item RAW_GEAR_PRESS_MOLD = registerItem("raw_gear_press_mold");
    public static final Item RAW_ROD_PRESS_MOLD = registerItem("raw_rod_press_mold");
    public static final Item RAW_WIRE_PRESS_MOLD = registerItem("raw_wire_press_mold");

    public static final Item GEAR_PRESS_MOLD = registerItem("gear_press_mold",
            new Item.Settings().maxDamage(2000));
    public static final Item ROD_PRESS_MOLD = registerItem("rod_press_mold",
            new Item.Settings().maxDamage(2000));
    public static final Item WIRE_PRESS_MOLD = registerItem("wire_press_mold",
            new Item.Settings().maxDamage(2000));

    public static final Item BASIC_SOLAR_CELL = registerItem("basic_solar_cell");
    public static final Item ADVANCED_SOLAR_CELL = registerItem("advanced_solar_cell");
    public static final Item REINFORCED_ADVANCED_SOLAR_CELL = registerItem("reinforced_advanced_solar_cell");

    public static final Item BASIC_CIRCUIT = registerItem("basic_circuit");
    public static final Item ADVANCED_CIRCUIT = registerItem("advanced_circuit");
    public static final Item PROCESSING_UNIT = registerItem("processing_unit");

    public static final Item TELEPORTER_MATRIX = registerItem("teleporter_matrix",
            TeleporterMatrixItem::new);
    public static final Item TELEPORTER_PROCESSING_UNIT = registerItem("teleporter_processing_unit");

    public static final Item BASIC_UPGRADE_MODULE = registerItem("basic_upgrade_module");
    public static final Item ADVANCED_UPGRADE_MODULE = registerItem("advanced_upgrade_module");
    public static final Item REINFORCED_ADVANCED_UPGRADE_MODULE = registerItem("reinforced_advanced_upgrade_module");

    public static final Item SPEED_UPGRADE_MODULE_1 = registerItem("speed_upgrade_module_1",
            props -> new SpeedUpgradeModuleItem(props, 1));
    public static final Item SPEED_UPGRADE_MODULE_2 = registerItem("speed_upgrade_module_2",
            props -> new SpeedUpgradeModuleItem(props, 2));
    public static final Item SPEED_UPGRADE_MODULE_3 = registerItem("speed_upgrade_module_3",
            props -> new SpeedUpgradeModuleItem(props, 3));
    public static final Item SPEED_UPGRADE_MODULE_4 = registerItem("speed_upgrade_module_4",
            props -> new SpeedUpgradeModuleItem(props, 4));
    public static final Item SPEED_UPGRADE_MODULE_5 = registerItem("speed_upgrade_module_5",
            props -> new SpeedUpgradeModuleItem(props, 5));

    public static final Item ENERGY_EFFICIENCY_UPGRADE_MODULE_1 = registerItem("energy_efficiency_upgrade_module_1",
            props -> new EnergyEfficiencyUpgradeModuleItem(props, 1));
    public static final Item ENERGY_EFFICIENCY_UPGRADE_MODULE_2 = registerItem("energy_efficiency_upgrade_module_2",
            props -> new EnergyEfficiencyUpgradeModuleItem(props, 2));
    public static final Item ENERGY_EFFICIENCY_UPGRADE_MODULE_3 = registerItem("energy_efficiency_upgrade_module_3",
            props -> new EnergyEfficiencyUpgradeModuleItem(props, 3));
    public static final Item ENERGY_EFFICIENCY_UPGRADE_MODULE_4 = registerItem("energy_efficiency_upgrade_module_4",
            props -> new EnergyEfficiencyUpgradeModuleItem(props, 4));
    public static final Item ENERGY_EFFICIENCY_UPGRADE_MODULE_5 = registerItem("energy_efficiency_upgrade_module_5",
            props -> new EnergyEfficiencyUpgradeModuleItem(props, 5));

    public static final Item ENERGY_CAPACITY_UPGRADE_MODULE_1 = registerItem("energy_capacity_upgrade_module_1",
            props -> new EnergyCapacityUpgradeModuleItem(props, 1));
    public static final Item ENERGY_CAPACITY_UPGRADE_MODULE_2 = registerItem("energy_capacity_upgrade_module_2",
            props -> new EnergyCapacityUpgradeModuleItem(props, 2));
    public static final Item ENERGY_CAPACITY_UPGRADE_MODULE_3 = registerItem("energy_capacity_upgrade_module_3",
            props -> new EnergyCapacityUpgradeModuleItem(props, 3));
    public static final Item ENERGY_CAPACITY_UPGRADE_MODULE_4 = registerItem("energy_capacity_upgrade_module_4",
            props -> new EnergyCapacityUpgradeModuleItem(props, 4));
    public static final Item ENERGY_CAPACITY_UPGRADE_MODULE_5 = registerItem("energy_capacity_upgrade_module_5",
            props -> new EnergyCapacityUpgradeModuleItem(props, 5));

    public static final Item DURATION_UPGRADE_MODULE_1 = registerItem("duration_upgrade_module_1",
            props -> new DurationUpgradeModuleItem(props, 1));
    public static final Item DURATION_UPGRADE_MODULE_2 = registerItem("duration_upgrade_module_2",
            props -> new DurationUpgradeModuleItem(props, 2));
    public static final Item DURATION_UPGRADE_MODULE_3 = registerItem("duration_upgrade_module_3",
            props -> new DurationUpgradeModuleItem(props, 3));
    public static final Item DURATION_UPGRADE_MODULE_4 = registerItem("duration_upgrade_module_4",
            props -> new DurationUpgradeModuleItem(props, 4));
    public static final Item DURATION_UPGRADE_MODULE_5 = registerItem("duration_upgrade_module_5",
            props -> new DurationUpgradeModuleItem(props, 5));
    public static final Item DURATION_UPGRADE_MODULE_6 = registerItem("duration_upgrade_module_6",
            props -> new DurationUpgradeModuleItem(props, 6));

    public static final Item RANGE_UPGRADE_MODULE_1 = registerItem("range_upgrade_module_1",
            props -> new RangeUpgradeModuleItem(props, 1));
    public static final Item RANGE_UPGRADE_MODULE_2 = registerItem("range_upgrade_module_2",
            props -> new RangeUpgradeModuleItem(props, 2));
    public static final Item RANGE_UPGRADE_MODULE_3 = registerItem("range_upgrade_module_3",
            props -> new RangeUpgradeModuleItem(props, 3));

    public static final Item EXTRACTION_DEPTH_UPGRADE_MODULE_1 = registerItem("extraction_depth_upgrade_module_1",
            props -> new ExtractionDepthUpgradeModuleItem(props, 1));
    public static final Item EXTRACTION_DEPTH_UPGRADE_MODULE_2 = registerItem("extraction_depth_upgrade_module_2",
            props -> new ExtractionDepthUpgradeModuleItem(props, 2));
    public static final Item EXTRACTION_DEPTH_UPGRADE_MODULE_3 = registerItem("extraction_depth_upgrade_module_3",
            props -> new ExtractionDepthUpgradeModuleItem(props, 3));
    public static final Item EXTRACTION_DEPTH_UPGRADE_MODULE_4 = registerItem("extraction_depth_upgrade_module_4",
            props -> new ExtractionDepthUpgradeModuleItem(props, 4));
    public static final Item EXTRACTION_DEPTH_UPGRADE_MODULE_5 = registerItem("extraction_depth_upgrade_module_5",
            props -> new ExtractionDepthUpgradeModuleItem(props, 5));

    public static final Item EXTRACTION_RANGE_UPGRADE_MODULE_1 = registerItem("extraction_range_upgrade_module_1",
            props -> new ExtractionRangeUpgradeModuleItem(props, 1));
    public static final Item EXTRACTION_RANGE_UPGRADE_MODULE_2 = registerItem("extraction_range_upgrade_module_2",
            props -> new ExtractionRangeUpgradeModuleItem(props, 2));
    public static final Item EXTRACTION_RANGE_UPGRADE_MODULE_3 = registerItem("extraction_range_upgrade_module_3",
            props -> new ExtractionRangeUpgradeModuleItem(props, 3));
    public static final Item EXTRACTION_RANGE_UPGRADE_MODULE_4 = registerItem("extraction_range_upgrade_module_4",
            props -> new ExtractionRangeUpgradeModuleItem(props, 4));
    public static final Item EXTRACTION_RANGE_UPGRADE_MODULE_5 = registerItem("extraction_range_upgrade_module_5",
            props -> new ExtractionRangeUpgradeModuleItem(props, 5));

    public static final Item BLAST_FURNACE_UPGRADE_MODULE = registerItem("blast_furnace_upgrade_module",
            props -> new FurnaceModeUpgradeModuleItem(props, 1));
    public static final Item SMOKER_UPGRADE_MODULE = registerItem("smoker_upgrade_module",
            props -> new FurnaceModeUpgradeModuleItem(props, 2));

    public static final Item MOON_LIGHT_UPGRADE_MODULE_1 = registerItem("moon_light_upgrade_module_1",
            props -> new MoonLightUpgradeModuleItem(props, 1));
    public static final Item MOON_LIGHT_UPGRADE_MODULE_2 = registerItem("moon_light_upgrade_module_2",
            props -> new MoonLightUpgradeModuleItem(props, 2));
    public static final Item MOON_LIGHT_UPGRADE_MODULE_3 = registerItem("moon_light_upgrade_module_3",
            props -> new MoonLightUpgradeModuleItem(props, 3));

    public static final Item ENERGIZED_POWER_BOOK = registerItem("energized_power_book",
            EnergizedPowerBookItem::new, new Item.Settings().maxCount(1));

    public static final Item CABLE_INSULATOR = registerItem("cable_insulator",
            CableInsulatorItem::new);

    public static final Item CHARCOAL_FILTER = registerItem("charcoal_filter",
            new Item.Settings().maxDamage(200));

    public static final Item SAW_BLADE = registerItem("saw_blade");

    public static final Item CRYSTAL_MATRIX = registerItem("crystal_matrix");

    public static final Item ENERGIZED_CRYSTAL_MATRIX = registerItem("energized_crystal_matrix");

    public static final Item INVENTORY_COAL_ENGINE = registerItem("inventory_coal_engine",
            InventoryCoalEngineItem::new, new Item.Settings().maxCount(1));

    public static final Item INVENTORY_CHARGER = registerItem("inventory_charger",
            InventoryChargerItem::new, new Item.Settings().maxCount(1));

    public static final Item INVENTORY_TELEPORTER = registerItem("inventory_teleporter",
            InventoryTeleporterItem::new, new Item.Settings().maxCount(1));

    public static final Item BATTERY_1 = registerItem("battery_1",
            props -> new BatteryItem(props, BatteryTier.BATTERY_1));
    public static final Item BATTERY_2 = registerItem("battery_2",
            props -> new BatteryItem(props, BatteryTier.BATTERY_2));
    public static final Item BATTERY_3 = registerItem("battery_3",
            props -> new BatteryItem(props, BatteryTier.BATTERY_3));
    public static final Item BATTERY_4 = registerItem("battery_4",
            props -> new BatteryItem(props, BatteryTier.BATTERY_4));
    public static final Item BATTERY_5 = registerItem("battery_5",
            props -> new BatteryItem(props, BatteryTier.BATTERY_5));
    public static final Item BATTERY_6 = registerItem("battery_6",
            props -> new BatteryItem(props, BatteryTier.BATTERY_6));
    public static final Item BATTERY_7 = registerItem("battery_7",
            props -> new BatteryItem(props, BatteryTier.BATTERY_7));
    public static final Item BATTERY_8 = registerItem("battery_8",
            props -> new BatteryItem(props, BatteryTier.BATTERY_8));
    public static final Item CREATIVE_BATTERY = registerItem("creative_battery",
            CreativeBatteryItem::new, new Item.Settings().maxCount(1));

    public static final Item ENERGY_ANALYZER = registerItem("energy_analyzer",
            EnergyAnalyzerItem::new, new Item.Settings().maxCount(1));

    public static final Item FLUID_ANALYZER = registerItem("fluid_analyzer",
            FluidAnalyzerItem::new, new Item.Settings().maxCount(1));

    public static final Item WOODEN_HAMMER = registerItem("wooden_hammer",
            props -> new HammerItem(ToolMaterial.WOOD, props));
    public static final Item STONE_HAMMER = registerItem("stone_hammer",
            props -> new HammerItem(ToolMaterial.STONE, props));
    public static final Item COPPER_HAMMER = registerItem("copper_hammer",
            props -> new HammerItem(ToolMaterial.COPPER, props));
    public static final Item IRON_HAMMER = registerItem("iron_hammer",
            props -> new HammerItem(ToolMaterial.IRON, props));
    public static final Item GOLDEN_HAMMER = registerItem("golden_hammer",
            props -> new HammerItem(ToolMaterial.GOLD, props));
    public static final Item DIAMOND_HAMMER = registerItem("diamond_hammer",
            props -> new HammerItem(ToolMaterial.DIAMOND, props));
    public static final Item NETHERITE_HAMMER = registerItem("netherite_hammer",
            props -> new HammerItem(ToolMaterial.NETHERITE, props), new Item.Settings().fireproof());

    public static final Item CUTTER = registerItem("cutter",
            props -> new CutterItem(ToolMaterial.IRON, props));

    public static final Item WRENCH = registerItem("wrench",
            WrenchItem::new, new Item.Settings().maxCount(1));

    public static final Item BATTERY_BOX_MINECART = registerItem("battery_box_minecart",
            BatteryBoxMinecartItem::new, new Item.Settings().maxCount(1));
    public static final Item ADVANCED_BATTERY_BOX_MINECART = registerItem("advanced_battery_box_minecart",
            AdvancedBatteryBoxMinecartItem::new, new Item.Settings().maxCount(1));

    //Register energy storage for items
    static {
        //EnergizedPower Energy Items
        EnergyStorage.ITEM.registerFallback((stack, ctx) -> {
            if(stack.getItem() instanceof EnergizedPowerEnergyItem energyItem)
                return new EnergizedPowerEnergyItemStorage(ctx, energyItem.getEnergyCapacity(stack),
                        energyItem.getEnergyMaxInput(stack), energyItem.getEnergyMaxOutput(stack));

            return null;
        });

        //Creative Battery
        EnergyStorage.ITEM.registerFallback((stack, ctx) -> {
            if(stack.getItem() instanceof CreativeBatteryItem)
                return new InfinityEnergyStorage();

            return null;
        });
    }

    public static void register() {

    }
}
