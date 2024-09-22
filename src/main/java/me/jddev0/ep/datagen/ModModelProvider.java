package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.datagen.model.ItemWithDisplayModelSupplier;
import me.jddev0.ep.datagen.model.ItemWithOverridesModelSupplier;
import me.jddev0.ep.datagen.model.ModTexturedModel;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;

import java.util.List;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataGenerator output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        registerBlocks(generator);
    }

    private void registerBlocks(BlockStateModelGenerator generator) {
        cubeAllBlockWithItem(generator, ModBlocks.SILICON_BLOCK);

        cubeAllBlockWithItem(generator, ModBlocks.TIN_BLOCK);

        cubeAllBlockWithItem(generator, ModBlocks.SAWDUST_BLOCK);

        cubeAllBlockWithItem(generator, ModBlocks.TIN_ORE);
        cubeAllBlockWithItem(generator, ModBlocks.DEEPSLATE_TIN_ORE);

        cubeAllBlockWithItem(generator, ModBlocks.RAW_TIN_BLOCK);

        cableBlockWithItem(generator, ModBlocks.TIN_CABLE);
        cableBlockWithItem(generator, ModBlocks.COPPER_CABLE);
        cableBlockWithItem(generator, ModBlocks.GOLD_CABLE);
        cableBlockWithItem(generator, ModBlocks.ENERGIZED_COPPER_CABLE);
        cableBlockWithItem(generator, ModBlocks.ENERGIZED_GOLD_CABLE);
        cableBlockWithItem(generator, ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE);
    }

    private void cubeAllBlockWithItem(BlockStateModelGenerator generator, Block block) {
        generator.registerSimpleCubeAll(block);
    }

    private void cableBlockWithItem(BlockStateModelGenerator generator, Block block) {
        Identifier cableCore = ModTexturedModel.CABLE_CORE.get(block).upload(block, "_core", generator.modelCollector);
        Identifier cableSide = ModTexturedModel.CABLE_SIDE.get(block).upload(block, "_side", generator.modelCollector);

        generator.blockStateCollector.accept(
                MultipartBlockStateSupplier.create(block).
                        with(BlockStateVariant.create().
                                put(VariantSettings.MODEL, cableCore)).
                        with(When.create().set(CableBlock.UP, true), BlockStateVariant.create().
                                put(VariantSettings.MODEL, cableSide).
                                put(VariantSettings.X, VariantSettings.Rotation.R270)).
                        with(When.create().set(CableBlock.DOWN, true), BlockStateVariant.create().
                                put(VariantSettings.MODEL, cableSide).
                                put(VariantSettings.X, VariantSettings.Rotation.R90)).
                        with(When.create().set(CableBlock.NORTH, true), BlockStateVariant.create().
                                put(VariantSettings.MODEL, cableSide)).
                        with(When.create().set(CableBlock.SOUTH, true), BlockStateVariant.create().
                                put(VariantSettings.MODEL, cableSide).
                                put(VariantSettings.X, VariantSettings.Rotation.R180)).
                        with(When.create().set(CableBlock.EAST, true), BlockStateVariant.create().
                                put(VariantSettings.MODEL, cableSide).
                                put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                        with(When.create().set(CableBlock.WEST, true), BlockStateVariant.create().
                                put(VariantSettings.MODEL, cableSide).
                                put(VariantSettings.Y, VariantSettings.Rotation.R270))
        );

        generator.modelCollector.accept(ModelIds.getItemModelId(block.asItem()), new ItemWithDisplayModelSupplier(cableCore,
                new Vec3f(1.01f, 1.01f, 1.01f),
                new Vec3f(1.5f, 1.5f, 1.5f),
                new Vec3i(30, 45, 0)
        ));
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        registerBasicModels(generator);
        registerSpecialModels(generator);
    }

    private void registerBasicModels(ItemModelGenerator generator) {
        basicItem(generator, ModItems.ENERGIZED_COPPER_INGOT);
        basicItem(generator, ModItems.ENERGIZED_GOLD_INGOT);

        basicItem(generator, ModItems.ENERGIZED_COPPER_PLATE);
        basicItem(generator, ModItems.ENERGIZED_GOLD_PLATE);

        basicItem(generator, ModItems.ENERGIZED_COPPER_WIRE);
        basicItem(generator, ModItems.ENERGIZED_GOLD_WIRE);

        basicItem(generator, ModItems.SILICON);

        basicItem(generator, ModItems.STONE_PEBBLE);

        basicItem(generator, ModItems.RAW_TIN);

        basicItem(generator, ModItems.TIN_DUST);
        basicItem(generator, ModItems.COPPER_DUST);
        basicItem(generator, ModItems.IRON_DUST);
        basicItem(generator, ModItems.GOLD_DUST);

        basicItem(generator, ModItems.TIN_NUGGET);

        basicItem(generator, ModItems.TIN_INGOT);

        basicItem(generator, ModItems.TIN_PLATE);
        basicItem(generator, ModItems.COPPER_PLATE);
        basicItem(generator, ModItems.IRON_PLATE);
        basicItem(generator, ModItems.GOLD_PLATE);

        basicItem(generator, ModItems.STEEL_INGOT);

        basicItem(generator, ModItems.REDSTONE_ALLOY_INGOT);

        basicItem(generator, ModItems.ADVANCED_ALLOY_INGOT);

        basicItem(generator, ModItems.ADVANCED_ALLOY_PLATE);

        basicItem(generator, ModItems.IRON_GEAR);

        basicItem(generator, ModItems.IRON_ROD);

        basicItem(generator, ModItems.TIN_WIRE);
        basicItem(generator, ModItems.COPPER_WIRE);
        basicItem(generator, ModItems.GOLD_WIRE);

        basicItem(generator, ModItems.SAWDUST);

        basicItem(generator, ModItems.CHARCOAL_DUST);

        basicItem(generator, ModItems.BASIC_FERTILIZER);
        basicItem(generator, ModItems.GOOD_FERTILIZER);
        basicItem(generator, ModItems.ADVANCED_FERTILIZER);

        basicItem(generator, ModItems.RAW_GEAR_PRESS_MOLD);
        basicItem(generator, ModItems.RAW_ROD_PRESS_MOLD);
        basicItem(generator, ModItems.RAW_WIRE_PRESS_MOLD);

        basicItem(generator, ModItems.GEAR_PRESS_MOLD);
        basicItem(generator, ModItems.ROD_PRESS_MOLD);
        basicItem(generator, ModItems.WIRE_PRESS_MOLD);

        basicItem(generator, ModItems.BASIC_SOLAR_CELL);
        basicItem(generator, ModItems.ADVANCED_SOLAR_CELL);
        basicItem(generator, ModItems.REINFORCED_ADVANCED_SOLAR_CELL);

        basicItem(generator, ModItems.BASIC_CIRCUIT);
        basicItem(generator, ModItems.ADVANCED_CIRCUIT);
        basicItem(generator, ModItems.PROCESSING_UNIT);

        basicItem(generator, ModItems.TELEPORTER_MATRIX);
        basicItem(generator, ModItems.TELEPORTER_PROCESSING_UNIT);

        basicItem(generator, ModItems.BASIC_UPGRADE_MODULE);
        basicItem(generator, ModItems.ADVANCED_UPGRADE_MODULE);
        basicItem(generator, ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE);

        basicItem(generator, ModItems.SPEED_UPGRADE_MODULE_1);
        basicItem(generator, ModItems.SPEED_UPGRADE_MODULE_2);
        basicItem(generator, ModItems.SPEED_UPGRADE_MODULE_3);
        basicItem(generator, ModItems.SPEED_UPGRADE_MODULE_4);
        basicItem(generator, ModItems.SPEED_UPGRADE_MODULE_5);

        basicItem(generator, ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1);
        basicItem(generator, ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2);
        basicItem(generator, ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3);
        basicItem(generator, ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4);
        basicItem(generator, ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5);

        basicItem(generator, ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_1);
        basicItem(generator, ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_2);
        basicItem(generator, ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3);
        basicItem(generator, ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4);
        basicItem(generator, ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_5);

        basicItem(generator, ModItems.DURATION_UPGRADE_MODULE_1);
        basicItem(generator, ModItems.DURATION_UPGRADE_MODULE_2);
        basicItem(generator, ModItems.DURATION_UPGRADE_MODULE_3);
        basicItem(generator, ModItems.DURATION_UPGRADE_MODULE_4);
        basicItem(generator, ModItems.DURATION_UPGRADE_MODULE_5);
        basicItem(generator, ModItems.DURATION_UPGRADE_MODULE_6);

        basicItem(generator, ModItems.RANGE_UPGRADE_MODULE_1);
        basicItem(generator, ModItems.RANGE_UPGRADE_MODULE_2);
        basicItem(generator, ModItems.RANGE_UPGRADE_MODULE_3);

        basicItem(generator, ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1);
        basicItem(generator, ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2);
        basicItem(generator, ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3);
        basicItem(generator, ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4);
        basicItem(generator, ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5);

        basicItem(generator, ModItems.BLAST_FURNACE_UPGRADE_MODULE);
        basicItem(generator, ModItems.SMOKER_UPGRADE_MODULE);

        basicItem(generator, ModItems.MOON_LIGHT_UPGRADE_MODULE_1);
        basicItem(generator, ModItems.MOON_LIGHT_UPGRADE_MODULE_2);
        basicItem(generator, ModItems.MOON_LIGHT_UPGRADE_MODULE_3);

        basicItem(generator, ModItems.ENERGIZED_POWER_BOOK);

        basicItem(generator, ModItems.CABLE_INSULATOR);

        basicItem(generator, ModItems.CHARCOAL_FILTER);

        basicItem(generator, ModItems.SAW_BLADE);

        basicItem(generator, ModItems.CRYSTAL_MATRIX);

        basicItem(generator, ModItems.ENERGIZED_CRYSTAL_MATRIX);

        basicItem(generator, ModItems.INVENTORY_CHARGER);

        basicItem(generator, ModItems.INVENTORY_TELEPORTER);

        basicItem(generator, ModItems.BATTERY_1);
        basicItem(generator, ModItems.BATTERY_2);
        basicItem(generator, ModItems.BATTERY_3);
        basicItem(generator, ModItems.BATTERY_4);
        basicItem(generator, ModItems.BATTERY_5);
        basicItem(generator, ModItems.BATTERY_6);
        basicItem(generator, ModItems.BATTERY_7);
        basicItem(generator, ModItems.BATTERY_8);

        basicItem(generator, ModItems.ENERGY_ANALYZER);

        basicItem(generator, ModItems.FLUID_ANALYZER);

        basicItem(generator, ModItems.WOODEN_HAMMER);
        basicItem(generator, ModItems.STONE_HAMMER);
        basicItem(generator, ModItems.IRON_HAMMER);
        basicItem(generator, ModItems.GOLDEN_HAMMER);
        basicItem(generator, ModItems.DIAMOND_HAMMER);
        basicItem(generator, ModItems.NETHERITE_HAMMER);

        basicItem(generator, ModItems.CUTTER);

        basicItem(generator, ModItems.WRENCH);

        basicItem(generator, ModItems.BATTERY_BOX_MINECART);
        basicItem(generator, ModItems.ADVANCED_BATTERY_BOX_MINECART);

        basicItem(generator, ModFluids.DIRTY_WATER_BUCKET_ITEM);
    }

    private void registerSpecialModels(ItemModelGenerator generator) {
        Identifier inventoryCoalEngineActive = basicItem(generator, ModItems.INVENTORY_COAL_ENGINE, "_active");
        Identifier inventoryCoalEngineOn = basicItem(generator, ModItems.INVENTORY_COAL_ENGINE, "_on");

        generator.writer.accept(ModelIds.getItemModelId(ModItems.INVENTORY_COAL_ENGINE), new ItemWithOverridesModelSupplier(
                ModelIds.getItemModelId(ModItems.INVENTORY_COAL_ENGINE),
                List.of(
                        new ItemWithOverridesModelSupplier.ItemPredicateOverrides(
                                List.of(
                                        new ItemWithOverridesModelSupplier.ItemPredicateValue(
                                                Identifier.of(EnergizedPowerMod.MODID, "active"),
                                                1.f
                                        )
                                ),
                                inventoryCoalEngineActive
                        ),
                        new ItemWithOverridesModelSupplier.ItemPredicateOverrides(
                                List.of(
                                        new ItemWithOverridesModelSupplier.ItemPredicateValue(
                                                Identifier.of(EnergizedPowerMod.MODID, "active"),
                                                1.f
                                        ),
                                        new ItemWithOverridesModelSupplier.ItemPredicateValue(
                                                Identifier.of(EnergizedPowerMod.MODID, "working"),
                                                1.f
                                        )
                                ),
                                inventoryCoalEngineOn
                        )
                )
        ));
    }

    private Identifier basicItem(ItemModelGenerator generator, Item item) {
        generator.register(item, Models.GENERATED);

        return ModelIds.getItemModelId(item);
    }

    private Identifier basicItem(ItemModelGenerator generator, Item item, String suffix) {
        generator.register(item, suffix, Models.GENERATED);

        return ModelIds.getItemSubModelId(item, suffix);
    }
}
