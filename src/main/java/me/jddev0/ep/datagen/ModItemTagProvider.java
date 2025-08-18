package me.jddev0.ep.datagen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.registry.tags.CompatibilityItemTags;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookupProvider) {
        getOrCreateTagBuilder(ItemTags.BOOKSHELF_BOOKS).
                add(EPItems.ENERGIZED_POWER_BOOK);

        getOrCreateTagBuilder(ItemTags.LECTERN_BOOKS).
                add(EPItems.ENERGIZED_POWER_BOOK);

        getOrCreateTagBuilder(ItemTags.PIGLIN_LOVED).
                add(EPItems.GOLD_DUST,
                        EPItems.GOLD_PLATE,
                        EPItems.GOLDEN_HAMMER);

        getOrCreateTagBuilder(EnergizedPowerItemTags.RAW_METAL_PRESS_MOLDS).
                add(EPItems.RAW_GEAR_PRESS_MOLD,
                        EPItems.RAW_ROD_PRESS_MOLD,
                        EPItems.RAW_WIRE_PRESS_MOLD);

        getOrCreateTagBuilder(EnergizedPowerItemTags.METAL_PRESS_MOLDS).
                add(EPItems.GEAR_PRESS_MOLD,
                        EPItems.ROD_PRESS_MOLD,
                        EPItems.WIRE_PRESS_MOLD);

        getOrCreateTagBuilder(CompatibilityItemTags.AE2_ITEM_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM,
                        EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM,
                        EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM,
                        EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM,
                        EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM,
                        EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM,

                        EPBlocks.ITEM_SILO_TINY_ITEM,
                        EPBlocks.ITEM_SILO_SMALL_ITEM,
                        EPBlocks.ITEM_SILO_MEDIUM_ITEM,
                        EPBlocks.ITEM_SILO_LARGE_ITEM,
                        EPBlocks.ITEM_SILO_GIANT_ITEM,
                        EPBlocks.CREATIVE_ITEM_SILO_ITEM
                );

        getOrCreateTagBuilder(CompatibilityItemTags.AE2_FLUID_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        EPBlocks.IRON_FLUID_PIPE_ITEM,
                        EPBlocks.GOLDEN_FLUID_PIPE_ITEM,

                        EPBlocks.FLUID_TANK_SMALL_ITEM,
                        EPBlocks.FLUID_TANK_MEDIUM_ITEM,
                        EPBlocks.FLUID_TANK_LARGE_ITEM,
                        EPBlocks.CREATIVE_FLUID_TANK_ITEM
                );

        getOrCreateTagBuilder(CompatibilityItemTags.AE2_FE_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        EPBlocks.TIN_CABLE_ITEM,
                        EPBlocks.COPPER_CABLE_ITEM,
                        EPBlocks.GOLD_CABLE_ITEM,
                        EPBlocks.ENERGIZED_COPPER_CABLE_ITEM,
                        EPBlocks.ENERGIZED_GOLD_CABLE_ITEM,
                        EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM,

                        EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM,
                        EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM,
                        EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM,
                        EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM,
                        EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM,
                        EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM,
                        EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM,
                        EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM,
                        EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM,
                        EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM,
                        EPBlocks.EHV_TRANSFORMER_3_TO_3_ITEM,
                        EPBlocks.EHV_TRANSFORMER_N_TO_1_ITEM,

                        EPBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM,
                        EPBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM,
                        EPBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM,
                        EPBlocks.CONFIGURABLE_EHV_TRANSFORMER_ITEM,

                        EPBlocks.BATTERY_BOX_ITEM,
                        EPBlocks.ADVANCED_BATTERY_BOX_ITEM,
                        EPBlocks.CREATIVE_BATTERY_BOX_ITEM
                );

        getOrCreateTagBuilder(CommonItemTags.COBBLESTONES_NORMAL).
                add(Items.COBBLESTONE);

        getOrCreateTagBuilder(CommonItemTags.GRAVELS).
                add(Items.GRAVEL);

        getOrCreateTagBuilder(ConventionalItemTags.ORES).
                addTag(CommonItemTags.ORES_COAL).
                addTag(CommonItemTags.ORES_TIN).
                addTag(CommonItemTags.ORES_COPPER).
                addTag(CommonItemTags.ORES_IRON).
                addTag(CommonItemTags.ORES_GOLD).
                addTag(CommonItemTags.ORES_REDSTONE).
                addTag(CommonItemTags.ORES_LAPIS).
                addTag(CommonItemTags.ORES_EMERALD).
                addTag(CommonItemTags.ORES_DIAMOND);
        getOrCreateTagBuilder(CommonItemTags.ORES_COAL).
                addOptionalTag(ItemTags.COAL_ORES);
        getOrCreateTagBuilder(CommonItemTags.ORES_TIN).
                add(EPBlocks.TIN_ORE_ITEM,
                        EPBlocks.DEEPSLATE_TIN_ORE_ITEM);
        getOrCreateTagBuilder(CommonItemTags.ORES_COPPER).
                addOptionalTag(ItemTags.COPPER_ORES);
        getOrCreateTagBuilder(CommonItemTags.ORES_IRON).
                addOptionalTag(ItemTags.IRON_ORES);
        getOrCreateTagBuilder(CommonItemTags.ORES_GOLD).
                addOptionalTag(ItemTags.GOLD_ORES);
        getOrCreateTagBuilder(CommonItemTags.ORES_REDSTONE).
                addOptionalTag(ItemTags.REDSTONE_ORES);
        getOrCreateTagBuilder(CommonItemTags.ORES_LAPIS).
                addOptionalTag(ItemTags.LAPIS_ORES);
        getOrCreateTagBuilder(CommonItemTags.ORES_EMERALD).
                addOptionalTag(ItemTags.EMERALD_ORES);
        getOrCreateTagBuilder(CommonItemTags.ORES_DIAMOND).
                addOptionalTag(ItemTags.DIAMOND_ORES);

        getOrCreateTagBuilder(CommonItemTags.ORES_IN_GROUND_STONE).
                add(EPBlocks.TIN_ORE_ITEM);
        getOrCreateTagBuilder(CommonItemTags.ORES_IN_GROUND_DEEPSLATE).
                add(EPBlocks.DEEPSLATE_TIN_ORE_ITEM);

        getOrCreateTagBuilder(ConventionalItemTags.STORAGE_BLOCKS).
                addTag(CommonItemTags.STORAGE_BLOCKS_SILICON).
                addTag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN).
                addTag(CommonItemTags.STORAGE_BLOCKS_TIN);
        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_SILICON).
                add(EPBlocks.SILICON_BLOCK_ITEM);
        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_RAW_TIN).
                add(EPBlocks.RAW_TIN_BLOCK_ITEM);
        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_TIN).
                add(EPBlocks.TIN_BLOCK_ITEM);

        getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS).
                addTag(CommonItemTags.RAW_MATERIALS_TIN);
        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_TIN).
                add(EPItems.RAW_TIN);

        getOrCreateTagBuilder(ConventionalItemTags.DUSTS).
                addTag(CommonItemTags.DUSTS_WOOD).
                addTag(CommonItemTags.DUSTS_CHARCOAL).
                addTag(CommonItemTags.DUSTS_TIN).
                addTag(CommonItemTags.DUSTS_COPPER).
                addTag(CommonItemTags.DUSTS_IRON).
                addTag(CommonItemTags.DUSTS_GOLD);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_WOOD).
                add(EPItems.SAWDUST);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_CHARCOAL).
                add(EPItems.CHARCOAL_DUST);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_TIN).
                add(EPItems.TIN_DUST);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_COPPER).
                add(EPItems.COPPER_DUST);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_IRON).
                add(EPItems.IRON_DUST);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_GOLD).
                add(EPItems.GOLD_DUST);

        getOrCreateTagBuilder(ConventionalItemTags.NUGGETS).
                addTag(CommonItemTags.NUGGETS_TIN);
        getOrCreateTagBuilder(CommonItemTags.NUGGETS_TIN).
                add(EPItems.TIN_NUGGET);

        getOrCreateTagBuilder(CommonItemTags.SILICON).
                add(EPItems.SILICON);

        getOrCreateTagBuilder(ConventionalItemTags.INGOTS).
                addTag(CommonItemTags.INGOTS_TIN).
                addTag(CommonItemTags.INGOTS_STEEL).
                addTag(CommonItemTags.INGOTS_REDSTONE_ALLOY).
                addTag(CommonItemTags.INGOTS_ADVANCED_ALLOY).
                addTag(CommonItemTags.INGOTS_ENERGIZED_COPPER).
                addTag(CommonItemTags.INGOTS_ENERGIZED_GOLD);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_TIN).
                add(EPItems.TIN_INGOT);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_STEEL).
                add(EPItems.STEEL_INGOT);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_REDSTONE_ALLOY).
                add(EPItems.REDSTONE_ALLOY_INGOT);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_ADVANCED_ALLOY).
                add(EPItems.ADVANCED_ALLOY_INGOT);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_ENERGIZED_COPPER).
                add(EPItems.ENERGIZED_COPPER_INGOT);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_ENERGIZED_GOLD).
                add(EPItems.ENERGIZED_GOLD_INGOT);

        getOrCreateTagBuilder(CommonItemTags.PLATES).
                addTag(CommonItemTags.PLATES_TIN).
                addTag(CommonItemTags.PLATES_COPPER).
                addTag(CommonItemTags.PLATES_IRON).
                addTag(CommonItemTags.PLATES_GOLD).
                addTag(CommonItemTags.PLATES_ADVANCED_ALLOY).
                addTag(CommonItemTags.PLATES_ENERGIZED_COPPER).
                addTag(CommonItemTags.PLATES_ENERGIZED_GOLD);
        getOrCreateTagBuilder(CommonItemTags.PLATES_TIN).
                add(EPItems.TIN_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_COPPER).
                add(EPItems.COPPER_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_IRON).
                add(EPItems.IRON_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_GOLD).
                add(EPItems.GOLD_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_ADVANCED_ALLOY).
                add(EPItems.ADVANCED_ALLOY_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_ENERGIZED_COPPER).
                add(EPItems.ENERGIZED_COPPER_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_ENERGIZED_GOLD).
                add(EPItems.ENERGIZED_GOLD_PLATE);

        getOrCreateTagBuilder(CommonItemTags.GEARS).
                addTag(CommonItemTags.GEARS_IRON);
        getOrCreateTagBuilder(CommonItemTags.GEARS_IRON).
                add(EPItems.IRON_GEAR);

        getOrCreateTagBuilder(ConventionalItemTags.RODS).
                addTag(CommonItemTags.RODS_IRON);
        getOrCreateTagBuilder(CommonItemTags.RODS_IRON).
                add(EPItems.IRON_ROD);

        getOrCreateTagBuilder(CommonItemTags.WIRES).
                addTag(CommonItemTags.WIRES_TIN).
                addTag(CommonItemTags.WIRES_COPPER).
                addTag(CommonItemTags.WIRES_GOLD).
                addTag(CommonItemTags.WIRES_ENERGIZED_COPPER).
                addTag(CommonItemTags.WIRES_ENERGIZED_GOLD);
        getOrCreateTagBuilder(CommonItemTags.WIRES_TIN).
                add(EPItems.TIN_WIRE);
        getOrCreateTagBuilder(CommonItemTags.WIRES_COPPER).
                add(EPItems.COPPER_WIRE);
        getOrCreateTagBuilder(CommonItemTags.WIRES_GOLD).
                add(EPItems.GOLD_WIRE);
        getOrCreateTagBuilder(CommonItemTags.WIRES_ENERGIZED_COPPER).
                add(EPItems.ENERGIZED_COPPER_WIRE);
        getOrCreateTagBuilder(CommonItemTags.WIRES_ENERGIZED_GOLD).
                add(EPItems.ENERGIZED_GOLD_WIRE);

        getOrCreateTagBuilder(ConventionalItemTags.TOOLS).
                addTag(CommonItemTags.TOOLS_HAMMERS).
                addTag(CommonItemTags.TOOLS_CUTTERS);

        getOrCreateTagBuilder(CommonItemTags.TOOLS_HAMMERS).
                add(EPItems.WOODEN_HAMMER).
                add(EPItems.STONE_HAMMER).
                add(EPItems.IRON_HAMMER).
                add(EPItems.GOLDEN_HAMMER).
                add(EPItems.DIAMOND_HAMMER).
                add(EPItems.NETHERITE_HAMMER);

        getOrCreateTagBuilder(CommonItemTags.TOOLS_CUTTERS).
                add(EPItems.CUTTER);
    }
}
