package me.jddev0.ep.item;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.energy.InfinityEnergyStorage;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import team.reborn.energy.api.EnergyStorage;

public final class ModItems {
    private ModItems() {}

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(EnergizedPowerMod.MODID, name), item);
    }

    public static final Item ENERGIZED_COPPER_INGOT = registerItem("energized_copper_ingot",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final Item ENERGIZED_GOLD_INGOT = registerItem("energized_gold_ingot",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item ENERGIZED_COPPER_PLATE = registerItem("energized_copper_plate",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final Item ENERGIZED_GOLD_PLATE = registerItem("energized_gold_plate",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final Item SILICON = registerItem("silicon",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item COPPER_PLATE = registerItem("copper_plate",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final Item IRON_PLATE = registerItem("iron_plate",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final Item GOLD_PLATE = registerItem("gold_plate",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item SAWDUST = registerItem("sawdust",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item BASIC_FERTILIZER = registerItem("basic_fertilizer",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item GOOD_FERTILIZER = registerItem("good_fertilizer",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item ADVANCED_FERTILIZER = registerItem("advanced_fertilizer",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item BASIC_SOLAR_CELL = registerItem("basic_solar_cell",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final Item ADVANCED_SOLAR_CELL = registerItem("advanced_solar_cell",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item ENERGIZED_POWER_BOOK = registerItem("energized_power_book",
            new EnergizedPowerBookItem(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).maxCount(1)));

    public static final Item CABLE_INSULATOR = registerItem("cable_insulator",
            new CableInsulatorItem(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item SAW_BLADE = registerItem("saw_blade",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item CRYSTAL_MATRIX = registerItem("crystal_matrix",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item ENERGIZED_CRYSTAL_MATRIX = registerItem("energized_crystal_matrix",
            new Item(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));

    public static final Item INVENTORY_COAL_ENGINE = registerItem("inventory_coal_engine",
            new InventoryCoalEngine(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).maxCount(1)));

    public static final Item INVENTORY_CHARGER = registerItem("inventory_charger",
            new InventoryChargerItem(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).maxCount(1)));

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
    public static final Item CREATIVE_BATTERY = registerItem("creative_battery",
            new CreativeBatteryItem(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).maxCount(1)));
    //Register energy storage for creative battery
    static {
        EnergyStorage.ITEM.registerFallback((stack, ctx) -> {
            if(stack.getItem() instanceof CreativeBatteryItem)
                return new InfinityEnergyStorage();

            return null;
        });
    }

    public static final Item ENERGY_ANALYZER = registerItem("energy_analyzer",
            new EnergyAnalyzerItem(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).maxCount(1)));

    public static final Item FLUID_ANALYZER = registerItem("fluid_analyzer",
            new FluidAnalyzerItem(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).maxCount(1)));

    public static final Item WOODEN_HAMMER = registerItem("wooden_hammer",
            new HammerItem(ToolMaterials.WOOD, new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final Item STONE_HAMMER = registerItem("stone_hammer",
            new HammerItem(ToolMaterials.STONE, new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final Item IRON_HAMMER = registerItem("iron_hammer",
            new HammerItem(ToolMaterials.IRON, new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final Item GOLDEN_HAMMER = registerItem("golden_hammer",
            new HammerItem(ToolMaterials.GOLD, new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final Item DIAMOND_HAMMER = registerItem("diamond_hammer",
            new HammerItem(ToolMaterials.DIAMOND, new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB)));
    public static final Item NETHERITE_HAMMER = registerItem("netherite_hammer",
            new HammerItem(ToolMaterials.NETHERITE, new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).fireproof()));

    public static final Item WRENCH = registerItem("wrench",
            new WrenchItem(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).maxCount(1)));

    public static final Item BATTERY_BOX_MINECART = registerItem("battery_box_minecart",
            new BatteryBoxMinecartItem(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).maxCount(1)));
    public static final Item ADVANCED_BATTERY_BOX_MINECART = registerItem("advanced_battery_box_minecart",
            new AdvancedBatteryBoxMinecartItem(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).maxCount(1)));

    public static void register() {

    }
}
