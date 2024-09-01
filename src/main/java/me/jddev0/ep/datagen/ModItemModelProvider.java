package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EnergizedPowerMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerBasicModels();
        registerSpecialModels();
    }

    private void registerBasicModels() {
        basicItem(ModItems.ENERGIZED_COPPER_INGOT);
        basicItem(ModItems.ENERGIZED_GOLD_INGOT);

        basicItem(ModItems.ENERGIZED_COPPER_PLATE);
        basicItem(ModItems.ENERGIZED_GOLD_PLATE);

        basicItem(ModItems.ENERGIZED_COPPER_WIRE);
        basicItem(ModItems.ENERGIZED_GOLD_WIRE);

        basicItem(ModItems.SILICON);

        basicItem(ModItems.STONE_PEBBLE);

        basicItem(ModItems.RAW_TIN);

        basicItem(ModItems.TIN_DUST);
        basicItem(ModItems.COPPER_DUST);
        basicItem(ModItems.IRON_DUST);
        basicItem(ModItems.GOLD_DUST);

        basicItem(ModItems.TIN_NUGGET);

        basicItem(ModItems.TIN_INGOT);

        basicItem(ModItems.TIN_PLATE);
        basicItem(ModItems.COPPER_PLATE);
        basicItem(ModItems.IRON_PLATE);
        basicItem(ModItems.GOLD_PLATE);

        basicItem(ModItems.STEEL_INGOT);

        basicItem(ModItems.REDSTONE_ALLOY_INGOT);

        basicItem(ModItems.ADVANCED_ALLOY_INGOT);

        basicItem(ModItems.ADVANCED_ALLOY_PLATE);

        basicItem(ModItems.IRON_GEAR);

        basicItem(ModItems.IRON_ROD);

        basicItem(ModItems.TIN_WIRE);
        basicItem(ModItems.COPPER_WIRE);
        basicItem(ModItems.GOLD_WIRE);

        basicItem(ModItems.SAWDUST);

        basicItem(ModItems.CHARCOAL_DUST);

        basicItem(ModItems.BASIC_FERTILIZER);
        basicItem(ModItems.GOOD_FERTILIZER);
        basicItem(ModItems.ADVANCED_FERTILIZER);

        basicItem(ModItems.RAW_GEAR_PRESS_MOLD);
        basicItem(ModItems.RAW_ROD_PRESS_MOLD);
        basicItem(ModItems.RAW_WIRE_PRESS_MOLD);

        basicItem(ModItems.GEAR_PRESS_MOLD);
        basicItem(ModItems.ROD_PRESS_MOLD);
        basicItem(ModItems.WIRE_PRESS_MOLD);

        basicItem(ModItems.BASIC_SOLAR_CELL);
        basicItem(ModItems.ADVANCED_SOLAR_CELL);
        basicItem(ModItems.REINFORCED_ADVANCED_SOLAR_CELL);

        basicItem(ModItems.BASIC_CIRCUIT);
        basicItem(ModItems.ADVANCED_CIRCUIT);
        basicItem(ModItems.PROCESSING_UNIT);

        basicItem(ModItems.TELEPORTER_MATRIX);
        basicItem(ModItems.TELEPORTER_PROCESSING_UNIT);

        basicItem(ModItems.BASIC_UPGRADE_MODULE);
        basicItem(ModItems.ADVANCED_UPGRADE_MODULE);
        basicItem(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE);

        basicItem(ModItems.SPEED_UPGRADE_MODULE_1);
        basicItem(ModItems.SPEED_UPGRADE_MODULE_2);
        basicItem(ModItems.SPEED_UPGRADE_MODULE_3);
        basicItem(ModItems.SPEED_UPGRADE_MODULE_4);
        basicItem(ModItems.SPEED_UPGRADE_MODULE_5);

        basicItem(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1);
        basicItem(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2);
        basicItem(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3);
        basicItem(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4);
        basicItem(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5);

        basicItem(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_1);
        basicItem(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_2);
        basicItem(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3);
        basicItem(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4);
        basicItem(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_5);

        basicItem(ModItems.DURATION_UPGRADE_MODULE_1);
        basicItem(ModItems.DURATION_UPGRADE_MODULE_2);
        basicItem(ModItems.DURATION_UPGRADE_MODULE_3);
        basicItem(ModItems.DURATION_UPGRADE_MODULE_4);
        basicItem(ModItems.DURATION_UPGRADE_MODULE_5);
        basicItem(ModItems.DURATION_UPGRADE_MODULE_6);

        basicItem(ModItems.RANGE_UPGRADE_MODULE_1);
        basicItem(ModItems.RANGE_UPGRADE_MODULE_2);
        basicItem(ModItems.RANGE_UPGRADE_MODULE_3);

        basicItem(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1);
        basicItem(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2);
        basicItem(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3);
        basicItem(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4);
        basicItem(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5);

        basicItem(ModItems.BLAST_FURNACE_UPGRADE_MODULE);
        basicItem(ModItems.SMOKER_UPGRADE_MODULE);

        basicItem(ModItems.MOON_LIGHT_UPGRADE_MODULE_1);
        basicItem(ModItems.MOON_LIGHT_UPGRADE_MODULE_2);
        basicItem(ModItems.MOON_LIGHT_UPGRADE_MODULE_3);

        basicItem(ModItems.ENERGIZED_POWER_BOOK);

        basicItem(ModItems.CABLE_INSULATOR);

        basicItem(ModItems.CHARCOAL_FILTER);

        basicItem(ModItems.SAW_BLADE);

        basicItem(ModItems.CRYSTAL_MATRIX);

        basicItem(ModItems.ENERGIZED_CRYSTAL_MATRIX);

        basicItem(ModItems.INVENTORY_CHARGER);

        basicItem(ModItems.INVENTORY_TELEPORTER);

        basicItem(ModItems.BATTERY_1);
        basicItem(ModItems.BATTERY_2);
        basicItem(ModItems.BATTERY_3);
        basicItem(ModItems.BATTERY_4);
        basicItem(ModItems.BATTERY_5);
        basicItem(ModItems.BATTERY_6);
        basicItem(ModItems.BATTERY_7);
        basicItem(ModItems.BATTERY_8);

        basicItem(ModItems.ENERGY_ANALYZER);

        basicItem(ModItems.FLUID_ANALYZER);

        basicItem(ModItems.WOODEN_HAMMER);
        basicItem(ModItems.STONE_HAMMER);
        basicItem(ModItems.IRON_HAMMER);
        basicItem(ModItems.GOLDEN_HAMMER);
        basicItem(ModItems.DIAMOND_HAMMER);
        basicItem(ModItems.NETHERITE_HAMMER);

        basicItem(ModItems.CUTTER);

        basicItem(ModItems.WRENCH);

        basicItem(ModItems.BATTERY_BOX_MINECART);
        basicItem(ModItems.ADVANCED_BATTERY_BOX_MINECART);
    }

    private void registerSpecialModels() {
        ModelFile inventoryCoalEngineActive = basicItem(ModItems.INVENTORY_COAL_ENGINE, "_active");
        ModelFile inventoryCoalEngineOn = basicItem(ModItems.INVENTORY_COAL_ENGINE, "_on");

        ResourceLocation inventoryCoalEngineItemId = Objects.requireNonNull(ModItems.INVENTORY_COAL_ENGINE.getKey()).location();

        withExistingParent(inventoryCoalEngineItemId.getPath(), "generated").
                texture("layer0", ResourceLocation.fromNamespaceAndPath(inventoryCoalEngineItemId.getNamespace(),
                        "item/" + inventoryCoalEngineItemId.getPath())).
                override().
                predicate(ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "active"), 1.f).
                model(inventoryCoalEngineActive).
                end().
                override().
                predicate(ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "active"), 1.f).
                predicate(ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "working"), 1.f).
                model(inventoryCoalEngineOn).
                end();
    }

    private ItemModelBuilder basicItem(Holder<Item> item) {
        ResourceLocation itemID = Objects.requireNonNull(item.getKey()).location();

        return withExistingParent(itemID.getPath(), "generated")
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(itemID.getNamespace(), "item/" + itemID.getPath()));
    }

    private ItemModelBuilder basicItem(Holder<Item> item, String pathSuffix) {
        ResourceLocation itemID = Objects.requireNonNull(item.getKey()).location();

        return withExistingParent(itemID.getPath() + pathSuffix, "generated")
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(itemID.getNamespace(), "item/" + itemID.getPath() + pathSuffix));
    }
}
