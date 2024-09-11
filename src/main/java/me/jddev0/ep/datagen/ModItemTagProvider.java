package me.jddev0.ep.datagen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
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
                add(ModItems.ENERGIZED_POWER_BOOK);

        getOrCreateTagBuilder(ItemTags.LECTERN_BOOKS).
                add(ModItems.ENERGIZED_POWER_BOOK);

        getOrCreateTagBuilder(ItemTags.PIGLIN_LOVED).
                add(ModItems.GOLD_DUST,
                        ModItems.GOLD_PLATE,
                        ModItems.GOLDEN_HAMMER);

        getOrCreateTagBuilder(EnergizedPowerItemTags.RAW_METAL_PRESS_MOLDS).
                add(ModItems.RAW_GEAR_PRESS_MOLD,
                        ModItems.RAW_ROD_PRESS_MOLD,
                        ModItems.RAW_WIRE_PRESS_MOLD);

        getOrCreateTagBuilder(EnergizedPowerItemTags.METAL_PRESS_MOLDS).
                add(ModItems.GEAR_PRESS_MOLD,
                        ModItems.ROD_PRESS_MOLD,
                        ModItems.WIRE_PRESS_MOLD);

        getOrCreateTagBuilder(CompatibilityItemTags.AE2_ITEM_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        ModBlocks.ITEM_CONVEYOR_BELT_ITEM,
                        ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM
                );

        getOrCreateTagBuilder(CompatibilityItemTags.AE2_FLUID_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        ModBlocks.IRON_FLUID_PIPE_ITEM,
                        ModBlocks.GOLDEN_FLUID_PIPE_ITEM,

                        ModBlocks.FLUID_TANK_SMALL_ITEM,
                        ModBlocks.FLUID_TANK_MEDIUM_ITEM,
                        ModBlocks.FLUID_TANK_LARGE_ITEM
                );

        getOrCreateTagBuilder(CompatibilityItemTags.AE2_FE_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        ModBlocks.TIN_CABLE_ITEM,
                        ModBlocks.COPPER_CABLE_ITEM,
                        ModBlocks.GOLD_CABLE_ITEM,
                        ModBlocks.ENERGIZED_COPPER_CABLE_ITEM,
                        ModBlocks.ENERGIZED_GOLD_CABLE_ITEM,
                        ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM,

                        ModBlocks.LV_TRANSFORMER_1_TO_N_ITEM,
                        ModBlocks.LV_TRANSFORMER_3_TO_3_ITEM,
                        ModBlocks.LV_TRANSFORMER_N_TO_1_ITEM,
                        ModBlocks.MV_TRANSFORMER_1_TO_N_ITEM,
                        ModBlocks.MV_TRANSFORMER_3_TO_3_ITEM,
                        ModBlocks.MV_TRANSFORMER_N_TO_1_ITEM,
                        ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM,
                        ModBlocks.HV_TRANSFORMER_3_TO_3_ITEM,
                        ModBlocks.HV_TRANSFORMER_N_TO_1_ITEM,
                        ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM,
                        ModBlocks.EHV_TRANSFORMER_3_TO_3_ITEM,
                        ModBlocks.EHV_TRANSFORMER_N_TO_1_ITEM,

                        ModBlocks.BATTERY_BOX_ITEM,
                        ModBlocks.ADVANCED_BATTERY_BOX_ITEM,
                        ModBlocks.CREATIVE_BATTERY_BOX_ITEM
                );

        getOrCreateTagBuilder(CommonItemTags.COBBLESTONES_NORMAL).
                add(Items.COBBLESTONE);

        getOrCreateTagBuilder(CommonItemTags.GRAVELS).
                add(Items.GRAVEL);

        getOrCreateTagBuilder(ConventionalItemTags.ORES).
                addTag(CommonItemTags.ORES_TIN);
        getOrCreateTagBuilder(CommonItemTags.ORES_TIN).
                add(ModBlocks.TIN_ORE_ITEM,
                        ModBlocks.DEEPSLATE_TIN_ORE_ITEM);

        getOrCreateTagBuilder(CommonItemTags.ORES_IN_GROUND_STONE).
                add(ModBlocks.TIN_ORE_ITEM);
        getOrCreateTagBuilder(CommonItemTags.ORES_IN_GROUND_DEEPSLATE).
                add(ModBlocks.DEEPSLATE_TIN_ORE_ITEM);

        getOrCreateTagBuilder(ConventionalItemTags.STORAGE_BLOCKS).
                addTag(CommonItemTags.STORAGE_BLOCKS_SILICON).
                addTag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN).
                addTag(CommonItemTags.STORAGE_BLOCKS_TIN);
        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_SILICON).
                add(ModBlocks.SILICON_BLOCK_ITEM);
        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_RAW_TIN).
                add(ModBlocks.RAW_TIN_BLOCK_ITEM);
        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_TIN).
                add(ModBlocks.TIN_BLOCK_ITEM);

        getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS).
                addTag(CommonItemTags.RAW_MATERIALS_TIN);
        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_TIN).
                add(ModItems.RAW_TIN);

        getOrCreateTagBuilder(ConventionalItemTags.DUSTS).
                addTag(CommonItemTags.DUSTS_WOOD).
                addTag(CommonItemTags.DUSTS_CHARCOAL).
                addTag(CommonItemTags.DUSTS_TIN).
                addTag(CommonItemTags.DUSTS_COPPER).
                addTag(CommonItemTags.DUSTS_IRON).
                addTag(CommonItemTags.DUSTS_GOLD);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_WOOD).
                add(ModItems.SAWDUST);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_CHARCOAL).
                add(ModItems.CHARCOAL_DUST);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_TIN).
                add(ModItems.TIN_DUST);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_COPPER).
                add(ModItems.COPPER_DUST);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_IRON).
                add(ModItems.IRON_DUST);
        getOrCreateTagBuilder(CommonItemTags.DUSTS_GOLD).
                add(ModItems.GOLD_DUST);

        getOrCreateTagBuilder(ConventionalItemTags.NUGGETS).
                addTag(CommonItemTags.NUGGETS_TIN);
        getOrCreateTagBuilder(CommonItemTags.NUGGETS_TIN).
                add(ModItems.TIN_NUGGET);

        getOrCreateTagBuilder(CommonItemTags.SILICON).
                add(ModItems.SILICON);

        getOrCreateTagBuilder(ConventionalItemTags.INGOTS).
                addTag(CommonItemTags.INGOTS_TIN).
                addTag(CommonItemTags.INGOTS_STEEL).
                addTag(CommonItemTags.INGOTS_REDSTONE_ALLOY).
                addTag(CommonItemTags.INGOTS_ADVANCED_ALLOY).
                addTag(CommonItemTags.INGOTS_ENERGIZED_COPPER).
                addTag(CommonItemTags.INGOTS_ENERGIZED_GOLD);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_TIN).
                add(ModItems.TIN_INGOT);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_STEEL).
                add(ModItems.STEEL_INGOT);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_REDSTONE_ALLOY).
                add(ModItems.REDSTONE_ALLOY_INGOT);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_ADVANCED_ALLOY).
                add(ModItems.ADVANCED_ALLOY_INGOT);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_ENERGIZED_COPPER).
                add(ModItems.ENERGIZED_COPPER_INGOT);
        getOrCreateTagBuilder(CommonItemTags.INGOTS_ENERGIZED_GOLD).
                add(ModItems.ENERGIZED_GOLD_INGOT);

        getOrCreateTagBuilder(CommonItemTags.PLATES).
                addTag(CommonItemTags.PLATES_TIN).
                addTag(CommonItemTags.PLATES_COPPER).
                addTag(CommonItemTags.PLATES_IRON).
                addTag(CommonItemTags.PLATES_GOLD).
                addTag(CommonItemTags.PLATES_ADVANCED_ALLOY).
                addTag(CommonItemTags.PLATES_ENERGIZED_COPPER).
                addTag(CommonItemTags.PLATES_ENERGIZED_GOLD);
        getOrCreateTagBuilder(CommonItemTags.PLATES_TIN).
                add(ModItems.TIN_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_COPPER).
                add(ModItems.COPPER_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_IRON).
                add(ModItems.IRON_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_GOLD).
                add(ModItems.GOLD_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_ADVANCED_ALLOY).
                add(ModItems.ADVANCED_ALLOY_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_ENERGIZED_COPPER).
                add(ModItems.ENERGIZED_COPPER_PLATE);
        getOrCreateTagBuilder(CommonItemTags.PLATES_ENERGIZED_GOLD).
                add(ModItems.ENERGIZED_GOLD_PLATE);

        getOrCreateTagBuilder(CommonItemTags.GEARS).
                addTag(CommonItemTags.GEARS_IRON);
        getOrCreateTagBuilder(CommonItemTags.GEARS_IRON).
                add(ModItems.IRON_GEAR);

        getOrCreateTagBuilder(ConventionalItemTags.RODS).
                addTag(CommonItemTags.RODS_IRON);
        getOrCreateTagBuilder(CommonItemTags.RODS_IRON).
                add(ModItems.IRON_ROD);

        getOrCreateTagBuilder(CommonItemTags.WIRES).
                addTag(CommonItemTags.WIRES_TIN).
                addTag(CommonItemTags.WIRES_COPPER).
                addTag(CommonItemTags.WIRES_GOLD).
                addTag(CommonItemTags.WIRES_ENERGIZED_COPPER).
                addTag(CommonItemTags.WIRES_ENERGIZED_GOLD);
        getOrCreateTagBuilder(CommonItemTags.WIRES_TIN).
                add(ModItems.TIN_WIRE);
        getOrCreateTagBuilder(CommonItemTags.WIRES_COPPER).
                add(ModItems.COPPER_WIRE);
        getOrCreateTagBuilder(CommonItemTags.WIRES_GOLD).
                add(ModItems.GOLD_WIRE);
        getOrCreateTagBuilder(CommonItemTags.WIRES_ENERGIZED_COPPER).
                add(ModItems.ENERGIZED_COPPER_WIRE);
        getOrCreateTagBuilder(CommonItemTags.WIRES_ENERGIZED_GOLD).
                add(ModItems.ENERGIZED_GOLD_WIRE);

        getOrCreateTagBuilder(ConventionalItemTags.TOOLS).
                addTag(CommonItemTags.TOOLS_HAMMERS).
                addTag(CommonItemTags.TOOLS_CUTTERS);

        getOrCreateTagBuilder(CommonItemTags.TOOLS_HAMMERS).
                add(ModItems.WOODEN_HAMMER).
                add(ModItems.STONE_HAMMER).
                add(ModItems.IRON_HAMMER).
                add(ModItems.GOLDEN_HAMMER).
                add(ModItems.DIAMOND_HAMMER).
                add(ModItems.NETHERITE_HAMMER);

        getOrCreateTagBuilder(CommonItemTags.TOOLS_CUTTERS).
                add(ModItems.CUTTER);
    }
}
