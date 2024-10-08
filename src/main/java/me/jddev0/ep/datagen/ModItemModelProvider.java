package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.datagen.model.ItemWithOverridesModelSupplier;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.item.EPItems;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.List;

class ModItemModelProvider {
    private final ItemModelGenerator generator;

    ModItemModelProvider(ItemModelGenerator generator) {
        this.generator = generator;
    }

    void registeritems() {
        registerBasicModels();
        registerSpecialModels();
    }

    private void registerBasicModels() {
        basicItem(EPItems.ENERGIZED_COPPER_INGOT);
        basicItem(EPItems.ENERGIZED_GOLD_INGOT);

        basicItem(EPItems.ENERGIZED_COPPER_PLATE);
        basicItem(EPItems.ENERGIZED_GOLD_PLATE);

        basicItem(EPItems.ENERGIZED_COPPER_WIRE);
        basicItem(EPItems.ENERGIZED_GOLD_WIRE);

        basicItem(EPItems.SILICON);

        basicItem(EPItems.STONE_PEBBLE);

        basicItem(EPItems.RAW_TIN);

        basicItem(EPItems.TIN_DUST);
        basicItem(EPItems.COPPER_DUST);
        basicItem(EPItems.IRON_DUST);
        basicItem(EPItems.GOLD_DUST);

        basicItem(EPItems.TIN_NUGGET);

        basicItem(EPItems.TIN_INGOT);

        basicItem(EPItems.TIN_PLATE);
        basicItem(EPItems.COPPER_PLATE);
        basicItem(EPItems.IRON_PLATE);
        basicItem(EPItems.GOLD_PLATE);

        basicItem(EPItems.STEEL_INGOT);

        basicItem(EPItems.REDSTONE_ALLOY_INGOT);

        basicItem(EPItems.ADVANCED_ALLOY_INGOT);

        basicItem(EPItems.ADVANCED_ALLOY_PLATE);

        basicItem(EPItems.IRON_GEAR);

        basicItem(EPItems.IRON_ROD);

        basicItem(EPItems.TIN_WIRE);
        basicItem(EPItems.COPPER_WIRE);
        basicItem(EPItems.GOLD_WIRE);

        basicItem(EPItems.SAWDUST);

        basicItem(EPItems.CHARCOAL_DUST);

        basicItem(EPItems.BASIC_FERTILIZER);
        basicItem(EPItems.GOOD_FERTILIZER);
        basicItem(EPItems.ADVANCED_FERTILIZER);

        basicItem(EPItems.RAW_GEAR_PRESS_MOLD);
        basicItem(EPItems.RAW_ROD_PRESS_MOLD);
        basicItem(EPItems.RAW_WIRE_PRESS_MOLD);

        basicItem(EPItems.GEAR_PRESS_MOLD);
        basicItem(EPItems.ROD_PRESS_MOLD);
        basicItem(EPItems.WIRE_PRESS_MOLD);

        basicItem(EPItems.BASIC_SOLAR_CELL);
        basicItem(EPItems.ADVANCED_SOLAR_CELL);
        basicItem(EPItems.REINFORCED_ADVANCED_SOLAR_CELL);

        basicItem(EPItems.BASIC_CIRCUIT);
        basicItem(EPItems.ADVANCED_CIRCUIT);
        basicItem(EPItems.PROCESSING_UNIT);

        basicItem(EPItems.TELEPORTER_MATRIX);
        basicItem(EPItems.TELEPORTER_PROCESSING_UNIT);

        basicItem(EPItems.BASIC_UPGRADE_MODULE);
        basicItem(EPItems.ADVANCED_UPGRADE_MODULE);
        basicItem(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE);

        basicItem(EPItems.SPEED_UPGRADE_MODULE_1);
        basicItem(EPItems.SPEED_UPGRADE_MODULE_2);
        basicItem(EPItems.SPEED_UPGRADE_MODULE_3);
        basicItem(EPItems.SPEED_UPGRADE_MODULE_4);
        basicItem(EPItems.SPEED_UPGRADE_MODULE_5);

        basicItem(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1);
        basicItem(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2);
        basicItem(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3);
        basicItem(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4);
        basicItem(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5);

        basicItem(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1);
        basicItem(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2);
        basicItem(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3);
        basicItem(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4);
        basicItem(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5);

        basicItem(EPItems.DURATION_UPGRADE_MODULE_1);
        basicItem(EPItems.DURATION_UPGRADE_MODULE_2);
        basicItem(EPItems.DURATION_UPGRADE_MODULE_3);
        basicItem(EPItems.DURATION_UPGRADE_MODULE_4);
        basicItem(EPItems.DURATION_UPGRADE_MODULE_5);
        basicItem(EPItems.DURATION_UPGRADE_MODULE_6);

        basicItem(EPItems.RANGE_UPGRADE_MODULE_1);
        basicItem(EPItems.RANGE_UPGRADE_MODULE_2);
        basicItem(EPItems.RANGE_UPGRADE_MODULE_3);

        basicItem(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1);
        basicItem(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2);
        basicItem(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3);
        basicItem(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4);
        basicItem(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5);

        basicItem(EPItems.BLAST_FURNACE_UPGRADE_MODULE);
        basicItem(EPItems.SMOKER_UPGRADE_MODULE);

        basicItem(EPItems.MOON_LIGHT_UPGRADE_MODULE_1);
        basicItem(EPItems.MOON_LIGHT_UPGRADE_MODULE_2);
        basicItem(EPItems.MOON_LIGHT_UPGRADE_MODULE_3);

        basicItem(EPItems.ENERGIZED_POWER_BOOK);

        basicItem(EPItems.CABLE_INSULATOR);

        basicItem(EPItems.CHARCOAL_FILTER);

        basicItem(EPItems.SAW_BLADE);

        basicItem(EPItems.CRYSTAL_MATRIX);

        basicItem(EPItems.ENERGIZED_CRYSTAL_MATRIX);

        basicItem(EPItems.INVENTORY_CHARGER);

        basicItem(EPItems.INVENTORY_TELEPORTER);

        basicItem(EPItems.BATTERY_1);
        basicItem(EPItems.BATTERY_2);
        basicItem(EPItems.BATTERY_3);
        basicItem(EPItems.BATTERY_4);
        basicItem(EPItems.BATTERY_5);
        basicItem(EPItems.BATTERY_6);
        basicItem(EPItems.BATTERY_7);
        basicItem(EPItems.BATTERY_8);
        basicItem(EPItems.CREATIVE_BATTERY);

        basicItem(EPItems.ENERGY_ANALYZER);

        basicItem(EPItems.FLUID_ANALYZER);

        basicItem(EPItems.WOODEN_HAMMER);
        basicItem(EPItems.STONE_HAMMER);
        basicItem(EPItems.IRON_HAMMER);
        basicItem(EPItems.GOLDEN_HAMMER);
        basicItem(EPItems.DIAMOND_HAMMER);
        basicItem(EPItems.NETHERITE_HAMMER);

        basicItem(EPItems.CUTTER);

        basicItem(EPItems.WRENCH);

        basicItem(EPItems.BATTERY_BOX_MINECART);
        basicItem(EPItems.ADVANCED_BATTERY_BOX_MINECART);

        basicItem(EPFluids.DIRTY_WATER_BUCKET_ITEM);
    }

    private void registerSpecialModels() {
        Identifier inventoryCoalEngineActive = basicItem(EPItems.INVENTORY_COAL_ENGINE, "_active");
        Identifier inventoryCoalEngineOn = basicItem(EPItems.INVENTORY_COAL_ENGINE, "_on");

        generator.writer.accept(ModelIds.getItemModelId(EPItems.INVENTORY_COAL_ENGINE), new ItemWithOverridesModelSupplier(
                ModelIds.getItemModelId(EPItems.INVENTORY_COAL_ENGINE),
                List.of(
                        new ItemWithOverridesModelSupplier.ItemPredicateOverrides(
                                List.of(
                                        new ItemWithOverridesModelSupplier.ItemPredicateValue(
                                                EPAPI.id("active"),
                                                1.f
                                        )
                                ),
                                inventoryCoalEngineActive
                        ),
                        new ItemWithOverridesModelSupplier.ItemPredicateOverrides(
                                List.of(
                                        new ItemWithOverridesModelSupplier.ItemPredicateValue(
                                                EPAPI.id("active"),
                                                1.f
                                        ),
                                        new ItemWithOverridesModelSupplier.ItemPredicateValue(
                                                EPAPI.id("working"),
                                                1.f
                                        )
                                ),
                                inventoryCoalEngineOn
                        )
                )
        ));
    }

    private Identifier basicItem(Item item) {
        generator.register(item, Models.GENERATED);

        return ModelIds.getItemModelId(item);
    }

    private Identifier basicItem(Item item, String suffix) {
        generator.register(item, suffix, Models.GENERATED);

        return ModelIds.getItemSubModelId(item, suffix);
    }
}
