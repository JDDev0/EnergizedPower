package me.jddev0.ep.datagen.loot;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ModBlockLootTables extends SimpleFabricLootTableProvider {
    private BiConsumer<Identifier, LootTable.Builder> lootTableBuilder;

    public ModBlockLootTables(FabricDataGenerator dataOutput) {
        super(dataOutput, LootContextTypes.BLOCK);
    }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> lootTableBuilder) {
        this.lootTableBuilder = lootTableBuilder;

        generate();

        this.lootTableBuilder = null;
    }

    private void generate() {
        dropSelf(ModBlocks.SILICON_BLOCK);

        dropSelf(ModBlocks.TIN_BLOCK);
        dropSelf(ModBlocks.RAW_TIN_BLOCK);

        dropSelf(ModBlocks.SAWDUST_BLOCK);

        add(ModBlocks.TIN_ORE, this::createTinOreDrops);
        add(ModBlocks.DEEPSLATE_TIN_ORE, this::createTinOreDrops);

        dropSelf(ModBlocks.ITEM_CONVEYOR_BELT);

        dropSelf(ModBlocks.ITEM_CONVEYOR_BELT_LOADER);

        dropSelf(ModBlocks.ITEM_CONVEYOR_BELT_SORTER);

        dropSelf(ModBlocks.ITEM_CONVEYOR_BELT_SWITCH);

        dropSelf(ModBlocks.ITEM_CONVEYOR_BELT_SPLITTER);

        dropSelf(ModBlocks.ITEM_CONVEYOR_BELT_MERGER);

        dropSelf(ModBlocks.IRON_FLUID_PIPE);
        dropSelf(ModBlocks.GOLDEN_FLUID_PIPE);

        dropSelf(ModBlocks.FLUID_TANK_SMALL);
        dropSelf(ModBlocks.FLUID_TANK_MEDIUM);
        dropSelf(ModBlocks.FLUID_TANK_LARGE);

        dropSelf(ModBlocks.TIN_CABLE);
        dropSelf(ModBlocks.COPPER_CABLE);
        dropSelf(ModBlocks.GOLD_CABLE);
        dropSelf(ModBlocks.ENERGIZED_COPPER_CABLE);
        dropSelf(ModBlocks.ENERGIZED_GOLD_CABLE);
        dropSelf(ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE);

        dropSelf(ModBlocks.LV_TRANSFORMER_1_TO_N);
        dropSelf(ModBlocks.LV_TRANSFORMER_3_TO_3);
        dropSelf(ModBlocks.LV_TRANSFORMER_N_TO_1);
        dropSelf(ModBlocks.MV_TRANSFORMER_1_TO_N);
        dropSelf(ModBlocks.MV_TRANSFORMER_3_TO_3);
        dropSelf(ModBlocks.MV_TRANSFORMER_N_TO_1);
        dropSelf(ModBlocks.HV_TRANSFORMER_1_TO_N);
        dropSelf(ModBlocks.HV_TRANSFORMER_3_TO_3);
        dropSelf(ModBlocks.HV_TRANSFORMER_N_TO_1);
        dropSelf(ModBlocks.EHV_TRANSFORMER_1_TO_N);
        dropSelf(ModBlocks.EHV_TRANSFORMER_3_TO_3);
        dropSelf(ModBlocks.EHV_TRANSFORMER_N_TO_1);

        dropSelf(ModBlocks.BATTERY_BOX);
        dropSelf(ModBlocks.ADVANCED_BATTERY_BOX);

        dropSelf(ModBlocks.PRESS_MOLD_MAKER);

        dropSelf(ModBlocks.ALLOY_FURNACE);

        dropSelf(ModBlocks.AUTO_CRAFTER);
        dropSelf(ModBlocks.ADVANCED_AUTO_CRAFTER);

        dropSelf(ModBlocks.CRUSHER);
        dropSelf(ModBlocks.ADVANCED_CRUSHER);

        dropSelf(ModBlocks.PULVERIZER);
        dropSelf(ModBlocks.ADVANCED_PULVERIZER);

        dropSelf(ModBlocks.SAWMILL);

        dropSelf(ModBlocks.COMPRESSOR);

        dropSelf(ModBlocks.METAL_PRESS);

        dropSelf(ModBlocks.AUTO_PRESS_MOLD_MAKER);

        dropSelf(ModBlocks.AUTO_STONECUTTER);

        dropSelf(ModBlocks.PLANT_GROWTH_CHAMBER);

        dropSelf(ModBlocks.BLOCK_PLACER);

        dropSelf(ModBlocks.ASSEMBLING_MACHINE);

        dropSelf(ModBlocks.INDUCTION_SMELTER);

        dropSelf(ModBlocks.FLUID_FILLER);

        dropSelf(ModBlocks.STONE_SOLIDIFIER);

        dropSelf(ModBlocks.FLUID_TRANSPOSER);

        dropSelf(ModBlocks.FILTRATION_PLANT);

        dropSelf(ModBlocks.FLUID_DRAINER);

        dropSelf(ModBlocks.FLUID_PUMP);

        dropSelf(ModBlocks.DRAIN);

        dropSelf(ModBlocks.CHARGER);
        dropSelf(ModBlocks.ADVANCED_CHARGER);

        dropSelf(ModBlocks.UNCHARGER);
        dropSelf(ModBlocks.ADVANCED_UNCHARGER);

        dropSelf(ModBlocks.MINECART_CHARGER);
        dropSelf(ModBlocks.ADVANCED_MINECART_CHARGER);

        dropSelf(ModBlocks.MINECART_UNCHARGER);
        dropSelf(ModBlocks.ADVANCED_MINECART_UNCHARGER);

        dropSelf(ModBlocks.SOLAR_PANEL_1);
        dropSelf(ModBlocks.SOLAR_PANEL_2);
        dropSelf(ModBlocks.SOLAR_PANEL_3);
        dropSelf(ModBlocks.SOLAR_PANEL_4);
        dropSelf(ModBlocks.SOLAR_PANEL_5);
        dropSelf(ModBlocks.SOLAR_PANEL_6);

        dropSelf(ModBlocks.COAL_ENGINE);

        dropSelf(ModBlocks.POWERED_LAMP);

        dropSelf(ModBlocks.POWERED_FURNACE);
        dropSelf(ModBlocks.ADVANCED_POWERED_FURNACE);

        dropSelf(ModBlocks.LIGHTNING_GENERATOR);

        dropSelf(ModBlocks.ENERGIZER);

        dropSelf(ModBlocks.CHARGING_STATION);

        dropSelf(ModBlocks.HEAT_GENERATOR);

        dropSelf(ModBlocks.THERMAL_GENERATOR);

        dropSelf(ModBlocks.CRYSTAL_GROWTH_CHAMBER);

        dropSelf(ModBlocks.WEATHER_CONTROLLER);

        dropSelf(ModBlocks.TIME_CONTROLLER);

        dropSelf(ModBlocks.TELEPORTER);

        dropSelf(ModBlocks.BASIC_MACHINE_FRAME);
        dropSelf(ModBlocks.HARDENED_MACHINE_FRAME);
        dropSelf(ModBlocks.ADVANCED_MACHINE_FRAME);
        dropSelf(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME);
    }

    private void dropSelf(Block block) {
        addDrop(block);
    }

    private void add(Block block, Function<Block, LootTable.Builder> builderFunction) {
        addDrop(block, builderFunction);
    }

    private LootTable.Builder createTinOreDrops(Block block) {
        return BlockLootTableGenerator.dropsWithSilkTouch(block,
                BlockLootTableGenerator.applyExplosionDecay(
                        block,
                        ItemEntry.builder(ModItems.RAW_TIN)
                                .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
                )
        );
    }

    private void addDrop(Block block) {
        addDrop(block, BlockLootTableGenerator::drops);
    }

    private void addDrop(Block block, Function<Block, LootTable.Builder> builderFunction) {
        Identifier blockId = Registry.BLOCK.getId(block);

        lootTableBuilder.accept(
                new Identifier(blockId.getNamespace(), "blocks/" + blockId.getPath()),
                builderFunction.apply(block)
        );
    }
}
