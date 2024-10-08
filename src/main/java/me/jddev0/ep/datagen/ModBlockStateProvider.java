package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.*;
import me.jddev0.ep.datagen.model.ItemWithDisplayModelSupplier;
import me.jddev0.ep.datagen.model.ModModels;
import me.jddev0.ep.datagen.model.ModTexturedModel;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;

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

        itemConveyorBeltBlockWithItem(EPBlocks.ITEM_CONVEYOR_BELT);

        orientableSixDirsBlockWithBackItem(EPBlocks.ITEM_CONVEYOR_BELT_LOADER, false);

        orientableBlockWithItem(EPBlocks.ITEM_CONVEYOR_BELT_SORTER,
                cubeBlockModel(EPBlocks.ITEM_CONVEYOR_BELT_SORTER, "", "_top", "_top",
                        "_input", "_output_2", "_output_3", "_output_1"));

        activatableOrientableBlockWithItem(EPBlocks.ITEM_CONVEYOR_BELT_SWITCH,
                cubeBlockModel(EPBlocks.ITEM_CONVEYOR_BELT_SWITCH, "", "_top", "_top",
                        "_input", "_side", "_output_disabled", "_output_enabled"),
                cubeBlockModel(EPBlocks.ITEM_CONVEYOR_BELT_SWITCH, "_powered", "_top", "_top",
                        "_input", "_side", "_output_enabled", "_output_disabled"),
                ItemConveyorBeltSwitchBlock.POWERED);

        orientableBlockWithItem(EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER,
                cubeBlockModel(EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER, "", "_top", "_top",
                        "_input", "_output", "_output", "_output"));

        orientableBlockWithItem(EPBlocks.ITEM_CONVEYOR_BELT_MERGER,
                cubeBlockModel(EPBlocks.ITEM_CONVEYOR_BELT_MERGER, "", "_top", "_top",
                        "_output", "_input", "_input", "_input"));

        fluidPipeBlockWithItem(EPBlocks.IRON_FLUID_PIPE);
        fluidPipeBlockWithItem(EPBlocks.GOLDEN_FLUID_PIPE);

        fluidTankBlockWithItem(EPBlocks.FLUID_TANK_SMALL);
        fluidTankBlockWithItem(EPBlocks.FLUID_TANK_MEDIUM);
        fluidTankBlockWithItem(EPBlocks.FLUID_TANK_LARGE);
        fluidTankBlockWithItem(EPBlocks.CREATIVE_FLUID_TANK);

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

        orientableBlockWithItem(EPBlocks.STONE_SOLIDIFIER,
                orientableBlockModel(EPBlocks.STONE_SOLIDIFIER, false));

        orientableBlockWithItem(EPBlocks.FLUID_TRANSPOSER,
                orientableBlockModel(EPBlocks.FLUID_TRANSPOSER, false));

        horizontalTwoSideBlockWithItem(EPBlocks.FILTRATION_PLANT, false);

        horizontalBlockWithItem(EPBlocks.FLUID_DRAINER, true);

        horizontalBlockWithItem(EPBlocks.FLUID_PUMP, false);

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

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block,
                BlockStateVariant.create().put(VariantSettings.MODEL, model)));

        generator.registerParentedItemModel(block.asItem(), model);
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

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block,
                BlockStateVariant.create().put(VariantSettings.MODEL, model)));

        generator.registerParentedItemModel(block.asItem(), model);
    }

    private void orientableBlockWithItem(Block block, Identifier model) {
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).
                coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING).
                        register(Direction.NORTH, BlockStateVariant.create().
                                put(VariantSettings.MODEL, model)).
                        register(Direction.SOUTH, BlockStateVariant.create().
                                put(VariantSettings.MODEL, model).
                                put(VariantSettings.Y, VariantSettings.Rotation.R180)).
                        register(Direction.EAST, BlockStateVariant.create().
                                put(VariantSettings.MODEL, model).
                                put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                        register(Direction.WEST, BlockStateVariant.create().
                                put(VariantSettings.MODEL, model).
                                put(VariantSettings.Y, VariantSettings.Rotation.R270))
                ));

        generator.registerParentedItemModel(block.asItem(), model);
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
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).
                coordinate(BlockStateVariantMap.create(Properties.FACING).
                        register(Direction.UP, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelVertical)).
                        register(Direction.DOWN, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelVertical).
                                put(VariantSettings.X, VariantSettings.Rotation.R180)).
                        register(Direction.NORTH, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelNormal)).
                        register(Direction.SOUTH, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelNormal).
                                put(VariantSettings.Y, VariantSettings.Rotation.R180)).
                        register(Direction.EAST, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelNormal).
                                put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                        register(Direction.WEST, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelNormal).
                                put(VariantSettings.Y, VariantSettings.Rotation.R270))
                ));

        generator.registerParentedItemModel(block.asItem(), modelNormal);
    }

    private void activatableBlockWithItem(Block block, Identifier modelNormal,
                                          Identifier modelActive, BooleanProperty isActiveProperty) {
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).
                coordinate(BlockStateVariantMap.create(isActiveProperty).
                        register(false, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelNormal)).
                        register(true, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelActive))
                ));

        generator.registerParentedItemModel(block.asItem(), modelNormal);
    }

    private void activatableOrientableBlockWithItem(Block block, Identifier modelNormal,
                                                    Identifier modelActive, BooleanProperty isActiveProperty) {
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).
                coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, isActiveProperty).
                        register(Direction.NORTH, false, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelNormal)).
                        register(Direction.NORTH, true, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelActive)).
                        register(Direction.SOUTH, false, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelNormal).
                                put(VariantSettings.Y, VariantSettings.Rotation.R180)).
                        register(Direction.SOUTH, true, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelActive).
                                put(VariantSettings.Y, VariantSettings.Rotation.R180)).
                        register(Direction.EAST, false, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelNormal).
                                put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                        register(Direction.EAST, true, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelActive).
                                put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                        register(Direction.WEST, false, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelNormal).
                                put(VariantSettings.Y, VariantSettings.Rotation.R270)).
                        register(Direction.WEST, true, BlockStateVariant.create().
                                put(VariantSettings.MODEL, modelActive).
                                put(VariantSettings.Y, VariantSettings.Rotation.R270))
                ));

        generator.registerParentedItemModel(block.asItem(), modelNormal);
    }

    private void itemConveyorBeltBlockWithItem(ItemConveyorBeltBlock block) {
        Identifier modelFlat = ModTexturedModel.ITEM_CONVEYOR_BELT_FLAT.get(block).
                upload(block, "_flat", generator.modelCollector);
        Identifier modelAscending = ModTexturedModel.ITEM_CONVEYOR_BELT_ASCENDING.get(block).
                upload(block, "_ascending", generator.modelCollector);
        Identifier modelDescending = ModTexturedModel.ITEM_CONVEYOR_BELT_DESCENDING.get(block).
                upload(block, "_descending", generator.modelCollector);

        BlockStateVariantMap.SingleProperty<EPBlockStateProperties.ConveyorBeltDirection> builder =
                BlockStateVariantMap.create(ItemConveyorBeltBlock.FACING);

        for(EPBlockStateProperties.ConveyorBeltDirection beltDir: EPBlockStateProperties.ConveyorBeltDirection.values()) {
            BlockStateVariant blockStateVariant = BlockStateVariant.create();

            if(beltDir.isAscending()) {
                switch(beltDir.getDirection()) {
                    case NORTH -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180);
                    case WEST -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90);
                    case EAST -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270);
                }

                blockStateVariant.put(VariantSettings.MODEL, modelAscending);
            }else if(beltDir.isDescending()) {
                switch(beltDir.getDirection()) {
                    case SOUTH -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180);
                    case WEST -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270);
                    case EAST -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90);
                }

                blockStateVariant.put(VariantSettings.MODEL, modelDescending);
            }else {
                switch(beltDir.getDirection()) {
                    case NORTH -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180);
                    case WEST -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90);
                    case EAST -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270);
                }

                blockStateVariant.put(VariantSettings.MODEL, modelFlat);
            }

            builder.register(beltDir, blockStateVariant);
        }

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).
                coordinate(builder));

        Models.GENERATED.upload(ModelIds.getBlockModelId(block), TextureMap.layer0(TextureMap.getId(block)), generator.modelCollector);
    }

    private void fluidPipeBlockWithItem(Block block) {
        Identifier fluidPipeCore = ModTexturedModel.FLUID_PIPE_CORE.get(block).
                upload(block, "_core", generator.modelCollector);
        Identifier fluidPipeSideConnected = ModTexturedModel.FLUID_PIPE_SIDE_CONNECTED.get(block).
                upload(block, "_side_connected", generator.modelCollector);
        Identifier fluidPipeSideExtract = ModTexturedModel.FLUID_PIPE_SIDE_EXTRACT.get(block).
                upload(block, "_side_extract", generator.modelCollector);

        generator.blockStateCollector.accept(
                MultipartBlockStateSupplier.create(block).
                        with(BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeCore)).
                        with(When.create().set(FluidPipeBlock.UP, EPBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected).
                                put(VariantSettings.X, VariantSettings.Rotation.R270)).
                        with(When.create().set(FluidPipeBlock.UP, EPBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract).
                                put(VariantSettings.X, VariantSettings.Rotation.R270)).
                        with(When.create().set(FluidPipeBlock.DOWN, EPBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected).
                                put(VariantSettings.X, VariantSettings.Rotation.R90)).
                        with(When.create().set(FluidPipeBlock.DOWN, EPBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract).
                                put(VariantSettings.X, VariantSettings.Rotation.R90)).
                        with(When.create().set(FluidPipeBlock.NORTH, EPBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected)).
                        with(When.create().set(FluidPipeBlock.NORTH, EPBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract)).
                        with(When.create().set(FluidPipeBlock.SOUTH, EPBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected).
                                put(VariantSettings.X, VariantSettings.Rotation.R180)).
                        with(When.create().set(FluidPipeBlock.SOUTH, EPBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract).
                                put(VariantSettings.X, VariantSettings.Rotation.R180)).
                        with(When.create().set(FluidPipeBlock.EAST, EPBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected).
                                put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                        with(When.create().set(FluidPipeBlock.EAST, EPBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract).
                                put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                        with(When.create().set(FluidPipeBlock.WEST, EPBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected).
                                put(VariantSettings.Y, VariantSettings.Rotation.R270)).
                        with(When.create().set(FluidPipeBlock.WEST, EPBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract).
                                put(VariantSettings.Y, VariantSettings.Rotation.R270))
        );

        generator.modelCollector.accept(ModelIds.getItemModelId(block.asItem()), new ItemWithDisplayModelSupplier(fluidPipeCore,
                new Vector3f(.65f, .65f, .65f),
                new Vector3f(1.f, 1.f, 1.f),
                new Vec3i(30, 45, 0)
        ));
    }

    private void fluidTankBlockWithItem(Block block) {
        Identifier fluidTank = ModTexturedModel.FLUID_TANK.get(block).upload(block, generator.modelCollector);

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).
                coordinate(BlockStateVariantMap.create(FluidTankBlock.FACING).
                        register(Direction.NORTH, BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidTank)).
                        register(Direction.SOUTH, BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidTank).
                                put(VariantSettings.Y, VariantSettings.Rotation.R180)).
                        register(Direction.EAST, BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidTank).
                                put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                        register(Direction.WEST, BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidTank).
                                put(VariantSettings.Y, VariantSettings.Rotation.R270))
                ));

        generator.registerParentedItemModel(block.asItem(), fluidTank);
    }

    private void cableBlockWithItem(Block block) {
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
                new Vector3f(1.01f, 1.01f, 1.01f),
                new Vector3f(1.5f, 1.5f, 1.5f),
                new Vec3i(30, 45, 0)
        ));
    }

    private void transformerBlockWithItem(TransformerBlock block) {
        String textureName = switch(block.getTier()) {
            case TIER_LV -> "lv_transformer";
            case TIER_MV -> "mv_transformer";
            case TIER_HV -> "hv_transformer";
            case TIER_EHV -> "ehv_transformer";
        };

        TransformerBlock.Type transformerType = block.getTransformerType();
        switch(transformerType) {
            case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                String singleSuffix = transformerType == TransformerBlock.Type.TYPE_1_TO_N?"_input":"_output";
                String multipleSuffix = transformerType == TransformerBlock.Type.TYPE_1_TO_N?"_output":"_input";

                Identifier transformer = TexturedModel.makeFactory(unused -> new TextureMap().
                                put(TextureKey.TOP, EPAPI.id("block/" + textureName + multipleSuffix)).
                                put(TextureKey.BOTTOM, EPAPI.id("block/" + textureName + multipleSuffix)).
                                put(TextureKey.FRONT, EPAPI.id("block/" + textureName + singleSuffix)).
                                put(TextureKey.SIDE, EPAPI.id("block/" + textureName + multipleSuffix)).
                                copy(TextureKey.TOP, TextureKey.PARTICLE),
                        Models.ORIENTABLE_WITH_BOTTOM).get(block).upload(block, generator.modelCollector);

                generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).
                        coordinate(BlockStateVariantMap.create(Properties.FACING).
                                register(Direction.UP, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer).
                                        put(VariantSettings.X, VariantSettings.Rotation.R270)).
                                register(Direction.DOWN, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer).
                                        put(VariantSettings.X, VariantSettings.Rotation.R90)).
                                register(Direction.NORTH, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer)).
                                register(Direction.SOUTH, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer).
                                        put(VariantSettings.Y, VariantSettings.Rotation.R180)).
                                register(Direction.EAST, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer).
                                        put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                                register(Direction.WEST, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer).
                                        put(VariantSettings.Y, VariantSettings.Rotation.R270))
                        ));

                generator.registerParentedItemModel(block.asItem(), transformer);
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

                generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).
                        coordinate(BlockStateVariantMap.create(Properties.FACING).
                                register(Direction.UP, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer).
                                        put(VariantSettings.X, VariantSettings.Rotation.R270)).
                                register(Direction.DOWN, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer).
                                        put(VariantSettings.X, VariantSettings.Rotation.R90).
                                        put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                                register(Direction.NORTH, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer)).
                                register(Direction.SOUTH, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer).
                                        put(VariantSettings.X, VariantSettings.Rotation.R90).
                                        put(VariantSettings.Y, VariantSettings.Rotation.R180)).
                                register(Direction.EAST, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer).
                                        put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                                register(Direction.WEST, BlockStateVariant.create().
                                        put(VariantSettings.MODEL, transformer).
                                        put(VariantSettings.X, VariantSettings.Rotation.R90).
                                        put(VariantSettings.Y, VariantSettings.Rotation.R270))
                        ));

                generator.registerParentedItemModel(block.asItem(), transformer);
            }
        }
    }

    private void solarPanelBlockWithItem(Block block) {
        Identifier solarPanel = ModTexturedModel.SOLAR_PANEL.get(block).upload(block, generator.modelCollector);

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block,
                BlockStateVariant.create().put(VariantSettings.MODEL, solarPanel)));

        generator.registerParentedItemModel(block.asItem(), solarPanel);
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

        BlockStateVariantMap.SingleProperty<Integer> builder = BlockStateVariantMap.create(Properties.LEVEL_15).
                register(0, BlockStateVariant.create().
                        put(VariantSettings.MODEL, modelOff));

        for(int i = 1;i < 16;i++)
            builder.register(i, BlockStateVariant.create().
                    put(VariantSettings.MODEL, modelOn));

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).
                coordinate(builder));

        generator.registerParentedItemModel(block.asItem(), modelOff);
    }
}
