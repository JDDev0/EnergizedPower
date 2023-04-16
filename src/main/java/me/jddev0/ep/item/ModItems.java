package me.jddev0.ep.item;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {
    private ModItems() {}

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(EnergizedPowerMod.MODID, name), item);
    }

    public static final Item ENERGIZED_COPPER_INGOT = registerItem("energized_copper_ingot",
            new Item(new FabricItemSettings()));
    public static final Item ENERGIZED_GOLD_INGOT = registerItem("energized_gold_ingot",
            new Item(new FabricItemSettings()));

    public static final Item ENERGIZED_COPPER_PLATE = registerItem("energized_copper_plate",
            new Item(new FabricItemSettings()));
    public static final Item ENERGIZED_GOLD_PLATE = registerItem("energized_gold_plate",
            new Item(new FabricItemSettings()));

    public static final Item SILICON = registerItem("silicon",
            new Item(new FabricItemSettings()));

    public static final Item COPPER_PLATE = registerItem("copper_plate",
            new Item(new FabricItemSettings()));
    public static final Item IRON_PLATE = registerItem("iron_plate",
            new Item(new FabricItemSettings()));
    public static final Item GOLD_PLATE = registerItem("gold_plate",
            new Item(new FabricItemSettings()));

    public static final Item SAWDUST = registerItem("sawdust",
            new Item(new FabricItemSettings()));

    public static final Item BASIC_SOLAR_CELL = registerItem("basic_solar_cell",
            new Item(new FabricItemSettings()));
    public static final Item ADVANCED_SOLAR_CELL = registerItem("advanced_solar_cell",
            new Item(new FabricItemSettings()));

    public static final Item ENERGIZED_POWER_BOOK = registerItem("energized_power_book",
            new EnergizedPowerBookItem(new FabricItemSettings().maxCount(1)));

    public static final Item CABLE_INSULATOR = registerItem("cable_insulator",
            new CableInsulatorItem(new FabricItemSettings()));

    public static final Item SAW_BLADE = registerItem("saw_blade",
            new Item(new FabricItemSettings()));

    public static final Item INVENTORY_COAL_ENGINE = registerItem("inventory_coal_engine",
            new InventoryCoalEngine(new FabricItemSettings().maxCount(1)));

    public static final Item BATTERY_1 = registerItem("battery_1",
            new BatteryItem(BatteryItem.Tier.BATTERY_1));
    public static final Item BATTERY_2 = registerItem("battery_2",
            new BatteryItem(BatteryItem.Tier.BATTERY_2));
    public static final Item BATTERY_3 = registerItem("battery_3",
            new BatteryItem(BatteryItem.Tier.BATTERY_3));
    public static final Item BATTERY_4 = registerItem("battery_4",
            new BatteryItem(BatteryItem.Tier.BATTERY_4));
    public static final Item BATTERY_5 = registerItem("battery_5",
            new BatteryItem(BatteryItem.Tier.BATTERY_5));
    public static final Item BATTERY_6 = registerItem("battery_6",
            new BatteryItem(BatteryItem.Tier.BATTERY_6));
    public static final Item BATTERY_7 = registerItem("battery_7",
            new BatteryItem(BatteryItem.Tier.BATTERY_7));
    public static final Item BATTERY_8 = registerItem("battery_8",
            new BatteryItem(BatteryItem.Tier.BATTERY_8));

    public static final Item ENERGY_ANALYZER = registerItem("energy_analyzer",
            new EnergyAnalyzerItem(new FabricItemSettings().maxCount(1)));

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

    public static void register() {

    }
}
