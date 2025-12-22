package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.*;
import me.jddev0.ep.datagen.model.ModModelTemplates;
import me.jddev0.ep.datagen.model.ModTexturedModel;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.machine.tier.TransformerType;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider {
    private final BlockModelGenerators generator;

    public ModBlockStateProvider(BlockModelGenerators generator) {
        this.generator = generator;
    }

    protected void registerStatesAndModels() {
        registerBlocks();
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

        generator.createNonTemplateModelBlock(EPFluids.DIRTY_WATER_BLOCK.get());
    }

    private void cubeAllBlockWithItem(Holder<Block> block) {
        generator.createTrivialCube(block.value());
    }

    private Identifier cubeBlockModel(Holder<Block> block, String fileSuffix, String upSuffix,
                                            String bottomSuffix, String northSuffix, String southSuffix,
                                            String westSuffix, String eastSuffix) {
        return TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, TextureMapping.getBlockTexture(block.value(), upSuffix)).
                        put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block.value(), bottomSuffix)).
                        put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block.value(), northSuffix)).
                        put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block.value(), southSuffix)).
                        put(TextureSlot.EAST, TextureMapping.getBlockTexture(block.value(), eastSuffix)).
                        put(TextureSlot.WEST, TextureMapping.getBlockTexture(block.value(), westSuffix)).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block.value()).createWithSuffix(block.value(), fileSuffix, generator.modelOutput);
    }

    private Identifier orientableBlockModel(Holder<Block> block, boolean uniqueBottomTexture) {
        return orientableBlockModel(block, "", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_side");
    }

    private Identifier orientableOnBlockModel(Holder<Block> block, boolean uniqueBottomTexture) {
        return orientableBlockModel(block, "_on", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front_on", "_side");
    }

    private Identifier orientableBlockModel(Holder<Block> block, String fileSuffix, String topSuffix,
                                            String bottomSuffix, String frontSuffix, String sideSuffix) {
        return TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.TOP, TextureMapping.getBlockTexture(block.value(), topSuffix)).
                        put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block.value(), bottomSuffix)).
                        put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block.value(), frontSuffix)).
                        put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block.value(), sideSuffix)).
                        copySlot(TextureSlot.TOP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM).get(block.value()).createWithSuffix(block.value(), fileSuffix, generator.modelOutput);
    }

    private Identifier orientableWithBackBlockModel(Holder<Block> block, boolean uniqueBottomTexture) {
        return orientableWithBackBlockModel(block, "", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_back", "_side");
    }

    private Identifier orientableWithBackBlockModel(Holder<Block> block, String fileSuffix, String topSuffix,
                                                    String bottomSuffix, String frontSuffix, String backSuffix, String sideSuffix) {
        return TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, TextureMapping.getBlockTexture(block.value(), topSuffix)).
                        put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block.value(), bottomSuffix)).
                        put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block.value(), frontSuffix)).
                        put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block.value(), backSuffix)).
                        put(TextureSlot.EAST, TextureMapping.getBlockTexture(block.value(), sideSuffix)).
                        put(TextureSlot.WEST, TextureMapping.getBlockTexture(block.value(), sideSuffix)).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block.value()).createWithSuffix(block.value(), fileSuffix, generator.modelOutput);
    }

    private Identifier orientableVerticalWithBackBlockModel(Holder<Block> block, boolean uniqueBottomTexture) {
        return orientableVerticalWithBackBlockModel(block, "_vertical", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_back", "_side");
    }

    private Identifier orientableVerticalWithBackBlockModel(Holder<Block> block, String fileSuffix, String topSuffix,
                                                            String bottomSuffix, String frontSuffix, String backSuffix, String sideSuffix) {
        return TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.TOP, TextureMapping.getBlockTexture(block.value(), topSuffix)).
                        put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block.value(), bottomSuffix)).
                        put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block.value(), frontSuffix)).
                        put(TextureSlot.BACK, TextureMapping.getBlockTexture(block.value(), backSuffix)).
                        put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block.value(), sideSuffix)).
                        copySlot(TextureSlot.FRONT, TextureSlot.PARTICLE),
                ModModelTemplates.ORIENTABLE_VERTICAL_WITH_BACK).get(block.value()).createWithSuffix(block.value(), fileSuffix, generator.modelOutput);
    }

    private Identifier orientableVerticalBlockModel(Holder<Block> block, boolean uniqueBottomTexture) {
        return orientableVerticalBlockModel(block, "_vertical", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_side");
    }

    private Identifier orientableVerticalBlockModel(Holder<Block> block, String fileSuffix, String topSuffix,
                                                    String bottomSuffix, String frontSuffix, String sideSuffix) {
        return TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.TOP, TextureMapping.getBlockTexture(block.value(), topSuffix)).
                        put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block.value(), bottomSuffix)).
                        put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block.value(), frontSuffix)).
                        put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block.value(), sideSuffix)).
                        copySlot(TextureSlot.FRONT, TextureSlot.PARTICLE),
                ModModelTemplates.ORIENTABLE_VERTICAL).get(block.value()).createWithSuffix(block.value(), fileSuffix, generator.modelOutput);
    }

    private void horizontalBlockWithItem(Holder<Block> block, boolean uniqueBottomTexture) {
        Identifier model = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, TextureMapping.getBlockTexture(block.value(), "_top")).
                        put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block.value(), uniqueBottomTexture?"_bottom":"_top")).
                        put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block.value(), "_side")).
                        put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block.value(), "_side")).
                        put(TextureSlot.EAST, TextureMapping.getBlockTexture(block.value(), "_side")).
                        put(TextureSlot.WEST, TextureMapping.getBlockTexture(block.value(), "_side")).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block.value()).create(block.value(), generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value(),
                new MultiVariant(WeightedList.of(new Variant(model)))));

        generator.registerSimpleItemModel(block.value(), model);
    }

    private void horizontalTwoSideBlockWithItem(Holder<Block> block, boolean uniqueBottomTexture) {
        Identifier model = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, TextureMapping.getBlockTexture(block.value(), "_top")).
                        put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block.value(), uniqueBottomTexture?"_bottom":"_top")).
                        put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block.value(), "_front")).
                        put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block.value(), "_side")).
                        put(TextureSlot.EAST, TextureMapping.getBlockTexture(block.value(), "_side")).
                        put(TextureSlot.WEST, TextureMapping.getBlockTexture(block.value(), "_front")).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block.value()).create(block.value(), generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value(),
                new MultiVariant(WeightedList.of(new Variant(model)))));

        generator.registerSimpleItemModel(block.value(), model);
    }

    private void orientableBlockWithItem(Holder<Block> block, Identifier model) {
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value(), new MultiVariant(WeightedList.of(new Variant(model)))).
                with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).
                        select(Direction.NORTH, BlockModelGenerators.NOP).
                        select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).
                        select(Direction.EAST, BlockModelGenerators.Y_ROT_90).
                        select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
                ));

        generator.registerSimpleItemModel(block.value(), model);
    }

    private void orientableSixDirsBlockWithBackItem(Holder<Block> block, boolean uniqueBottomTexture) {
        orientableSixDirsBlockWithItem(block,
                orientableWithBackBlockModel(block, uniqueBottomTexture),
                orientableVerticalWithBackBlockModel(block, uniqueBottomTexture));
    }

    private void orientableSixDirsBlockWithItem(Holder<Block> block, boolean uniqueBottomTexture) {
        orientableSixDirsBlockWithItem(block,
                orientableBlockModel(block, uniqueBottomTexture),
                orientableVerticalBlockModel(block, uniqueBottomTexture));
    }

    private void orientableSixDirsBlockWithItem(Holder<Block> block, Identifier modelNormal, Identifier modelVertical) {
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value()).
                with(PropertyDispatch.initial(BlockStateProperties.FACING).
                        select(Direction.UP, new MultiVariant(WeightedList.of(new Variant(modelVertical)))).
                        select(Direction.DOWN, new MultiVariant(WeightedList.of(new Variant(modelVertical).
                                with(BlockModelGenerators.X_ROT_180)))).
                        select(Direction.NORTH, new MultiVariant(WeightedList.of(new Variant(modelNormal)))).
                        select(Direction.SOUTH, new MultiVariant(WeightedList.of(new Variant(modelNormal).
                                with(BlockModelGenerators.Y_ROT_180)))).
                        select(Direction.EAST, new MultiVariant(WeightedList.of(new Variant(modelNormal).
                                with(BlockModelGenerators.Y_ROT_90)))).
                        select(Direction.WEST, new MultiVariant(WeightedList.of(new Variant(modelNormal).
                                with(BlockModelGenerators.Y_ROT_270))))
                ));

        generator.registerSimpleItemModel(block.value(), modelNormal);
    }

    private void activatableBlockWithItem(Holder<Block> block, Identifier modelNormal,
                                          Identifier modelActive, BooleanProperty isActiveProperty) {
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value()).
                with(PropertyDispatch.initial(isActiveProperty).
                        select(false, new MultiVariant(WeightedList.of(new Variant(modelNormal)))).
                        select(true, new MultiVariant(WeightedList.of(new Variant(modelActive))))
                ));

        generator.registerSimpleItemModel(block.value(), modelNormal);
    }

    private void activatableOrientableBlockWithItem(Holder<Block> block, Identifier modelNormal,
                                                    Identifier modelActive, BooleanProperty isActiveProperty) {
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value()).
                with(PropertyDispatch.initial(isActiveProperty).
                        select(false, new MultiVariant(WeightedList.of(new Variant(modelNormal)))).
                        select(true, new MultiVariant(WeightedList.of(new Variant(modelActive))))).
                with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).
                        select(Direction.NORTH, BlockModelGenerators.NOP).
                        select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).
                        select(Direction.EAST, BlockModelGenerators.Y_ROT_90).
                        select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
                ));

        generator.registerSimpleItemModel(block.value(), modelNormal);
    }

    private void itemConveyorBeltBlockWithItem(DeferredBlock<ItemConveyorBeltBlock> block) {
        Identifier modelFlat = ModTexturedModel.ITEM_CONVEYOR_BELT_FLAT.get(block.value()).
                createWithSuffix(block.value(), "_flat", generator.modelOutput);
        Identifier modelAscending = ModTexturedModel.ITEM_CONVEYOR_BELT_ASCENDING.get(block.value()).
                createWithSuffix(block.value(), "_ascending", generator.modelOutput);
        Identifier modelDescending = ModTexturedModel.ITEM_CONVEYOR_BELT_DESCENDING.get(block.value()).
                createWithSuffix(block.value(), "_descending", generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value()).
                with(PropertyDispatch.initial(ItemConveyorBeltBlock.FACING).
                        select(EPBlockStateProperties.ConveyorBeltDirection.ASCENDING_NORTH_SOUTH, new MultiVariant(WeightedList.of(new Variant(modelAscending)))).
                        select(EPBlockStateProperties.ConveyorBeltDirection.ASCENDING_SOUTH_NORTH, new MultiVariant(WeightedList.of(new Variant(modelAscending).
                                with(BlockModelGenerators.Y_ROT_180)))).
                        select(EPBlockStateProperties.ConveyorBeltDirection.ASCENDING_WEST_EAST, new MultiVariant(WeightedList.of(new Variant(modelAscending).
                                with(BlockModelGenerators.Y_ROT_270)))).
                        select(EPBlockStateProperties.ConveyorBeltDirection.ASCENDING_EAST_WEST, new MultiVariant(WeightedList.of(new Variant(modelAscending).
                                with(BlockModelGenerators.Y_ROT_90)))).
                        select(EPBlockStateProperties.ConveyorBeltDirection.DESCENDING_NORTH_SOUTH, new MultiVariant(WeightedList.of(new Variant(modelDescending).
                                with(BlockModelGenerators.Y_ROT_180)))).
                        select(EPBlockStateProperties.ConveyorBeltDirection.DESCENDING_SOUTH_NORTH, new MultiVariant(WeightedList.of(new Variant(modelDescending)))).
                        select(EPBlockStateProperties.ConveyorBeltDirection.DESCENDING_WEST_EAST, new MultiVariant(WeightedList.of(new Variant(modelDescending).
                                with(BlockModelGenerators.Y_ROT_90)))).
                        select(EPBlockStateProperties.ConveyorBeltDirection.DESCENDING_EAST_WEST, new MultiVariant(WeightedList.of(new Variant(modelDescending).
                                with(BlockModelGenerators.Y_ROT_270)))).
                        select(EPBlockStateProperties.ConveyorBeltDirection.NORTH_SOUTH, new MultiVariant(WeightedList.of(new Variant(modelFlat)))).
                        select(EPBlockStateProperties.ConveyorBeltDirection.SOUTH_NORTH, new MultiVariant(WeightedList.of(new Variant(modelFlat).
                                with(BlockModelGenerators.Y_ROT_180)))).
                        select(EPBlockStateProperties.ConveyorBeltDirection.WEST_EAST, new MultiVariant(WeightedList.of(new Variant(modelFlat).
                                with(BlockModelGenerators.Y_ROT_270)))).
                        select(EPBlockStateProperties.ConveyorBeltDirection.EAST_WEST, new MultiVariant(WeightedList.of(new Variant(modelFlat).
                                with(BlockModelGenerators.Y_ROT_90))))));

        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(block.value()), TextureMapping.layer0(TextureMapping.getBlockTexture(block.value())), generator.modelOutput);
    }

    private void fluidPipeBlockWithItem(Holder<Block> block) {
        Identifier fluidPipeCore = ModTexturedModel.FLUID_PIPE_CORE.get(block.value()).
                createWithSuffix(block.value(), "_core", generator.modelOutput);
        Identifier fluidPipeSideConnected = ModTexturedModel.FLUID_PIPE_SIDE_CONNECTED.get(block.value()).
                createWithSuffix(block.value(), "_side_connected", generator.modelOutput);
        Identifier fluidPipeSideExtract = ModTexturedModel.FLUID_PIPE_SIDE_EXTRACT.get(block.value()).
                createWithSuffix(block.value(), "_side_extract", generator.modelOutput);

        generator.blockStateOutput.accept(MultiPartGenerator.multiPart(block.value()).
                with(
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeCore)))).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.UP, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideConnected))).
                                with(BlockModelGenerators.X_ROT_270)).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.UP, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideExtract))).
                                with(BlockModelGenerators.X_ROT_270)).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.DOWN, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideConnected))).
                                with(BlockModelGenerators.X_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.DOWN, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideExtract))).
                                with(BlockModelGenerators.X_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.NORTH, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideConnected)))).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.NORTH, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideExtract)))).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.SOUTH, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideConnected))).
                                with(BlockModelGenerators.Y_ROT_180)).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.SOUTH, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideExtract))).
                                with(BlockModelGenerators.Y_ROT_180)).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.EAST, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideConnected))).
                                with(BlockModelGenerators.Y_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.EAST, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideExtract))).
                                with(BlockModelGenerators.Y_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.WEST, EPBlockStateProperties.PipeConnection.CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideConnected))).
                                with(BlockModelGenerators.Y_ROT_270)).
                with(
                        new ConditionBuilder().
                                term(FluidPipeBlock.WEST, EPBlockStateProperties.PipeConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(fluidPipeSideExtract))).
                                with(BlockModelGenerators.Y_ROT_270))
        );

        generator.registerSimpleItemModel(block.value(), fluidPipeCore);
    }

    private void fluidTankBlockWithItem(Holder<Block> block) {
        Identifier fluidTank = ModTexturedModel.FLUID_TANK.get(block.value()).create(block.value(), generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value(), new MultiVariant(WeightedList.of(new Variant(fluidTank)))).
                with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).
                        select(Direction.NORTH, BlockModelGenerators.NOP).
                        select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).
                        select(Direction.EAST, BlockModelGenerators.Y_ROT_90).
                        select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
                ));

        generator.registerSimpleItemModel(block.value(), fluidTank);
    }

    private void cableBlockWithItem(Holder<Block> block) {
        Identifier cableCore = ModTexturedModel.CABLE_CORE.get(block.value()).createWithSuffix(block.value(), "_core", generator.modelOutput);
        Identifier cableSide = ModTexturedModel.CABLE_SIDE.get(block.value()).createWithSuffix(block.value(), "_side", generator.modelOutput);

        generator.blockStateOutput.accept(MultiPartGenerator.multiPart(block.value()).
                with(
                        new MultiVariant(WeightedList.of(new Variant(cableCore)))).
                with(
                        new ConditionBuilder().
                                term(CableBlock.UP, true),
                        new MultiVariant(WeightedList.of(new Variant(cableSide))).
                                with(BlockModelGenerators.X_ROT_270)).
                with(
                        new ConditionBuilder().
                                term(CableBlock.DOWN, true),
                        new MultiVariant(WeightedList.of(new Variant(cableSide))).
                                with(BlockModelGenerators.X_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(CableBlock.NORTH, true),
                        new MultiVariant(WeightedList.of(new Variant(cableSide)))).
                with(
                        new ConditionBuilder().
                                term(CableBlock.SOUTH, true),
                        new MultiVariant(WeightedList.of(new Variant(cableSide))).
                                with(BlockModelGenerators.Y_ROT_180)).
                with(
                        new ConditionBuilder().
                                term(CableBlock.EAST, true),
                        new MultiVariant(WeightedList.of(new Variant(cableSide))).
                                with(BlockModelGenerators.Y_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(CableBlock.WEST, true),
                        new MultiVariant(WeightedList.of(new Variant(cableSide))).
                                with(BlockModelGenerators.Y_ROT_270))
        );

        generator.registerSimpleItemModel(block.value(), cableCore);
    }

    private void transformerBlockWithItem(DeferredBlock<TransformerBlock> block) {
        String textureName = switch(block.value().getTier()) {
            case LV -> "lv_transformer";
            case MV -> "mv_transformer";
            case HV -> "hv_transformer";
            case EHV -> "ehv_transformer";
        };

        TransformerType transformerType = block.value().getTransformerType();
        switch(transformerType) {
            case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                String singleSuffix = transformerType == TransformerType.TYPE_1_TO_N?"_input":"_output";
                String multipleSuffix = transformerType == TransformerType.TYPE_1_TO_N?"_output":"_input";

                Identifier transformer = TexturedModel.createDefault(unused -> new TextureMapping().
                                put(TextureSlot.TOP, EPAPI.id("block/" + textureName + multipleSuffix)).
                                put(TextureSlot.BOTTOM, EPAPI.id("block/" + textureName + multipleSuffix)).
                                put(TextureSlot.FRONT, EPAPI.id("block/" + textureName + singleSuffix)).
                                put(TextureSlot.SIDE, EPAPI.id("block/" + textureName + multipleSuffix)).
                                copySlot(TextureSlot.TOP, TextureSlot.PARTICLE),
                        ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM).get(block.value()).create(block.value(), generator.modelOutput);

                generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value(), new MultiVariant(WeightedList.of(new Variant(transformer)))).
                        with(PropertyDispatch.modify(BlockStateProperties.FACING).
                                select(Direction.UP, BlockModelGenerators.X_ROT_270).
                                select(Direction.DOWN, BlockModelGenerators.X_ROT_90).
                                select(Direction.NORTH, BlockModelGenerators.NOP).
                                select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).
                                select(Direction.EAST, BlockModelGenerators.Y_ROT_90).
                                select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
                        ));

                generator.registerSimpleItemModel(block.value(), transformer);
            }
            case TYPE_3_TO_3 -> {
                Identifier transformer = TexturedModel.createDefault(unused -> new TextureMapping().
                                put(TextureSlot.UP, EPAPI.id("block/" + textureName + "_input")).
                                put(TextureSlot.DOWN, EPAPI.id("block/" + textureName + "_output")).
                                put(TextureSlot.NORTH, EPAPI.id("block/" + textureName + "_input")).
                                put(TextureSlot.SOUTH, EPAPI.id("block/" + textureName + "_output")).
                                put(TextureSlot.EAST, EPAPI.id("block/" + textureName + "_output")).
                                put(TextureSlot.WEST, EPAPI.id("block/" + textureName + "_input")).
                                copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                        ModelTemplates.CUBE).get(block.value()).create(block.value(), generator.modelOutput);

                generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value(), new MultiVariant(WeightedList.of(new Variant(transformer)))).
                        with(PropertyDispatch.modify(BlockStateProperties.FACING).
                                select(Direction.UP, BlockModelGenerators.X_ROT_270).
                                select(Direction.DOWN, BlockModelGenerators.X_ROT_90.
                                        then(BlockModelGenerators.Y_ROT_90)).
                                select(Direction.NORTH, BlockModelGenerators.NOP).
                                select(Direction.SOUTH, BlockModelGenerators.X_ROT_90.
                                        then(BlockModelGenerators.Y_ROT_180)).
                                select(Direction.EAST, BlockModelGenerators.Y_ROT_90).
                                select(Direction.WEST, BlockModelGenerators.X_ROT_90.
                                        then(BlockModelGenerators.Y_ROT_270))
                        ));

                generator.registerSimpleItemModel(block.value(), transformer);
            }
        }
    }

    private void configurableTransformerBlockWithItem(DeferredBlock<ConfigurableTransformerBlock> block) {
        String textureName = switch(block.value().getTier()) {
            case LV -> "lv_transformer";
            case MV -> "mv_transformer";
            case HV -> "hv_transformer";
            case EHV -> "ehv_transformer";
        };

        Identifier allCube = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, EPAPI.id("block/" + textureName + "_not_connected")).
                        put(TextureSlot.DOWN, EPAPI.id("block/" + textureName + "_not_connected")).
                        put(TextureSlot.NORTH, EPAPI.id("block/" + textureName + "_output")).
                        put(TextureSlot.SOUTH, EPAPI.id("block/" + textureName + "_not_connected")).
                        put(TextureSlot.EAST, EPAPI.id("block/" + textureName + "_input")).
                        put(TextureSlot.WEST, EPAPI.id("block/" + textureName + "_not_connected")).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block.value()).createWithSuffix(block.value(), "_cube", generator.modelOutput);

        Identifier notConnectedSide = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.SIDE, EPAPI.id("block/" + textureName + "_not_connected")).
                        copySlot(TextureSlot.SIDE, TextureSlot.PARTICLE),
                ModModelTemplates.SINGLE_SIDE).get(block.value()).createWithSuffix(block.value(), "_not_connected", generator.modelOutput);
        Identifier receiveSide = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.SIDE, EPAPI.id("block/" + textureName + "_input")).
                        copySlot(TextureSlot.SIDE, TextureSlot.PARTICLE),
                ModModelTemplates.SINGLE_SIDE).get(block.value()).createWithSuffix(block.value(), "_input", generator.modelOutput);
        Identifier extractSide = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.SIDE, EPAPI.id("block/" + textureName + "_output")).
                        copySlot(TextureSlot.SIDE, TextureSlot.PARTICLE),
                ModModelTemplates.SINGLE_SIDE).get(block.value()).createWithSuffix(block.value(), "_output", generator.modelOutput);

        generator.blockStateOutput.accept(MultiPartGenerator.multiPart(block.value()).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.UP, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(notConnectedSide))).
                                with(BlockModelGenerators.X_ROT_270)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.UP, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new MultiVariant(WeightedList.of(new Variant(receiveSide))).
                                with(BlockModelGenerators.X_ROT_270)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.UP, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(extractSide))).
                                with(BlockModelGenerators.X_ROT_270)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.DOWN, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(notConnectedSide))).
                                with(BlockModelGenerators.X_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.DOWN, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new MultiVariant(WeightedList.of(new Variant(receiveSide))).
                                with(BlockModelGenerators.X_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.DOWN, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(extractSide))).
                                with(BlockModelGenerators.X_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.NORTH, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(notConnectedSide)))).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.NORTH, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new MultiVariant(WeightedList.of(new Variant(receiveSide)))).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.NORTH, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(extractSide)))).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.SOUTH, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(notConnectedSide))).
                                with(BlockModelGenerators.Y_ROT_180)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.SOUTH, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new MultiVariant(WeightedList.of(new Variant(receiveSide))).
                                with(BlockModelGenerators.Y_ROT_180)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.SOUTH, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(extractSide))).
                                with(BlockModelGenerators.Y_ROT_180)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.EAST, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(notConnectedSide))).
                                with(BlockModelGenerators.Y_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.EAST, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new MultiVariant(WeightedList.of(new Variant(receiveSide))).
                                with(BlockModelGenerators.Y_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.EAST, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(extractSide))).
                                with(BlockModelGenerators.Y_ROT_90)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.WEST, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        new MultiVariant(WeightedList.of(new Variant(notConnectedSide))).
                                with(BlockModelGenerators.Y_ROT_270)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.WEST, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        new MultiVariant(WeightedList.of(new Variant(receiveSide))).
                                with(BlockModelGenerators.Y_ROT_270)).
                with(
                        new ConditionBuilder().
                                term(ConfigurableTransformerBlock.WEST, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        new MultiVariant(WeightedList.of(new Variant(extractSide))).
                                with(BlockModelGenerators.Y_ROT_270))
        );

        generator.registerSimpleItemModel(block.value(), allCube);
    }

    private void solarPanelBlockWithItem(Holder<Block> block) {
        Identifier solarPanel = ModTexturedModel.SOLAR_PANEL.get(block.value()).create(block.value(), generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value(),
                new MultiVariant(WeightedList.of(new Variant(solarPanel)))));

        generator.registerSimpleItemModel(block.value(), solarPanel);
    }

    private void activatableOrientableMachineBlockWithItem(Holder<Block> block, boolean uniqueBottomTexture) {
        activatableOrientableBlockWithItem(block,
                orientableBlockModel(block, uniqueBottomTexture),
                orientableOnBlockModel(block, uniqueBottomTexture),
                BlockStateProperties.LIT);
    }

    private void poweredLampBlockWithItem(Holder<Block> block) {
        Identifier modelOff = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.ALL, TextureMapping.getBlockTexture(block.value())),
                ModelTemplates.CUBE_ALL).get(block.value()).create(block.value(), generator.modelOutput);

        Identifier modelOn = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.ALL, TextureMapping.getBlockTexture(block.value(), "_on")),
                ModelTemplates.CUBE_ALL).get(block.value()).createWithSuffix(block.value(), "_on", generator.modelOutput);

        PropertyDispatch.C1<MultiVariant, Integer> builder = PropertyDispatch.initial(BlockStateProperties.LEVEL).
                select(0, new MultiVariant(WeightedList.of(new Variant(modelOff))));

        for(int i = 1;i < 16;i++)
            builder.select(i, new MultiVariant(WeightedList.of(new Variant(modelOn))));

        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.value()).
                with(builder));

        generator.registerSimpleItemModel(block.value(), modelOff);
    }
}
