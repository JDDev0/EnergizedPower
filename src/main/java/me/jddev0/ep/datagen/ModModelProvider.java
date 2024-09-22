package me.jddev0.ep.datagen;

import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.datagen.model.DisplayModelSupplier;
import me.jddev0.ep.datagen.model.ModTexturedModel;
import me.jddev0.ep.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;

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

        generator.modelCollector.accept(ModelIds.getItemModelId(block.asItem()), new DisplayModelSupplier(cableCore,
                new Vec3f(1.01f, 1.01f, 1.01f),
                new Vec3f(1.5f, 1.5f, 1.5f),
                new Vec3i(30, 45, 0)
        ));
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        basicItem(generator, ModItems.SILICON);
    }

    private void basicItem(ItemModelGenerator generator, Item item) {
        generator.register(item, Models.GENERATED);
    }
}
