package me.jddev0.ep.datagen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.registry.tags.CompatibilityItemTags;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
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

        getOrCreateTagBuilder(CommonItemTags.COBBLESTONES).
                add(Items.COBBLESTONE);

        getOrCreateTagBuilder(CommonItemTags.GRAVELS).
                add(Items.GRAVEL);

        getOrCreateTagBuilder(CommonItemTags.GLASS_BLOCKS_COLORLESS).
                add(Items.GLASS);

        getOrCreateTagBuilder(CommonItemTags.GLASS_PANES_COLORLESS).
                add(Items.GLASS_PANE);

        getOrCreateTagBuilder(CommonItemTags.LEATHER).
                add(Items.LEATHER);

        getOrCreateTagBuilder(CommonItemTags.ENDER_PEARLS).
                add(Items.ENDER_PEARL);

        getOrCreateTagBuilder(ConventionalItemTags.ORES).
                addTag(CommonItemTags.TIN_ORES);
        getOrCreateTagBuilder(CommonItemTags.TIN_ORES).
                add(ModBlocks.TIN_ORE_ITEM,
                        ModBlocks.DEEPSLATE_TIN_ORE_ITEM);

        getOrCreateTagBuilder(CommonItemTags.COPPER_BLOCKS).
                add(Items.COPPER_BLOCK);
        getOrCreateTagBuilder(CommonItemTags.IRON_BLOCKS).
                add(Items.IRON_BLOCK);
        getOrCreateTagBuilder(CommonItemTags.GOLD_BLOCKS).
                add(Items.GOLD_BLOCK);
        getOrCreateTagBuilder(CommonItemTags.REDSTONE_BLOCKS).
                add(Items.REDSTONE_BLOCK);
        getOrCreateTagBuilder(CommonItemTags.SILICON_BLOCKS).
                add(ModBlocks.SILICON_BLOCK_ITEM);
        getOrCreateTagBuilder(CommonItemTags.RAW_TIN_BLOCKS).
                add(ModBlocks.RAW_TIN_BLOCK_ITEM);
        getOrCreateTagBuilder(CommonItemTags.TIN_BLOCKS).
                add(ModBlocks.TIN_BLOCK_ITEM);

        getOrCreateTagBuilder(ConventionalItemTags.RAW_ORES).
                addTag(CommonItemTags.RAW_TIN_ORES);
        getOrCreateTagBuilder(CommonItemTags.RAW_TIN_ORES).
                add(ModItems.RAW_TIN);

        getOrCreateTagBuilder(ConventionalItemTags.DUSTS).
                addTag(CommonItemTags.SAW_DUSTS).
                addTag(CommonItemTags.CHARCOAL_DUSTS).
                addTag(CommonItemTags.TIN_DUSTS).
                addTag(CommonItemTags.COPPER_DUSTS).
                addTag(CommonItemTags.IRON_DUSTS).
                addTag(CommonItemTags.GOLD_DUSTS);
        getOrCreateTagBuilder(CommonItemTags.SAW_DUSTS).
                add(ModItems.SAWDUST);
        getOrCreateTagBuilder(CommonItemTags.CHARCOAL_DUSTS).
                add(ModItems.CHARCOAL_DUST);
        getOrCreateTagBuilder(CommonItemTags.TIN_DUSTS).
                add(ModItems.TIN_DUST);
        getOrCreateTagBuilder(CommonItemTags.COPPER_DUSTS).
                add(ModItems.COPPER_DUST);
        getOrCreateTagBuilder(CommonItemTags.IRON_DUSTS).
                add(ModItems.IRON_DUST);
        getOrCreateTagBuilder(CommonItemTags.GOLD_DUSTS).
                add(ModItems.GOLD_DUST);

        getOrCreateTagBuilder(ConventionalItemTags.NUGGETS).
                addTag(CommonItemTags.TIN_NUGGETS).
                addTag(CommonItemTags.IRON_NUGGETS).
                addTag(CommonItemTags.GOLD_NUGGETS);
        getOrCreateTagBuilder(CommonItemTags.IRON_NUGGETS).
                add(Items.IRON_NUGGET);
        getOrCreateTagBuilder(CommonItemTags.GOLD_NUGGETS).
                add(Items.GOLD_NUGGET);
        getOrCreateTagBuilder(CommonItemTags.TIN_NUGGETS).
                add(ModItems.TIN_NUGGET);

        getOrCreateTagBuilder(CommonItemTags.AMETHYSTS).
                add(Items.AMETHYST_SHARD);

        getOrCreateTagBuilder(CommonItemTags.SILICON).
                add(ModItems.SILICON);

        getOrCreateTagBuilder(ConventionalItemTags.INGOTS).
                addTag(CommonItemTags.TIN_INGOTS).
                addTag(CommonItemTags.STEEL_INGOTS).
                addTag(CommonItemTags.REDSTONE_ALLOY_INGOTS).
                addTag(CommonItemTags.ADVANCED_ALLOY_INGOTS).
                addTag(CommonItemTags.ENERGIZED_COPPER_INGOTS).
                addTag(CommonItemTags.ENERGIZED_GOLD_INGOTS);
        getOrCreateTagBuilder(CommonItemTags.TIN_INGOTS).
                add(ModItems.TIN_INGOT);
        getOrCreateTagBuilder(CommonItemTags.STEEL_INGOTS).
                add(ModItems.STEEL_INGOT);
        getOrCreateTagBuilder(CommonItemTags.REDSTONE_ALLOY_INGOTS).
                add(ModItems.REDSTONE_ALLOY_INGOT);
        getOrCreateTagBuilder(CommonItemTags.ADVANCED_ALLOY_INGOTS).
                add(ModItems.ADVANCED_ALLOY_INGOT);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_COPPER_INGOTS).
                add(ModItems.ENERGIZED_COPPER_INGOT);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_GOLD_INGOTS).
                add(ModItems.ENERGIZED_GOLD_INGOT);

        getOrCreateTagBuilder(CommonItemTags.PLATES).
                addTag(CommonItemTags.TIN_PLATES).
                addTag(CommonItemTags.COPPER_PLATES).
                addTag(CommonItemTags.IRON_PLATES).
                addTag(CommonItemTags.GOLD_PLATES).
                addTag(CommonItemTags.ADVANCED_ALLOY_PLATES).
                addTag(CommonItemTags.ENERGIZED_COPPER_PLATES).
                addTag(CommonItemTags.ENERGIZED_GOLD_PLATES);
        getOrCreateTagBuilder(CommonItemTags.TIN_PLATES).
                add(ModItems.TIN_PLATE);
        getOrCreateTagBuilder(CommonItemTags.COPPER_PLATES).
                add(ModItems.COPPER_PLATE);
        getOrCreateTagBuilder(CommonItemTags.IRON_PLATES).
                add(ModItems.IRON_PLATE);
        getOrCreateTagBuilder(CommonItemTags.GOLD_PLATES).
                add(ModItems.GOLD_PLATE);
        getOrCreateTagBuilder(CommonItemTags.ADVANCED_ALLOY_PLATES).
                add(ModItems.ADVANCED_ALLOY_PLATE);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_COPPER_PLATES).
                add(ModItems.ENERGIZED_COPPER_PLATE);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_GOLD_PLATES).
                add(ModItems.ENERGIZED_GOLD_PLATE);

        getOrCreateTagBuilder(CommonItemTags.GEARS).
                addTag(CommonItemTags.IRON_GEARS);
        getOrCreateTagBuilder(CommonItemTags.IRON_GEARS).
                add(ModItems.IRON_GEAR);

        getOrCreateTagBuilder(CommonItemTags.RODS).
                addTag(CommonItemTags.IRON_RODS);
        getOrCreateTagBuilder(CommonItemTags.IRON_RODS).
                add(ModItems.IRON_ROD);

        getOrCreateTagBuilder(CommonItemTags.WIRES).
                addTag(CommonItemTags.TIN_WIRES).
                addTag(CommonItemTags.COPPER_WIRES).
                addTag(CommonItemTags.GOLD_WIRES).
                addTag(CommonItemTags.ENERGIZED_COPPER_WIRES).
                addTag(CommonItemTags.ENERGIZED_GOLD_WIRES);
        getOrCreateTagBuilder(CommonItemTags.TIN_WIRES).
                add(ModItems.TIN_WIRE);
        getOrCreateTagBuilder(CommonItemTags.COPPER_WIRES).
                add(ModItems.COPPER_WIRE);
        getOrCreateTagBuilder(CommonItemTags.GOLD_WIRES).
                add(ModItems.GOLD_WIRE);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_COPPER_WIRES).
                add(ModItems.ENERGIZED_COPPER_WIRE);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_GOLD_WIRES).
                add(ModItems.ENERGIZED_GOLD_WIRE);

        getOrCreateTagBuilder(CommonItemTags.HAMMERS).
                add(ModItems.WOODEN_HAMMER).
                add(ModItems.STONE_HAMMER).
                add(ModItems.IRON_HAMMER).
                add(ModItems.GOLDEN_HAMMER).
                add(ModItems.DIAMOND_HAMMER).
                add(ModItems.NETHERITE_HAMMER);

        getOrCreateTagBuilder(CommonItemTags.CUTTERS).
                add(ModItems.CUTTER);
    }
}
