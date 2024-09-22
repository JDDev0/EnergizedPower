package me.jddev0.ep.datagen.loot;

import me.jddev0.ep.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.BiConsumer;

public class ModBlockLootTables extends SimpleFabricLootTableProvider {
    private BiConsumer<Identifier, LootTable.Builder> lootTableBuilder;

    public ModBlockLootTables(FabricDataGenerator dataOutput) {
        super(dataOutput, LootContextTypes.BLOCK);
    }

    private void generate() {
        addDrop(ModBlocks.SILICON_BLOCK);

        addDrop(ModBlocks.TIN_BLOCK);
        addDrop(ModBlocks.RAW_TIN_BLOCK);

        addDrop(ModBlocks.SAWDUST_BLOCK);
    }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> lootTableBuilder) {
        this.lootTableBuilder = lootTableBuilder;

        generate();

        this.lootTableBuilder = null;
    }

    private void addDrop(Block block) {
        Identifier blockId = Registry.BLOCK.getId(block);

        lootTableBuilder.accept(
                new Identifier(blockId.getNamespace(), "blocks/" + blockId.getPath()),
                BlockLootTableGenerator.drops(block)
        );
    }
}
