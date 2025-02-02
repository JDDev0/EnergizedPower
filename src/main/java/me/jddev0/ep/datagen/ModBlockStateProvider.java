package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.*;
import me.jddev0.ep.datagen.model.ModModelTemplates;
import me.jddev0.ep.datagen.model.ModTexturedModel;
import me.jddev0.ep.fluid.EPFluids;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
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

        generator.createNonTemplateModelBlock(EPFluids.DIRTY_WATER_BLOCK.get());
    }

    private void cubeAllBlockWithItem(Holder<Block> block) {
        generator.createTrivialCube(block.value());
    }

    private ResourceLocation cubeBlockModel(Holder<Block> block, String fileSuffix, String upSuffix,
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

    private ResourceLocation orientableBlockModel(Holder<Block> block, boolean uniqueBottomTexture) {
        return orientableBlockModel(block, "", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_side");
    }

    private ResourceLocation orientableOnBlockModel(Holder<Block> block, boolean uniqueBottomTexture) {
        return orientableBlockModel(block, "_on", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front_on", "_side");
    }

    private ResourceLocation orientableBlockModel(Holder<Block> block, String fileSuffix, String topSuffix,
                                            String bottomSuffix, String frontSuffix, String sideSuffix) {
        return TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.TOP, TextureMapping.getBlockTexture(block.value(), topSuffix)).
                        put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block.value(), bottomSuffix)).
                        put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block.value(), frontSuffix)).
                        put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block.value(), sideSuffix)).
                        copySlot(TextureSlot.TOP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM).get(block.value()).createWithSuffix(block.value(), fileSuffix, generator.modelOutput);
    }

    private ResourceLocation orientableWithBackBlockModel(Holder<Block> block, boolean uniqueBottomTexture) {
        return orientableWithBackBlockModel(block, "", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_back", "_side");
    }

    private ResourceLocation orientableWithBackBlockModel(Holder<Block> block, String fileSuffix, String topSuffix,
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

    private ResourceLocation orientableVerticalWithBackBlockModel(Holder<Block> block, boolean uniqueBottomTexture) {
        return orientableVerticalWithBackBlockModel(block, "_vertical", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_back", "_side");
    }

    private ResourceLocation orientableVerticalWithBackBlockModel(Holder<Block> block, String fileSuffix, String topSuffix,
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

    private ResourceLocation orientableVerticalBlockModel(Holder<Block> block, boolean uniqueBottomTexture) {
        return orientableVerticalBlockModel(block, "_vertical", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_side");
    }

    private ResourceLocation orientableVerticalBlockModel(Holder<Block> block, String fileSuffix, String topSuffix,
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
        ResourceLocation model = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, TextureMapping.getBlockTexture(block.value(), "_top")).
                        put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block.value(), uniqueBottomTexture?"_bottom":"_top")).
                        put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block.value(), "_side")).
                        put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block.value(), "_side")).
                        put(TextureSlot.EAST, TextureMapping.getBlockTexture(block.value(), "_side")).
                        put(TextureSlot.WEST, TextureMapping.getBlockTexture(block.value(), "_side")).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block.value()).create(block.value(), generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value(),
                Variant.variant().with(VariantProperties.MODEL, model)));

        generator.registerSimpleItemModel(block.value(), model);
    }

    private void horizontalTwoSideBlockWithItem(Holder<Block> block, boolean uniqueBottomTexture) {
        ResourceLocation model = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.UP, TextureMapping.getBlockTexture(block.value(), "_top")).
                        put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block.value(), uniqueBottomTexture?"_bottom":"_top")).
                        put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block.value(), "_front")).
                        put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block.value(), "_side")).
                        put(TextureSlot.EAST, TextureMapping.getBlockTexture(block.value(), "_side")).
                        put(TextureSlot.WEST, TextureMapping.getBlockTexture(block.value(), "_front")).
                        copySlot(TextureSlot.UP, TextureSlot.PARTICLE),
                ModelTemplates.CUBE).get(block.value()).create(block.value(), generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value(),
                Variant.variant().with(VariantProperties.MODEL, model)));

        generator.registerSimpleItemModel(block.value(), model);
    }

    private void orientableBlockWithItem(Holder<Block> block, ResourceLocation model) {
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value()).
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

    private void orientableSixDirsBlockWithItem(Holder<Block> block, ResourceLocation modelNormal, ResourceLocation modelVertical) {
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value()).
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

        generator.registerSimpleItemModel(block.value(), modelNormal);
    }

    private void activatableBlockWithItem(Holder<Block> block, ResourceLocation modelNormal,
                                          ResourceLocation modelActive, BooleanProperty isActiveProperty) {
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value()).
                with(PropertyDispatch.property(isActiveProperty).
                        select(false, Variant.variant().
                                with(VariantProperties.MODEL, modelNormal)).
                        select(true, Variant.variant().
                                with(VariantProperties.MODEL, modelActive))
                ));

        generator.registerSimpleItemModel(block.value(), modelNormal);
    }

    private void activatableOrientableBlockWithItem(Holder<Block> block, ResourceLocation modelNormal,
                                                    ResourceLocation modelActive, BooleanProperty isActiveProperty) {
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value()).
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

        generator.registerSimpleItemModel(block.value(), modelNormal);
    }

    private void itemConveyorBeltBlockWithItem(DeferredBlock<ItemConveyorBeltBlock> block) {
        ResourceLocation modelFlat = ModTexturedModel.ITEM_CONVEYOR_BELT_FLAT.get(block.value()).
                createWithSuffix(block.value(), "_flat", generator.modelOutput);
        ResourceLocation modelAscending = ModTexturedModel.ITEM_CONVEYOR_BELT_ASCENDING.get(block.value()).
                createWithSuffix(block.value(), "_ascending", generator.modelOutput);
        ResourceLocation modelDescending = ModTexturedModel.ITEM_CONVEYOR_BELT_DESCENDING.get(block.value()).
                createWithSuffix(block.value(), "_descending", generator.modelOutput);

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

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value()).
                with(builder));

        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(block.value()), TextureMapping.layer0(TextureMapping.getBlockTexture(block.value())), generator.modelOutput);
    }

    private void fluidPipeBlockWithItem(Holder<Block> block) {
        ResourceLocation fluidPipeCore = ModTexturedModel.FLUID_PIPE_CORE.get(block.value()).
                createWithSuffix(block.value(), "_core", generator.modelOutput);
        ResourceLocation fluidPipeSideConnected = ModTexturedModel.FLUID_PIPE_SIDE_CONNECTED.get(block.value()).
                createWithSuffix(block.value(), "_side_connected", generator.modelOutput);
        ResourceLocation fluidPipeSideExtract = ModTexturedModel.FLUID_PIPE_SIDE_EXTRACT.get(block.value()).
                createWithSuffix(block.value(), "_side_extract", generator.modelOutput);

        generator.blockStateOutput.accept(
                MultiPartGenerator.multiPart(block.value()).
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

        generator.registerSimpleItemModel(block.value(), fluidPipeCore);
    }

    private void fluidTankBlockWithItem(Holder<Block> block) {
        ResourceLocation fluidTank = ModTexturedModel.FLUID_TANK.get(block.value()).create(block.value(), generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value()).
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

        generator.registerSimpleItemModel(block.value(), fluidTank);
    }

    private void cableBlockWithItem(Holder<Block> block) {
        ResourceLocation cableCore = ModTexturedModel.CABLE_CORE.get(block.value()).createWithSuffix(block.value(), "_core", generator.modelOutput);
        ResourceLocation cableSide = ModTexturedModel.CABLE_SIDE.get(block.value()).createWithSuffix(block.value(), "_side", generator.modelOutput);

        generator.blockStateOutput.accept(
                MultiPartGenerator.multiPart(block.value()).
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

        generator.registerSimpleItemModel(block.value(), cableCore);
    }

    private void transformerBlockWithItem(DeferredBlock<TransformerBlock> block) {
        String textureName = switch(block.value().getTier()) {
            case TIER_LV -> "lv_transformer";
            case TIER_MV -> "mv_transformer";
            case TIER_HV -> "hv_transformer";
            case TIER_EHV -> "ehv_transformer";
        };

        TransformerBlock.Type transformerType = block.value().getTransformerType();
        switch(transformerType) {
            case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                String singleSuffix = transformerType == TransformerBlock.Type.TYPE_1_TO_N?"_input":"_output";
                String multipleSuffix = transformerType == TransformerBlock.Type.TYPE_1_TO_N?"_output":"_input";

                ResourceLocation transformer = TexturedModel.createDefault(unused -> new TextureMapping().
                                put(TextureSlot.TOP, EPAPI.id("block/" + textureName + multipleSuffix)).
                                put(TextureSlot.BOTTOM, EPAPI.id("block/" + textureName + multipleSuffix)).
                                put(TextureSlot.FRONT, EPAPI.id("block/" + textureName + singleSuffix)).
                                put(TextureSlot.SIDE, EPAPI.id("block/" + textureName + multipleSuffix)).
                                copySlot(TextureSlot.TOP, TextureSlot.PARTICLE),
                        ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM).get(block.value()).create(block.value(), generator.modelOutput);

                generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value()).
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

                generator.registerSimpleItemModel(block.value(), transformer);
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
                        ModelTemplates.CUBE).get(block.value()).create(block.value(), generator.modelOutput);

                generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value()).
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

                generator.registerSimpleItemModel(block.value(), transformer);
            }
        }
    }

    private void solarPanelBlockWithItem(Holder<Block> block) {
        ResourceLocation solarPanel = ModTexturedModel.SOLAR_PANEL.get(block.value()).create(block.value(), generator.modelOutput);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value(),
                Variant.variant().with(VariantProperties.MODEL, solarPanel)));

        generator.registerSimpleItemModel(block.value(), solarPanel);
    }

    private void activatableOrientableMachineBlockWithItem(Holder<Block> block, boolean uniqueBottomTexture) {
        activatableOrientableBlockWithItem(block,
                orientableBlockModel(block, uniqueBottomTexture),
                orientableOnBlockModel(block, uniqueBottomTexture),
                BlockStateProperties.LIT);
    }

    private void poweredLampBlockWithItem(Holder<Block> block) {
        ResourceLocation modelOff = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.ALL, TextureMapping.getBlockTexture(block.value())),
                ModelTemplates.CUBE_ALL).get(block.value()).create(block.value(), generator.modelOutput);

        ResourceLocation modelOn = TexturedModel.createDefault(unused -> new TextureMapping().
                        put(TextureSlot.ALL, TextureMapping.getBlockTexture(block.value(), "_on")),
                ModelTemplates.CUBE_ALL).get(block.value()).createWithSuffix(block.value(), "_on", generator.modelOutput);

        PropertyDispatch.C1<Integer> builder = PropertyDispatch.property(BlockStateProperties.LEVEL).
                select(0, Variant.variant().
                        with(VariantProperties.MODEL, modelOff));

        for(int i = 1;i < 16;i++)
            builder.select(i, Variant.variant().
                    with(VariantProperties.MODEL, modelOn));

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.value()).
                with(builder));

        generator.registerSimpleItemModel(block.value(), modelOff);
    }
}
