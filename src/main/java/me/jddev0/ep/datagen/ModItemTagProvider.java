package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.registry.tags.CompatibilityItemTags;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, EPAPI.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(ItemTags.BOOKSHELF_BOOKS).
                add(EPItems.ENERGIZED_POWER_BOOK.get());

        tag(ItemTags.LECTERN_BOOKS).
                add(EPItems.ENERGIZED_POWER_BOOK.get());

        tag(ItemTags.PIGLIN_LOVED).
                add(EPItems.GOLD_DUST.get(),
                        EPItems.GOLD_PLATE.get(),
                        EPItems.GOLDEN_HAMMER.get());

        tag(EnergizedPowerItemTags.RAW_METAL_PRESS_MOLDS).
                add(EPItems.RAW_GEAR_PRESS_MOLD.get(),
                        EPItems.RAW_ROD_PRESS_MOLD.get(),
                        EPItems.RAW_WIRE_PRESS_MOLD.get());

        tag(EnergizedPowerItemTags.METAL_PRESS_MOLDS).
                add(EPItems.GEAR_PRESS_MOLD.get(),
                        EPItems.ROD_PRESS_MOLD.get(),
                        EPItems.WIRE_PRESS_MOLD.get());

        tag(CompatibilityItemTags.AE2_ITEM_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM.get(),
                        EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM.get(),
                        EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM.get(),
                        EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM.get(),
                        EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM.get(),
                        EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM.get(),

                        EPBlocks.ITEM_SILO_TINY_ITEM.get(),
                        EPBlocks.ITEM_SILO_SMALL_ITEM.get(),
                        EPBlocks.ITEM_SILO_MEDIUM_ITEM.get(),
                        EPBlocks.ITEM_SILO_LARGE_ITEM.get(),
                        EPBlocks.ITEM_SILO_GIANT_ITEM.get(),
                        EPBlocks.CREATIVE_ITEM_SILO_ITEM.get()
                );

        tag(CompatibilityItemTags.AE2_FLUID_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        EPBlocks.IRON_FLUID_PIPE_ITEM.get(),
                        EPBlocks.GOLDEN_FLUID_PIPE_ITEM.get(),

                        EPBlocks.FLUID_TANK_SMALL_ITEM.get(),
                        EPBlocks.FLUID_TANK_MEDIUM_ITEM.get(),
                        EPBlocks.FLUID_TANK_LARGE_ITEM.get(),
                        EPBlocks.CREATIVE_FLUID_TANK_ITEM.get()
                );

        tag(CompatibilityItemTags.AE2_FE_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        EPBlocks.TIN_CABLE_ITEM.get(),
                        EPBlocks.COPPER_CABLE_ITEM.get(),
                        EPBlocks.GOLD_CABLE_ITEM.get(),
                        EPBlocks.ENERGIZED_COPPER_CABLE_ITEM.get(),
                        EPBlocks.ENERGIZED_GOLD_CABLE_ITEM.get(),
                        EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM.get(),

                        EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM.get(),
                        EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM.get(),
                        EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM.get(),
                        EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM.get(),
                        EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM.get(),
                        EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM.get(),
                        EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM.get(),
                        EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM.get(),
                        EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM.get(),
                        EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM.get(),
                        EPBlocks.EHV_TRANSFORMER_3_TO_3_ITEM.get(),
                        EPBlocks.EHV_TRANSFORMER_N_TO_1_ITEM.get(),

                        EPBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM.get(),
                        EPBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM.get(),
                        EPBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM.get(),
                        EPBlocks.CONFIGURABLE_EHV_TRANSFORMER_ITEM.get(),

                        EPBlocks.BATTERY_BOX_ITEM.get(),
                        EPBlocks.ADVANCED_BATTERY_BOX_ITEM.get(),
                        EPBlocks.CREATIVE_BATTERY_BOX_ITEM.get()
                );

        tag(Tags.Items.ORES).
                addTag(CommonItemTags.ORES_TIN);
        tag(CommonItemTags.ORES_TIN).
                add(EPBlocks.TIN_ORE_ITEM.get(),
                        EPBlocks.DEEPSLATE_TIN_ORE_ITEM.get());

        tag(Tags.Items.ORES_IN_GROUND_STONE).
                add(EPBlocks.TIN_ORE_ITEM.get());
        tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).
                add(EPBlocks.DEEPSLATE_TIN_ORE_ITEM.get());

        tag(Tags.Items.STORAGE_BLOCKS).
                addTag(CommonItemTags.STORAGE_BLOCKS_SILICON).
                addTag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN).
                addTag(CommonItemTags.STORAGE_BLOCKS_TIN);
        tag(CommonItemTags.STORAGE_BLOCKS_SILICON).
                add(EPBlocks.SILICON_BLOCK_ITEM.get());
        tag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN).
                add(EPBlocks.RAW_TIN_BLOCK_ITEM.get());
        tag(CommonItemTags.STORAGE_BLOCKS_TIN).
                add(EPBlocks.TIN_BLOCK_ITEM.get());

        tag(Tags.Items.RAW_MATERIALS).
                addTag(CommonItemTags.RAW_MATERIALS_TIN);
        tag(CommonItemTags.RAW_MATERIALS_TIN).
                add(EPItems.RAW_TIN.get());

        tag(Tags.Items.DUSTS).
                addTag(CommonItemTags.DUSTS_WOOD).
                addTag(CommonItemTags.DUSTS_CHARCOAL).
                addTag(CommonItemTags.DUSTS_TIN).
                addTag(CommonItemTags.DUSTS_COPPER).
                addTag(CommonItemTags.DUSTS_IRON).
                addTag(CommonItemTags.DUSTS_GOLD);
        tag(CommonItemTags.DUSTS_WOOD).
                add(EPItems.SAWDUST.get());
        tag(CommonItemTags.DUSTS_CHARCOAL).
                add(EPItems.CHARCOAL_DUST.get());
        tag(CommonItemTags.DUSTS_TIN).
                add(EPItems.TIN_DUST.get());
        tag(CommonItemTags.DUSTS_COPPER).
                add(EPItems.COPPER_DUST.get());
        tag(CommonItemTags.DUSTS_IRON).
                add(EPItems.IRON_DUST.get());
        tag(CommonItemTags.DUSTS_GOLD).
                add(EPItems.GOLD_DUST.get());

        tag(Tags.Items.NUGGETS).
                addTag(CommonItemTags.NUGGETS_TIN);
        tag(CommonItemTags.NUGGETS_TIN).
                add(EPItems.TIN_NUGGET.get());

        tag(CommonItemTags.SILICON).
                add(EPItems.SILICON.get());

        tag(Tags.Items.INGOTS).
                addTag(CommonItemTags.INGOTS_TIN).
                addTag(CommonItemTags.INGOTS_STEEL).
                addTag(CommonItemTags.INGOTS_REDSTONE_ALLOY).
                addTag(CommonItemTags.INGOTS_ADVANCED_ALLOY).
                addTag(CommonItemTags.INGOTS_ENERGIZED_COPPER).
                addTag(CommonItemTags.INGOTS_ENERGIZED_GOLD);
        tag(CommonItemTags.INGOTS_TIN).
                add(EPItems.TIN_INGOT.get());
        tag(CommonItemTags.INGOTS_STEEL).
                add(EPItems.STEEL_INGOT.get());
        tag(CommonItemTags.INGOTS_REDSTONE_ALLOY).
                add(EPItems.REDSTONE_ALLOY_INGOT.get());
        tag(CommonItemTags.INGOTS_ADVANCED_ALLOY).
                add(EPItems.ADVANCED_ALLOY_INGOT.get());
        tag(CommonItemTags.INGOTS_ENERGIZED_COPPER).
                add(EPItems.ENERGIZED_COPPER_INGOT.get());
        tag(CommonItemTags.INGOTS_ENERGIZED_GOLD).
                add(EPItems.ENERGIZED_GOLD_INGOT.get());

        tag(CommonItemTags.PLATES).
                addTag(CommonItemTags.PLATES_TIN).
                addTag(CommonItemTags.PLATES_COPPER).
                addTag(CommonItemTags.PLATES_IRON).
                addTag(CommonItemTags.PLATES_GOLD).
                addTag(CommonItemTags.PLATES_ADVANCED_ALLOY).
                addTag(CommonItemTags.PLATES_ENERGIZED_COPPER).
                addTag(CommonItemTags.PLATES_ENERGIZED_GOLD);
        tag(CommonItemTags.PLATES_TIN).
                add(EPItems.TIN_PLATE.get());
        tag(CommonItemTags.PLATES_COPPER).
                add(EPItems.COPPER_PLATE.get());
        tag(CommonItemTags.PLATES_IRON).
                add(EPItems.IRON_PLATE.get());
        tag(CommonItemTags.PLATES_GOLD).
                add(EPItems.GOLD_PLATE.get());
        tag(CommonItemTags.PLATES_ADVANCED_ALLOY).
                add(EPItems.ADVANCED_ALLOY_PLATE.get());
        tag(CommonItemTags.PLATES_ENERGIZED_COPPER).
                add(EPItems.ENERGIZED_COPPER_PLATE.get());
        tag(CommonItemTags.PLATES_ENERGIZED_GOLD).
                add(EPItems.ENERGIZED_GOLD_PLATE.get());

        tag(CommonItemTags.GEARS).
                addTag(CommonItemTags.GEARS_IRON);
        tag(CommonItemTags.GEARS_IRON).
                add(EPItems.IRON_GEAR.get());

        tag(Tags.Items.RODS).
                addTag(CommonItemTags.RODS_IRON);
        tag(CommonItemTags.RODS_IRON).
                add(EPItems.IRON_ROD.get());

        tag(CommonItemTags.WIRES).
                addTag(CommonItemTags.WIRES_TIN).
                addTag(CommonItemTags.WIRES_COPPER).
                addTag(CommonItemTags.WIRES_GOLD).
                addTag(CommonItemTags.WIRES_ENERGIZED_COPPER).
                addTag(CommonItemTags.WIRES_ENERGIZED_GOLD);
        tag(CommonItemTags.WIRES_TIN).
                add(EPItems.TIN_WIRE.get());
        tag(CommonItemTags.WIRES_COPPER).
                add(EPItems.COPPER_WIRE.get());
        tag(CommonItemTags.WIRES_GOLD).
                add(EPItems.GOLD_WIRE.get());
        tag(CommonItemTags.WIRES_ENERGIZED_COPPER).
                add(EPItems.ENERGIZED_COPPER_WIRE.get());
        tag(CommonItemTags.WIRES_ENERGIZED_GOLD).
                add(EPItems.ENERGIZED_GOLD_WIRE.get());

        tag(Tags.Items.TOOLS).
                addTag(CommonItemTags.TOOLS_HAMMERS).
                addTag(CommonItemTags.TOOLS_CUTTERS);

        tag(CommonItemTags.TOOLS_HAMMERS).
                add(EPItems.WOODEN_HAMMER.get()).
                add(EPItems.STONE_HAMMER.get()).
                add(EPItems.COPPER_HAMMER.get()).
                add(EPItems.IRON_HAMMER.get()).
                add(EPItems.GOLDEN_HAMMER.get()).
                add(EPItems.DIAMOND_HAMMER.get()).
                add(EPItems.NETHERITE_HAMMER.get());

        tag(CommonItemTags.TOOLS_CUTTERS).
                add(EPItems.CUTTER.get());
    }
}
