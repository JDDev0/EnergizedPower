package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.*;
import me.jddev0.ep.datagen.model.ItemWithDisplayModelSupplier;
import me.jddev0.ep.datagen.model.ItemWithOverridesModelSupplier;
import me.jddev0.ep.datagen.model.ModModels;
import me.jddev0.ep.datagen.model.ModTexturedModel;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
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

        fluidPipeBlockWithItem(generator, ModBlocks.IRON_FLUID_PIPE);
        fluidPipeBlockWithItem(generator, ModBlocks.GOLDEN_FLUID_PIPE);

        fluidTankBlockWithItem(generator, ModBlocks.FLUID_TANK_SMALL);
        fluidTankBlockWithItem(generator, ModBlocks.FLUID_TANK_MEDIUM);
        fluidTankBlockWithItem(generator, ModBlocks.FLUID_TANK_LARGE);
        fluidTankBlockWithItem(generator, ModBlocks.CREATIVE_FLUID_TANK);

        cableBlockWithItem(generator, ModBlocks.TIN_CABLE);
        cableBlockWithItem(generator, ModBlocks.COPPER_CABLE);
        cableBlockWithItem(generator, ModBlocks.GOLD_CABLE);
        cableBlockWithItem(generator, ModBlocks.ENERGIZED_COPPER_CABLE);
        cableBlockWithItem(generator, ModBlocks.ENERGIZED_GOLD_CABLE);
        cableBlockWithItem(generator, ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE);

        transformerBlockWithItem(generator, ModBlocks.LV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(generator, ModBlocks.LV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(generator, ModBlocks.LV_TRANSFORMER_N_TO_1);
        transformerBlockWithItem(generator, ModBlocks.MV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(generator, ModBlocks.MV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(generator, ModBlocks.MV_TRANSFORMER_N_TO_1);
        transformerBlockWithItem(generator, ModBlocks.HV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(generator, ModBlocks.HV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(generator, ModBlocks.HV_TRANSFORMER_N_TO_1);
        transformerBlockWithItem(generator, ModBlocks.EHV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(generator, ModBlocks.EHV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(generator, ModBlocks.EHV_TRANSFORMER_N_TO_1);

        horizontalBlockWithItem(generator, ModBlocks.BATTERY_BOX, true);
        horizontalBlockWithItem(generator, ModBlocks.ADVANCED_BATTERY_BOX, true);
        horizontalBlockWithItem(generator, ModBlocks.CREATIVE_BATTERY_BOX, true);

        horizontalTwoSideBlockWithItem(generator, ModBlocks.PRESS_MOLD_MAKER, true);

        horizontalTwoSideBlockWithItem(generator, ModBlocks.AUTO_CRAFTER, true);
        horizontalTwoSideBlockWithItem(generator, ModBlocks.ADVANCED_AUTO_CRAFTER, true);

        horizontalBlockWithItem(generator, ModBlocks.CRUSHER, true);
        horizontalBlockWithItem(generator, ModBlocks.ADVANCED_CRUSHER, true);

        horizontalBlockWithItem(generator, ModBlocks.PULVERIZER, true);
        horizontalBlockWithItem(generator, ModBlocks.ADVANCED_PULVERIZER, true);

        horizontalBlockWithItem(generator, ModBlocks.SAWMILL, true);

        horizontalBlockWithItem(generator, ModBlocks.COMPRESSOR, true);

        horizontalBlockWithItem(generator, ModBlocks.METAL_PRESS, false);

        horizontalTwoSideBlockWithItem(generator, ModBlocks.AUTO_PRESS_MOLD_MAKER, true);

        horizontalBlockWithItem(generator, ModBlocks.AUTO_STONECUTTER, false);

        orientableSixDirsBlockWithItem(generator, ModBlocks.BLOCK_PLACER, true);

        horizontalBlockWithItem(generator, ModBlocks.FLUID_FILLER, true);

        horizontalTwoSideBlockWithItem(generator, ModBlocks.FILTRATION_PLANT, false);

        horizontalBlockWithItem(generator, ModBlocks.FLUID_DRAINER, true);

        horizontalBlockWithItem(generator, ModBlocks.FLUID_PUMP, false);

        horizontalBlockWithItem(generator, ModBlocks.DRAIN, true);

        horizontalBlockWithItem(generator, ModBlocks.CHARGER, true);
        horizontalBlockWithItem(generator, ModBlocks.ADVANCED_CHARGER, true);

        horizontalBlockWithItem(generator, ModBlocks.UNCHARGER, true);
        horizontalBlockWithItem(generator, ModBlocks.ADVANCED_UNCHARGER, true);

        orientableSixDirsBlockWithItem(generator, ModBlocks.MINECART_CHARGER, true);
        orientableSixDirsBlockWithItem(generator, ModBlocks.ADVANCED_MINECART_CHARGER, true);

        orientableSixDirsBlockWithItem(generator, ModBlocks.MINECART_UNCHARGER, true);
        orientableSixDirsBlockWithItem(generator, ModBlocks.ADVANCED_MINECART_UNCHARGER, true);

        solarPanelBlockWithItem(generator, ModBlocks.SOLAR_PANEL_1);
        solarPanelBlockWithItem(generator, ModBlocks.SOLAR_PANEL_2);
        solarPanelBlockWithItem(generator, ModBlocks.SOLAR_PANEL_3);
        solarPanelBlockWithItem(generator, ModBlocks.SOLAR_PANEL_4);
        solarPanelBlockWithItem(generator, ModBlocks.SOLAR_PANEL_5);
        solarPanelBlockWithItem(generator, ModBlocks.SOLAR_PANEL_6);

        poweredLampBlockWithItem(generator, ModBlocks.POWERED_LAMP);

        horizontalBlockWithItem(generator, ModBlocks.HEAT_GENERATOR, false);

        horizontalBlockWithItem(generator, ModBlocks.CRYSTAL_GROWTH_CHAMBER, false);

        horizontalTwoSideBlockWithItem(generator, ModBlocks.WEATHER_CONTROLLER, false);

        horizontalTwoSideBlockWithItem(generator, ModBlocks.TIME_CONTROLLER, false);

        horizontalBlockWithItem(generator, ModBlocks.BASIC_MACHINE_FRAME, false);
        horizontalBlockWithItem(generator, ModBlocks.HARDENED_MACHINE_FRAME, false);
        horizontalBlockWithItem(generator, ModBlocks.ADVANCED_MACHINE_FRAME, false);
        horizontalBlockWithItem(generator, ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME, false);
    }

    private void cubeAllBlockWithItem(BlockStateModelGenerator generator, Block block) {
        generator.registerSimpleCubeAll(block);
    }

    private void horizontalBlockWithItem(BlockStateModelGenerator generator, Block block, boolean uniqueBottomTexture) {
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

    private void horizontalTwoSideBlockWithItem(BlockStateModelGenerator generator, Block block, boolean uniqueBottomTexture) {
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

    private void orientableSixDirsBlockWithItem(BlockStateModelGenerator generator, Block block, boolean uniqueBottomTexture) {
        Identifier modelNormal = TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.TOP, TextureMap.getSubId(block, "_top")).
                        put(TextureKey.BOTTOM, TextureMap.getSubId(block, uniqueBottomTexture?"_bottom":"_top")).
                        put(TextureKey.FRONT, TextureMap.getSubId(block, "_front")).
                        put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).
                        copy(TextureKey.UP, TextureKey.PARTICLE),
                Models.ORIENTABLE_WITH_BOTTOM).get(block).upload(block, generator.modelCollector);

        Identifier modelVertical = TexturedModel.makeFactory(unused -> new TextureMap().
                        put(TextureKey.TOP, TextureMap.getSubId(block, "_top")).
                        put(TextureKey.BOTTOM, TextureMap.getSubId(block, uniqueBottomTexture?"_bottom":"_top")).
                        put(TextureKey.FRONT, TextureMap.getSubId(block, "_front")).
                        put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).
                        copy(TextureKey.FRONT, TextureKey.PARTICLE),
                ModModels.ORIENTABLE_VERTICAL).get(block).upload(block, "_vertical", generator.modelCollector);

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

    private void fluidPipeBlockWithItem(BlockStateModelGenerator generator, Block block) {
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
                        with(When.create().set(FluidPipeBlock.UP, ModBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected).
                                put(VariantSettings.X, VariantSettings.Rotation.R270)).
                        with(When.create().set(FluidPipeBlock.UP, ModBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract).
                                put(VariantSettings.X, VariantSettings.Rotation.R270)).
                        with(When.create().set(FluidPipeBlock.DOWN, ModBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected).
                                put(VariantSettings.X, VariantSettings.Rotation.R90)).
                        with(When.create().set(FluidPipeBlock.DOWN, ModBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract).
                                put(VariantSettings.X, VariantSettings.Rotation.R90)).
                        with(When.create().set(FluidPipeBlock.NORTH, ModBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected)).
                        with(When.create().set(FluidPipeBlock.NORTH, ModBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract)).
                        with(When.create().set(FluidPipeBlock.SOUTH, ModBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected).
                                put(VariantSettings.X, VariantSettings.Rotation.R180)).
                        with(When.create().set(FluidPipeBlock.SOUTH, ModBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract).
                                put(VariantSettings.X, VariantSettings.Rotation.R180)).
                        with(When.create().set(FluidPipeBlock.EAST, ModBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected).
                                put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                        with(When.create().set(FluidPipeBlock.EAST, ModBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract).
                                put(VariantSettings.Y, VariantSettings.Rotation.R90)).
                        with(When.create().set(FluidPipeBlock.WEST, ModBlockStateProperties.PipeConnection.CONNECTED), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideConnected).
                                put(VariantSettings.Y, VariantSettings.Rotation.R270)).
                        with(When.create().set(FluidPipeBlock.WEST, ModBlockStateProperties.PipeConnection.EXTRACT), BlockStateVariant.create().
                                put(VariantSettings.MODEL, fluidPipeSideExtract).
                                put(VariantSettings.Y, VariantSettings.Rotation.R270))
        );

        generator.modelCollector.accept(ModelIds.getItemModelId(block.asItem()), new ItemWithDisplayModelSupplier(fluidPipeCore,
                new Vec3f(.65f, .65f, .65f),
                new Vec3f(1.f, 1.f, 1.f),
                new Vec3i(30, 45, 0)
        ));
    }

    private void fluidTankBlockWithItem(BlockStateModelGenerator generator, Block block) {
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

    private void transformerBlockWithItem(BlockStateModelGenerator generator, TransformerBlock block) {
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
                                put(TextureKey.TOP, Identifier.of(EnergizedPowerMod.MODID, "block/" + textureName + multipleSuffix)).
                                put(TextureKey.BOTTOM, Identifier.of(EnergizedPowerMod.MODID, "block/" + textureName + multipleSuffix)).
                                put(TextureKey.FRONT, Identifier.of(EnergizedPowerMod.MODID, "block/" + textureName + singleSuffix)).
                                put(TextureKey.SIDE, Identifier.of(EnergizedPowerMod.MODID, "block/" + textureName + multipleSuffix)).
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
                                put(TextureKey.UP, Identifier.of(EnergizedPowerMod.MODID, "block/" + textureName + "_input")).
                                put(TextureKey.DOWN, Identifier.of(EnergizedPowerMod.MODID, "block/" + textureName + "_output")).
                                put(TextureKey.NORTH, Identifier.of(EnergizedPowerMod.MODID, "block/" + textureName + "_input")).
                                put(TextureKey.SOUTH, Identifier.of(EnergizedPowerMod.MODID, "block/" + textureName + "_output")).
                                put(TextureKey.EAST, Identifier.of(EnergizedPowerMod.MODID, "block/" + textureName + "_output")).
                                put(TextureKey.WEST, Identifier.of(EnergizedPowerMod.MODID, "block/" + textureName + "_input")).
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

    private void solarPanelBlockWithItem(BlockStateModelGenerator generator, Block block) {
        Identifier solarPanel = ModTexturedModel.SOLAR_PANEL.get(block).upload(block, generator.modelCollector);

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block,
                BlockStateVariant.create().put(VariantSettings.MODEL, solarPanel)));

        generator.registerParentedItemModel(block.asItem(), solarPanel);
    }

    private void poweredLampBlockWithItem(BlockStateModelGenerator generator, Block block) {
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
        basicItem(generator, ModItems.CREATIVE_BATTERY);

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
