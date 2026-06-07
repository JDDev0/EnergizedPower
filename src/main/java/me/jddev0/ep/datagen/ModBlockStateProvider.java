package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.*;
import me.jddev0.ep.datagen.model.ItemWithDisplayModelSupplier;
import me.jddev0.ep.datagen.model.ModModels;
import me.jddev0.ep.datagen.model.ModTexturedModel;
import me.jddev0.ep.machine.tier.TransformerType;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.joml.Vector3f;

class ModBlockStateProvider {
    private final BlockModelGenerators generator;

    ModBlockStateProvider(BlockModelGenerators generator) {
        this.generator = generator;
    }

    void registerBlocks() {
        cubeAllBlockWithItem(EPBlocks.SILICON_BLOCK);

        cubeAllBlockWithItem(EPBlocks.TIN_BLOCK);
        cubeAllBlockWithItem(EPBlocks.STEEL_BLOCK);

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
                EPBlockStateProperties.WORKING);

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

        orientableBlockWithItem(EPBlocks.FLUID_FREEZER,
                orientableBlockModel(EPBlocks.FLUID_FREEZER, false));

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
        solarPanelBlockWithItem(EPBlocks.SOLAR_PANEL_7);

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
                EPBlockStateProperties.WORKING);

        activatableBlockWithItem(EPBlocks.CHARGING_STATION,
                cubeBlockModel(EPBlocks.CHARGING_STATION, "", "_top", "_bottom",
                        "_side", "_side", "_side", "_side"),
                cubeBlockModel(EPBlocks.CHARGING_STATION, "_on", "_top_on", "_bottom",
                        "_side_on", "_side_on", "_side_on", "_side_on"),
                EPBlockStateProperties.WORKING);

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
        generator.createTrivialCube(block);
    }

    private ResourceLocation cubeBlockModel(Block block, String fileSuffix, String upSuffix,
                                      String bottomSuffix, String northSuffix, String southSuffix,
                                      String westSuffix, String eastSuffix) {
        return TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, TextureMapping.getBlockTexture(block, upSuffix)).
                        put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block, bottomSuffix)).
                        put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block, northSuffix)).
                        put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block, southSuffix)).
                        put(TextureSlot.EAST, TextureMapping.getBlockTexture(block, eastSuffix)).
                        put(TextureSlot.WEST, TextureMapping.getBlockTexture(block, westSuffix)).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block).createWithSuffix(block, fileSuffix, generator.modelOutput);
    }

    private ResourceLocation orientableBlockModel(Block block, boolean uniqueBottomTexture) {
        return orientableBlockModel(block, "", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_side");
    }

    private ResourceLocation orientableOnBlockModel(Block block, boolean uniqueBottomTexture) {
        return orientableBlockModel(block, "_on", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front_on", "_side");
    }

    private ResourceLocation orientableBlockModel(Block block, String fileSuffix, String topSuffix,
                                            String bottomSuffix, String frontSuffix, String sideSuffix) {
        return TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, topSuffix)).
                        put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, bottomSuffix)).
                        put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, frontSuffix)).
                        put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, sideSuffix)).
                        copySlot(TextureSlot.TOP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM).get(block).createWithSuffix(block, fileSuffix, generator.modelOutput);
    }

    private ResourceLocation orientableWithBackBlockModel(Block block, boolean uniqueBottomTexture) {
        return orientableWithBackBlockModel(block, "", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_back", "_side");
    }

    private ResourceLocation orientableWithBackBlockModel(Block block, String fileSuffix, String topSuffix,
                                                    String bottomSuffix, String frontSuffix, String backSuffix, String sideSuffix) {
        return TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, TextureMapping.getBlockTexture(block, topSuffix)).
                        put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block, bottomSuffix)).
                        put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block, frontSuffix)).
                        put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block, backSuffix)).
                        put(TextureSlot.EAST, TextureMapping.getBlockTexture(block, sideSuffix)).
                        put(TextureSlot.WEST, TextureMapping.getBlockTexture(block, sideSuffix)).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block).createWithSuffix(block, fileSuffix, generator.modelOutput);
    }

    private ResourceLocation orientableVerticalWithBackBlockModel(Block block, boolean uniqueBottomTexture) {
        return orientableVerticalWithBackBlockModel(block, "_vertical", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_back", "_side");
    }

    private ResourceLocation orientableVerticalWithBackBlockModel(Block block, String fileSuffix, String topSuffix,
                                                           String bottomSuffix, String frontSuffix, String backSuffix, String sideSuffix) {
        return TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, topSuffix)).
                        put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, bottomSuffix)).
                        put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, frontSuffix)).
                        put(TextureSlot.BACK, TextureMapping.getBlockTexture(block, backSuffix)).
                        put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, sideSuffix)).
                        copySlot(TextureSlot.FRONT, TextureSlot.PARTICLE),
                ModModels.ORIENTABLE_VERTICAL_WITH_BACK).get(block).createWithSuffix(block, fileSuffix, generator.modelOutput);
    }

    private ResourceLocation orientableVerticalBlockModel(Block block, boolean uniqueBottomTexture) {
        return orientableVerticalBlockModel(block, "_vertical", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_side");
    }

    private ResourceLocation orientableVerticalBlockModel(Block block, String fileSuffix, String topSuffix,
                                                   String bottomSuffix, String frontSuffix, String sideSuffix) {
        return TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, topSuffix)).
                        put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, bottomSuffix)).
                        put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, frontSuffix)).
                        put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, sideSuffix)).
                        copySlot(TextureSlot.FRONT, TextureSlot.PARTICLE),
                ModModels.ORIENTABLE_VERTICAL).get(block).createWithSuffix(block, fileSuffix, generator.modelOutput);
    }

    private void horizontalBlockWithItem(Block block, boolean uniqueBottomTexture) {
        ResourceLocation model = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, TextureMapping.getBlockTexture(block, "_top")).
                        put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block, uniqueBottomTexture?"_bottom":"_top")).
                        put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block, "_side")).
                        put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block, "_side")).
                        put(TextureSlot.EAST, TextureMapping.getBlockTexture(block, "_side")).
                        put(TextureSlot.WEST, TextureMapping.getBlockTexture(block, "_side")).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block).create(block, generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block,
                Variant.variant().with(VariantProperties.MODEL, model)));

        generator.delegateItemModel(block.asItem(), model);
    }

    private void horizontalTwoSideBlockWithItem(Block block, boolean uniqueBottomTexture) {
        ResourceLocation model = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, TextureMapping.getBlockTexture(block, "_top")).
                        put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block, uniqueBottomTexture?"_bottom":"_top")).
                        put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block, "_front")).
                        put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block, "_side")).
                        put(TextureSlot.EAST, TextureMapping.getBlockTexture(block, "_side")).
                        put(TextureSlot.WEST, TextureMapping.getBlockTexture(block, "_front")).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block).create(block, generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block,
                Variant.variant().with(VariantProperties.MODEL, model)));

        generator.delegateItemModel(block.asItem(), model);
    }

    private void orientableBlockWithItem(Block block, ResourceLocation model) {
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).
                with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).
                        select(Direction.NORTH, Variant.variant().
                                with(VariantProperties.MODEL, model)).
                        select(Direction.SOUTH, Variant.variant().
                                with(VariantProperties.MODEL, model).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                        select(Direction.EAST, Variant.variant().
                                with(VariantProperties.MODEL, model).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                        select(Direction.WEST, Variant.variant().
                                with(VariantProperties.MODEL, model).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                ));

        generator.delegateItemModel(block.asItem(), model);
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

    private void orientableSixDirsBlockWithItem(Block block, ResourceLocation modelNormal, ResourceLocation modelVertical) {
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).
                with(PropertyDispatch.property(BlockStateProperties.FACING).
                        select(Direction.UP, Variant.variant().
                                with(VariantProperties.MODEL, modelVertical)).
                        select(Direction.DOWN, Variant.variant().
                                with(VariantProperties.MODEL, modelVertical).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).
                        select(Direction.NORTH, Variant.variant().
                                with(VariantProperties.MODEL, modelNormal)).
                        select(Direction.SOUTH, Variant.variant().
                                with(VariantProperties.MODEL, modelNormal).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                        select(Direction.EAST, Variant.variant().
                                with(VariantProperties.MODEL, modelNormal).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                        select(Direction.WEST, Variant.variant().
                                with(VariantProperties.MODEL, modelNormal).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                ));

        generator.delegateItemModel(block.asItem(), modelNormal);
    }

    private void activatableBlockWithItem(Block block, ResourceLocation modelNormal,
                                          ResourceLocation modelActive, BooleanProperty isActiveProperty) {
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).
                with(PropertyDispatch.property(isActiveProperty).
                        select(false, Variant.variant().
                                with(VariantProperties.MODEL, modelNormal)).
                        select(true, Variant.variant().
                                with(VariantProperties.MODEL, modelActive))
                ));

        generator.delegateItemModel(block.asItem(), modelNormal);
    }

    private void activatableOrientableBlockWithItem(Block block, ResourceLocation modelNormal,
                                                    ResourceLocation modelActive, BooleanProperty isActiveProperty) {
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).
                with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, isActiveProperty).
                        select(Direction.NORTH, false, Variant.variant().
                                with(VariantProperties.MODEL, modelNormal)).
                        select(Direction.NORTH, true, Variant.variant().
                                with(VariantProperties.MODEL, modelActive)).
                        select(Direction.SOUTH, false, Variant.variant().
                                with(VariantProperties.MODEL, modelNormal).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                        select(Direction.SOUTH, true, Variant.variant().
                                with(VariantProperties.MODEL, modelActive).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                        select(Direction.EAST, false, Variant.variant().
                                with(VariantProperties.MODEL, modelNormal).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                        select(Direction.EAST, true, Variant.variant().
                                with(VariantProperties.MODEL, modelActive).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                        select(Direction.WEST, false, Variant.variant().
                                with(VariantProperties.MODEL, modelNormal).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).
                        select(Direction.WEST, true, Variant.variant().
                                with(VariantProperties.MODEL, modelActive).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                ));

        generator.delegateItemModel(block.asItem(), modelNormal);
    }

    private void itemConveyorBeltBlockWithItem(ItemConveyorBeltBlock block) {
        ResourceLocation modelFlat = ModTexturedModel.ITEM_CONVEYOR_BELT_FLAT.get(block).
                createWithSuffix(block, "_flat", generator.modelOutput);
        ResourceLocation modelAscending = ModTexturedModel.ITEM_CONVEYOR_BELT_ASCENDING.get(block).
                createWithSuffix(block, "_ascending", generator.modelOutput);
        ResourceLocation modelDescending = ModTexturedModel.ITEM_CONVEYOR_BELT_DESCENDING.get(block).
                createWithSuffix(block, "_descending", generator.modelOutput);

        PropertyDispatch.C1<EPBlockStateProperties.ConveyorBeltDirection> builder =
                PropertyDispatch.property(ItemConveyorBeltBlock.FACING);

        for(EPBlockStateProperties.ConveyorBeltDirection beltDir: EPBlockStateProperties.ConveyorBeltDirection.values()) {
            Variant blockStateVariant = Variant.variant();

            if(beltDir.isAscending()) {
                switch(beltDir.getDirection()) {
                    case NORTH -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
                    case WEST -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                    case EAST -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
                }

                blockStateVariant.with(VariantProperties.MODEL, modelAscending);
            }else if(beltDir.isDescending()) {
                switch(beltDir.getDirection()) {
                    case SOUTH -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
                    case WEST -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
                    case EAST -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                }

                blockStateVariant.with(VariantProperties.MODEL, modelDescending);
            }else {
                switch(beltDir.getDirection()) {
                    case NORTH -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
                    case WEST -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                    case EAST -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
                }

                blockStateVariant.with(VariantProperties.MODEL, modelFlat);
            }

            builder.select(beltDir, blockStateVariant);
        }

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).
                with(builder));

        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(block), TextureMapping.layer0(TextureMapping.getBlockTexture(block)), generator.modelOutput);
    }

    private void fluidPipeBlockWithItem(Block block) {
        ResourceLocation fluidPipeCore = ModTexturedModel.FLUID_PIPE_CORE.get(block).
                createWithSuffix(block, "_core", generator.modelOutput);
        ResourceLocation fluidPipeSideConnected = ModTexturedModel.FLUID_PIPE_SIDE_CONNECTED.get(block).
                createWithSuffix(block, "_side_connected", generator.modelOutput);
        ResourceLocation fluidPipeSideExtract = ModTexturedModel.FLUID_PIPE_SIDE_EXTRACT.get(block).
                createWithSuffix(block, "_side_extract", generator.modelOutput);

        generator.blockStateOutput.accept(
                MultiPartGenerator.multiPart(block).
                        with(Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeCore)).
                        with(Condition.condition().term(FluidPipeBlock.UP, EPBlockStateProperties.PipeConnection.CONNECTED), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideConnected).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)).
                        with(Condition.condition().term(FluidPipeBlock.UP, EPBlockStateProperties.PipeConnection.EXTRACT), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideExtract).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)).
                        with(Condition.condition().term(FluidPipeBlock.DOWN, EPBlockStateProperties.PipeConnection.CONNECTED), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideConnected).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).
                        with(Condition.condition().term(FluidPipeBlock.DOWN, EPBlockStateProperties.PipeConnection.EXTRACT), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideExtract).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).
                        with(Condition.condition().term(FluidPipeBlock.NORTH, EPBlockStateProperties.PipeConnection.CONNECTED), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideConnected)).
                        with(Condition.condition().term(FluidPipeBlock.NORTH, EPBlockStateProperties.PipeConnection.EXTRACT), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideExtract)).
                        with(Condition.condition().term(FluidPipeBlock.SOUTH, EPBlockStateProperties.PipeConnection.CONNECTED), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideConnected).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).
                        with(Condition.condition().term(FluidPipeBlock.SOUTH, EPBlockStateProperties.PipeConnection.EXTRACT), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideExtract).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).
                        with(Condition.condition().term(FluidPipeBlock.EAST, EPBlockStateProperties.PipeConnection.CONNECTED), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideConnected).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                        with(Condition.condition().term(FluidPipeBlock.EAST, EPBlockStateProperties.PipeConnection.EXTRACT), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideExtract).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                        with(Condition.condition().term(FluidPipeBlock.WEST, EPBlockStateProperties.PipeConnection.CONNECTED), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideConnected).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).
                        with(Condition.condition().term(FluidPipeBlock.WEST, EPBlockStateProperties.PipeConnection.EXTRACT), Variant.variant().
                                with(VariantProperties.MODEL, fluidPipeSideExtract).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
        );

        generator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.asItem()), new ItemWithDisplayModelSupplier(fluidPipeCore,
                new Vector3f(.65f, .65f, .65f),
                new Vector3f(1.f, 1.f, 1.f),
                new Vec3i(30, 45, 0)
        ));
    }

    private void fluidTankBlockWithItem(Block block) {
        ResourceLocation fluidTank = ModTexturedModel.FLUID_TANK.get(block).create(block, generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).
                with(PropertyDispatch.property(FluidTankBlock.FACING).
                        select(Direction.NORTH, Variant.variant().
                                with(VariantProperties.MODEL, fluidTank)).
                        select(Direction.SOUTH, Variant.variant().
                                with(VariantProperties.MODEL, fluidTank).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                        select(Direction.EAST, Variant.variant().
                                with(VariantProperties.MODEL, fluidTank).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                        select(Direction.WEST, Variant.variant().
                                with(VariantProperties.MODEL, fluidTank).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                ));

        generator.delegateItemModel(block.asItem(), fluidTank);
    }

    private void cableBlockWithItem(Block block) {
        ResourceLocation cableCore = ModTexturedModel.CABLE_CORE.get(block).createWithSuffix(block, "_core", generator.modelOutput);
        ResourceLocation cableSide = ModTexturedModel.CABLE_SIDE.get(block).createWithSuffix(block, "_side", generator.modelOutput);

        generator.blockStateOutput.accept(
                MultiPartGenerator.multiPart(block).
                        with(Variant.variant().
                                with(VariantProperties.MODEL, cableCore)).
                        with(Condition.condition().term(CableBlock.UP, true), Variant.variant().
                                with(VariantProperties.MODEL, cableSide).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)).
                        with(Condition.condition().term(CableBlock.DOWN, true), Variant.variant().
                                with(VariantProperties.MODEL, cableSide).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).
                        with(Condition.condition().term(CableBlock.NORTH, true), Variant.variant().
                                with(VariantProperties.MODEL, cableSide)).
                        with(Condition.condition().term(CableBlock.SOUTH, true), Variant.variant().
                                with(VariantProperties.MODEL, cableSide).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).
                        with(Condition.condition().term(CableBlock.EAST, true), Variant.variant().
                                with(VariantProperties.MODEL, cableSide).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                        with(Condition.condition().term(CableBlock.WEST, true), Variant.variant().
                                with(VariantProperties.MODEL, cableSide).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
        );

        generator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.asItem()), new ItemWithDisplayModelSupplier(cableCore,
                new Vector3f(1.01f, 1.01f, 1.01f),
                new Vector3f(1.5f, 1.5f, 1.5f),
                new Vec3i(30, 45, 0)
        ));
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

                ResourceLocation transformer = TexturedModel.createDefault(unused -> new TextureMapping().
                                put(TextureSlot.TOP, EPAPI.id("block/" + textureName + multipleSuffix)).
                                put(TextureSlot.BOTTOM, EPAPI.id("block/" + textureName + multipleSuffix)).
                                put(TextureSlot.FRONT, EPAPI.id("block/" + textureName + singleSuffix)).
                                put(TextureSlot.SIDE, EPAPI.id("block/" + textureName + multipleSuffix)).
                                copySlot(TextureSlot.TOP, TextureSlot.PARTICLE),
                        ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM).get(block).create(block, generator.modelOutput);

                generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).
                        with(PropertyDispatch.property(BlockStateProperties.FACING).
                                select(Direction.UP, Variant.variant().
                                        with(VariantProperties.MODEL, transformer).
                                        with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)).
                                select(Direction.DOWN, Variant.variant().
                                        with(VariantProperties.MODEL, transformer).
                                        with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).
                                select(Direction.NORTH, Variant.variant().
                                        with(VariantProperties.MODEL, transformer)).
                                select(Direction.SOUTH, Variant.variant().
                                        with(VariantProperties.MODEL, transformer).
                                        with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                                select(Direction.EAST, Variant.variant().
                                        with(VariantProperties.MODEL, transformer).
                                        with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                                select(Direction.WEST, Variant.variant().
                                        with(VariantProperties.MODEL, transformer).
                                        with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                        ));

                generator.delegateItemModel(block.asItem(), transformer);
            }
            case TYPE_3_TO_3 -> {
                ResourceLocation transformer = TexturedModel.createDefault(unused -> new TextureMapping().
                                put(TextureSlot.UP, EPAPI.id("block/" + textureName + "_input")).
                                put(TextureSlot.DOWN, EPAPI.id("block/" + textureName + "_output")).
                                put(TextureSlot.NORTH, EPAPI.id("block/" + textureName + "_input")).
                                put(TextureSlot.SOUTH, EPAPI.id("block/" + textureName + "_output")).
                                put(TextureSlot.EAST, EPAPI.id("block/" + textureName + "_output")).
                                put(TextureSlot.WEST, EPAPI.id("block/" + textureName + "_input")).
                                copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                        ModelTemplates.CUBE).get(block).create(block, generator.modelOutput);

                generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).
                        with(PropertyDispatch.property(BlockStateProperties.FACING).
                                select(Direction.UP, Variant.variant().
                                        with(VariantProperties.MODEL, transformer).
                                        with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)).
                                select(Direction.DOWN, Variant.variant().
                                        with(VariantProperties.MODEL, transformer).
                                        with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).
                                        with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                                select(Direction.NORTH, Variant.variant().
                                        with(VariantProperties.MODEL, transformer)).
                                select(Direction.SOUTH, Variant.variant().
                                        with(VariantProperties.MODEL, transformer).
                                        with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).
                                        with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                                select(Direction.EAST, Variant.variant().
                                        with(VariantProperties.MODEL, transformer).
                                        with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                                select(Direction.WEST, Variant.variant().
                                        with(VariantProperties.MODEL, transformer).
                                        with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).
                                        with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                        ));

                generator.delegateItemModel(block.asItem(), transformer);
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

        ResourceLocation allCube = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, EPAPI.id("block/" + textureName + "_not_connected")).
                        put(TextureSlot.DOWN, EPAPI.id("block/" + textureName + "_not_connected")).
                        put(TextureSlot.NORTH, EPAPI.id("block/" + textureName + "_output")).
                        put(TextureSlot.SOUTH, EPAPI.id("block/" + textureName + "_not_connected")).
                        put(TextureSlot.EAST, EPAPI.id("block/" + textureName + "_input")).
                        put(TextureSlot.WEST, EPAPI.id("block/" + textureName + "_not_connected")).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block).createWithSuffix(block, "_cube", generator.modelOutput);

        ResourceLocation notConnectedSide = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.SIDE, EPAPI.id("block/" + textureName + "_not_connected")).
                        copySlot(TextureSlot.SIDE, TextureSlot.PARTICLE),
                ModModels.SINGLE_SIDE).get(block).createWithSuffix(block, "_not_connected", generator.modelOutput);
        ResourceLocation receiveSide = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.SIDE, EPAPI.id("block/" + textureName + "_input")).
                        copySlot(TextureSlot.SIDE, TextureSlot.PARTICLE),
                ModModels.SINGLE_SIDE).get(block).createWithSuffix(block, "_input", generator.modelOutput);
        ResourceLocation extractSide = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.SIDE, EPAPI.id("block/" + textureName + "_output")).
                        copySlot(TextureSlot.SIDE, TextureSlot.PARTICLE),
                ModModels.SINGLE_SIDE).get(block).createWithSuffix(block, "_output", generator.modelOutput);

        generator.blockStateOutput.accept(MultiPartGenerator.multiPart(block).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.UP, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        Variant.variant().with(VariantProperties.MODEL, notConnectedSide).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.UP, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        Variant.variant().with(VariantProperties.MODEL, receiveSide).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.UP, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        Variant.variant().with(VariantProperties.MODEL, extractSide).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.DOWN, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        Variant.variant().with(VariantProperties.MODEL, notConnectedSide).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.DOWN, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        Variant.variant().with(VariantProperties.MODEL, receiveSide).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.DOWN, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        Variant.variant().with(VariantProperties.MODEL, extractSide).
                                with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.NORTH, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        Variant.variant().with(VariantProperties.MODEL, notConnectedSide)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.NORTH, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        Variant.variant().with(VariantProperties.MODEL, receiveSide)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.NORTH, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        Variant.variant().with(VariantProperties.MODEL, extractSide)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.SOUTH, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        Variant.variant().with(VariantProperties.MODEL, notConnectedSide).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.SOUTH, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        Variant.variant().with(VariantProperties.MODEL, receiveSide).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.SOUTH, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        Variant.variant().with(VariantProperties.MODEL, extractSide).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.EAST, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        Variant.variant().with(VariantProperties.MODEL, notConnectedSide).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.EAST, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        Variant.variant().with(VariantProperties.MODEL, receiveSide).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.EAST, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        Variant.variant().with(VariantProperties.MODEL, extractSide).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.WEST, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED),
                        Variant.variant().with(VariantProperties.MODEL, notConnectedSide).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.WEST, EPBlockStateProperties.TransformerConnection.RECEIVE),
                        Variant.variant().with(VariantProperties.MODEL, receiveSide).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).
                with(
                        Condition.condition().
                                term(ConfigurableTransformerBlock.WEST, EPBlockStateProperties.TransformerConnection.EXTRACT),
                        Variant.variant().with(VariantProperties.MODEL, extractSide).
                                with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
        );

        generator.delegateItemModel(block, allCube);
    }

    private void solarPanelBlockWithItem(Block block) {
        ResourceLocation solarPanel = ModTexturedModel.SOLAR_PANEL.get(block).create(block, generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block,
                Variant.variant().with(VariantProperties.MODEL, solarPanel)));

        generator.delegateItemModel(block.asItem(), solarPanel);
    }

    private void activatableOrientableMachineBlockWithItem(Block block, boolean uniqueBottomTexture) {
        activatableOrientableBlockWithItem(block,
                orientableBlockModel(block, uniqueBottomTexture),
                orientableOnBlockModel(block, uniqueBottomTexture),
                EPBlockStateProperties.WORKING);
    }

    private void poweredLampBlockWithItem(Block block) {
        ResourceLocation modelOff = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.ALL, TextureMapping.getBlockTexture(block)),
                ModelTemplates.CUBE_ALL).get(block).create(block, generator.modelOutput);

        ResourceLocation modelOn = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.ALL, TextureMapping.getBlockTexture(block, "_on")),
                ModelTemplates.CUBE_ALL).get(block).createWithSuffix(block, "_on", generator.modelOutput);

        PropertyDispatch.C1<Integer> builder = PropertyDispatch.property(BlockStateProperties.LEVEL).
                select(0, Variant.variant().
                        with(VariantProperties.MODEL, modelOff));

        for(int i = 1;i < 16;i++)
            builder.select(i, Variant.variant().
                    with(VariantProperties.MODEL, modelOn));

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).
                with(builder));

        generator.delegateItemModel(block.asItem(), modelOff);
    }
}
