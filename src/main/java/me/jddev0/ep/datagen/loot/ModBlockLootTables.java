package me.jddev0.ep.datagen.loot;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ModBlockLootTables extends FabricBlockLootTableProvider {
    public ModBlockLootTables(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(dataOutput, lookupProvider);
    }

    @Override
    public void generate() {
        dropSelf(EPBlocks.SILICON_BLOCK);

        dropSelf(EPBlocks.TIN_BLOCK);
        dropSelf(EPBlocks.RAW_TIN_BLOCK);

        dropSelf(EPBlocks.SAWDUST_BLOCK);

        add(EPBlocks.TIN_ORE, this::createTinOreDrops);
        add(EPBlocks.DEEPSLATE_TIN_ORE, this::createTinOreDrops);

        dropSelf(EPBlocks.RAW_TIN_BLOCK);

        dropSelf(EPBlocks.ITEM_CONVEYOR_BELT);

        dropSelf(EPBlocks.ITEM_CONVEYOR_BELT_LOADER);

        dropSelf(EPBlocks.ITEM_CONVEYOR_BELT_SORTER);

        dropSelf(EPBlocks.ITEM_CONVEYOR_BELT_SWITCH);

        dropSelf(EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER);

        dropSelf(EPBlocks.ITEM_CONVEYOR_BELT_MERGER);

        dropSelf(EPBlocks.IRON_FLUID_PIPE);
        dropSelf(EPBlocks.GOLDEN_FLUID_PIPE);

        dropSelf(EPBlocks.FLUID_TANK_SMALL);
        dropSelf(EPBlocks.FLUID_TANK_MEDIUM);
        dropSelf(EPBlocks.FLUID_TANK_LARGE);

        dropSelf(EPBlocks.TIN_CABLE);
        dropSelf(EPBlocks.COPPER_CABLE);
        dropSelf(EPBlocks.GOLD_CABLE);
        dropSelf(EPBlocks.ENERGIZED_COPPER_CABLE);
        dropSelf(EPBlocks.ENERGIZED_GOLD_CABLE);
        dropSelf(EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE);

        dropSelf(EPBlocks.LV_TRANSFORMER_1_TO_N);
        dropSelf(EPBlocks.LV_TRANSFORMER_3_TO_3);
        dropSelf(EPBlocks.LV_TRANSFORMER_N_TO_1);
        dropSelf(EPBlocks.MV_TRANSFORMER_1_TO_N);
        dropSelf(EPBlocks.MV_TRANSFORMER_3_TO_3);
        dropSelf(EPBlocks.MV_TRANSFORMER_N_TO_1);
        dropSelf(EPBlocks.HV_TRANSFORMER_1_TO_N);
        dropSelf(EPBlocks.HV_TRANSFORMER_3_TO_3);
        dropSelf(EPBlocks.HV_TRANSFORMER_N_TO_1);
        dropSelf(EPBlocks.EHV_TRANSFORMER_1_TO_N);
        dropSelf(EPBlocks.EHV_TRANSFORMER_3_TO_3);
        dropSelf(EPBlocks.EHV_TRANSFORMER_N_TO_1);

        dropSelf(EPBlocks.BATTERY_BOX);
        dropSelf(EPBlocks.ADVANCED_BATTERY_BOX);

        dropSelf(EPBlocks.PRESS_MOLD_MAKER);

        dropSelf(EPBlocks.ALLOY_FURNACE);

        dropSelf(EPBlocks.AUTO_CRAFTER);
        dropSelf(EPBlocks.ADVANCED_AUTO_CRAFTER);

        dropSelf(EPBlocks.CRUSHER);
        dropSelf(EPBlocks.ADVANCED_CRUSHER);

        dropSelf(EPBlocks.PULVERIZER);
        dropSelf(EPBlocks.ADVANCED_PULVERIZER);

        dropSelf(EPBlocks.SAWMILL);

        dropSelf(EPBlocks.COMPRESSOR);

        dropSelf(EPBlocks.METAL_PRESS);

        dropSelf(EPBlocks.AUTO_PRESS_MOLD_MAKER);

        dropSelf(EPBlocks.AUTO_STONECUTTER);

        dropSelf(EPBlocks.PLANT_GROWTH_CHAMBER);

        dropSelf(EPBlocks.BLOCK_PLACER);

        dropSelf(EPBlocks.ASSEMBLING_MACHINE);

        dropSelf(EPBlocks.INDUCTION_SMELTER);

        dropSelf(EPBlocks.FLUID_FILLER);

        dropSelf(EPBlocks.STONE_SOLIDIFIER);

        dropSelf(EPBlocks.FLUID_TRANSPOSER);

        dropSelf(EPBlocks.FILTRATION_PLANT);

        dropSelf(EPBlocks.FLUID_DRAINER);

        dropSelf(EPBlocks.FLUID_PUMP);

        dropSelf(EPBlocks.DRAIN);

        dropSelf(EPBlocks.CHARGER);
        dropSelf(EPBlocks.ADVANCED_CHARGER);

        dropSelf(EPBlocks.UNCHARGER);
        dropSelf(EPBlocks.ADVANCED_UNCHARGER);

        dropSelf(EPBlocks.MINECART_CHARGER);
        dropSelf(EPBlocks.ADVANCED_MINECART_CHARGER);

        dropSelf(EPBlocks.MINECART_UNCHARGER);
        dropSelf(EPBlocks.ADVANCED_MINECART_UNCHARGER);

        dropSelf(EPBlocks.SOLAR_PANEL_1);
        dropSelf(EPBlocks.SOLAR_PANEL_2);
        dropSelf(EPBlocks.SOLAR_PANEL_3);
        dropSelf(EPBlocks.SOLAR_PANEL_4);
        dropSelf(EPBlocks.SOLAR_PANEL_5);
        dropSelf(EPBlocks.SOLAR_PANEL_6);

        dropSelf(EPBlocks.COAL_ENGINE);

        dropSelf(EPBlocks.POWERED_LAMP);

        dropSelf(EPBlocks.POWERED_FURNACE);
        dropSelf(EPBlocks.ADVANCED_POWERED_FURNACE);

        dropSelf(EPBlocks.LIGHTNING_GENERATOR);

        dropSelf(EPBlocks.ENERGIZER);

        dropSelf(EPBlocks.CHARGING_STATION);

        dropSelf(EPBlocks.HEAT_GENERATOR);

        dropSelf(EPBlocks.THERMAL_GENERATOR);

        dropSelf(EPBlocks.CRYSTAL_GROWTH_CHAMBER);

        dropSelf(EPBlocks.WEATHER_CONTROLLER);

        dropSelf(EPBlocks.TIME_CONTROLLER);

        dropSelf(EPBlocks.TELEPORTER);

        dropSelf(EPBlocks.BASIC_MACHINE_FRAME);
        dropSelf(EPBlocks.HARDENED_MACHINE_FRAME);
        dropSelf(EPBlocks.ADVANCED_MACHINE_FRAME);
        dropSelf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME);
    }

    private void dropSelf(Block block) {
        addDrop(block);
    }

    private void add(Block block, Function<Block, LootTable.Builder> builderFunction) {
        addDrop(block, builderFunction);
    }

    private LootTable.Builder createTinOreDrops(Block block) {
        RegistryWrapper.Impl<Enchantment> impl = registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

        return dropsWithSilkTouch(block,
                applyExplosionDecay(
                        block,
                        ItemEntry.builder(EPItems.RAW_TIN)
                                .apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }
}
