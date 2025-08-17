package me.jddev0.ep.datagen.loot;

import me.jddev0.ep.block.*;
import me.jddev0.ep.item.EPItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.function.Function;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return EPBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }

    @Override
    protected void generate() {
        dropSelf(EPBlocks.SILICON_BLOCK);

        dropSelf(EPBlocks.TIN_BLOCK);
        dropSelf(EPBlocks.RAW_TIN_BLOCK);

        dropSelf(EPBlocks.SAWDUST_BLOCK);

        add(EPBlocks.TIN_ORE, this::createTinOreDrops);
        add(EPBlocks.DEEPSLATE_TIN_ORE, this::createTinOreDrops);

        dropSelf(EPBlocks.RAW_TIN_BLOCK);

        dropSelf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT);
        dropSelf(EPBlocks.FAST_ITEM_CONVEYOR_BELT);
        dropSelf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT);

        dropSelf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER);
        dropSelf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER);
        dropSelf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER);

        dropSelf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER);
        dropSelf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER);
        dropSelf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER);

        dropSelf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH);
        dropSelf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH);
        dropSelf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH);

        dropSelf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER);
        dropSelf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER);
        dropSelf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER);

        dropSelf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER);
        dropSelf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER);
        dropSelf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER);

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

        dropSelf(EPBlocks.STONE_LIQUEFIER);
        dropSelf(EPBlocks.STONE_SOLIDIFIER);

        dropSelf(EPBlocks.FLUID_TRANSPOSER);

        dropSelf(EPBlocks.FILTRATION_PLANT);

        dropSelf(EPBlocks.FLUID_DRAINER);

        dropSelf(EPBlocks.FLUID_PUMP);
        dropSelf(EPBlocks.ADVANCED_FLUID_PUMP);

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

    private void dropSelf(DeferredHolder<Block, ? extends Block> block) {
        dropSelf(block.get());
    }

    private void add(DeferredHolder<Block, ? extends Block> block, Function<Block, LootTable.Builder> builderFunction) {
        add(block.get(), builderFunction);
    }

    private LootTable.Builder createTinOreDrops(Block block) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = registries.lookupOrThrow(Registries.ENCHANTMENT);

        return createSilkTouchDispatchTable(block,
                applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(EPItems.RAW_TIN)
                                .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }
}
