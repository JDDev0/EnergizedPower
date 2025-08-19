package me.jddev0.ep.datagen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
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
                        EPBlocks.ITEM_SILO_GIANT_ITEM
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
                addTag(CommonItemTags.COAL_ORES).
                addTag(CommonItemTags.TIN_ORES).
                addTag(CommonItemTags.COPPER_ORES).
                addTag(CommonItemTags.IRON_ORES).
                addTag(CommonItemTags.GOLD_ORES).
                addTag(CommonItemTags.REDSTONE_ORES).
                addTag(CommonItemTags.LAPIS_ORES).
                addTag(CommonItemTags.EMERALD_ORES).
                addTag(CommonItemTags.DIAMOND_ORES).
                addTag(CommonItemTags.NETHERITE_SCRAP_ORES);
        getOrCreateTagBuilder(CommonItemTags.COAL_ORES).
                addOptionalTag(ItemTags.COAL_ORES);
        getOrCreateTagBuilder(CommonItemTags.TIN_ORES).
                add(EPBlocks.TIN_ORE_ITEM,
                        EPBlocks.DEEPSLATE_TIN_ORE_ITEM);
        getOrCreateTagBuilder(CommonItemTags.COPPER_ORES).
                addOptionalTag(ItemTags.COPPER_ORES);
        getOrCreateTagBuilder(CommonItemTags.IRON_ORES).
                addOptionalTag(ItemTags.IRON_ORES);
        getOrCreateTagBuilder(CommonItemTags.GOLD_ORES).
                addOptionalTag(ItemTags.GOLD_ORES);
        getOrCreateTagBuilder(CommonItemTags.REDSTONE_ORES).
                addOptionalTag(ItemTags.REDSTONE_ORES);
        getOrCreateTagBuilder(CommonItemTags.LAPIS_ORES).
                addOptionalTag(ItemTags.LAPIS_ORES);
        getOrCreateTagBuilder(CommonItemTags.EMERALD_ORES).
                addOptionalTag(ItemTags.EMERALD_ORES);
        getOrCreateTagBuilder(CommonItemTags.DIAMOND_ORES).
                addOptionalTag(ItemTags.DIAMOND_ORES);
        getOrCreateTagBuilder(CommonItemTags.NETHERITE_SCRAP_ORES).
                add(Items.ANCIENT_DEBRIS);

        getOrCreateTagBuilder(CommonItemTags.COPPER_BLOCKS).
                add(Items.COPPER_BLOCK);
        getOrCreateTagBuilder(CommonItemTags.IRON_BLOCKS).
                add(Items.IRON_BLOCK);
        getOrCreateTagBuilder(CommonItemTags.GOLD_BLOCKS).
                add(Items.GOLD_BLOCK);
        getOrCreateTagBuilder(CommonItemTags.REDSTONE_BLOCKS).
                add(Items.REDSTONE_BLOCK);
        getOrCreateTagBuilder(CommonItemTags.SILICON_BLOCKS).
                add(EPBlocks.SILICON_BLOCK_ITEM);
        getOrCreateTagBuilder(CommonItemTags.RAW_TIN_BLOCKS).
                add(EPBlocks.RAW_TIN_BLOCK_ITEM);
        getOrCreateTagBuilder(CommonItemTags.TIN_BLOCKS).
                add(EPBlocks.TIN_BLOCK_ITEM);

        getOrCreateTagBuilder(ConventionalItemTags.RAW_ORES).
                addTag(CommonItemTags.RAW_TIN_ORES);
        getOrCreateTagBuilder(CommonItemTags.RAW_TIN_ORES).
                add(EPItems.RAW_TIN);

        getOrCreateTagBuilder(ConventionalItemTags.DUSTS).
                addTag(CommonItemTags.SAW_DUSTS).
                addTag(CommonItemTags.CHARCOAL_DUSTS).
                addTag(CommonItemTags.TIN_DUSTS).
                addTag(CommonItemTags.COPPER_DUSTS).
                addTag(CommonItemTags.IRON_DUSTS).
                addTag(CommonItemTags.GOLD_DUSTS);
        getOrCreateTagBuilder(CommonItemTags.SAW_DUSTS).
                add(EPItems.SAWDUST);
        getOrCreateTagBuilder(CommonItemTags.CHARCOAL_DUSTS).
                add(EPItems.CHARCOAL_DUST);
        getOrCreateTagBuilder(CommonItemTags.TIN_DUSTS).
                add(EPItems.TIN_DUST);
        getOrCreateTagBuilder(CommonItemTags.COPPER_DUSTS).
                add(EPItems.COPPER_DUST);
        getOrCreateTagBuilder(CommonItemTags.IRON_DUSTS).
                add(EPItems.IRON_DUST);
        getOrCreateTagBuilder(CommonItemTags.GOLD_DUSTS).
                add(EPItems.GOLD_DUST);

        getOrCreateTagBuilder(ConventionalItemTags.NUGGETS).
                addTag(CommonItemTags.TIN_NUGGETS).
                addTag(CommonItemTags.IRON_NUGGETS).
                addTag(CommonItemTags.GOLD_NUGGETS);
        getOrCreateTagBuilder(CommonItemTags.IRON_NUGGETS).
                add(Items.IRON_NUGGET);
        getOrCreateTagBuilder(CommonItemTags.GOLD_NUGGETS).
                add(Items.GOLD_NUGGET);
        getOrCreateTagBuilder(CommonItemTags.TIN_NUGGETS).
                add(EPItems.TIN_NUGGET);

        getOrCreateTagBuilder(CommonItemTags.AMETHYSTS).
                add(Items.AMETHYST_SHARD);

        getOrCreateTagBuilder(CommonItemTags.SILICON).
                add(EPItems.SILICON);

        getOrCreateTagBuilder(ConventionalItemTags.INGOTS).
                addTag(CommonItemTags.TIN_INGOTS).
                addTag(CommonItemTags.STEEL_INGOTS).
                addTag(CommonItemTags.REDSTONE_ALLOY_INGOTS).
                addTag(CommonItemTags.ADVANCED_ALLOY_INGOTS).
                addTag(CommonItemTags.ENERGIZED_COPPER_INGOTS).
                addTag(CommonItemTags.ENERGIZED_GOLD_INGOTS);
        getOrCreateTagBuilder(CommonItemTags.TIN_INGOTS).
                add(EPItems.TIN_INGOT);
        getOrCreateTagBuilder(CommonItemTags.STEEL_INGOTS).
                add(EPItems.STEEL_INGOT);
        getOrCreateTagBuilder(CommonItemTags.REDSTONE_ALLOY_INGOTS).
                add(EPItems.REDSTONE_ALLOY_INGOT);
        getOrCreateTagBuilder(CommonItemTags.ADVANCED_ALLOY_INGOTS).
                add(EPItems.ADVANCED_ALLOY_INGOT);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_COPPER_INGOTS).
                add(EPItems.ENERGIZED_COPPER_INGOT);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_GOLD_INGOTS).
                add(EPItems.ENERGIZED_GOLD_INGOT);

        getOrCreateTagBuilder(CommonItemTags.PLATES).
                addTag(CommonItemTags.TIN_PLATES).
                addTag(CommonItemTags.COPPER_PLATES).
                addTag(CommonItemTags.IRON_PLATES).
                addTag(CommonItemTags.GOLD_PLATES).
                addTag(CommonItemTags.ADVANCED_ALLOY_PLATES).
                addTag(CommonItemTags.ENERGIZED_COPPER_PLATES).
                addTag(CommonItemTags.ENERGIZED_GOLD_PLATES);
        getOrCreateTagBuilder(CommonItemTags.TIN_PLATES).
                add(EPItems.TIN_PLATE);
        getOrCreateTagBuilder(CommonItemTags.COPPER_PLATES).
                add(EPItems.COPPER_PLATE);
        getOrCreateTagBuilder(CommonItemTags.IRON_PLATES).
                add(EPItems.IRON_PLATE);
        getOrCreateTagBuilder(CommonItemTags.GOLD_PLATES).
                add(EPItems.GOLD_PLATE);
        getOrCreateTagBuilder(CommonItemTags.ADVANCED_ALLOY_PLATES).
                add(EPItems.ADVANCED_ALLOY_PLATE);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_COPPER_PLATES).
                add(EPItems.ENERGIZED_COPPER_PLATE);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_GOLD_PLATES).
                add(EPItems.ENERGIZED_GOLD_PLATE);

        getOrCreateTagBuilder(CommonItemTags.GEARS).
                addTag(CommonItemTags.IRON_GEARS);
        getOrCreateTagBuilder(CommonItemTags.IRON_GEARS).
                add(EPItems.IRON_GEAR);

        getOrCreateTagBuilder(CommonItemTags.RODS).
                addTag(CommonItemTags.WOODEN_RODS).
                addTag(CommonItemTags.IRON_RODS);
        getOrCreateTagBuilder(CommonItemTags.WOODEN_RODS).
                add(Items.STICK);
        getOrCreateTagBuilder(CommonItemTags.IRON_RODS).
                add(EPItems.IRON_ROD);

        getOrCreateTagBuilder(CommonItemTags.WIRES).
                addTag(CommonItemTags.TIN_WIRES).
                addTag(CommonItemTags.COPPER_WIRES).
                addTag(CommonItemTags.GOLD_WIRES).
                addTag(CommonItemTags.ENERGIZED_COPPER_WIRES).
                addTag(CommonItemTags.ENERGIZED_GOLD_WIRES);
        getOrCreateTagBuilder(CommonItemTags.TIN_WIRES).
                add(EPItems.TIN_WIRE);
        getOrCreateTagBuilder(CommonItemTags.COPPER_WIRES).
                add(EPItems.COPPER_WIRE);
        getOrCreateTagBuilder(CommonItemTags.GOLD_WIRES).
                add(EPItems.GOLD_WIRE);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_COPPER_WIRES).
                add(EPItems.ENERGIZED_COPPER_WIRE);
        getOrCreateTagBuilder(CommonItemTags.ENERGIZED_GOLD_WIRES).
                add(EPItems.ENERGIZED_GOLD_WIRE);

        getOrCreateTagBuilder(CommonItemTags.HAMMERS).
                add(EPItems.WOODEN_HAMMER).
                add(EPItems.STONE_HAMMER).
                add(EPItems.IRON_HAMMER).
                add(EPItems.GOLDEN_HAMMER).
                add(EPItems.DIAMOND_HAMMER).
                add(EPItems.NETHERITE_HAMMER);

        getOrCreateTagBuilder(CommonItemTags.CUTTERS).
                add(EPItems.CUTTER);
    }
}
