package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.*;
import me.jddev0.ep.datagen.model.ModModels;
import me.jddev0.ep.datagen.model.ModTexturedModel;
import me.jddev0.ep.machine.tier.TransformerType;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.Direction;

class ModBlockStateProvider {
    private final BlockStateModelGenerator generator;

    ModBlockStateProvider(BlockStateModelGenerator generator) {
        this.generator = generator;
    }

    void registerBlocks() {
        cubeAllBlockWithItem(EPBlocks.SILICON_BLOCK);

        cubeAllBlockWithItem(EPBlocks.TIN_BLOCK);

        cubeAllBlockWithItem(EPBlocks.SAWDUST_BLOCK);

        cubeAllBlockWithItem(EPBlocks.TIN_ORE);
        cubeAllBlockWithItem(EPBlocks.DEEPSLATE_TIN_ORE);

        cubeAllBlockWithItem(EPBlocks.RAW_TIN_BLOCK);

        itemConveyorBeltBlockWithItem(EPBlocks.BASIC_ITEM_CONVEYOR_BELT);
        itemConveyorBeltBlockWithItem(EPBlocks.FAST_ITEM_CONVEYOR_BELT);
        itemConveyorBeltBlockWithItem(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT);

        orientableSixDirsBlockWithBackItem(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER, false);
        orientableSixDirsBlockWithBackItem(EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER, false);
        orientableSixDirsBlockWithBackItem(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER, false);

        orientableBlockWithItem(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER,
                cubeBlockModel(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER, "", "_top", "_top",
                        "_input", "_output_2", "_output_3", "_output_1"));
        orientableBlockWithItem(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER,
                cubeBlockModel(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER, "", "_top", "_top",
                        "_input", "_output_2", "_output_3", "_output_1"));
        orientableBlockWithItem(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER,
                cubeBlockModel(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER, "", "_top", "_top",
                        "_input", "_output_2", "_output_3", "_output_1"));

        activatableOrientableBlockWithItem(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH,
                cubeBlockModel(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH, "", "_top", "_top",
                        "_input", "_side", "_output_disabled", "_output_enabled"),
                cubeBlockModel(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH, "_powered", "_top", "_top",
                        "_input", "_side", "_output_enabled", "_output_disabled"),
                ItemConveyorBeltSwitchBlock.POWERED);
        activatableOrientableBlockWithItem(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH,
                cubeBlockModel(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH, "", "_top", "_top",
                        "_input", "_side", "_output_disabled", "_output_enabled"),
                cubeBlockModel(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH, "_powered", "_top", "_top",
                        "_input", "_side", "_output_enabled", "_output_disabled"),
                ItemConveyorBeltSwitchBlock.POWERED);
        activatableOrientableBlockWithItem(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH,
                cubeBlockModel(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH, "", "_top", "_top",
                        "_input", "_side", "_output_disabled", "_output_enabled"),
                cubeBlockModel(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH, "_powered", "_top", "_top",
                        "_input", "_side", "_output_enabled", "_output_disabled"),
                ItemConveyorBeltSwitchBlock.POWERED);

        orientableBlockWithItem(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER,
                cubeBlockModel(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER, "", "_top", "_top",
                        "_input", "_output", "_output", "_output"));
        orientableBlockWithItem(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER,
                cubeBlockModel(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER, "", "_top", "_top",
                        "_input", "_output", "_output", "_output"));
        orientableBlockWithItem(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER,
                cubeBlockModel(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER, "", "_top", "_top",
                        "_input", "_output", "_output", "_output"));

        orientableBlockWithItem(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER,
                cubeBlockModel(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER, "", "_top", "_top",
                        "_output", "_input", "_input", "_input"));
        orientableBlockWithItem(EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER,
                cubeBlockModel(EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER, "", "_top", "_top",
                        "_output", "_input", "_input", "_input"));
        orientableBlockWithItem(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER,
                cubeBlockModel(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER, "", "_top", "_top",
                        "_output", "_input", "_input", "_input"));

        fluidPipeBlockWithItem(EPBlocks.IRON_FLUID_PIPE);
        fluidPipeBlockWithItem(EPBlocks.GOLDEN_FLUID_PIPE);

        fluidTankBlockWithItem(EPBlocks.FLUID_TANK_SMALL);
        fluidTankBlockWithItem(EPBlocks.FLUID_TANK_MEDIUM);
        fluidTankBlockWithItem(EPBlocks.FLUID_TANK_LARGE);
        fluidTankBlockWithItem(EPBlocks.CREATIVE_FLUID_TANK);

        horizontalBlockWithItem(EPBlocks.ITEM_SILO_TINY, false);
        horizontalBlockWithItem(EPBlocks.ITEM_SILO_SMALL, false);
        horizontalBlockWithItem(EPBlocks.ITEM_SILO_MEDIUM, false);
        horizontalBlockWithItem(EPBlocks.ITEM_SILO_LARGE, false);
        horizontalBlockWithItem(EPBlocks.ITEM_SILO_GIANT, false);
        horizontalBlockWithItem(EPBlocks.CREATIVE_ITEM_SILO, false);

        cableBlockWithItem(EPBlocks.TIN_CABLE);
        cableBlockWithItem(EPBlocks.COPPER_CABLE);
        cableBlockWithItem(EPBlocks.GOLD_CABLE);
        cableBlockWithItem(EPBlocks.ENERGIZED_COPPER_CABLE);
        cableBlockWithItem(EPBlocks.ENERGIZED_GOLD_CABLE);
        cableBlockWithItem(EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE);

        transformerBlockWithItem(EPBlocks.LV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(EPBlocks.LV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(EPBlocks.LV_TRANSFORMER_N_TO_1);
        transformerBlockWithItem(EPBlocks.MV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(EPBlocks.MV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(EPBlocks.MV_TRANSFORMER_N_TO_1);
        transformerBlockWithItem(EPBlocks.HV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(EPBlocks.HV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(EPBlocks.HV_TRANSFORMER_N_TO_1);
        transformerBlockWithItem(EPBlocks.EHV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(EPBlocks.EHV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(EPBlocks.EHV_TRANSFORMER_N_TO_1);

        configurableTransformerBlockWithItem(EPBlocks.CONFIGURABLE_LV_TRANSFORMER);
        configurableTransformerBlockWithItem(EPBlocks.CONFIGURABLE_MV_TRANSFORMER);
        configurableTransformerBlockWithItem(EPBlocks.CONFIGURABLE_HV_TRANSFORMER);
        configurableTransformerBlockWithItem(EPBlocks.CONFIGURABLE_EHV_TRANSFORMER);

        horizontalBlockWithItem(EPBlocks.BATTERY_BOX, true);
        horizontalBlockWithItem(EPBlocks.ADVANCED_BATTERY_BOX, true);
        horizontalBlockWithItem(EPBlocks.CREATIVE_BATTERY_BOX, true);

        horizontalTwoSideBlockWithItem(EPBlocks.PRESS_MOLD_MAKER, true);

        activatableOrientableBlockWithItem(EPBlocks.ALLOY_FURNACE,
                orientableBlockModel(EPBlocks.ALLOY_FURNACE, false),
                orientableOnBlockModel(EPBlocks.ALLOY_FURNACE, false),
                AlloyFurnaceBlock.LIT);

        horizontalTwoSideBlockWithItem(EPBlocks.AUTO_CRAFTER, true);
        horizontalTwoSideBlockWithItem(EPBlocks.ADVANCED_AUTO_CRAFTER, true);

        horizontalBlockWithItem(EPBlocks.CRUSHER, true);
        horizontalBlockWithItem(EPBlocks.ADVANCED_CRUSHER, true);

        horizontalBlockWithItem(EPBlocks.PULVERIZER, true);
        horizontalBlockWithItem(EPBlocks.ADVANCED_PULVERIZER, true);

        horizontalBlockWithItem(EPBlocks.SAWMILL, true);

        horizontalBlockWithItem(EPBlocks.COMPRESSOR, true);

        horizontalBlockWithItem(EPBlocks.METAL_PRESS, false);

        horizontalTwoSideBlockWithItem(EPBlocks.AUTO_PRESS_MOLD_MAKER, true);

        horizontalBlockWithItem(EPBlocks.AUTO_STONECUTTER, false);

        orientableBlockWithItem(EPBlocks.PLANT_GROWTH_CHAMBER,
                cubeBlockModel(EPBlocks.PLANT_GROWTH_CHAMBER, "", "_top", "_top",
                        "_front", "_front", "_side", "_side"));

        orientableSixDirsBlockWithItem(EPBlocks.BLOCK_PLACER, true);

        orientableBlockWithItem(EPBlocks.ASSEMBLING_MACHINE,
                orientableBlockModel(EPBlocks.ASSEMBLING_MACHINE, false));

        activatableOrientableMachineBlockWithItem(EPBlocks.INDUCTION_SMELTER, false);

        horizontalBlockWithItem(EPBlocks.FLUID_FILLER, true);

        orientableBlockWithItem(EPBlocks.STONE_LIQUEFIER,
                orientableBlockModel(EPBlocks.STONE_LIQUEFIER, false));

        orientableBlockWithItem(EPBlocks.STONE_SOLIDIFIER,
                orientableBlockModel(EPBlocks.STONE_SOLIDIFIER, false));

        orientableBlockWithItem(EPBlocks.FLUID_TRANSPOSER,
                orientableBlockModel(EPBlocks.FLUID_TRANSPOSER, false));

        horizontalTwoSideBlockWithItem(EPBlocks.FILTRATION_PLANT, false);

        horizontalBlockWithItem(EPBlocks.FLUID_DRAINER, true);

        horizontalBlockWithItem(EPBlocks.FLUID_PUMP, false);
        horizontalBlockWithItem(EPBlocks.ADVANCED_FLUID_PUMP, false);

        horizontalBlockWithItem(EPBlocks.DRAIN, true);

        horizontalBlockWithItem(EPBlocks.CHARGER, true);
        horizontalBlockWithItem(EPBlocks.ADVANCED_CHARGER, true);

        horizontalBlockWithItem(EPBlocks.UNCHARGER, true);
        horizontalBlockWithItem(EPBlocks.ADVANCED_UNCHARGER, true);

        orientableSixDirsBlockWithItem(EPBlocks.MINECART_CHARGER, true);
        orientableSixDirsBlockWithItem(EPBlocks.ADVANCED_MINECART_CHARGER, true);

        orientableSixDirsBlockWithItem(EPBlocks.MINECART_UNCHARGER, true);
        orientableSixDirsBlockWithItem(EPBlocks.ADVANCED_MINECART_UNCHARGER, true);

        solarPanelBlockWithItem(EPBlocks.SOLAR_PANEL_1);
        solarPanelBlockWithItem(EPBlocks.SOLAR_PANEL_2);
        solarPanelBlockWithItem(EPBlocks.SOLAR_PANEL_3);
        solarPanelBlockWithItem(EPBlocks.SOLAR_PANEL_4);
        solarPanelBlockWithItem(EPBlocks.SOLAR_PANEL_5);
        solarPanelBlockWithItem(EPBlocks.SOLAR_PANEL_6);

        activatableOrientableMachineBlockWithItem(EPBlocks.COAL_ENGINE, false);

        poweredLampBlockWithItem(EPBlocks.POWERED_LAMP);

        activatableOrientableMachineBlockWithItem(EPBlocks.POWERED_FURNACE, false);
        activatableOrientableMachineBlockWithItem(EPBlocks.ADVANCED_POWERED_FURNACE, false);

        activatableBlockWithItem(EPBlocks.LIGHTNING_GENERATOR,
                cubeBlockModel(EPBlocks.LIGHTNING_GENERATOR, "", "_top", "_bottom",
                        "_side", "_side", "_side", "_side"),
                cubeBlockModel(EPBlocks.LIGHTNING_GENERATOR, "_on", "_top_on", "_bottom",
                        "_side_on", "_side_on", "_side_on", "_side_on"),
                LightningGeneratorBlock.HIT_BY_LIGHTNING_BOLT);

        activatableOrientableBlockWithItem(EPBlocks.ENERGIZER,
                orientableBlockModel(EPBlocks.ENERGIZER, true),
                orientableBlockModel(EPBlocks.ENERGIZER, "_on", "_top_on", "_bottom",
                        "_front_on", "_side_on"),
                EnergizerBlock.LIT);

        activatableBlockWithItem(EPBlocks.CHARGING_STATION,
                cubeBlockModel(EPBlocks.CHARGING_STATION, "", "_top", "_bottom",
                        "_side", "_side", "_side", "_side"),
                cubeBlockModel(EPBlocks.CHARGING_STATION, "_on", "_top_on", "_bottom",
                        "_side_on", "_side_on", "_side_on", "_side_on"),
                ChargingStationBlock.CHARGING);

        horizontalBlockWithItem(EPBlocks.HEAT_GENERATOR, false);

        orientableBlockWithItem(EPBlocks.THERMAL_GENERATOR,
                orientableBlockModel(EPBlocks.THERMAL_GENERATOR, false));

        horizontalBlockWithItem(EPBlocks.CRYSTAL_GROWTH_CHAMBER, false);

        horizontalTwoSideBlockWithItem(EPBlocks.WEATHER_CONTROLLER, false);

        horizontalTwoSideBlockWithItem(EPBlocks.TIME_CONTROLLER, false);

        activatableBlockWithItem(EPBlocks.TELEPORTER,
                cubeBlockModel(EPBlocks.TELEPORTER, "", "_top", "_bottom",
                        "_side", "_side", "_side", "_side"),
                cubeBlockModel(EPBlocks.TELEPORTER, "_ready", "_top_ready", "_bottom",
                        "_side", "_side", "_side", "_side"),
                TeleporterBlock.POWERED);

        horizontalBlockWithItem(EPBlocks.BASIC_MACHINE_FRAME, false);
        horizontalBlockWithItem(EPBlocks.HARDENED_MACHINE_FRAME, false);
        horizontalBlockWithItem(EPBlocks.ADVANCED_MACHINE_FRAME, false);
        horizontalBlockWithItem(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME, false);
    }

    private void cubeAllBlockWithItem(Block block) {
        generator.registerSimpleCubeAll(block);
    }

    private Identifier cubeBlockModel(Block block, String fileSuffix, String upSuffix,
                                      String bottomSuffix, String northSuffix, String southSuffix,
                                      String westSuffix, String eastSuffix) {
        return TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.UP, TextureMap.getSubId(block, upSuffix)).
                        put(TextureKey.DOWN, TextureMap.getSubId(block, bottomSuffix)).
                        put(TextureKey.NORTH, TextureMap.getSubId(block, northSuffix)).
                        put(TextureKey.SOUTH, TextureMap.getSubId(block, southSuffix)).
                        put(TextureKey.EAST, TextureMap.getSubId(block, eastSuffix)).
                        put(TextureKey.WEST, TextureMap.getSubId(block, westSuffix)).
                        copy(TextureKey.UP, TextureKey.PARTICLE),
                Models.CUBE).get(block).upload(block, fileSuffix, generator.modelCollector);
    }

    private Identifier orientableBlockModel(Block block, boolean uniqueBottomTexture) {
        return orientableBlockModel(block, "", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_side");
    }

    private Identifier orientableOnBlockModel(Block block, boolean uniqueBottomTexture) {
        return orientableBlockModel(block, "_on", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front_on", "_side");
    }

    private Identifier orientableBlockModel(Block block, String fileSuffix, String topSuffix,
                                            String bottomSuffix, String frontSuffix, String sideSuffix) {
        return TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.TOP, TextureMap.getSubId(block, topSuffix)).
                        put(TextureKey.BOTTOM, TextureMap.getSubId(block, bottomSuffix)).
                        put(TextureKey.FRONT, TextureMap.getSubId(block, frontSuffix)).
                        put(TextureKey.SIDE, TextureMap.getSubId(block, sideSuffix)).
                        copy(TextureKey.TOP, TextureKey.PARTICLE),
                Models.ORIENTABLE_WITH_BOTTOM).get(block).upload(block, fileSuffix, generator.modelCollector);
    }

    private Identifier orientableWithBackBlockModel(Block block, boolean uniqueBottomTexture) {
        return orientableWithBackBlockModel(block, "", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_back", "_side");
    }

    private Identifier orientableWithBackBlockModel(Block block, String fileSuffix, String topSuffix,
                                                    String bottomSuffix, String frontSuffix, String backSuffix, String sideSuffix) {
        return TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.UP, TextureMap.getSubId(block, topSuffix)).
                        put(TextureKey.DOWN, TextureMap.getSubId(block, bottomSuffix)).
                        put(TextureKey.NORTH, TextureMap.getSubId(block, frontSuffix)).
                        put(TextureKey.SOUTH, TextureMap.getSubId(block, backSuffix)).
                        put(TextureKey.EAST, TextureMap.getSubId(block, sideSuffix)).
                        put(TextureKey.WEST, TextureMap.getSubId(block, sideSuffix)).
                        copy(TextureKey.UP, TextureKey.PARTICLE),
                Models.CUBE).get(block).upload(block, fileSuffix, generator.modelCollector);
    }

    private Identifier orientableVerticalWithBackBlockModel(Block block, boolean uniqueBottomTexture) {
        return orientableVerticalWithBackBlockModel(block, "_vertical", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_back", "_side");
    }

    private Identifier orientableVerticalWithBackBlockModel(Block block, String fileSuffix, String topSuffix,
                                                           String bottomSuffix, String frontSuffix, String backSuffix, String sideSuffix) {
        return TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.TOP, TextureMap.getSubId(block, topSuffix)).
                        put(TextureKey.BOTTOM, TextureMap.getSubId(block, bottomSuffix)).
                        put(TextureKey.FRONT, TextureMap.getSubId(block, frontSuffix)).
                        put(TextureKey.BACK, TextureMap.getSubId(block, backSuffix)).
                        put(TextureKey.SIDE, TextureMap.getSubId(block, sideSuffix)).
                        copy(TextureKey.FRONT, TextureKey.PARTICLE),
                ModModels.ORIENTABLE_VERTICAL_WITH_BACK).get(block).upload(block, fileSuffix, generator.modelCollector);
    }

    private Identifier orientableVerticalBlockModel(Block block, boolean uniqueBottomTexture) {
        return orientableVerticalBlockModel(block, "_vertical", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_side");
    }

    private Identifier orientableVerticalBlockModel(Block block, String fileSuffix, String topSuffix,
                                                   String bottomSuffix, String frontSuffix, String sideSuffix) {
        return TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.TOP, TextureMap.getSubId(block, topSuffix)).
                        put(TextureKey.BOTTOM, TextureMap.getSubId(block, bottomSuffix)).
                        put(TextureKey.FRONT, TextureMap.getSubId(block, frontSuffix)).
                        put(TextureKey.SIDE, TextureMap.getSubId(block, sideSuffix)).
                        copy(TextureKey.FRONT, TextureKey.PARTICLE),
                ModModels.ORIENTABLE_VERTICAL).get(block).upload(block, fileSuffix, generator.modelCollector);
    }

    private void horizontalBlockWithItem(Block block, boolean uniqueBottomTexture) {
        Identifier model = TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.UP, TextureMap.getSubId(block, "_top")).
                        put(TextureKey.DOWN, TextureMap.getSubId(block, uniqueBottomTexture?"_bottom":"_top")).
                        put(TextureKey.NORTH, TextureMap.getSubId(block, "_side")).
                        put(TextureKey.SOUTH, TextureMap.getSubId(block, "_side")).
                        put(TextureKey.EAST, TextureMap.getSubId(block, "_side")).
                        put(TextureKey.WEST, TextureMap.getSubId(block, "_side")).
                        copy(TextureKey.UP, TextureKey.PARTICLE),
                Models.CUBE).get(block).upload(block, generator.modelCollector);

        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block,
                new WeightedVariant(Pool.of(new ModelVariant(model)))));

        generator.registerParentedItemModel(block, model);
    }

    private void horizontalTwoSideBlockWithItem(Block block, boolean uniqueBottomTexture) {
        Identifier model = TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.UP, TextureMap.getSubId(block, "_top")).
                        put(TextureKey.DOWN, TextureMap.getSubId(block, uniqueBottomTexture?"_bottom":"_top")).
                        put(TextureKey.NORTH, TextureMap.getSubId(block, "_front")).
                        put(TextureKey.SOUTH, TextureMap.getSubId(block, "_side")).
                        put(TextureKey.EAST, TextureMap.getSubId(block, "_side")).
                        put(TextureKey.WEST, TextureMap.getSubId(block, "_front")).
                        copy(TextureKey.UP, TextureKey.PARTICLE),
                Models.CUBE).get(block).upload(block, generator.modelCollector);

        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block,
                new WeightedVariant(Pool.of(new ModelVariant(model)))));

        generator.registerParentedItemModel(block, model);
    }

    private void orientableBlockWithItem(Block block, Identifier model) {
        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, new WeightedVariant(Pool.of(new ModelVariant(model)))).
                apply(BlockStateVariantMap.operations(Properties.HORIZONTAL_FACING).
                        register(Direction.NORTH, BlockStateModelGenerator.NO_OP).
                        register(Direction.SOUTH, BlockStateModelGenerator.ROTATE_Y_180).
                        register(Direction.EAST, BlockStateModelGenerator.ROTATE_Y_90).
                        register(Direction.WEST, BlockStateModelGenerator.ROTATE_Y_270)
                ));

        generator.registerParentedItemModel(block, model);
    }

    private void orientableSixDirsBlockWithBackItem(Block block, boolean uniqueBottomTexture) {
        orientableSixDirsBlockWithItem(block,
                orientableWithBackBlockModel(block, uniqueBottomTexture),
                orientableVerticalWithBackBlockModel(block, uniqueBottomTexture));
    }

    private void orientableSixDirsBlockWithItem(Block block, boolean uniqueBottomTexture) {
        orientableSixDirsBlockWithItem(block,
                orientableBlockModel(block, uniqueBottomTexture),
                orientableVerticalBlockModel(block, uniqueBottomTexture));
    }

    private void orientableSixDirsBlockWithItem(Block block, Identifier modelNormal, Identifier modelVertical) {
        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).
                with(BlockStateVariantMap.models(Properties.FACING).
                        register(Direction.UP, new WeightedVariant(Pool.of(new ModelVariant(modelVertical)))).
                        register(Direction.DOWN, new WeightedVariant(Pool.of(new ModelVariant(modelVertical).
                                with(BlockStateModelGenerator.ROTATE_X_180)))).
                        register(Direction.NORTH, new WeightedVariant(Pool.of(new ModelVariant(modelNormal)))).
                        register(Direction.SOUTH, new WeightedVariant(Pool.of(new ModelVariant(modelNormal).
                                with(BlockStateModelGenerator.ROTATE_Y_180)))).
                        register(Direction.EAST, new WeightedVariant(Pool.of(new ModelVariant(modelNormal).
                                with(BlockStateModelGenerator.ROTATE_Y_90)))).
                        register(Direction.WEST, new WeightedVariant(Pool.of(new ModelVariant(modelNormal).
                                with(BlockStateModelGenerator.ROTATE_Y_270))))
                ));

        generator.registerParentedItemModel(block, modelNormal);
    }

    private void activatableBlockWithItem(Block block, Identifier modelNormal,
                                          Identifier modelActive, BooleanProperty isActiveProperty) {
        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).
                with(BlockStateVariantMap.models(isActiveProperty).
                        register(false, new WeightedVariant(Pool.of(new ModelVariant(modelNormal)))).
                        register(true, new WeightedVariant(Pool.of(new ModelVariant(modelActive))))
                ));

        generator.registerParentedItemModel(block, modelNormal);
    }

    private void activatableOrientableBlockWithItem(Block block, Identifier modelNormal,
                                                    Identifier modelActive, BooleanProperty isActiveProperty) {
        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).
                with(BlockStateVariantMap.models(isActiveProperty).
                        register(false, new WeightedVariant(Pool.of(new ModelVariant(modelNormal)))).
                        register(true, new WeightedVariant(Pool.of(new ModelVariant(modelActive))))).
                apply(BlockStateVariantMap.operations(Properties.HORIZONTAL_FACING).
                        register(Direction.NORTH, BlockStateModelGenerator.NO_OP).
                        register(Direction.SOUTH, BlockStateModelGenerator.ROTATE_Y_180).
                        register(Direction.EAST, BlockStateModelGenerator.ROTATE_Y_90).
                        register(Direction.WEST, BlockStateModelGenerator.ROTATE_Y_270)
                ));

        generator.registerParentedItemModel(block, modelNormal);
    }

    private void itemConveyorBeltBlockWithItem(ItemConveyorBeltBlock block) {
        Identifier modelFlat = ModTexturedModel.ITEM_CONVEYOR_BELT_FLAT.get(block).
                upload(block, "_flat", generator.modelCollector);
        Identifier modelAscending = ModTexturedModel.ITEM_CONVEYOR_BELT_ASCENDING.get(block).
                upload(block, "_ascending", generator.modelCollector);
        Identifier modelDescending = ModTexturedModel.ITEM_CONVEYOR_BELT_DESCENDING.get(block).
                upload(block, "_descending", generator.modelCollector);

        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).
                with(BlockStateVariantMap.models(ItemConveyorBeltBlock.FACING).
                            register(EPBlockStateProperties.ConveyorBeltDirection.ASCENDING_NORTH_SOUTH, new WeightedVariant(Pool.of(new ModelVariant(modelAscending)))).
                            register(EPBlockStateProperties.ConveyorBeltDirection.ASCENDING_SOUTH_NORTH, new WeightedVariant(Pool.of(new ModelVariant(modelAscending).
                                    with(BlockStateModelGenerator.ROTATE_Y_180)))).
                            register(EPBlockStateProperties.ConveyorBeltDirection.ASCENDING_WEST_EAST, new WeightedVariant(Pool.of(new ModelVariant(modelAscending).
                                    with(BlockStateModelGenerator.ROTATE_Y_270)))).
                            register(EPBlockStateProperties.ConveyorBeltDirection.ASCENDING_EAST_WEST, new WeightedVariant(Pool.of(new ModelVariant(modelAscending).
                                    with(BlockStateModelGenerator.ROTATE_Y_90)))).
                            register(EPBlockStateProperties.ConveyorBeltDirection.DESCENDING_NORTH_SOUTH, new WeightedVariant(Pool.of(new ModelVariant(modelDescending).
                                    with(BlockStateModelGenerator.ROTATE_Y_180)))).
                            register(EPBlockStateProperties.ConveyorBeltDirection.DESCENDING_SOUTH_NORTH, new WeightedVariant(Pool.of(new ModelVariant(modelDescending)))).
                            register(EPBlockStateProperties.ConveyorBeltDirection.DESCENDING_WEST_EAST, new WeightedVariant(Pool.of(new ModelVariant(modelDescending).
                                    with(BlockStateModelGenerator.ROTATE_Y_90)))).
                            register(EPBlockStateProperties.ConveyorBeltDirection.DESCENDING_EAST_WEST, new WeightedVariant(Pool.of(new ModelVariant(modelDescending).
                                    with(BlockStateModelGenerator.ROTATE_Y_270)))).
                            register(EPBlockStateProperties.ConveyorBeltDirection.NORTH_SOUTH, new WeightedVariant(Pool.of(new ModelVariant(modelFlat)))).
                            register(EPBlockStateProperties.ConveyorBeltDirection.SOUTH_NORTH, new WeightedVariant(Pool.of(new ModelVariant(modelFlat).
                                    with(BlockStateModelGenerator.ROTATE_Y_180)))).
                            register(EPBlockStateProperties.ConveyorBeltDirection.WEST_EAST, new WeightedVariant(Pool.of(new ModelVariant(modelFlat).
                                    with(BlockStateModelGenerator.ROTATE_Y_270)))).
                            register(EPBlockStateProperties.ConveyorBeltDirection.EAST_WEST, new WeightedVariant(Pool.of(new ModelVariant(modelFlat).
                                    with(BlockStateModelGenerator.ROTATE_Y_90))))));

        Models.GENERATED.upload(ModelIds.getBlockModelId(block), TextureMap.layer0(TextureMap.getId(block)), generator.modelCollector);
    }

    private void fluidPipeBlockWithItem(Block block) {
        Identifier fluidPipeCore = ModTexturedModel.FLUID_PIPE_CORE.get(block).
                upload(block, "_core", generator.modelCollector);
        Identifier fluidPipeSideConnected = ModTexturedModel.FLUID_PIPE_SIDE_CONNECTED.get(block).
                upload(block, "_side_connected", generator.modelCollector);
        Identifier fluidPipeSideExtract = ModTexturedModel.FLUID_PIPE_SIDE_EXTRACT.get(block).
                upload(block, "_side_extract", generator.modelCollector);

        generator.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(block).
                with(
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeCore)))).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.UP, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideConnected))).
                                apply(BlockStateModelGenerator.ROTATE_X_270)).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.UP, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideExtract))).
                                apply(BlockStateModelGenerator.ROTATE_X_270)).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.DOWN, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideConnected))).
                                apply(BlockStateModelGenerator.ROTATE_X_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.DOWN, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideExtract))).
                                apply(BlockStateModelGenerator.ROTATE_X_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.NORTH, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideConnected)))).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.NORTH, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideExtract)))).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.SOUTH, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideConnected))).
                                apply(BlockStateModelGenerator.ROTATE_Y_180)).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.SOUTH, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideExtract))).
                                apply(BlockStateModelGenerator.ROTATE_Y_180)).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.EAST, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideConnected))).
                                apply(BlockStateModelGenerator.ROTATE_Y_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.EAST, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideExtract))).
                                apply(BlockStateModelGenerator.ROTATE_Y_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.WEST, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideConnected))).
                                apply(BlockStateModelGenerator.ROTATE_Y_270)).
                with(
                        new MultipartModelConditionBuilder().
                                put(FluidPipeBlock.WEST, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(fluidPipeSideExtract))).
                                apply(BlockStateModelGenerator.ROTATE_Y_270))
        );

        generator.registerParentedItemModel(block, fluidPipeCore);
    }

    private void fluidTankBlockWithItem(Block block) {
        Identifier fluidTank = ModTexturedModel.FLUID_TANK.get(block).upload(block, generator.modelCollector);

        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, new WeightedVariant(Pool.of(new ModelVariant(fluidTank)))).
                apply(BlockStateVariantMap.operations(Properties.HORIZONTAL_FACING).
                        register(Direction.NORTH, BlockStateModelGenerator.NO_OP).
                        register(Direction.SOUTH, BlockStateModelGenerator.ROTATE_Y_180).
                        register(Direction.EAST, BlockStateModelGenerator.ROTATE_Y_90).
                        register(Direction.WEST, BlockStateModelGenerator.ROTATE_Y_270)
                ));

        generator.registerParentedItemModel(block, fluidTank);
    }

    private void cableBlockWithItem(Block block) {
        Identifier cableCore = ModTexturedModel.CABLE_CORE.get(block).upload(block, "_core", generator.modelCollector);
        Identifier cableSide = ModTexturedModel.CABLE_SIDE.get(block).upload(block, "_side", generator.modelCollector);

        generator.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(block).
                with(
                        new WeightedVariant(Pool.of(new ModelVariant(cableCore)))).
                with(
                        new MultipartModelConditionBuilder().
                                put(CableBlock.UP, true),
                        new WeightedVariant(Pool.of(new ModelVariant(cableSide))).
                                apply(BlockStateModelGenerator.ROTATE_X_270)).
                with(
                        new MultipartModelConditionBuilder().
                                put(CableBlock.DOWN, true),
                        new WeightedVariant(Pool.of(new ModelVariant(cableSide))).
                                apply(BlockStateModelGenerator.ROTATE_X_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(CableBlock.NORTH, true),
                        new WeightedVariant(Pool.of(new ModelVariant(cableSide)))).
                with(
                        new MultipartModelConditionBuilder().
                                put(CableBlock.SOUTH, true),
                        new WeightedVariant(Pool.of(new ModelVariant(cableSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_180)).
                with(
                        new MultipartModelConditionBuilder().
                                put(CableBlock.EAST, true),
                        new WeightedVariant(Pool.of(new ModelVariant(cableSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(CableBlock.WEST, true),
                        new WeightedVariant(Pool.of(new ModelVariant(cableSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_270))
        );

        generator.registerParentedItemModel(block, cableCore);
    }

    private void transformerBlockWithItem(TransformerBlock block) {
        String textureName = switch(block.getTier()) {
            case LV -> "lv_transformer";
            case MV -> "mv_transformer";
            case HV -> "hv_transformer";
            case EHV -> "ehv_transformer";
        };

        TransformerType transformerType = block.getTransformerType();
        switch(transformerType) {
            case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                String singleSuffix = transformerType == TransformerType.TYPE_1_TO_N?"_input":"_output";
                String multipleSuffix = transformerType == TransformerType.TYPE_1_TO_N?"_output":"_input";

                Identifier transformer = TexturedModel.makeFactory(unused -> new TextureMap().
                                put(TextureKey.TOP, EPAPI.id("block/" + textureName + multipleSuffix)).
                                put(TextureKey.BOTTOM, EPAPI.id("block/" + textureName + multipleSuffix)).
                                put(TextureKey.FRONT, EPAPI.id("block/" + textureName + singleSuffix)).
                                put(TextureKey.SIDE, EPAPI.id("block/" + textureName + multipleSuffix)).
                                copy(TextureKey.TOP, TextureKey.PARTICLE),
                        Models.ORIENTABLE_WITH_BOTTOM).get(block).upload(block, generator.modelCollector);

                generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, new WeightedVariant(Pool.of(new ModelVariant(transformer)))).
                        apply(BlockStateVariantMap.operations(Properties.FACING).
                                register(Direction.UP, BlockStateModelGenerator.ROTATE_X_270).
                                register(Direction.DOWN, BlockStateModelGenerator.ROTATE_X_90).
                                register(Direction.NORTH, BlockStateModelGenerator.NO_OP).
                                register(Direction.SOUTH, BlockStateModelGenerator.ROTATE_Y_180).
                                register(Direction.EAST, BlockStateModelGenerator.ROTATE_Y_90).
                                register(Direction.WEST, BlockStateModelGenerator.ROTATE_Y_270)
                        ));

                generator.registerParentedItemModel(block, transformer);
            }
            case TYPE_3_TO_3 -> {
                Identifier transformer = TexturedModel.makeFactory(unused -> new TextureMap().
                                put(TextureKey.UP, EPAPI.id("block/" + textureName + "_input")).
                                put(TextureKey.DOWN, EPAPI.id("block/" + textureName + "_output")).
                                put(TextureKey.NORTH, EPAPI.id("block/" + textureName + "_input")).
                                put(TextureKey.SOUTH, EPAPI.id("block/" + textureName + "_output")).
                                put(TextureKey.EAST, EPAPI.id("block/" + textureName + "_output")).
                                put(TextureKey.WEST, EPAPI.id("block/" + textureName + "_input")).
                                copy(TextureKey.UP, TextureKey.PARTICLE),
                        Models.CUBE).get(block).upload(block, generator.modelCollector);

                generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, new WeightedVariant(Pool.of(new ModelVariant(transformer)))).
                        apply(BlockStateVariantMap.operations(Properties.FACING).
                                register(Direction.UP, BlockStateModelGenerator.ROTATE_X_270).
                                register(Direction.DOWN, BlockStateModelGenerator.ROTATE_X_90.
                                        then(BlockStateModelGenerator.ROTATE_Y_90)).
                                register(Direction.NORTH, BlockStateModelGenerator.NO_OP).
                                register(Direction.SOUTH, BlockStateModelGenerator.ROTATE_X_90.
                                        then(BlockStateModelGenerator.ROTATE_Y_180)).
                                register(Direction.EAST, BlockStateModelGenerator.ROTATE_Y_90).
                                register(Direction.WEST, BlockStateModelGenerator.ROTATE_X_90.
                                        then(BlockStateModelGenerator.ROTATE_Y_270))
                        ));

                generator.registerParentedItemModel(block, transformer);
            }
        }
    }

    private void configurableTransformerBlockWithItem(ConfigurableTransformerBlock block) {
        String textureName = switch(block.getTier()) {
            case LV -> "lv_transformer";
            case MV -> "mv_transformer";
            case HV -> "hv_transformer";
            case EHV -> "ehv_transformer";
        };

        Identifier allCube = TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.UP, EPAPI.id("block/" + textureName + "_not_connected")).
                        put(TextureKey.DOWN, EPAPI.id("block/" + textureName + "_not_connected")).
                        put(TextureKey.NORTH, EPAPI.id("block/" + textureName + "_output")).
                        put(TextureKey.SOUTH, EPAPI.id("block/" + textureName + "_not_connected")).
                        put(TextureKey.EAST, EPAPI.id("block/" + textureName + "_input")).
                        put(TextureKey.WEST, EPAPI.id("block/" + textureName + "_not_connected")).
                        copy(TextureKey.UP, TextureKey.PARTICLE),
                Models.CUBE).get(block).upload(block, "_cube", generator.modelCollector);

        Identifier notConnectedSide = TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.SIDE, EPAPI.id("block/" + textureName + "_not_connected")).
                        copy(TextureKey.SIDE, TextureKey.PARTICLE),
                ModModels.SINGLE_SIDE).get(block).upload(block, "_not_connected", generator.modelCollector);
        Identifier receiveSide = TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.SIDE, EPAPI.id("block/" + textureName + "_input")).
                        copy(TextureKey.SIDE, TextureKey.PARTICLE),
                ModModels.SINGLE_SIDE).get(block).upload(block, "_input", generator.modelCollector);
        Identifier extractSide = TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.SIDE, EPAPI.id("block/" + textureName + "_output")).
                        copy(TextureKey.SIDE, TextureKey.PARTICLE),
                ModModels.SINGLE_SIDE).get(block).upload(block, "_output", generator.modelCollector);

        generator.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(block).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.UP, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(notConnectedSide))).
                                apply(BlockStateModelGenerator.ROTATE_X_270)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.UP, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new WeightedVariant(Pool.of(new ModelVariant(receiveSide))).
                                apply(BlockStateModelGenerator.ROTATE_X_270)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.UP, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(extractSide))).
                                apply(BlockStateModelGenerator.ROTATE_X_270)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.DOWN, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(notConnectedSide))).
                                apply(BlockStateModelGenerator.ROTATE_X_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.DOWN, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new WeightedVariant(Pool.of(new ModelVariant(receiveSide))).
                                apply(BlockStateModelGenerator.ROTATE_X_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.DOWN, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(extractSide))).
                                apply(BlockStateModelGenerator.ROTATE_X_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.NORTH, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(notConnectedSide)))).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.NORTH, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new WeightedVariant(Pool.of(new ModelVariant(receiveSide)))).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.NORTH, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(extractSide)))).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.SOUTH, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(notConnectedSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_180)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.SOUTH, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new WeightedVariant(Pool.of(new ModelVariant(receiveSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_180)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.SOUTH, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(extractSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_180)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.EAST, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(notConnectedSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.EAST, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new WeightedVariant(Pool.of(new ModelVariant(receiveSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.EAST, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(extractSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_90)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.WEST, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new WeightedVariant(Pool.of(new ModelVariant(notConnectedSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_270)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.WEST, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new WeightedVariant(Pool.of(new ModelVariant(receiveSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_270)).
                with(
                        new MultipartModelConditionBuilder().
                                put(ConfigurableTransformerBlock.WEST, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new WeightedVariant(Pool.of(new ModelVariant(extractSide))).
                                apply(BlockStateModelGenerator.ROTATE_Y_270))
        );

        generator.registerParentedItemModel(block, allCube);
    }

    private void solarPanelBlockWithItem(Block block) {
        Identifier solarPanel = ModTexturedModel.SOLAR_PANEL.get(block).upload(block, generator.modelCollector);

        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block,
                new WeightedVariant(Pool.of(new ModelVariant(solarPanel)))));

        generator.registerParentedItemModel(block, solarPanel);
    }

    private void activatableOrientableMachineBlockWithItem(Block block, boolean uniqueBottomTexture) {
        activatableOrientableBlockWithItem(block,
                orientableBlockModel(block, uniqueBottomTexture),
                orientableOnBlockModel(block, uniqueBottomTexture),
                Properties.LIT);
    }

    private void poweredLampBlockWithItem(Block block) {
        Identifier modelOff = TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.ALL, TextureMap.getId(block)),
                Models.CUBE_ALL).get(block).upload(block, generator.modelCollector);

        Identifier modelOn = TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.ALL, TextureMap.getSubId(block, "_on")),
                Models.CUBE_ALL).get(block).upload(block, "_on", generator.modelCollector);

        BlockStateVariantMap.SingleProperty<WeightedVariant, Integer> builder = BlockStateVariantMap.models(Properties.LEVEL_15).
                register(0, new WeightedVariant(Pool.of(new ModelVariant(modelOff))));

        for(int i = 1;i < 16;i++)
            builder.register(i, new WeightedVariant(Pool.of(new ModelVariant(modelOn))));

        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).
                with(builder));

        generator.registerParentedItemModel(block, modelOff);
    }
}
