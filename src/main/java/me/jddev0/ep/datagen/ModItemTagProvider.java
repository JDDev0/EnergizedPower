package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.registry.tags.CompatibilityItemTags;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagsProvider.TagLookup<Block>> blockTagLookup,
                              @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagLookup, EnergizedPowerMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(ItemTags.BOOKSHELF_BOOKS).
                add(ModItems.ENERGIZED_POWER_BOOK.get());

        tag(ItemTags.LECTERN_BOOKS).
                add(ModItems.ENERGIZED_POWER_BOOK.get());

        tag(ItemTags.PIGLIN_LOVED).
                add(ModItems.GOLD_DUST.get(),
                        ModItems.GOLD_PLATE.get(),
                        ModItems.GOLDEN_HAMMER.get());

        tag(EnergizedPowerItemTags.RAW_METAL_PRESS_MOLDS).
                add(ModItems.RAW_GEAR_PRESS_MOLD.get(),
                        ModItems.RAW_ROD_PRESS_MOLD.get(),
                        ModItems.RAW_WIRE_PRESS_MOLD.get());

        tag(EnergizedPowerItemTags.METAL_PRESS_MOLDS).
                add(ModItems.GEAR_PRESS_MOLD.get(),
                        ModItems.ROD_PRESS_MOLD.get(),
                        ModItems.WIRE_PRESS_MOLD.get());

        tag(CompatibilityItemTags.AE2_ITEM_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        ModBlocks.ITEM_CONVEYOR_BELT_ITEM.get(),
                        ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM.get()
                );

        tag(CompatibilityItemTags.AE2_FLUID_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        ModBlocks.IRON_FLUID_PIPE_ITEM.get(),
                        ModBlocks.GOLDEN_FLUID_PIPE_ITEM.get(),

                        ModBlocks.FLUID_TANK_SMALL_ITEM.get(),
                        ModBlocks.FLUID_TANK_MEDIUM_ITEM.get(),
                        ModBlocks.FLUID_TANK_LARGE_ITEM.get()
                );

        tag(CompatibilityItemTags.AE2_FE_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        ModBlocks.TIN_CABLE_ITEM.get(),
                        ModBlocks.COPPER_CABLE_ITEM.get(),
                        ModBlocks.GOLD_CABLE_ITEM.get(),
                        ModBlocks.ENERGIZED_COPPER_CABLE_ITEM.get(),
                        ModBlocks.ENERGIZED_GOLD_CABLE_ITEM.get(),
                        ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM.get(),

                        ModBlocks.LV_TRANSFORMER_1_TO_N_ITEM.get(),
                        ModBlocks.LV_TRANSFORMER_3_TO_3_ITEM.get(),
                        ModBlocks.LV_TRANSFORMER_N_TO_1_ITEM.get(),
                        ModBlocks.MV_TRANSFORMER_1_TO_N_ITEM.get(),
                        ModBlocks.MV_TRANSFORMER_3_TO_3_ITEM.get(),
                        ModBlocks.MV_TRANSFORMER_N_TO_1_ITEM.get(),
                        ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM.get(),
                        ModBlocks.HV_TRANSFORMER_3_TO_3_ITEM.get(),
                        ModBlocks.HV_TRANSFORMER_N_TO_1_ITEM.get(),
                        ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM.get(),
                        ModBlocks.EHV_TRANSFORMER_3_TO_3_ITEM.get(),
                        ModBlocks.EHV_TRANSFORMER_N_TO_1_ITEM.get(),

                        ModBlocks.BATTERY_BOX_ITEM.get(),
                        ModBlocks.ADVANCED_BATTERY_BOX_ITEM.get(),
                        ModBlocks.CREATIVE_BATTERY_BOX_ITEM.get()
                );

        tag(Tags.Items.ORES).
                addTag(CommonItemTags.ORES_TIN);
        tag(CommonItemTags.ORES_TIN).
                add(ModBlocks.TIN_ORE_ITEM.get(),
                        ModBlocks.DEEPSLATE_TIN_ORE_ITEM.get());

        tag(Tags.Items.ORES_IN_GROUND_STONE).
                add(ModBlocks.TIN_ORE_ITEM.get());
        tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).
                add(ModBlocks.DEEPSLATE_TIN_ORE_ITEM.get());

        tag(Tags.Items.STORAGE_BLOCKS).
                addTag(CommonItemTags.STORAGE_BLOCKS_SILICON).
                addTag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN).
                addTag(CommonItemTags.STORAGE_BLOCKS_TIN);
        tag(CommonItemTags.STORAGE_BLOCKS_SILICON).
                add(ModBlocks.SILICON_BLOCK_ITEM.get());
        tag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN).
                add(ModBlocks.RAW_TIN_BLOCK_ITEM.get());
        tag(CommonItemTags.STORAGE_BLOCKS_TIN).
                add(ModBlocks.TIN_BLOCK_ITEM.get());

        tag(Tags.Items.RAW_MATERIALS).
                addTag(CommonItemTags.RAW_MATERIALS_TIN);
        tag(CommonItemTags.RAW_MATERIALS_TIN).
                add(ModItems.RAW_TIN.get());

        tag(Tags.Items.DUSTS).
                addTag(CommonItemTags.DUSTS_WOOD).
                addTag(CommonItemTags.DUSTS_CHARCOAL).
                addTag(CommonItemTags.DUSTS_TIN).
                addTag(CommonItemTags.DUSTS_COPPER).
                addTag(CommonItemTags.DUSTS_IRON).
                addTag(CommonItemTags.DUSTS_GOLD);
        tag(CommonItemTags.DUSTS_WOOD).
                add(ModItems.SAWDUST.get());
        tag(CommonItemTags.DUSTS_CHARCOAL).
                add(ModItems.CHARCOAL_DUST.get());
        tag(CommonItemTags.DUSTS_TIN).
                add(ModItems.TIN_DUST.get());
        tag(CommonItemTags.DUSTS_COPPER).
                add(ModItems.COPPER_DUST.get());
        tag(CommonItemTags.DUSTS_IRON).
                add(ModItems.IRON_DUST.get());
        tag(CommonItemTags.DUSTS_GOLD).
                add(ModItems.GOLD_DUST.get());

        tag(Tags.Items.NUGGETS).
                addTag(CommonItemTags.NUGGETS_TIN);
        tag(CommonItemTags.NUGGETS_TIN).
                add(ModItems.TIN_NUGGET.get());

        tag(CommonItemTags.SILICON).
                add(ModItems.SILICON.get());

        tag(Tags.Items.INGOTS).
                addTag(CommonItemTags.INGOTS_TIN).
                addTag(CommonItemTags.INGOTS_STEEL).
                addTag(CommonItemTags.INGOTS_REDSTONE_ALLOY).
                addTag(CommonItemTags.INGOTS_ADVANCED_ALLOY).
                addTag(CommonItemTags.INGOTS_ENERGIZED_COPPER).
                addTag(CommonItemTags.INGOTS_ENERGIZED_GOLD);
        tag(CommonItemTags.INGOTS_TIN).
                add(ModItems.TIN_INGOT.get());
        tag(CommonItemTags.INGOTS_STEEL).
                add(ModItems.STEEL_INGOT.get());
        tag(CommonItemTags.INGOTS_REDSTONE_ALLOY).
                add(ModItems.REDSTONE_ALLOY_INGOT.get());
        tag(CommonItemTags.INGOTS_ADVANCED_ALLOY).
                add(ModItems.ADVANCED_ALLOY_INGOT.get());
        tag(CommonItemTags.INGOTS_ENERGIZED_COPPER).
                add(ModItems.ENERGIZED_COPPER_INGOT.get());
        tag(CommonItemTags.INGOTS_ENERGIZED_GOLD).
                add(ModItems.ENERGIZED_GOLD_INGOT.get());

        tag(CommonItemTags.PLATES).
                addTag(CommonItemTags.PLATES_TIN).
                addTag(CommonItemTags.PLATES_COPPER).
                addTag(CommonItemTags.PLATES_IRON).
                addTag(CommonItemTags.PLATES_GOLD).
                addTag(CommonItemTags.PLATES_ADVANCED_ALLOY).
                addTag(CommonItemTags.PLATES_ENERGIZED_COPPER).
                addTag(CommonItemTags.PLATES_ENERGIZED_GOLD);
        tag(CommonItemTags.PLATES_TIN).
                add(ModItems.TIN_PLATE.get());
        tag(CommonItemTags.PLATES_COPPER).
                add(ModItems.COPPER_PLATE.get());
        tag(CommonItemTags.PLATES_IRON).
                add(ModItems.IRON_PLATE.get());
        tag(CommonItemTags.PLATES_GOLD).
                add(ModItems.GOLD_PLATE.get());
        tag(CommonItemTags.PLATES_ADVANCED_ALLOY).
                add(ModItems.ADVANCED_ALLOY_PLATE.get());
        tag(CommonItemTags.PLATES_ENERGIZED_COPPER).
                add(ModItems.ENERGIZED_COPPER_PLATE.get());
        tag(CommonItemTags.PLATES_ENERGIZED_GOLD).
                add(ModItems.ENERGIZED_GOLD_PLATE.get());

        tag(CommonItemTags.GEARS).
                addTag(CommonItemTags.GEARS_IRON);
        tag(CommonItemTags.GEARS_IRON).
                add(ModItems.IRON_GEAR.get());

        tag(Tags.Items.RODS).
                addTag(CommonItemTags.RODS_IRON);
        tag(CommonItemTags.RODS_IRON).
                add(ModItems.IRON_ROD.get());

        tag(CommonItemTags.WIRES).
                addTag(CommonItemTags.WIRES_TIN).
                addTag(CommonItemTags.WIRES_COPPER).
                addTag(CommonItemTags.WIRES_GOLD).
                addTag(CommonItemTags.WIRES_ENERGIZED_COPPER).
                addTag(CommonItemTags.WIRES_ENERGIZED_GOLD);
        tag(CommonItemTags.WIRES_TIN).
                add(ModItems.TIN_WIRE.get());
        tag(CommonItemTags.WIRES_COPPER).
                add(ModItems.COPPER_WIRE.get());
        tag(CommonItemTags.WIRES_GOLD).
                add(ModItems.GOLD_WIRE.get());
        tag(CommonItemTags.WIRES_ENERGIZED_COPPER).
                add(ModItems.ENERGIZED_COPPER_WIRE.get());
        tag(CommonItemTags.WIRES_ENERGIZED_GOLD).
                add(ModItems.ENERGIZED_GOLD_WIRE.get());

        tag(Tags.Items.TOOLS).
                addTag(CommonItemTags.TOOLS_HAMMERS).
                addTag(CommonItemTags.TOOLS_CUTTERS);

        tag(CommonItemTags.TOOLS_HAMMERS).
                add(ModItems.WOODEN_HAMMER.get()).
                add(ModItems.STONE_HAMMER.get()).
                add(ModItems.IRON_HAMMER.get()).
                add(ModItems.GOLDEN_HAMMER.get()).
                add(ModItems.DIAMOND_HAMMER.get()).
                add(ModItems.NETHERITE_HAMMER.get());

        tag(CommonItemTags.TOOLS_CUTTERS).
                add(ModItems.CUTTER.get());
    }
}
