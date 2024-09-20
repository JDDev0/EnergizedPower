package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.*;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    private ModelFile solarPanelTemplate;

    private ModelFile fluidPipeCoreTemplate;
    private ModelFile fluidPipeSideConnectedTemplate;
    private ModelFile fluidPipeSideExtractTemplate;

    private ModelFile fluidTankTemplate;

    private ModelFile cableCoreTemplate;
    private ModelFile cableSideTemplate;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, EnergizedPowerMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerTemplates();
        registerBlocks();
    }

    private void registerTemplates() {
        solarPanelTemplate = models().
                withExistingParent("solar_panel_template", ModelProvider.BLOCK_FOLDER + "/thin_block").
                element().from(0, 0, 0).to(16, 4, 16).
                face(Direction.DOWN).uvs(0, 0, 16, 16).cullface(Direction.DOWN).texture("#side").end().
                face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").end().
                face(Direction.NORTH).uvs(0, 12, 16, 16).cullface(Direction.NORTH).texture("#side").end().
                face(Direction.SOUTH).uvs(0, 12, 16, 16).cullface(Direction.SOUTH).texture("#side").end().
                face(Direction.WEST).uvs(0, 12, 16, 16).cullface(Direction.WEST).texture("#side").end().
                face(Direction.EAST).uvs(0, 12, 16, 16).cullface(Direction.EAST).texture("#side").end().
                end();

        fluidPipeCoreTemplate = models().getExistingFile(
                new ResourceLocation(EnergizedPowerMod.MODID, "fluid_pipe_core_template"));
        fluidPipeSideConnectedTemplate = models().getExistingFile(
                new ResourceLocation(EnergizedPowerMod.MODID, "fluid_pipe_side_connected_template"));
        fluidPipeSideExtractTemplate = models().getExistingFile(
                new ResourceLocation(EnergizedPowerMod.MODID, "fluid_pipe_side_extract_template"));

        fluidTankTemplate = models().getExistingFile(
                new ResourceLocation(EnergizedPowerMod.MODID, "fluid_tank_template"));

        cableCoreTemplate = models().
                withExistingParent("cable_core_template", ModelProvider.BLOCK_FOLDER + "/thin_block").
                element().from(6, 6, 6).to(10, 10, 10).
                face(Direction.DOWN).uvs(0, 7, 4, 11).cullface(Direction.DOWN).texture("#cable").end().
                face(Direction.UP).uvs(0, 7, 4, 11).cullface(Direction.UP).texture("#cable").end().
                face(Direction.NORTH).uvs(0, 7, 4, 11).cullface(Direction.NORTH).texture("#cable").end().
                face(Direction.SOUTH).uvs(0, 7, 4, 11).cullface(Direction.SOUTH).texture("#cable").end().
                face(Direction.WEST).uvs(0, 7, 4, 11).cullface(Direction.WEST).texture("#cable").end().
                face(Direction.EAST).uvs(0, 7, 4, 11).cullface(Direction.EAST).texture("#cable").end().
                end();

        cableSideTemplate = models().
                withExistingParent("cable_side_template", ModelProvider.BLOCK_FOLDER + "/thin_block").
                element().from(6, 6, 0).to(10, 10, 6).
                face(Direction.DOWN).uvs(0, 0, 4, 6).texture("#cable").end().
                face(Direction.UP).uvs(0, 0, 4, 6).texture("#cable").end().
                face(Direction.NORTH).uvs(0, 12, 4, 16).texture("#cable").end().
                face(Direction.SOUTH).uvs(0, 12, 4, 16).texture("#cable").end().
                face(Direction.WEST).uvs(5, 7, 11, 11).texture("#cable").end().
                face(Direction.EAST).uvs(5, 7, 11, 11).texture("#cable").end().
                end();
    }

    private void registerBlocks() {
        solarPanelBlockWithItem(ModBlocks.SOLAR_PANEL_1);
        solarPanelBlockWithItem(ModBlocks.SOLAR_PANEL_2);
        solarPanelBlockWithItem(ModBlocks.SOLAR_PANEL_3);
        solarPanelBlockWithItem(ModBlocks.SOLAR_PANEL_4);
        solarPanelBlockWithItem(ModBlocks.SOLAR_PANEL_5);
        solarPanelBlockWithItem(ModBlocks.SOLAR_PANEL_6);

        cubeAllBlockWithItem(ModBlocks.SILICON_BLOCK);

        cubeAllBlockWithItem(ModBlocks.TIN_BLOCK);

        cubeAllBlockWithItem(ModBlocks.SAWDUST_BLOCK);

        cubeAllBlockWithItem(ModBlocks.TIN_ORE);
        cubeAllBlockWithItem(ModBlocks.DEEPSLATE_TIN_ORE);

        cubeAllBlockWithItem(ModBlocks.RAW_TIN_BLOCK);

        fluidPipeBlockWithItem(ModBlocks.IRON_FLUID_PIPE);
        fluidPipeBlockWithItem(ModBlocks.GOLDEN_FLUID_PIPE);

        fluidTankBlockWithItem(ModBlocks.FLUID_TANK_SMALL);
        fluidTankBlockWithItem(ModBlocks.FLUID_TANK_MEDIUM);
        fluidTankBlockWithItem(ModBlocks.FLUID_TANK_LARGE);
        fluidTankBlockWithItem(ModBlocks.CREATIVE_FLUID_TANK);

        cableBlockWithItem(ModBlocks.TIN_CABLE);
        cableBlockWithItem(ModBlocks.COPPER_CABLE);
        cableBlockWithItem(ModBlocks.GOLD_CABLE);
        cableBlockWithItem(ModBlocks.ENERGIZED_COPPER_CABLE);
        cableBlockWithItem(ModBlocks.ENERGIZED_GOLD_CABLE);
        cableBlockWithItem(ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE);
    }

    private void cubeAllBlockWithItem(RegistryObject<? extends Block> block) {
        simpleBlockWithItem(block.get(), cubeAll(block.get()));
    }

    private void solarPanelBlockWithItem(RegistryObject<? extends Block> block) {
        ResourceLocation blockId = block.getId();

        ModelFile solarPanel = models().
                getBuilder(blockId.getPath()).parent(solarPanelTemplate).
                texture("particle", "#top").
                texture("top", getBlockTexture(block, "_top")).
                texture("side", getBlockTexture(block, "_side"));

        getVariantBuilder(block.get()).partialState().
                modelForState().modelFile(solarPanel).addModel();

        simpleBlockItem(block.get(), solarPanel);
    }

    private void fluidPipeBlockWithItem(RegistryObject<? extends Block> block) {
        ResourceLocation blockId = block.getId();

        ModelFile fluidPipeCore = models().
                getBuilder(blockId.getPath() + "_core").parent(fluidPipeCoreTemplate).
                texture("particle", getBlockTexture(block, "_core")).
                texture("fluid_pipe_core", getBlockTexture(block, "_core"));
        ModelFile fluidPipeSideConnected = models().
                getBuilder(blockId.getPath() + "_side_connected").parent(fluidPipeSideConnectedTemplate).
                texture("particle", getBlockTexture(block, "_side_connected")).
                texture("fluid_pipe_side", getBlockTexture(block, "_side_connected"));
        ModelFile fluidPipeSideExtract = models().
                getBuilder(blockId.getPath() + "_side_extract").parent(fluidPipeSideExtractTemplate).
                texture("particle", getBlockTexture(block, "_side_outer_extract")).
                texture("fluid_pipe_side_inner", getBlockTexture(block, "_side_inner_extract")).
                texture("fluid_pipe_side_outer", getBlockTexture(block, "_side_outer_extract"));

        getMultipartBuilder(block.get()).part().
                modelFile(fluidPipeCore).addModel().end().part().
                modelFile(fluidPipeSideConnected).rotationX(270).addModel().condition(FluidPipeBlock.UP,
                        ModBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).rotationX(270).addModel().condition(FluidPipeBlock.UP,
                        ModBlockStateProperties.PipeConnection.EXTRACT).end().part().
                modelFile(fluidPipeSideConnected).rotationX(90).addModel().condition(FluidPipeBlock.DOWN,
                        ModBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).rotationX(90).addModel().condition(FluidPipeBlock.DOWN,
                        ModBlockStateProperties.PipeConnection.EXTRACT).end().part().
                modelFile(fluidPipeSideConnected).addModel().condition(FluidPipeBlock.NORTH,
                        ModBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).addModel().condition(FluidPipeBlock.NORTH,
                        ModBlockStateProperties.PipeConnection.EXTRACT).end().part().
                modelFile(fluidPipeSideConnected).rotationX(180).addModel().condition(FluidPipeBlock.SOUTH,
                        ModBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).rotationX(180).addModel().condition(FluidPipeBlock.SOUTH,
                        ModBlockStateProperties.PipeConnection.EXTRACT).end().part().
                modelFile(fluidPipeSideConnected).rotationY(90).addModel().condition(FluidPipeBlock.EAST,
                        ModBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).rotationY(90).addModel().condition(FluidPipeBlock.EAST,
                        ModBlockStateProperties.PipeConnection.EXTRACT).end().part().
                modelFile(fluidPipeSideConnected).rotationY(270).addModel().condition(FluidPipeBlock.WEST,
                        ModBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).rotationY(270).addModel().condition(FluidPipeBlock.WEST,
                        ModBlockStateProperties.PipeConnection.EXTRACT).end();

        itemModels().
                getBuilder(blockId.getPath()).parent(fluidPipeCore).
                transforms().
                transform(ItemDisplayContext.GUI).rotation(30, 45, 0).end().
                transform(ItemDisplayContext.GROUND).scale(.65f, .65f, .65f).end().
                transform(ItemDisplayContext.FIXED).scale(.65f, .65f, .65f).end().
                transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).scale(.65f, .65f, .65f).end().
                transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).scale(.65f, .65f, .65f).end().
                transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale(.65f, .65f, .65f).end().
                transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).scale(.65f, .65f, .65f).end().end();
    }

    private void fluidTankBlockWithItem(RegistryObject<? extends Block> block) {
        ResourceLocation blockId = block.getId();

        ModelFile fluidTank = models().
                getBuilder(blockId.getPath()).parent(fluidTankTemplate).
                texture("particle", "#up").
                texture("front", getBlockTexture(block, "_front")).
                texture("side", getBlockTexture(block, "_side")).
                texture("up", getBlockTexture(block, "_top")).
                texture("interior", getBlockTexture(block, "_interior"));

        getVariantBuilder(block.get()).partialState().
                with(FluidTankBlock.FACING, Direction.NORTH).modelForState().modelFile(fluidTank).addModel().partialState().
                with(FluidTankBlock.FACING, Direction.SOUTH).modelForState().rotationY(180).modelFile(fluidTank).addModel().partialState().
                with(FluidTankBlock.FACING, Direction.EAST).modelForState().rotationY(90).modelFile(fluidTank).addModel().partialState().
                with(FluidTankBlock.FACING, Direction.WEST).modelForState().rotationY(270).modelFile(fluidTank).addModel().partialState();

        simpleBlockItem(block.get(), fluidTank);
    }

    private void cableBlockWithItem(RegistryObject<? extends Block> block) {
        ResourceLocation blockId = block.getId();

        ModelFile cableCore = models().
                getBuilder(blockId.getPath() + "_core").parent(cableCoreTemplate).
                texture("particle", getBlockTexture(block)).
                texture("cable", getBlockTexture(block));
        ModelFile cableSide = models().
                getBuilder(blockId.getPath() + "_side").parent(cableSideTemplate).
                texture("particle", getBlockTexture(block)).
                texture("cable", getBlockTexture(block));

        getMultipartBuilder(block.get()).part().
                modelFile(cableCore).addModel().end().part().
                modelFile(cableSide).rotationX(270).addModel().condition(CableBlock.UP, true).end().part().
                modelFile(cableSide).rotationX(90).addModel().condition(CableBlock.DOWN, true).end().part().
                modelFile(cableSide).addModel().condition(CableBlock.NORTH, true).end().part().
                modelFile(cableSide).rotationX(180).addModel().condition(CableBlock.SOUTH, true).end().part().
                modelFile(cableSide).rotationY(90).addModel().condition(CableBlock.EAST, true).end().part().
                modelFile(cableSide).rotationY(270).addModel().condition(CableBlock.WEST, true).end();

        itemModels().
                getBuilder(blockId.getPath()).parent(cableCore).
                transforms().
                transform(ItemDisplayContext.GUI).rotation(30, 45, 0).scale(1.5f, 1.5f, 1.5f).end().
                transform(ItemDisplayContext.GROUND).scale(1.01f, 1.01f, 1.01f).end().
                transform(ItemDisplayContext.FIXED).scale(1.01f, 1.01f, 1.01f).end().
                transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).scale(1.01f, 1.01f, 1.01f).end().
                transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).scale(1.01f, 1.01f, 1.01f).end().
                transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale(1.01f, 1.01f, 1.01f).end().
                transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).scale(1.01f, 1.01f, 1.01f).end().end();
    }

    private ResourceLocation getBlockTexture(RegistryObject<? extends Block> block) {
        ResourceLocation blockId = block.getId();

        return new ResourceLocation(blockId.getNamespace(),
                ModelProvider.BLOCK_FOLDER + "/" + blockId.getPath());
    }

    private ResourceLocation getBlockTexture(RegistryObject<? extends Block> block, String pathSuffix) {
        ResourceLocation blockId = block.getId();

        return new ResourceLocation(blockId.getNamespace(),
                ModelProvider.BLOCK_FOLDER + "/" + blockId.getPath() + pathSuffix);
    }
}
