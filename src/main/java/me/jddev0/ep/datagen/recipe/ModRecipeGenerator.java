package me.jddev0.ep.datagen.recipe;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class ModRecipeGenerator extends RecipeProvider {
    public ModRecipeGenerator(HolderLookup.Provider registries, RecipeOutput exporter) {
        super(registries, exporter);
    }

    @Override
    public void buildRecipes() {
        buildCraftingRecipes();
        buildCookingRecipes();
        buildSmithingRecipes();
        buildPressMoldMakerRecipes();
        buildAlloyFurnaceRecipes();
        buildCompressorRecipes();
        buildCrusherRecipes();
        buildPulverizerRecipes();
        buildSawmillRecipes();
        buildPlantGrowthChamberRecipes();
        buildPlantGrowthChamberFertilizerRecipes();
        buildMetalPressRecipes();
        buildHeatGeneratorRecipes();
        buildThermalGeneratorRecipes();
        buildFluidFreezerRecipe();
        buildStoneLiquefierRecipes();
        buildStoneSolidifierRecipes();
        buildAssemblingMachineRecipes();
        buildFiltrationPlantRecipes();
        buildFluidTransposerRecipes();
        buildChargerRecipes();
        buildEnergizerRecipes();
        buildCrystalGrowthChamberRecipes();
    }

    private void buildCraftingRecipes() {
        buildItemIngredientsCraftingRecipes();

        buildFertilizerCraftingRecipes();

        buildUpgradeModuleCraftingRecipes();

        buildToolsCraftingRecipes();

        buildEnergyItemsCraftingRecipes();

        buildItemTransportCraftingRecipes();
        buildFluidTransportCraftingRecipes();
        buildEnergyTransportCraftingRecipes();

        buildMachineCraftingRecipes();

        buildMiscCraftingRecipes();

        buildCustomCraftingRecipes();
    }
    private void buildItemIngredientsCraftingRecipes() {
        add3x3UnpackingCraftingRecipe(has(EPBlocks.SAWDUST_BLOCK),
                ingredientOf(EPBlocks.SAWDUST_BLOCK), EPItems.SAWDUST,
                CraftingBookCategory.MISC, "", "_from_sawdust_block");
        add3x3PackingCraftingRecipe(has(CommonItemTags.DUSTS_WOOD),
                ingredientOf(CommonItemTags.DUSTS_WOOD), EPBlocks.SAWDUST_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        add3x3UnpackingCraftingRecipe(has(CommonItemTags.STORAGE_BLOCKS_SILICON),
                ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON), EPItems.SILICON,
                CraftingBookCategory.MISC, "", "_from_silicon_block");
        add3x3PackingCraftingRecipe(has(CommonItemTags.SILICON),
                ingredientOf(CommonItemTags.SILICON), EPBlocks.SILICON_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        addMetalNuggetCraftingRecipe(CommonItemTags.INGOTS_TIN, EPItems.TIN_NUGGET);
        addMetalIngotCraftingRecipes(CommonItemTags.NUGGETS_TIN, CommonItemTags.STORAGE_BLOCKS_TIN,
                EPItems.TIN_INGOT, "tin");

        add3x3PackingCraftingRecipe(has(CommonItemTags.INGOTS_TIN),
                ingredientOf(CommonItemTags.INGOTS_TIN), EPBlocks.TIN_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        add3x3UnpackingCraftingRecipe(has(CommonItemTags.STORAGE_BLOCKS_RAW_TIN),
                ingredientOf(CommonItemTags.STORAGE_BLOCKS_RAW_TIN), EPItems.RAW_TIN,
                CraftingBookCategory.MISC, "", "");
        add3x3PackingCraftingRecipe(has(CommonItemTags.RAW_MATERIALS_TIN),
                ingredientOf(CommonItemTags.RAW_MATERIALS_TIN), EPBlocks.RAW_TIN_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        addMetalPlateCraftingRecipe(CommonItemTags.INGOTS_TIN, EPItems.TIN_PLATE);
        addMetalPlateCraftingRecipe(ConventionalItemTags.COPPER_INGOTS, EPItems.COPPER_PLATE);
        addMetalPlateCraftingRecipe(ConventionalItemTags.IRON_INGOTS, EPItems.IRON_PLATE);
        addMetalPlateCraftingRecipe(ConventionalItemTags.GOLD_INGOTS, EPItems.GOLD_PLATE);

        addMetalWireCraftingRecipe(CommonItemTags.PLATES_TIN, EPItems.TIN_WIRE);
        addMetalWireCraftingRecipe(CommonItemTags.PLATES_COPPER, EPItems.COPPER_WIRE);
        addMetalWireCraftingRecipe(CommonItemTags.PLATES_GOLD, EPItems.GOLD_WIRE);

        addShapedCraftingRecipe(has(CommonItemTags.SILICON), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'Q', ingredientOf(ConventionalItemTags.QUARTZ_GEMS),
                'T', ingredientOf(CommonItemTags.INGOTS_TIN),
                'C', ingredientOf(ConventionalItemTags.COPPER_INGOTS)
        ), new String[] {
                " C ",
                "SQS",
                " T "
        }, new ItemStackTemplate(EPItems.BASIC_SOLAR_CELL), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(CommonItemTags.WIRES_COPPER), Map.of(
                'C', ingredientOf(CommonItemTags.WIRES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'S', ingredientOf(CommonItemTags.SILICON)
        ), new String[] {
                "RCR",
                "CIC",
                "SCS"
        }, new ItemStackTemplate(EPItems.BASIC_CIRCUIT), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_CIRCUIT), Map.of(
                'G', ingredientOf(CommonItemTags.WIRES_GOLD),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPItems.BASIC_CIRCUIT)
        ), new String[] {
                "GGG",
                "CBC",
                "CBC"
        }, new ItemStackTemplate(EPItems.BASIC_UPGRADE_MODULE), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ADVANCED_CIRCUIT), Map.of(
                'G', ingredientOf(CommonItemTags.WIRES_ENERGIZED_GOLD),
                'C', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'A', ingredientOf(EPItems.ADVANCED_CIRCUIT),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "GGG",
                "CBC",
                "CAC"
        }, new ItemStackTemplate(EPItems.ADVANCED_UPGRADE_MODULE), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE), Map.of(
                'g', ingredientOf(CommonItemTags.WIRES_ENERGIZED_GOLD),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'P', ingredientOf(EPItems.PROCESSING_UNIT),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "ggg",
                "GAG",
                "GPG"
        }, new ItemStackTemplate(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(ConventionalItemTags.IRON_INGOTS), Map.of(
                'i', ingredientOf(ConventionalItemTags.IRON_NUGGETS),
                'I', ingredientOf(ConventionalItemTags.IRON_INGOTS)
        ), new String[] {
                " i ",
                "iIi",
                " i "
        }, new ItemStackTemplate(EPItems.SAW_BLADE), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(CommonItemTags.SILICON), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'I', ingredientOf(ConventionalItemTags.IRON_INGOTS),
                'C', ingredientOf(ConventionalItemTags.COPPER_INGOTS)
        ), new String[] {
                "CIC",
                "ISI",
                "CIC"
        }, new ItemStackTemplate(EPBlocks.BASIC_MACHINE_FRAME_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(CommonItemTags.GEARS_IRON), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'I', ingredientOf(CommonItemTags.GEARS_IRON),
                'R', ingredientOf(CommonItemTags.RODS_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "IsR",
                "SBS",
                "RsI"
        }, new ItemStackTemplate(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(CommonItemTags.INGOTS_ENERGIZED_COPPER), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'A', ingredientOf(CommonItemTags.INGOTS_ADVANCED_ALLOY),
                'E', ingredientOf(CommonItemTags.INGOTS_ENERGIZED_COPPER),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "ESE",
                "AHA",
                "ESE"
        }, new ItemStackTemplate(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSC",
                "SAS",
                "CSC"
        }, new ItemStackTemplate(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), CraftingBookCategory.MISC);
    }
    private void buildFertilizerCraftingRecipes() {
        addShapedCraftingRecipe(has(Items.BONE_MEAL), Map.of(
                'B', ingredientOf(Items.BONE_MEAL),
                'D', ingredientOf(Items.DANDELION),
                'b', ingredientOf(Items.BLUE_ORCHID),
                'L', ingredientOf(ConventionalItemTags.LAPIS_GEMS),
                'A', ingredientOf(Items.ALLIUM),
                'P', ingredientOf(Items.POPPY)
        ), new String[] {
                "DBb",
                "BLB",
                "ABP"
        }, new ItemStackTemplate(EPItems.BASIC_FERTILIZER, 4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_FERTILIZER), Map.of(
                'B', ingredientOf(EPItems.BASIC_FERTILIZER),
                'S', ingredientOf(Items.SUGAR_CANE),
                'K', ingredientOf(Items.KELP),
                's', ingredientOf(Items.SUGAR),
                'b', ingredientOf(Items.BAMBOO),
                'W', ingredientOf(Items.WHEAT_SEEDS)
        ), new String[] {
                "SBK",
                "BsB",
                "bBW"
        }, new ItemStackTemplate(EPItems.GOOD_FERTILIZER, 4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.GOOD_FERTILIZER), Map.of(
                'G', ingredientOf(EPItems.GOOD_FERTILIZER),
                'M', ingredientOf(Items.RED_MUSHROOM),
                'S', ingredientOf(Items.SWEET_BERRIES),
                'r', ingredientOf(ConventionalItemTags.RED_DYES),
                'T', ingredientOf(Items.RED_TULIP),
                'R', ingredientOf(Items.ROSE_BUSH)
        ), new String[] {
                "MGS",
                "GrG",
                "TGR"
        }, new ItemStackTemplate(EPItems.ADVANCED_FERTILIZER, 4), CraftingBookCategory.MISC);
    }
    private void buildUpgradeModuleCraftingRecipes() {
        addShapedCraftingRecipe(has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CSC",
                "RBR",
                "CSC"
        }, new ItemStackTemplate(EPItems.SPEED_UPGRADE_MODULE_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.SPEED_UPGRADE_MODULE_1), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                's', ingredientOf(EPItems.SPEED_UPGRADE_MODULE_1)
        ), new String[] {
                "CSC",
                "RBR",
                "CsC"
        }, new ItemStackTemplate(EPItems.SPEED_UPGRADE_MODULE_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.SPEED_UPGRADE_MODULE_2), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                's', ingredientOf(EPItems.SPEED_UPGRADE_MODULE_2)
        ), new String[] {
                "CSC",
                "RAR",
                "CsC"
        }, new ItemStackTemplate(EPItems.SPEED_UPGRADE_MODULE_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.SPEED_UPGRADE_MODULE_3), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                's', ingredientOf(EPItems.SPEED_UPGRADE_MODULE_3)
        ), new String[] {
                "CSC",
                "RAR",
                "CsC"
        }, new ItemStackTemplate(EPItems.SPEED_UPGRADE_MODULE_4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.SPEED_UPGRADE_MODULE_4), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                's', ingredientOf(EPItems.SPEED_UPGRADE_MODULE_4)
        ), new String[] {
                "CSC",
                "rRr",
                "CsC"
        }, new ItemStackTemplate(EPItems.SPEED_UPGRADE_MODULE_5), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.SPEED_UPGRADE_MODULE_5), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                's', ingredientOf(EPItems.SPEED_UPGRADE_MODULE_5)
        ), new String[] {
                "CsC",
                "SRS",
                "CsC"
        }, new ItemStackTemplate(EPItems.SPEED_UPGRADE_MODULE_6), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'G', ingredientOf(ConventionalItemTags.GOLD_INGOTS),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CGC",
                "RBR",
                "CGC"
        }, new ItemStackTemplate(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'G', ingredientOf(ConventionalItemTags.GOLD_INGOTS),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1)
        ), new String[] {
                "CGC",
                "RBR",
                "CEC"
        }, new ItemStackTemplate(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'G', ingredientOf(ConventionalItemTags.GOLD_INGOTS),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2)
        ), new String[] {
                "CGC",
                "RAR",
                "CEC"
        }, new ItemStackTemplate(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'G', ingredientOf(ConventionalItemTags.GOLD_INGOTS),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3)
        ), new String[] {
                "CGC",
                "RAR",
                "CEC"
        }, new ItemStackTemplate(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'G', ingredientOf(ConventionalItemTags.GOLD_INGOTS),
                'r', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4)
        ), new String[] {
                "CGC",
                "rRr",
                "CEC"
        }, new ItemStackTemplate(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'G', ingredientOf(ConventionalItemTags.GOLD_INGOTS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5)
        ), new String[] {
                "CEC",
                "GRG",
                "CEC"
        }, new ItemStackTemplate(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_6), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'A', ingredientOf(ConventionalItemTags.AMETHYST_GEMS),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "GAG",
                "RBR",
                "GAG"
        }, new ItemStackTemplate(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_1), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'A', ingredientOf(ConventionalItemTags.AMETHYST_GEMS),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_1)
        ), new String[] {
                "GAG",
                "RBR",
                "GEG"
        }, new ItemStackTemplate(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_2), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'a', ingredientOf(ConventionalItemTags.AMETHYST_GEMS),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_2)
        ), new String[] {
                "GaG",
                "RAR",
                "GEG"
        }, new ItemStackTemplate(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_3), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'a', ingredientOf(ConventionalItemTags.AMETHYST_GEMS),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_3)
        ), new String[] {
                "GaG",
                "RAR",
                "GEG"
        }, new ItemStackTemplate(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_4), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'A', ingredientOf(ConventionalItemTags.AMETHYST_GEMS),
                'r', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_4)
        ), new String[] {
                "GAG",
                "rRr",
                "GEG"
        }, new ItemStackTemplate(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_5), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_5), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'A', ingredientOf(ConventionalItemTags.AMETHYST_GEMS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_5)
        ), new String[] {
                "GEG",
                "ARA",
                "GEG"
        }, new ItemStackTemplate(EPItems.ENERGY_PRODUCTION_UPGRADE_MODULE_6), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'c', ingredientOf(ItemTags.COALS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CTC",
                "cBc",
                "CTC"
        }, new ItemStackTemplate(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'c', ingredientOf(ItemTags.COALS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1)
        ), new String[] {
                "CTC",
                "cBc",
                "CEC"
        }, new ItemStackTemplate(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'c', ingredientOf(ItemTags.COALS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2)
        ), new String[] {
                "CTC",
                "cAc",
                "CEC"
        }, new ItemStackTemplate(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'c', ingredientOf(ItemTags.COALS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3)
        ), new String[] {
                "CTC",
                "cAc",
                "CEC"
        }, new ItemStackTemplate(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'c', ingredientOf(ItemTags.COALS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4)
        ), new String[] {
                "CTC",
                "cRc",
                "CEC"
        }, new ItemStackTemplate(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5)
        ), new String[] {
                "CEC",
                "TRT",
                "CEC"
        }, new ItemStackTemplate(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_6), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "GCG",
                "rRr",
                "GCG"
        }, new ItemStackTemplate(EPItems.DURATION_UPGRADE_MODULE_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.DURATION_UPGRADE_MODULE_1), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', ingredientOf(EPItems.DURATION_UPGRADE_MODULE_1)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStackTemplate(EPItems.DURATION_UPGRADE_MODULE_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.DURATION_UPGRADE_MODULE_2), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', ingredientOf(EPItems.DURATION_UPGRADE_MODULE_2)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStackTemplate(EPItems.DURATION_UPGRADE_MODULE_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.DURATION_UPGRADE_MODULE_3), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', ingredientOf(EPItems.DURATION_UPGRADE_MODULE_3)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStackTemplate(EPItems.DURATION_UPGRADE_MODULE_4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.DURATION_UPGRADE_MODULE_4), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', ingredientOf(EPItems.DURATION_UPGRADE_MODULE_4)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStackTemplate(EPItems.DURATION_UPGRADE_MODULE_5), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.DURATION_UPGRADE_MODULE_5), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', ingredientOf(EPItems.DURATION_UPGRADE_MODULE_5)
        ), new String[] {
                "GDG",
                "CRC",
                "GDG"
        }, new ItemStackTemplate(EPItems.DURATION_UPGRADE_MODULE_6), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ADVANCED_UPGRADE_MODULE), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "GRG",
                "RAR",
                "GRG"
        }, new ItemStackTemplate(EPItems.RANGE_UPGRADE_MODULE_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.RANGE_UPGRADE_MODULE_1), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'R', ingredientOf(EPItems.RANGE_UPGRADE_MODULE_1)
        ), new String[] {
                "GrG",
                "rAr",
                "GRG"
        }, new ItemStackTemplate(EPItems.RANGE_UPGRADE_MODULE_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.RANGE_UPGRADE_MODULE_2), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'R', ingredientOf(EPItems.RANGE_UPGRADE_MODULE_2)
        ), new String[] {
                "GrG",
                "rAr",
                "GRG"
        }, new ItemStackTemplate(EPItems.RANGE_UPGRADE_MODULE_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM)
        ), new String[] {
                "IFI",
                "RBR",
                "IFI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1)
        ), new String[] {
                "IFI",
                "RBR",
                "IEI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2)
        ), new String[] {
                "IFI",
                "RAR",
                "IEI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3)
        ), new String[] {
                "IFI",
                "RAR",
                "IEI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4)
        ), new String[] {
                "IFI",
                "rRr",
                "IEI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5)
        ), new String[] {
                "IEI",
                "FRF",
                "IEI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_6), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'b', ingredientOf(Items.BLAST_FURNACE)
        ), new String[] {
                "CSC",
                "RBR",
                "CbC"
        }, new ItemStackTemplate(EPItems.BLAST_FURNACE_UPGRADE_MODULE), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM)
        ), new String[] {
                "IRI",
                "FBF",
                "IRI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1)
        ), new String[] {
                "IRI",
                "FBF",
                "IEI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2)
        ), new String[] {
                "IRI",
                "FAF",
                "IEI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3)
        ), new String[] {
                "IRI",
                "FAF",
                "IEI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4)
        ), new String[] {
                "IrI",
                "FRF",
                "IEI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_5), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_5), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_5)
        ), new String[] {
                "IEI",
                "rRr",
                "IEI"
        }, new ItemStackTemplate(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_6), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                's', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'S', ingredientOf(Items.SMOKER)
        ), new String[] {
                "CsC",
                "RBR",
                "CSC"
        }, new ItemStackTemplate(EPItems.SMOKER_UPGRADE_MODULE), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'S', ingredientOf(CommonItemTags.SILICON),
                'b', ingredientOf(EPItems.BASIC_SOLAR_CELL),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "GSG",
                "bBb",
                "GSG"
        }, new ItemStackTemplate(EPItems.MOON_LIGHT_UPGRADE_MODULE_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.MOON_LIGHT_UPGRADE_MODULE_1), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BASIC_SOLAR_CELL),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'M', ingredientOf(EPItems.MOON_LIGHT_UPGRADE_MODULE_1)
        ), new String[] {
                "GSG",
                "BAB",
                "GMG"
        }, new ItemStackTemplate(EPItems.MOON_LIGHT_UPGRADE_MODULE_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.MOON_LIGHT_UPGRADE_MODULE_2), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BASIC_SOLAR_CELL),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'M', ingredientOf(EPItems.MOON_LIGHT_UPGRADE_MODULE_2)
        ), new String[] {
                "GSG",
                "BRB",
                "GMG"
        }, new ItemStackTemplate(EPItems.MOON_LIGHT_UPGRADE_MODULE_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'b', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "TbT",
                "IBI",
                "TbT"
        }, new ItemStackTemplate(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_1), Map.of(
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'b', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'i', ingredientOf(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_1)
        ), new String[] {
                "TbT",
                "IBI",
                "TiT"
        }, new ItemStackTemplate(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_2), Map.of(
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'i', ingredientOf(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_2)
        ), new String[] {
                "TFT",
                "IAI",
                "TiT"
        }, new ItemStackTemplate(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_3), Map.of(
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'i', ingredientOf(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_3)
        ), new String[] {
                "TFT",
                "IAI",
                "TiT"
        }, new ItemStackTemplate(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_4), Map.of(
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'i', ingredientOf(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_4)
        ), new String[] {
                "TET",
                "IRI",
                "TiT"
        }, new ItemStackTemplate(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_5), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_5), Map.of(
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'i', ingredientOf(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_5)
        ), new String[] {
                "TiT",
                "ERE",
                "TiT"
        }, new ItemStackTemplate(EPItems.ITEM_EJECTOR_UPGRADE_MODULE_6), CraftingBookCategory.MISC);
    }
    private void buildToolsCraftingRecipes() {
        addHammerCraftingRecipe(ItemTags.WOODEN_TOOL_MATERIALS, EPItems.WOODEN_HAMMER);
        addHammerCraftingRecipe(ItemTags.STONE_TOOL_MATERIALS, EPItems.STONE_HAMMER);
        addHammerCraftingRecipe(ItemTags.COPPER_TOOL_MATERIALS, EPItems.COPPER_HAMMER);
        addHammerCraftingRecipe(ItemTags.IRON_TOOL_MATERIALS, EPItems.IRON_HAMMER);
        addHammerCraftingRecipe(ItemTags.GOLD_TOOL_MATERIALS, EPItems.GOLDEN_HAMMER);
        addHammerCraftingRecipe(ItemTags.DIAMOND_TOOL_MATERIALS, EPItems.DIAMOND_HAMMER);

        addShapedCraftingRecipe(has(CommonItemTags.PLATES_IRON), Map.of(
                'i', ingredientOf(ConventionalItemTags.IRON_NUGGETS),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'S', ingredientOf(ConventionalItemTags.WOODEN_RODS)
        ), new String[] {
                "I I",
                " i ",
                "S S"
        }, new ItemStackTemplate(EPItems.CUTTER), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(ConventionalItemTags.IRON_INGOTS), Map.of(
                'i', ingredientOf(ConventionalItemTags.IRON_NUGGETS),
                'I', ingredientOf(ConventionalItemTags.IRON_INGOTS)
        ), new String[] {
                " I ",
                " iI",
                "i  "
        }, new ItemStackTemplate(EPItems.WRENCH), CraftingBookCategory.MISC);
    }
    private void buildEnergyItemsCraftingRecipes() {
        addShapedCraftingRecipe(has(ConventionalItemTags.COPPER_INGOTS), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'c', ingredientOf(ItemTags.COALS)
        ), new String[] {
                "T T",
                "CRC",
                "CcC"
        }, new ItemStackTemplate(EPItems.BATTERY_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BATTERY_1), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'c', ingredientOf(ItemTags.COALS),
                'B', ingredientOf(EPItems.BATTERY_1)
        ), new String[] {
                "T T",
                "CBC",
                "IcI"
        }, new ItemStackTemplate(EPItems.BATTERY_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BATTERY_2), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BATTERY_2)
        ), new String[] {
                "T T",
                "BCB",
                "CSC"
        }, new ItemStackTemplate(EPItems.BATTERY_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BATTERY_3), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BATTERY_3)
        ), new String[] {
                "T T",
                "CBC",
                "SIS"
        }, new ItemStackTemplate(EPItems.BATTERY_4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BATTERY_4), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BATTERY_4)
        ), new String[] {
                "T T",
                "BSB",
                "IRI"
        }, new ItemStackTemplate(EPItems.BATTERY_5), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BATTERY_5), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'B', ingredientOf(EPItems.BATTERY_5)
        ), new String[] {
                "T T",
                "EBE",
                "EBE"
        }, new ItemStackTemplate(EPItems.BATTERY_6), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BATTERY_6), Map.of(
                'G', ingredientOf(ConventionalItemTags.GOLD_NUGGETS),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BATTERY_6)
        ), new String[] {
                "G G",
                "EBE",
                "SBS"
        }, new ItemStackTemplate(EPItems.BATTERY_7), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BATTERY_7), Map.of(
                'G', ingredientOf(ConventionalItemTags.GOLD_NUGGETS),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'B', ingredientOf(EPItems.BATTERY_7)
        ), new String[] {
                "G G",
                "EBE",
                "ABA"
        }, new ItemStackTemplate(EPItems.BATTERY_8), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.COAL_ENGINE_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'c', ingredientOf(CommonItemTags.PLATES_COPPER),
                'C', ingredientOf(EPBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "SIS",
                "RCR",
                "cIc"
        }, new ItemStackTemplate(EPItems.INVENTORY_COAL_ENGINE), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.CHARGER_ITEM), Map.of(
                'c', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(EPBlocks.CHARGER_ITEM)
        ), new String[] {
                "SIS",
                "RCR",
                "cIc"
        }, new ItemStackTemplate(EPItems.INVENTORY_CHARGER), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.TELEPORTER_ITEM), Map.of(
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'c', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'T', ingredientOf(EPBlocks.TELEPORTER_ITEM)
        ), new String[] {
                "CcC",
                "RTR",
                "GcG"
        }, new ItemStackTemplate(EPItems.INVENTORY_TELEPORTER), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BATTERY_3), Map.of(
                'b', ingredientOf(EPItems.BASIC_CIRCUIT),
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'B', ingredientOf(EPItems.BATTERY_3)
        ), new String[] {
                "S S",
                "bRb",
                "CBC"
        }, new ItemStackTemplate(EPItems.ENERGY_ANALYZER), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BATTERY_3), Map.of(
                'b', ingredientOf(EPItems.BASIC_CIRCUIT),
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'L', ingredientOf(ConventionalItemTags.LAPIS_GEMS),
                'B', ingredientOf(EPItems.BATTERY_3)
        ), new String[] {
                "S S",
                "bLb",
                "CBC"
        }, new ItemStackTemplate(EPItems.FLUID_ANALYZER), CraftingBookCategory.MISC);
    }
    private void buildItemTransportCraftingRecipes() {
        addShapedCraftingRecipe(has(CommonItemTags.PLATES_IRON), Map.of(
                'L', ingredientOf(ConventionalItemTags.LEATHERS),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS)
        ), new String[] {
                "   ",
                "LLL",
                "IRI"
        }, new ItemStackTemplate(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM, 6), CraftingBookCategory.MISC,
                "item_conveyor_belt", "_from_leather");

        addShapedCraftingRecipe(has(CommonItemTags.PLATES_IRON), Map.of(
                'K', ingredientOf(Items.DRIED_KELP),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS)
        ), new String[] {
                "   ",
                "KKK",
                "IRI"
        }, new ItemStackTemplate(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM, 6), CraftingBookCategory.MISC,
                "item_conveyor_belt", "_from_dried_kelp");

        addShapedCraftingRecipe(has(CommonItemTags.INGOTS_STEEL), Map.of(
                'S', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM)
        ), new String[] {
                "RIR",
                "SBS",
                "RIR"
        }, new ItemStackTemplate(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(CommonItemTags.GEARS_IRON), Map.of(
                'G', ingredientOf(CommonItemTags.GEARS_IRON),
                'R', ingredientOf(CommonItemTags.RODS_IRON),
                'r', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM)
        ), new String[] {
                "GRG",
                "rFr",
                "GRG"
        }, new ItemStackTemplate(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'C', ingredientOf(ConventionalItemTags.NORMAL_COBBLESTONES),
                'c', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM),
                'H', ingredientOf(Items.HOPPER)
        ), new String[] {
                "CCC",
                "CHC",
                "CcC"
        }, new ItemStackTemplate(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', ingredientOf(Items.BRICKS),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStackTemplate(EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStackTemplate(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientOf(ConventionalItemTags.NORMAL_COBBLESTONES),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'R', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_REDSTONE),
                'L', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CRC",
                "ILI",
                "CRC"
        }, new ItemStackTemplate(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', ingredientOf(Items.BRICKS),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStackTemplate(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStackTemplate(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientOf(ConventionalItemTags.NORMAL_COBBLESTONES),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'l', ingredientOf(Items.LEVER),
                'L', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "ClC",
                "ILI",
                "CRC"
        }, new ItemStackTemplate(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', ingredientOf(Items.BRICKS),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStackTemplate(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStackTemplate(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientOf(ConventionalItemTags.NORMAL_COBBLESTONES),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'L', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CIC",
                "ILI",
                "CRC"
        }, new ItemStackTemplate(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', ingredientOf(Items.BRICKS),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStackTemplate(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStackTemplate(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientOf(ConventionalItemTags.NORMAL_COBBLESTONES),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'L', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CRC",
                "ILI",
                "CIC"
        }, new ItemStackTemplate(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', ingredientOf(Items.BRICKS),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStackTemplate(EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStackTemplate(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(CommonItemTags.PLATES_IRON), Map.of(
                'B', ingredientOf(ConventionalItemTags.WOODEN_BARRELS),
                'I', ingredientOf(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "III",
                "IBI",
                "III"
        }, new ItemStackTemplate(EPBlocks.ITEM_SILO_TINY_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ITEM_SILO_TINY_ITEM), Map.of(
                'S', ingredientOf(EPBlocks.ITEM_SILO_TINY_ITEM),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "IiI",
                "iSi",
                "IiI"
        }, new ItemStackTemplate(EPBlocks.ITEM_SILO_SMALL_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ITEM_SILO_SMALL_ITEM), Map.of(
                'S', ingredientOf(EPBlocks.ITEM_SILO_SMALL_ITEM),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.PLATES_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL)
        ), new String[] {
                "IsI",
                "iSi",
                "IsI"
        }, new ItemStackTemplate(EPBlocks.ITEM_SILO_MEDIUM_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ITEM_SILO_MEDIUM_ITEM), Map.of(
                'S', ingredientOf(EPBlocks.ITEM_SILO_MEDIUM_ITEM),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL)
        ), new String[] {
                "IsI",
                "sSs",
                "IsI"
        }, new ItemStackTemplate(EPBlocks.ITEM_SILO_LARGE_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ITEM_SILO_LARGE_ITEM), Map.of(
                'S', ingredientOf(EPBlocks.ITEM_SILO_LARGE_ITEM),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON)
        ), new String[] {
                "III",
                "ISI",
                "III"
        }, new ItemStackTemplate(EPBlocks.ITEM_SILO_GIANT_ITEM), CraftingBookCategory.MISC);
    }
    private void buildFluidTransportCraftingRecipes() {
        addShapedCraftingRecipe(has(CommonItemTags.PLATES_IRON), Map.of(
                        'I', ingredientOf(ConventionalItemTags.IRON_INGOTS),
                        'i', ingredientOf(CommonItemTags.PLATES_IRON)
                ), new String[] {
                        "IiI",
                        "IiI",
                        "IiI"
                }, new ItemStackTemplate(EPBlocks.IRON_FLUID_PIPE_ITEM, 12), CraftingBookCategory.MISC,
                "", "", "iron_");

        addShapedCraftingRecipe(has(CommonItemTags.PLATES_GOLD), Map.of(
                'G', ingredientOf(ConventionalItemTags.GOLD_INGOTS),
                'g', ingredientOf(CommonItemTags.PLATES_GOLD)
        ), new String[] {
                "GgG",
                "GgG",
                "GgG"
        }, new ItemStackTemplate(EPBlocks.GOLDEN_FLUID_PIPE_ITEM, 12), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(CommonItemTags.PLATES_IRON), Map.of(
                'G', ingredientOf(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'I', ingredientOf(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "IGI",
                "IGI",
                "IGI"
        }, new ItemStackTemplate(EPBlocks.FLUID_TANK_SMALL_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.FLUID_TANK_SMALL_ITEM), Map.of(
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'S', ingredientOf(CommonItemTags.INGOTS_STEEL)
        ), new String[] {
                "SFS",
                "SFS",
                "SFS"
        }, new ItemStackTemplate(EPBlocks.FLUID_TANK_MEDIUM_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.FLUID_TANK_MEDIUM_ITEM), Map.of(
                'F', ingredientOf(EPBlocks.FLUID_TANK_MEDIUM_ITEM),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON)
        ), new String[] {
                "IFI",
                "IFI",
                "IFI"
        }, new ItemStackTemplate(EPBlocks.FLUID_TANK_LARGE_ITEM), CraftingBookCategory.MISC);
    }
    private void buildEnergyTransportCraftingRecipes() {
        addBasicCableCraftingRecipes(CommonItemTags.INGOTS_TIN, CommonItemTags.WIRES_TIN,
                new ItemStackTemplate(EPBlocks.TIN_CABLE_ITEM, 9));

        addBasicCableCraftingRecipes(ConventionalItemTags.COPPER_INGOTS, CommonItemTags.WIRES_COPPER,
                new ItemStackTemplate(EPBlocks.COPPER_CABLE_ITEM, 6));
        addBasicCableCraftingRecipes(ConventionalItemTags.GOLD_INGOTS, CommonItemTags.WIRES_GOLD,
                new ItemStackTemplate(EPBlocks.GOLD_CABLE_ITEM, 6));

        addBasicCableCraftingRecipes(CommonItemTags.INGOTS_ENERGIZED_COPPER, CommonItemTags.WIRES_ENERGIZED_COPPER,
                new ItemStackTemplate(EPBlocks.ENERGIZED_COPPER_CABLE_ITEM, 3));
        addBasicCableCraftingRecipes(CommonItemTags.INGOTS_ENERGIZED_GOLD, CommonItemTags.WIRES_ENERGIZED_GOLD,
                new ItemStackTemplate(EPBlocks.ENERGIZED_GOLD_CABLE_ITEM, 3));

        addShapedCraftingRecipe(has(EPItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'I', ingredientOf(EPItems.CABLE_INSULATOR),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX)
        ), new String[] {
                "ICI",
                "ICI",
                "ICI"
        }, new ItemStackTemplate(EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'M', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSI",
                "RMR",
                "CSI"
        }, new ItemStackTemplate(EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'M', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSI",
                "SMR",
                "CRI"
        }, new ItemStackTemplate(EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'M', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CRI",
                "SMS",
                "CRI"
        }, new ItemStackTemplate(EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                        'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                        'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                        'M', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                        'T', ingredientOf(EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM)
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStackTemplate(EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM), CraftingBookCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                        'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                        'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                        'M', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                        'T', ingredientOf(EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM)
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStackTemplate(EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM), CraftingBookCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                        'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                        'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                        'M', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                        'T', ingredientOf(EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM)
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStackTemplate(EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM), CraftingBookCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStackTemplate(EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStackTemplate(EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStackTemplate(EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStackTemplate(EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStackTemplate(EPBlocks.EHV_TRANSFORMER_3_TO_3_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStackTemplate(EPBlocks.EHV_TRANSFORMER_N_TO_1_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(EPItems.BASIC_CIRCUIT),
                'M', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSI",
                "BMB",
                "CSI"
        }, new ItemStackTemplate(EPBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'B', ingredientOf(EPItems.BASIC_CIRCUIT),
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'M', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM)
        ), new String[] {
                "CTI",
                "BMB",
                "CTI"
        }, new ItemStackTemplate(EPBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'A', ingredientOf(EPItems.ADVANCED_CIRCUIT),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM)
        ), new String[] {
                "GTG",
                "AMA",
                "GTG"
        }, new ItemStackTemplate(EPBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'R', ingredientOf(EPItems.PROCESSING_UNIT),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM)
        ), new String[] {
                "CTC",
                "RMR",
                "CTC"
        }, new ItemStackTemplate(EPBlocks.CONFIGURABLE_EHV_TRANSFORMER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPItems.BATTERY_5),
                'M', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CBC",
                "BMB",
                "SBS"
        }, new ItemStackTemplate(EPBlocks.BATTERY_BOX_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'B', ingredientOf(EPItems.BATTERY_8),
                'M', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "EBE",
                "BMB",
                "BSB"
        }, new ItemStackTemplate(EPBlocks.ADVANCED_BATTERY_BOX_ITEM), CraftingBookCategory.MISC);

        addShapelessCraftingRecipe(has(EPBlocks.BATTERY_BOX_ITEM), List.of(
                ingredientOf(EPBlocks.BATTERY_BOX_ITEM),
                ingredientOf(Items.MINECART)
        ), new ItemStackTemplate(EPItems.BATTERY_BOX_MINECART), CraftingBookCategory.MISC);

        addShapelessCraftingRecipe(has(EPBlocks.ADVANCED_BATTERY_BOX_ITEM), List.of(
                ingredientOf(EPBlocks.ADVANCED_BATTERY_BOX_ITEM),
                ingredientOf(Items.MINECART)
        ), new ItemStackTemplate(EPItems.ADVANCED_BATTERY_BOX_MINECART), CraftingBookCategory.MISC);
    }
    private void buildMachineCraftingRecipes() {
        addShapedCraftingRecipe(has(Items.SMOOTH_STONE), Map.of(
                'S', ingredientOf(Items.SMOOTH_STONE),
                'B', ingredientOf(Items.BRICKS),
                's', ingredientOf(ItemTags.SHOVELS)
        ), new String[] {
                "BBB",
                "BsB",
                "SSS"
        }, new ItemStackTemplate(EPBlocks.PRESS_MOLD_MAKER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(Items.FURNACE), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(Items.BRICKS),
                'F', ingredientOf(Items.FURNACE)
        ), new String[] {
                "III",
                "FIF",
                "BBB"
        }, new ItemStackTemplate(EPBlocks.ALLOY_FURNACE_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'b', ingredientOf(EPItems.BASIC_CIRCUIT),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'T', ingredientOf(Items.CRAFTING_TABLE)
        ), new String[] {
                "CTC",
                "bBb",
                "ITI"
        }, new ItemStackTemplate(EPBlocks.AUTO_CRAFTER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME), Map.of(
                'c', ingredientOf(EPItems.ADVANCED_CIRCUIT),
                'P', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'a', ingredientOf(EPBlocks.AUTO_CRAFTER_ITEM)
        ), new String[] {
                "GaG",
                "cAc",
                "PaP"
        }, new ItemStackTemplate(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientOf(Items.STONECUTTER)
        ), new String[] {
                "CsC",
                "SBS",
                "CIC"
        }, new ItemStackTemplate(EPBlocks.CRUSHER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'c', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'C', ingredientOf(EPBlocks.CRUSHER_ITEM)
        ), new String[] {
                "aCa",
                "FAF",
                "cCc"
        }, new ItemStackTemplate(EPBlocks.ADVANCED_CRUSHER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientOf(Items.STONECUTTER)
        ), new String[] {
                "ISI",
                "SBS",
                "ISI"
        }, new ItemStackTemplate(EPBlocks.PULVERIZER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'P', ingredientOf(EPBlocks.PULVERIZER_ITEM)
        ), new String[] {
                "aPa",
                "FAF",
                "CPC"
        }, new ItemStackTemplate(EPBlocks.ADVANCED_PULVERIZER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(EPItems.SAW_BLADE),
                's', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "sSs",
                "CBC",
                "sIs"
        }, new ItemStackTemplate(EPBlocks.SAWMILL_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'P', ingredientOf(Items.PISTON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "IPI",
                "PBP",
                "IPI"
        }, new ItemStackTemplate(EPBlocks.COMPRESSOR_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'P', ingredientOf(Items.PISTON),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'S', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SPS",
                "IBI",
                "SIS"
        }, new ItemStackTemplate(EPBlocks.METAL_PRESS_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'I', ingredientOf(CommonItemTags.GEARS_IRON),
                'i', ingredientOf(CommonItemTags.RODS_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'P', ingredientOf(EPBlocks.PRESS_MOLD_MAKER_ITEM)
        ), new String[] {
                "IPI",
                "iBi",
                "IPI"
        }, new ItemStackTemplate(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientOf(Items.STONECUTTER)
        ), new String[] {
                "CSC",
                "sBs",
                "ISI"
        }, new ItemStackTemplate(EPBlocks.AUTO_STONECUTTER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'G', ingredientOf(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'D', ingredientOf(Items.DIRT),
                'W', ingredientOf(Items.WATER_BUCKET),
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SWS",
                "GBG",
                "IDI"
        }, new ItemStackTemplate(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'D', ingredientOf(Items.DISPENSER)
        ), new String[] {
                "IDS",
                "DBD",
                "SDC"
        }, new ItemStackTemplate(EPBlocks.BLOCK_PLACER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'B', ingredientOf(EPItems.BASIC_CIRCUIT),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iRi",
                "BHB",
                "SsS"
        }, new ItemStackTemplate(EPBlocks.ASSEMBLING_MACHINE_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'B', ingredientOf(EPItems.BASIC_CIRCUIT),
                'S', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'A', ingredientOf(EPBlocks.ALLOY_FURNACE_ITEM)
        ), new String[] {
                "SAS",
                "BHB",
                "SAS"
        }, new ItemStackTemplate(EPBlocks.INDUCTION_SMELTER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SIS",
                "IBI",
                "CFC"
        }, new ItemStackTemplate(EPBlocks.FLUID_FILLER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'I', ingredientOf(CommonItemTags.GEARS_IRON),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "STS",
                "IHI",
                "CFC"
        }, new ItemStackTemplate(EPBlocks.FLUID_FREEZER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SiS",
                "IHI",
                "CFC"
        }, new ItemStackTemplate(EPBlocks.STONE_LIQUEFIER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iSi",
                "FHF",
                "CIC"
        }, new ItemStackTemplate(EPBlocks.STONE_SOLIDIFIER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'i', ingredientOf(CommonItemTags.PLATES_IRON),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'D', ingredientOf(EPBlocks.FLUID_DRAINER_ITEM),
                'F', ingredientOf(EPBlocks.FLUID_FILLER_ITEM),
                'f', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CiC",
                "DHF",
                "IfI"
        }, new ItemStackTemplate(EPBlocks.FLUID_TRANSPOSER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'B', ingredientOf(Items.IRON_BARS),
                'f', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iBi",
                "FHF",
                "IfI"
        }, new ItemStackTemplate(EPBlocks.FILTRATION_PLANT_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CFC",
                "IBI",
                "SIS"
        }, new ItemStackTemplate(EPBlocks.FLUID_DRAINER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'P', ingredientOf(Items.PISTON),
                'p', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM)
        ), new String[] {
                "RPR",
                "FBF",
                "IpI"
        }, new ItemStackTemplate(EPBlocks.FLUID_PUMP_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'F', ingredientOf(EPBlocks.FLUID_PUMP_ITEM),
                'f', ingredientOf(EPBlocks.FLUID_TANK_LARGE_ITEM)
        ), new String[] {
                "GFG",
                "fAf",
                "aFa"
        }, new ItemStackTemplate(EPBlocks.ADVANCED_FLUID_PUMP_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(ConventionalItemTags.STORAGE_BLOCKS_IRON), Map.of(
                'i', ingredientOf(CommonItemTags.PLATES_IRON),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'B', ingredientOf(Items.IRON_BARS),
                'G', ingredientOf(ConventionalItemTags.GLASS_BLOCKS_COLORLESS)
        ), new String[] {
                "IBI",
                "iGi",
                "IiI"
        }, new ItemStackTemplate(EPBlocks.DRAIN_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON)
        ), new String[] {
                "sCs",
                "IBI",
                "CSC"
        }, new ItemStackTemplate(EPBlocks.CHARGER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'C', ingredientOf(EPBlocks.CHARGER_ITEM)
        ), new String[] {
                "SGS",
                "aAa",
                "GCG"
        }, new ItemStackTemplate(EPBlocks.ADVANCED_CHARGER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON)
        ), new String[] {
                "CSC",
                "IBI",
                "sCs"
        }, new ItemStackTemplate(EPBlocks.UNCHARGER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'U', ingredientOf(EPBlocks.UNCHARGER_ITEM)
        ), new String[] {
                "GUG",
                "aAa",
                "SGS"
        }, new ItemStackTemplate(EPBlocks.ADVANCED_UNCHARGER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.CHARGER_ITEM), Map.of(
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'c', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.PLATES_IRON),
                'S', ingredientOf(CommonItemTags.SILICON),
                'H', ingredientOf(EPBlocks.CHARGER_ITEM)
        ), new String[] {
                "cCc",
                "SHS",
                "iIi"
        }, new ItemStackTemplate(EPBlocks.MINECART_CHARGER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_CHARGER_ITEM), Map.of(
                'G', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_GOLD),
                'g', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'c', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'H', ingredientOf(EPBlocks.ADVANCED_CHARGER_ITEM)
        ), new String[] {
                "gGg",
                "SHS",
                "cCc"
        }, new ItemStackTemplate(EPBlocks.ADVANCED_MINECART_CHARGER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.UNCHARGER_ITEM), Map.of(
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'c', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.PLATES_IRON),
                'S', ingredientOf(CommonItemTags.SILICON),
                'U', ingredientOf(EPBlocks.UNCHARGER_ITEM)
        ), new String[] {
                "iIi",
                "SUS",
                "cCc"
        }, new ItemStackTemplate(EPBlocks.MINECART_UNCHARGER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_UNCHARGER_ITEM), Map.of(
                'G', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_GOLD),
                'g', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'c', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'U', ingredientOf(EPBlocks.ADVANCED_UNCHARGER_ITEM)
        ), new String[] {
                "cCc",
                "SUS",
                "gGg"
        }, new ItemStackTemplate(EPBlocks.ADVANCED_MINECART_UNCHARGER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.BASIC_SOLAR_CELL), Map.of(
                'G', ingredientOf(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'I', ingredientOf(ConventionalItemTags.IRON_INGOTS),
                'C', ingredientOf(ConventionalItemTags.COPPER_INGOTS),
                'B', ingredientOf(EPItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "BRB",
                "ICI"
        }, new ItemStackTemplate(EPBlocks.SOLAR_PANEL_ITEM_1), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.SOLAR_PANEL_ITEM_1), Map.of(
                'G', ingredientOf(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'C', ingredientOf(ConventionalItemTags.COPPER_INGOTS),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'S', ingredientOf(EPBlocks.SOLAR_PANEL_ITEM_1),
                'B', ingredientOf(EPItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SBS",
                "CRC"
        }, new ItemStackTemplate(EPBlocks.SOLAR_PANEL_ITEM_2), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.SOLAR_PANEL_ITEM_2), Map.of(
                'G', ingredientOf(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                's', ingredientOf(CommonItemTags.SILICON),
                'S', ingredientOf(EPBlocks.SOLAR_PANEL_ITEM_2),
                'B', ingredientOf(EPItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SBS",
                "sRs"
        }, new ItemStackTemplate(EPBlocks.SOLAR_PANEL_ITEM_3), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.SOLAR_PANEL_ITEM_3), Map.of(
                'G', ingredientOf(ConventionalItemTags.GLASS_PANES_COLORLESS),
                's', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'S', ingredientOf(EPBlocks.SOLAR_PANEL_ITEM_3),
                'A', ingredientOf(EPItems.ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SsS",
                "ARA"
        }, new ItemStackTemplate(EPBlocks.SOLAR_PANEL_ITEM_4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.SOLAR_PANEL_ITEM_4), Map.of(
                'G', ingredientOf(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'a', ingredientOf(ConventionalItemTags.AMETHYST_GEMS),
                'E', ingredientOf(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                'S', ingredientOf(EPBlocks.SOLAR_PANEL_ITEM_4),
                'A', ingredientOf(EPItems.ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SaS",
                "AEA"
        }, new ItemStackTemplate(EPBlocks.SOLAR_PANEL_ITEM_5), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.SOLAR_PANEL_ITEM_5), Map.of(
                'G', ingredientOf(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'A', ingredientOf(ConventionalItemTags.AMETHYST_GEMS),
                'E', ingredientOf(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                'S', ingredientOf(EPBlocks.SOLAR_PANEL_ITEM_5),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SAS",
                "RER"
        }, new ItemStackTemplate(EPBlocks.SOLAR_PANEL_ITEM_6), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'F', ingredientOf(Items.FURNACE)
        ), new String[] {
                "ISI",
                "CBC",
                "IFI"
        }, new ItemStackTemplate(EPBlocks.COAL_ENGINE_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(Items.REDSTONE_LAMP), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(ConventionalItemTags.COPPER_INGOTS),
                'R', ingredientOf(Items.REDSTONE_LAMP)
        ), new String[] {
                "CSC",
                "SRS",
                "CSC"
        }, new ItemStackTemplate(EPBlocks.POWERED_LAMP_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'F', ingredientOf(Items.FURNACE)
        ), new String[] {
                "CFC",
                "SBS",
                "IFI"
        }, new ItemStackTemplate(EPBlocks.POWERED_FURNACE_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'P', ingredientOf(EPBlocks.POWERED_FURNACE_ITEM)
        ), new String[] {
                "GPG",
                "SAS",
                "aPa"
        }, new ItemStackTemplate(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "RaR",
                "GAG",
                "ECE"
        }, new ItemStackTemplate(EPBlocks.LIGHTNING_GENERATOR_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'R', ingredientOf(ConventionalItemTags.REDSTONE_DUSTS),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "RaR",
                "GAG",
                "ESE"
        }, new ItemStackTemplate(EPBlocks.ENERGIZER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'I', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'R', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_REDSTONE),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "aRa",
                "IAI",
                "ECE"
        }, new ItemStackTemplate(EPBlocks.CHARGING_STATION_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.COAL_ENGINE_ITEM), Map.of(
                's', ingredientOf(CommonItemTags.SILICON),
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'c', ingredientOf(CommonItemTags.WIRES_COPPER),
                'C', ingredientOf(EPBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "cSc",
                "sCs",
                "cSc"
        }, new ItemStackTemplate(EPBlocks.HEAT_GENERATOR_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'c', ingredientOf(CommonItemTags.PLATES_COPPER),
                'C', ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'E', ingredientOf(EPBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "cHc",
                "SES",
                "CFC"
        }, new ItemStackTemplate(EPBlocks.THERMAL_GENERATOR_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'a', ingredientOf(Items.AMETHYST_BLOCK),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'P', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "aPa",
                "EAE",
                "aPa"
        }, new ItemStackTemplate(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'P', ingredientOf(EPItems.PROCESSING_UNIT),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(ConventionalItemTags.AMETHYST_GEMS),
                'R', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CAC",
                "PRP",
                "aEa"
        }, new ItemStackTemplate(EPBlocks.WEATHER_CONTROLLER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'P', ingredientOf(EPItems.PROCESSING_UNIT),
                'c', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'C', ingredientOf(Items.CLOCK),
                'A', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'R', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "cCc",
                "PRP",
                "AEA"
        }, new ItemStackTemplate(EPBlocks.TIME_CONTROLLER_ITEM), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'T', ingredientOf(EPItems.TELEPORTER_PROCESSING_UNIT),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'A', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'R', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CEC",
                "TRT",
                "ASA"
        }, new ItemStackTemplate(EPBlocks.TELEPORTER_ITEM), CraftingBookCategory.MISC);
    }
    private void buildMiscCraftingRecipes() {
        addShapelessCraftingRecipe(InventoryChangeTrigger.TriggerInstance.hasItems(
                Items.BOOK,
                EPBlocks.BASIC_MACHINE_FRAME_ITEM
        ), List.of(
                ingredientOf(Items.BOOK),
                ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new ItemStackTemplate(EPItems.ENERGIZED_POWER_BOOK), CraftingBookCategory.MISC);

        addShapelessCraftingRecipe(has(CommonItemTags.DUSTS_WOOD), List.of(
                ingredientOf(CommonItemTags.DUSTS_WOOD),
                ingredientOf(CommonItemTags.DUSTS_WOOD),
                ingredientOf(CommonItemTags.DUSTS_WOOD),
                ingredientOf(Items.WATER_BUCKET)
        ), new ItemStackTemplate(Items.PAPER, 2), CraftingBookCategory.MISC, "", "_from_sawdust");

        addShapedCraftingRecipe(has(CommonItemTags.DUSTS_CHARCOAL), Map.of(
                'P', ingredientOf(Items.PAPER),
                'C', ingredientOf(CommonItemTags.DUSTS_CHARCOAL),
                'I', ingredientOf(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "PCP",
                "CIC",
                "PCP"
        }, new ItemStackTemplate(EPItems.CHARCOAL_FILTER), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(has(EPItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'A', ingredientOf(ConventionalItemTags.AMETHYST_GEMS),
                'E', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'e', ingredientOf(ConventionalItemTags.ENDER_PEARLS)
        ), new String[] {
                "AEA",
                "EeE",
                "AEA"
        }, new ItemStackTemplate(EPItems.TELEPORTER_MATRIX), CraftingBookCategory.MISC);
    }
    private void buildCustomCraftingRecipes() {
        addCustomCraftingRecipe(TeleporterMatrixSettingsCopyRecipe::new, "teleporter_matrix_settings_copy");
    }

    private void buildCookingRecipes() {
        addBlastingAndSmeltingRecipes(CommonItemTags.RAW_MATERIALS_TIN, new ItemStackTemplate(EPItems.TIN_INGOT), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "raw_tin");
        addBlastingAndSmeltingRecipes(CommonItemTags.ORES_TIN, new ItemStackTemplate(EPItems.TIN_INGOT), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "tin_ores");

        addBlastingAndSmeltingRecipes(CommonItemTags.DUSTS_TIN, new ItemStackTemplate(EPItems.TIN_INGOT), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "tin_dust");
        addBlastingAndSmeltingRecipes(CommonItemTags.DUSTS_COPPER, new ItemStackTemplate(Items.COPPER_INGOT), CookingBookCategory.MISC,
                100, .7f, "copper_ingot", "copper_dust");
        addBlastingAndSmeltingRecipes(CommonItemTags.DUSTS_IRON, new ItemStackTemplate(Items.IRON_INGOT), CookingBookCategory.MISC,
                100, .7f, "iron_ingot", "iron_dust");
        addBlastingAndSmeltingRecipes(CommonItemTags.DUSTS_GOLD, new ItemStackTemplate(Items.GOLD_INGOT), CookingBookCategory.MISC,
                100, .7f, "gold_ingot", "gold_dust");

        addBlastingAndSmeltingRecipes(EPItems.COPPER_HAMMER, new ItemStackTemplate(Items.COPPER_NUGGET), CookingBookCategory.MISC,
                100, .1f, "copper_nugget", "copper_hammer");
        addBlastingAndSmeltingRecipes(EPItems.IRON_HAMMER, new ItemStackTemplate(Items.IRON_NUGGET), CookingBookCategory.MISC,
                100, .1f, "iron_nugget", "iron_hammer");
        addBlastingAndSmeltingRecipes(EPItems.GOLDEN_HAMMER, new ItemStackTemplate(Items.GOLD_NUGGET), CookingBookCategory.MISC,
                100, .1f, "gold_nugget", "golden_hammer");

        addBlastingAndSmeltingRecipes(ConventionalItemTags.QUARTZ_GEMS, new ItemStackTemplate(EPItems.SILICON), CookingBookCategory.MISC,
                250, 4.1f, "silicon", "nether_quartz");

        addSmeltingRecipe(EPBlocks.SAWDUST_BLOCK_ITEM, new ItemStackTemplate(Items.CHARCOAL), CookingBookCategory.MISC,
                200, .15f, null, "sawdust_block");

        addSmeltingRecipe(EPItems.RAW_GEAR_PRESS_MOLD, new ItemStackTemplate(EPItems.GEAR_PRESS_MOLD), CookingBookCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(EPItems.RAW_ROD_PRESS_MOLD, new ItemStackTemplate(EPItems.ROD_PRESS_MOLD), CookingBookCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(EPItems.RAW_WIRE_PRESS_MOLD, new ItemStackTemplate(EPItems.WIRE_PRESS_MOLD), CookingBookCategory.MISC,
                200, .3f, null);
    }

    private void buildSmithingRecipes() {
        addNetheriteSmithingUpgradeRecipe(ingredientOf(EPItems.DIAMOND_HAMMER),
                new ItemStackTemplate(EPItems.NETHERITE_HAMMER));
    }

    private void buildPressMoldMakerRecipes() {
        addPressMoldMakerRecipe(4, new ItemStackTemplate(EPItems.RAW_GEAR_PRESS_MOLD));
        addPressMoldMakerRecipe(9, new ItemStackTemplate(EPItems.RAW_ROD_PRESS_MOLD));
        addPressMoldMakerRecipe(6, new ItemStackTemplate(EPItems.RAW_WIRE_PRESS_MOLD));
    }

    private void buildAlloyFurnaceRecipes() {
        addAlloyFurnaceRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(ConventionalItemTags.IRON_INGOTS)),
                new IngredientWithCount(ingredientOf(ItemTags.COALS), 3)
        }, new ItemStackTemplate(EPItems.STEEL_INGOT), 500);

        addAlloyFurnaceRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_TIN)),
                new IngredientWithCount(ingredientOf(CommonItemTags.SILICON)),
                new IngredientWithCount(ingredientOf(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStackTemplate(EPItems.REDSTONE_ALLOY_INGOT), 2500);

        addAlloyFurnaceRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_STEEL), 3),
                new IngredientWithCount(ingredientOf(ConventionalItemTags.COPPER_INGOTS), 3),
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_TIN), 3)
        }, new ItemStackTemplate(EPItems.ADVANCED_ALLOY_INGOT), 10000);
    }

    private void buildCompressorRecipes() {
        addCompressorRecipe(new IngredientWithCount(ingredientOf(EPItems.STONE_PEBBLE), 16), new ItemStackTemplate(Items.COBBLESTONE),
                "stone_pebbles");

        addPlateCompressorRecipes(ingredientOf(CommonItemTags.INGOTS_TIN),
                ingredientOf(CommonItemTags.STORAGE_BLOCKS_TIN), new ItemStackTemplate(EPItems.TIN_PLATE),
                "tin");
        addPlateCompressorRecipes(ingredientOf(ConventionalItemTags.COPPER_INGOTS),
                ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_COPPER), new ItemStackTemplate(EPItems.COPPER_PLATE),
                "copper");
        addPlateCompressorRecipes(ingredientOf(ConventionalItemTags.IRON_INGOTS),
                ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_IRON), new ItemStackTemplate(EPItems.IRON_PLATE),
                "iron");
        addPlateCompressorRecipes(ingredientOf(ConventionalItemTags.GOLD_INGOTS),
                ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_GOLD), new ItemStackTemplate(EPItems.GOLD_PLATE),
                "gold");

        addPlateCompressorIngotRecipe(ingredientOf(CommonItemTags.INGOTS_ADVANCED_ALLOY),
                new ItemStackTemplate(EPItems.ADVANCED_ALLOY_PLATE), "advanced_alloy");
        addPlateCompressorIngotRecipe(ingredientOf(CommonItemTags.INGOTS_ENERGIZED_COPPER),
                new ItemStackTemplate(EPItems.ENERGIZED_COPPER_PLATE), "energized_copper");
        addPlateCompressorIngotRecipe(ingredientOf(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                new ItemStackTemplate(EPItems.ENERGIZED_GOLD_PLATE), "energized_gold");
    }

    private void buildCrusherRecipes() {
        addCrusherRecipe(ingredientOf(Items.STONE), new ItemStackTemplate(Items.COBBLESTONE),
                "stone");
        addCrusherRecipe(ingredientOf(Items.STONE_BRICKS, Items.CHISELED_STONE_BRICKS, Items.CRACKED_STONE_BRICKS,
                        Items.SMOOTH_STONE), new ItemStackTemplate(Items.COBBLESTONE),
                "stone_variants");

        addCrusherRecipe(ingredientOf(Items.MOSSY_STONE_BRICKS), new ItemStackTemplate(Items.MOSSY_COBBLESTONE),
                "mossy_stone_bricks");

        addCrusherRecipe(ingredientOf(Items.TUFF_BRICKS, Items.CHISELED_TUFF_BRICKS, Items.CHISELED_TUFF,
                        Items.POLISHED_TUFF), new ItemStackTemplate(Items.TUFF),
                "tuff_variants");

        addCrusherRecipe(ingredientOf(Items.DEEPSLATE), new ItemStackTemplate(Items.COBBLED_DEEPSLATE),
                "deepslate");
        addCrusherRecipe(ingredientOf(Items.DEEPSLATE_BRICKS, Items.CHISELED_DEEPSLATE, Items.CRACKED_DEEPSLATE_BRICKS,
                        Items.DEEPSLATE_TILES, Items.CRACKED_DEEPSLATE_TILES, Items.POLISHED_DEEPSLATE), new ItemStackTemplate(Items.COBBLED_DEEPSLATE),
                "deepslate_variants");

        addCrusherRecipe(ingredientOf(Items.POLISHED_GRANITE), new ItemStackTemplate(Items.GRANITE),
                "polished_granite");
        addCrusherRecipe(ingredientOf(Items.POLISHED_DIORITE), new ItemStackTemplate(Items.DIORITE),
                "polished_diorite");
        addCrusherRecipe(ingredientOf(Items.POLISHED_ANDESITE), new ItemStackTemplate(Items.ANDESITE),
                "polished_andesite");

        addCrusherRecipe(ingredientOf(CommonItemTags.COBBLESTONES_NORMAL), new ItemStackTemplate(Items.GRAVEL),
                "cobblestone");

        addCrusherRecipe(ingredientOf(CommonItemTags.GRAVELS), new ItemStackTemplate(Items.SAND),
                "gravel");

        addCrusherRecipe(ingredientOf(Items.SANDSTONE), new ItemStackTemplate(Items.SAND),
                "sandstone");
        addCrusherRecipe(ingredientOf(Items.SMOOTH_SANDSTONE, Items.CUT_SANDSTONE,
                        Items.CHISELED_SANDSTONE), new ItemStackTemplate(Items.SAND),
                "sandstone_variants");

        addCrusherRecipe(ingredientOf(Items.RED_SANDSTONE), new ItemStackTemplate(Items.RED_SAND),
                "red_sandstone");
        addCrusherRecipe(ingredientOf(Items.SMOOTH_RED_SANDSTONE, Items.CUT_RED_SANDSTONE,
                        Items.CHISELED_RED_SANDSTONE), new ItemStackTemplate(Items.RED_SAND),
                "red_sandstone_variants");

        addCrusherRecipe(ingredientOf(Items.POLISHED_BLACKSTONE, Items.POLISHED_BLACKSTONE_BRICKS,
                        Items.CHISELED_POLISHED_BLACKSTONE, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                        Items.GILDED_BLACKSTONE), new ItemStackTemplate(Items.BLACKSTONE),
                "blackstone_variants");

        addCrusherRecipe(ingredientOf(Items.SMOOTH_BASALT, Items.POLISHED_BASALT), new ItemStackTemplate(Items.BASALT),
                "basalt_variants");
    }

    private void buildPulverizerRecipes() {
        addBasicMetalPulverizerRecipes(
                ingredientOf(CommonItemTags.ORES_TIN), ingredientOf(CommonItemTags.RAW_MATERIALS_TIN),
                ingredientOf(CommonItemTags.STORAGE_BLOCKS_RAW_TIN), ingredientOf(CommonItemTags.INGOTS_TIN),
                new ItemStackTemplate(EPItems.TIN_DUST), "tin");
        addBasicMetalPulverizerRecipes(
                ingredientOf(CommonItemTags.ORES_IRON), ingredientOf(ConventionalItemTags.IRON_RAW_MATERIALS),
                ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_RAW_IRON), ingredientOf(ConventionalItemTags.IRON_INGOTS),
                new ItemStackTemplate(EPItems.IRON_DUST), "iron");
        addBasicMetalPulverizerRecipes(
                ingredientOf(CommonItemTags.ORES_GOLD), ingredientOf(ConventionalItemTags.GOLD_RAW_MATERIALS),
                ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_RAW_GOLD), ingredientOf(ConventionalItemTags.GOLD_INGOTS),
                new ItemStackTemplate(EPItems.GOLD_DUST), "gold");

        addPulverizerRecipe(ingredientOf(CommonItemTags.ORES_COPPER),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(EPItems.COPPER_DUST), new double[] {
                        1., 1., 1., 1., .5, .5
                }, new double[] {
                        1., 1., 1., 1., .75, .5, .25
                }),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(EPItems.GOLD_DUST),
                        .1, .2), "copper_ores");
        addRawMetalAndIngotPulverizerRecipes(
                ingredientOf(ConventionalItemTags.COPPER_RAW_MATERIALS),
                ingredientOf(ConventionalItemTags.STORAGE_BLOCKS_RAW_COPPER), ingredientOf(ConventionalItemTags.COPPER_INGOTS),
                new ItemStackTemplate(EPItems.COPPER_DUST), "copper");

        addPulverizerRecipe(ingredientOf(CommonItemTags.ORES_COAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.COAL), new double[] {
                        1., 1., .25
                }, new double[] {
                        1., 1., .5, .25
                }), "coal_ores");

        addPulverizerRecipe(ingredientOf(CommonItemTags.ORES_REDSTONE),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.REDSTONE), new double[] {
                        1., 1., 1., 1., 1., .67, .33, .33, .17
                }, new double[] {
                        1., 1., 1., 1., 1., .67, .67, .33, .33, .17
                }), "redstone_ores");

        addPulverizerRecipe(ingredientOf(CommonItemTags.ORES_LAPIS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.LAPIS_LAZULI), new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .5, .5, .25, .125
                }, new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .75, .5, .5, .25, .125
                }), "lapis_ores");

        addPulverizerRecipe(ingredientOf(CommonItemTags.ORES_EMERALD),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.EMERALD), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "emerald_ores");

        addPulverizerRecipe(ingredientOf(CommonItemTags.ORES_DIAMOND),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.DIAMOND), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "diamond_ores");

        addPulverizerRecipe(ingredientOf(ConventionalItemTags.QUARTZ_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.QUARTZ), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "nether_quartz_ores");

        addPulverizerRecipe(ingredientOf(ConventionalItemTags.NETHERITE_SCRAP_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.NETHERITE_SCRAP), new double[] {
                        1., .125, .125
                }, new double[] {
                        1., .25, .25, .125
                }), "ancient_debris");

        addPulverizerRecipe(ingredientOf(Items.CHARCOAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(EPItems.CHARCOAL_DUST),
                        1., 1.), "charcoal");

        addPulverizerRecipe(ingredientOf(Items.CLAY),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.CLAY_BALL), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "clay");

        addPulverizerRecipe(ingredientOf(Items.GLOWSTONE),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.GLOWSTONE_DUST), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "glowstone");

        addPulverizerRecipe(ingredientOf(Items.MAGMA_BLOCK),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.MAGMA_CREAM), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "magma_block");

        addPulverizerRecipe(ingredientOf(Items.QUARTZ_BLOCK),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.QUARTZ), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "quartz_block");

        addPulverizerRecipe(ingredientOf(ItemTags.WOOL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.STRING), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "wool");

        addPulverizerRecipe(ingredientOf(ConventionalItemTags.GRAVELS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.FLINT),
                        1., 1.), "gravels");

        addPulverizerRecipe(ingredientOf(ConventionalItemTags.BONES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.BONE_MEAL), new double[] {
                        1., 1., 1., .25, .25
                }, new double[] {
                        1., 1., 1., .5, .25, .125
                }), "bones");

        addPulverizerRecipe(ingredientOf(ConventionalItemTags.BLAZE_RODS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.BLAZE_POWDER), new double[] {
                        1., 1., .5
                }, new double[] {
                        1., 1., .75, .25
                }), "blaze_rods");

        addPulverizerRecipe(ingredientOf(ConventionalItemTags.BREEZE_RODS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStackTemplate(Items.WIND_CHARGE), new double[] {
                        1., 1., 1., 1., .5, .5
                }, new double[] {
                        1., 1., 1., 1., .75, .5, .25
                }), "breeze_rods");
    }

    private void buildSawmillRecipes() {
        addBasicWoodSawmillRecipe(new ItemStackTemplate(Items.OAK_PLANKS),
                ingredientOf(ItemTags.OAK_LOGS), ingredientOf(Items.OAK_FENCE),
                ingredientOf(Items.OAK_FENCE_GATE), ingredientOf(Items.OAK_DOOR),
                ingredientOf(Items.OAK_TRAPDOOR), ingredientOf(Items.OAK_PRESSURE_PLATE),
                ingredientOf(Items.OAK_SIGN), ingredientOf(Items.OAK_SHELF),
                ingredientOf(Items.OAK_BOAT), ingredientOf(Items.OAK_CHEST_BOAT),
                false, "oak");

        addBasicWoodSawmillRecipe(new ItemStackTemplate(Items.SPRUCE_PLANKS),
                ingredientOf(ItemTags.SPRUCE_LOGS), ingredientOf(Items.SPRUCE_FENCE),
                ingredientOf(Items.SPRUCE_FENCE_GATE), ingredientOf(Items.SPRUCE_DOOR),
                ingredientOf(Items.SPRUCE_TRAPDOOR), ingredientOf(Items.SPRUCE_PRESSURE_PLATE),
                ingredientOf(Items.SPRUCE_SIGN), ingredientOf(Items.SPRUCE_SHELF),
                ingredientOf(Items.SPRUCE_BOAT), ingredientOf(Items.SPRUCE_CHEST_BOAT),
                false, "spruce");

        addBasicWoodSawmillRecipe(new ItemStackTemplate(Items.BIRCH_PLANKS),
                ingredientOf(ItemTags.BIRCH_LOGS), ingredientOf(Items.BIRCH_FENCE),
                ingredientOf(Items.BIRCH_FENCE_GATE), ingredientOf(Items.BIRCH_DOOR),
                ingredientOf(Items.BIRCH_TRAPDOOR), ingredientOf(Items.BIRCH_PRESSURE_PLATE),
                ingredientOf(Items.BIRCH_SIGN), ingredientOf(Items.BIRCH_SHELF),
                ingredientOf(Items.BIRCH_BOAT), ingredientOf(Items.BIRCH_CHEST_BOAT),
                false, "birch");

        addBasicWoodSawmillRecipe(new ItemStackTemplate(Items.JUNGLE_PLANKS),
                ingredientOf(ItemTags.JUNGLE_LOGS), ingredientOf(Items.JUNGLE_FENCE),
                ingredientOf(Items.JUNGLE_FENCE_GATE), ingredientOf(Items.JUNGLE_DOOR),
                ingredientOf(Items.JUNGLE_TRAPDOOR), ingredientOf(Items.JUNGLE_PRESSURE_PLATE),
                ingredientOf(Items.JUNGLE_SIGN), ingredientOf(Items.JUNGLE_SHELF),
                ingredientOf(Items.JUNGLE_BOAT), ingredientOf(Items.JUNGLE_CHEST_BOAT),
                false, "jungle");

        addBasicWoodSawmillRecipe(new ItemStackTemplate(Items.ACACIA_PLANKS),
                ingredientOf(ItemTags.ACACIA_LOGS), ingredientOf(Items.ACACIA_FENCE),
                ingredientOf(Items.ACACIA_FENCE_GATE), ingredientOf(Items.ACACIA_DOOR),
                ingredientOf(Items.ACACIA_TRAPDOOR), ingredientOf(Items.ACACIA_PRESSURE_PLATE),
                ingredientOf(Items.ACACIA_SIGN), ingredientOf(Items.ACACIA_SHELF),
                ingredientOf(Items.ACACIA_BOAT), ingredientOf(Items.ACACIA_CHEST_BOAT),
                false, "acacia");

        addBasicWoodSawmillRecipe(new ItemStackTemplate(Items.DARK_OAK_PLANKS),
                ingredientOf(ItemTags.DARK_OAK_LOGS), ingredientOf(Items.DARK_OAK_FENCE),
                ingredientOf(Items.DARK_OAK_FENCE_GATE), ingredientOf(Items.DARK_OAK_DOOR),
                ingredientOf(Items.DARK_OAK_TRAPDOOR), ingredientOf(Items.DARK_OAK_PRESSURE_PLATE),
                ingredientOf(Items.DARK_OAK_SIGN), ingredientOf(Items.DARK_OAK_SHELF),
                ingredientOf(Items.DARK_OAK_BOAT), ingredientOf(Items.DARK_OAK_CHEST_BOAT),
                false, "dark_oak");

        addBasicWoodSawmillRecipe(new ItemStackTemplate(Items.MANGROVE_PLANKS),
                ingredientOf(ItemTags.MANGROVE_LOGS), ingredientOf(Items.MANGROVE_FENCE),
                ingredientOf(Items.MANGROVE_FENCE_GATE), ingredientOf(Items.MANGROVE_DOOR),
                ingredientOf(Items.MANGROVE_TRAPDOOR), ingredientOf(Items.MANGROVE_PRESSURE_PLATE),
                ingredientOf(Items.MANGROVE_SIGN), ingredientOf(Items.MANGROVE_SHELF),
                ingredientOf(Items.MANGROVE_BOAT), ingredientOf(Items.MANGROVE_CHEST_BOAT),
                false, "mangrove");

        addBasicWoodSawmillRecipe(new ItemStackTemplate(Items.CHERRY_PLANKS),
                ingredientOf(ItemTags.CHERRY_LOGS), ingredientOf(Items.CHERRY_FENCE),
                ingredientOf(Items.CHERRY_FENCE_GATE), ingredientOf(Items.CHERRY_DOOR),
                ingredientOf(Items.CHERRY_TRAPDOOR), ingredientOf(Items.CHERRY_PRESSURE_PLATE),
                ingredientOf(Items.CHERRY_SIGN), ingredientOf(Items.CHERRY_SHELF),
                ingredientOf(Items.CHERRY_BOAT), ingredientOf(Items.CHERRY_CHEST_BOAT),
                false, "cherry");

        addBasicWoodSawmillRecipe(new ItemStackTemplate(Items.PALE_OAK_PLANKS),
                ingredientOf(ItemTags.PALE_OAK_LOGS), ingredientOf(Items.PALE_OAK_FENCE),
                ingredientOf(Items.PALE_OAK_FENCE_GATE), ingredientOf(Items.PALE_OAK_DOOR),
                ingredientOf(Items.PALE_OAK_TRAPDOOR), ingredientOf(Items.PALE_OAK_PRESSURE_PLATE),
                ingredientOf(Items.PALE_OAK_SIGN), ingredientOf(Items.PALE_OAK_SHELF),
                ingredientOf(Items.PALE_OAK_BOAT), ingredientOf(Items.PALE_OAK_CHEST_BOAT),
                false, "pale_oak");

        addSawmillRecipe(ingredientOf(ItemTags.BAMBOO_BLOCKS), new ItemStackTemplate(Items.BAMBOO_PLANKS, 3),
                1, "bamboo_planks", "bamboo_blocks");
        addBasicWoodWithoutLogsSawmillRecipe(new ItemStackTemplate(Items.BAMBOO_PLANKS),
                ingredientOf(Items.BAMBOO_FENCE), ingredientOf(Items.BAMBOO_FENCE_GATE), ingredientOf(Items.BAMBOO_DOOR),
                ingredientOf(Items.BAMBOO_TRAPDOOR), ingredientOf(Items.BAMBOO_PRESSURE_PLATE),
                ingredientOf(Items.BAMBOO_SIGN), ingredientOf(Items.BAMBOO_SHELF),
                ingredientOf(Items.BAMBOO_RAFT), ingredientOf(Items.BAMBOO_CHEST_RAFT),
                true, "bamboo");

        addSawmillRecipe(ingredientOf(ItemTags.CRIMSON_STEMS), new ItemStackTemplate(Items.CRIMSON_PLANKS, 6),
                1, "crimson_planks", "crimson_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(new ItemStackTemplate(Items.CRIMSON_PLANKS),
                ingredientOf(Items.CRIMSON_FENCE), ingredientOf(Items.CRIMSON_FENCE_GATE), ingredientOf(Items.CRIMSON_DOOR),
                ingredientOf(Items.CRIMSON_TRAPDOOR), ingredientOf(Items.CRIMSON_PRESSURE_PLATE),
                ingredientOf(Items.CRIMSON_SIGN), ingredientOf(Items.CRIMSON_SHELF), "crimson");

        addSawmillRecipe(ingredientOf(ItemTags.WARPED_STEMS), new ItemStackTemplate(Items.WARPED_PLANKS, 6),
                1, "warped_planks", "warped_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(new ItemStackTemplate(Items.WARPED_PLANKS),
                ingredientOf(Items.WARPED_FENCE), ingredientOf(Items.WARPED_FENCE_GATE), ingredientOf(Items.WARPED_DOOR),
                ingredientOf(Items.WARPED_TRAPDOOR), ingredientOf(Items.WARPED_PRESSURE_PLATE),
                ingredientOf(Items.WARPED_SIGN), ingredientOf(Items.WARPED_SHELF), "warped");

        addSawmillRecipe(ingredientOf(Items.CRAFTING_TABLE), new ItemStackTemplate(Items.OAK_PLANKS, 3),
                2, "oak_planks", "crafting_table");
        addSawmillRecipe(ingredientOf(Items.CARTOGRAPHY_TABLE), new ItemStackTemplate(Items.OAK_PLANKS, 4),
                new ItemStackTemplate(Items.PAPER, 2), "oak_planks", "cartography_table");
        addSawmillRecipe(ingredientOf(Items.FLETCHING_TABLE), new ItemStackTemplate(Items.OAK_PLANKS, 4),
                new ItemStackTemplate(Items.FLINT, 2), "oak_planks", "fletching_table");
        addSawmillRecipe(ingredientOf(Items.SMITHING_TABLE), new ItemStackTemplate(Items.OAK_PLANKS, 4),
                new ItemStackTemplate(Items.IRON_INGOT, 2), "oak_planks", "smithing_table");
        addSawmillRecipe(ingredientOf(Items.LOOM), new ItemStackTemplate(Items.OAK_PLANKS, 2),
                new ItemStackTemplate(Items.STRING, 2), "oak_planks", "loom");
        addSawmillRecipe(ingredientOf(Items.COMPOSTER), new ItemStackTemplate(Items.OAK_PLANKS, 3),
                2, "oak_planks", "composter");
        addSawmillRecipe(ingredientOf(Items.NOTE_BLOCK), new ItemStackTemplate(Items.OAK_PLANKS, 8),
                new ItemStackTemplate(Items.REDSTONE), "oak_planks", "note_block");
        addSawmillRecipe(ingredientOf(Items.JUKEBOX), new ItemStackTemplate(Items.OAK_PLANKS, 8),
                new ItemStackTemplate(Items.DIAMOND), "oak_planks", "jukebox");

        addSawmillRecipe(ingredientOf(Items.BOOKSHELF), new ItemStackTemplate(Items.OAK_PLANKS, 6),
                new ItemStackTemplate(Items.BOOK, 3), "oak_planks", "bookshelf");
        addSawmillRecipe(ingredientOf(Items.CHISELED_BOOKSHELF), new ItemStackTemplate(Items.OAK_PLANKS, 6),
                5, "oak_planks", "chiseled_bookshelf");
        addSawmillRecipe(ingredientOf(Items.LECTERN), new ItemStackTemplate(Items.OAK_PLANKS, 8),
                new ItemStackTemplate(Items.BOOK, 3), "oak_planks", "lectern");

        addSawmillRecipe(ingredientOf(Items.CHEST), new ItemStackTemplate(Items.OAK_PLANKS, 7),
                3, "oak_planks", "chest");
        addSawmillRecipe(ingredientOf(Items.BARREL), new ItemStackTemplate(Items.OAK_PLANKS, 6),
                5, "oak_planks", "barrel");

        addSawmillRecipe(ingredientOf(Items.WOODEN_SWORD), new ItemStackTemplate(Items.OAK_PLANKS, 2),
                1, "oak_planks", "wooden_sword");
        addSawmillRecipe(ingredientOf(Items.WOODEN_SPEAR), new ItemStackTemplate(Items.OAK_PLANKS),
                2, "oak_planks", "wooden_spear");
        addSawmillRecipe(ingredientOf(Items.WOODEN_SHOVEL), new ItemStackTemplate(Items.OAK_PLANKS),
                2, "oak_planks", "wooden_shovel");
        addSawmillRecipe(ingredientOf(Items.WOODEN_PICKAXE), new ItemStackTemplate(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_pickaxe");
        addSawmillRecipe(ingredientOf(Items.WOODEN_AXE), new ItemStackTemplate(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_axe");
        addSawmillRecipe(ingredientOf(Items.WOODEN_HOE), new ItemStackTemplate(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hoe");
        addSawmillRecipe(ingredientOf(EPItems.WOODEN_HAMMER), new ItemStackTemplate(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hammer");

        addSawmillRecipe(ingredientOf(Items.SHIELD), new ItemStackTemplate(Items.OAK_PLANKS, 6),
                new ItemStackTemplate(Items.IRON_INGOT), "oak_planks", "shield");

        addSawmillRecipe(ingredientOf(ItemTags.PLANKS), new ItemStackTemplate(Items.STICK, 3),
                1, "sticks", "planks");
        addSawmillRecipe(ingredientOf(Items.BAMBOO_MOSAIC), new ItemStackTemplate(Items.STICK, 3),
                3, "sticks", "bamboo_mosaic");

        addSawmillRecipe(ingredientOf(ItemTags.WOODEN_STAIRS),
                new ItemStackTemplate(Items.STICK, 3), 1, "sticks", "stairs");
        addSawmillRecipe(ingredientOf(Items.BAMBOO_MOSAIC_STAIRS),
                new ItemStackTemplate(Items.STICK, 3), 1, "sticks", "bamboo_mosaic_stairs");
        addSawmillRecipe(ingredientOf(ItemTags.WOODEN_SLABS),
                new ItemStackTemplate(Items.STICK, 1), 1, "sticks", "slabs");
        addSawmillRecipe(ingredientOf(Items.BAMBOO_MOSAIC_SLAB),
                new ItemStackTemplate(Items.STICK, 1), 1, "sticks", "bamboo_mosaic_slabs");
        addSawmillRecipe(ingredientOf(ItemTags.WOODEN_BUTTONS), new ItemStackTemplate(Items.STICK, 3),
                1, "sticks", "buttons");

        addSawmillRecipe(ingredientOf(Items.LADDER), new ItemStackTemplate(Items.STICK, 2),
                1, "sticks", "ladder");

        addSawmillRecipe(ingredientOf(Items.BOWL), new ItemStackTemplate(Items.STICK),
                2, "sticks", "bowl");
        addSawmillRecipe(ingredientOf(Items.BOW), new ItemStackTemplate(Items.STICK, 3),
                new ItemStackTemplate(Items.STRING, 3), "sticks", "bow");
        addSawmillRecipe(ingredientOf(Items.FISHING_ROD), new ItemStackTemplate(Items.STICK, 3),
                new ItemStackTemplate(Items.STRING, 2), "sticks", "fishing_rod");

        addSawmillRecipe(ingredientOf(Items.PAINTING), new ItemStackTemplate(Items.STICK, 8),
                new ItemStackTemplate(Items.WHITE_WOOL), "sticks", "painting");
        addSawmillRecipe(ingredientOf(Items.ITEM_FRAME), new ItemStackTemplate(Items.STICK, 8),
                new ItemStackTemplate(Items.LEATHER), "sticks", "item_frame");

        addSawmillRecipe(ingredientOf(ConventionalItemTags.WOODEN_RODS), new ItemStackTemplate(EPItems.SAWDUST),
                0, "sawdust", "sticks");
    }

    private void buildPlantGrowthChamberRecipes() {
        addBasicFlowerGrowingRecipe(Items.DANDELION, "dandelions");
        addBasicFlowerGrowingRecipe(Items.POPPY, "poppies");
        addBasicFlowerGrowingRecipe(Items.BLUE_ORCHID, "blue_orchids");
        addBasicFlowerGrowingRecipe(Items.ALLIUM, "alliums");
        addBasicFlowerGrowingRecipe(Items.AZURE_BLUET, "azure_bluets");
        addBasicFlowerGrowingRecipe(Items.RED_TULIP, "red_tulips");
        addBasicFlowerGrowingRecipe(Items.ORANGE_TULIP, "orange_tulips");
        addBasicFlowerGrowingRecipe(Items.WHITE_TULIP, "white_tulips");
        addBasicFlowerGrowingRecipe(Items.PINK_TULIP, "pink_tulips");
        addBasicFlowerGrowingRecipe(Items.OXEYE_DAISY, "oxeye_daisies");
        addBasicFlowerGrowingRecipe(Items.CORNFLOWER, "cornflowers");
        addBasicFlowerGrowingRecipe(Items.LILY_OF_THE_VALLEY, "lily_of_the_valley");
        addBasicFlowerGrowingRecipe(Items.OPEN_EYEBLOSSOM, "open_eyeblossoms");
        addBasicFlowerGrowingRecipe(Items.CLOSED_EYEBLOSSOM, "closed_eyeblossoms");

        addBasicFlowerGrowingRecipe(Items.SUNFLOWER, "sunflowers");
        addBasicFlowerGrowingRecipe(Items.LILAC, "lilacs");
        addBasicFlowerGrowingRecipe(Items.ROSE_BUSH, "rose_bushes");
        addBasicFlowerGrowingRecipe(Items.PEONY, "peonies");

        addBasicMushroomsGrowingRecipe(Items.BROWN_MUSHROOM, "brown_mushrooms");
        addBasicMushroomsGrowingRecipe(Items.RED_MUSHROOM, "red_mushrooms");

        addBasicAncientFlowerGrowingRecipe(Items.TORCHFLOWER_SEEDS, Items.TORCHFLOWER, "torchflowers");
        addBasicAncientFlowerGrowingRecipe(Items.PITCHER_POD, Items.PITCHER_PLANT, "pitcher_plants");

        addPlantGrowthChamberRecipe(ingredientOf(Items.PINK_PETALS), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.PINK_PETALS), new double[] {
                        1., 1., 1., .67, .33, .33, .15
                })
        }, 16000, "pink_petals", "pink_petals");

        addPlantGrowthChamberRecipe(ingredientOf(Items.WILDFLOWERS), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.WILDFLOWERS), new double[] {
                        1., 1., 1., .67, .33, .33, .15
                })
        }, 16000, "wildflowers", "wildflowers");

        addPlantGrowthChamberRecipe(ingredientOf(Items.SWEET_BERRIES), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.SWEET_BERRIES), new double[] {
                        1., 1., .33, .17
                })
        }, 16000, "sweet_berries", "sweet_berries");

        addPlantGrowthChamberRecipe(ingredientOf(Items.GLOW_BERRIES), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.GLOW_BERRIES), new double[] {
                        1., 1., .67, .33, .17, .17
                })
        }, 16000, "glow_berries", "glow_berries");

        addPlantGrowthChamberRecipe(ingredientOf(Items.WHEAT_SEEDS), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.WHEAT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.WHEAT), new double[] {
                        1., .75, .25
                })
        }, 16000, "wheat", "wheat_seeds");

        addPlantGrowthChamberRecipe(ingredientOf(Items.BEETROOT_SEEDS), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.BEETROOT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.BEETROOT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "beetroots", "beetroot_seeds");

        addPlantGrowthChamberRecipe(ingredientOf(Items.POTATO), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.POTATO), new double[] {
                        1., .75, .25, .25
                }),
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.POISONOUS_POTATO), new double[] {
                        .125
                })
        }, 16000, "potatoes", "potato");

        addPlantGrowthChamberRecipe(ingredientOf(Items.CARROT), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.CARROT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "carrots", "carrot");

        addPlantGrowthChamberRecipe(ingredientOf(Items.MELON_SEEDS), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.MELON_SLICE), new double[] {
                        1., 1., .75, .25, .25
                })
        }, 16000, "melon_slices", "melon_seeds");

        addPlantGrowthChamberRecipe(ingredientOf(Items.PUMPKIN_SEEDS), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.PUMPKIN), new double[] {
                        1.
                })
        }, 16000, "pumpkin", "pumpkin_seeds");

        addPlantGrowthChamberRecipe(ingredientOf(Items.SUGAR_CANE), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.SUGAR_CANE), new double[] {
                        1., 1., .67, .67, .33, .17, .17
                })
        }, 16000, "sugar_canes", "sugar_cane");
        addPlantGrowthChamberRecipe(ingredientOf(Items.BAMBOO), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.BAMBOO), new double[] {
                        1., 1., .67, .17
                })
        }, 16000, "bamboo", "bamboo");
    }

    private void buildPlantGrowthChamberFertilizerRecipes() {
        addPlantGrowthChamberFertilizerRecipe(ingredientOf(Items.BONE_MEAL),
                1.5, 3., "bone_meal");

        addPlantGrowthChamberFertilizerRecipe(ingredientOf(EPItems.BASIC_FERTILIZER),
                2.5, 3.5, "basic_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(ingredientOf(EPItems.GOOD_FERTILIZER),
                3.5, 5., "good_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(ingredientOf(EPItems.ADVANCED_FERTILIZER),
                5., 6.5, "advanced_fertilizer");
    }

    private void buildMetalPressRecipes() {
        addGearMetalPressRecipe(ingredientOf(CommonItemTags.PLATES_IRON), new ItemStackTemplate(EPItems.IRON_GEAR));

        addRodMetalPressRecipe(ingredientOf(CommonItemTags.PLATES_IRON), new ItemStackTemplate(EPItems.IRON_ROD));

        addWireMetalPressRecipe(ingredientOf(CommonItemTags.PLATES_TIN), new ItemStackTemplate(EPItems.TIN_WIRE));
        addWireMetalPressRecipe(ingredientOf(CommonItemTags.PLATES_COPPER), new ItemStackTemplate(EPItems.COPPER_WIRE));
        addWireMetalPressRecipe(ingredientOf(CommonItemTags.PLATES_GOLD), new ItemStackTemplate(EPItems.GOLD_WIRE));

        addWireMetalPressRecipe(ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER), new ItemStackTemplate(EPItems.ENERGIZED_COPPER_WIRE));
        addWireMetalPressRecipe(ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD), new ItemStackTemplate(EPItems.ENERGIZED_GOLD_WIRE));
    }

    private void buildHeatGeneratorRecipes() {
        addHeatGeneratorRecipe(Fluids.FLOWING_LAVA, 15, "flowing_lava");
        addHeatGeneratorRecipe(Fluids.LAVA, 25, "still_lava");
    }

    private void buildThermalGeneratorRecipes() {
        addThermalGeneratorRecipe(Fluids.LAVA, 50000, "lava");
    }

    private void buildAssemblingMachineRecipes() {
        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(EPItems.BASIC_SOLAR_CELL), 2),
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_ENERGIZED_COPPER), 4),
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_TIN), 2),
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY), 1)
        }, new ItemStackTemplate(EPItems.ADVANCED_SOLAR_CELL));

        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(EPItems.ADVANCED_SOLAR_CELL), 2),
                new IngredientWithCount(ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX), 4),
                new IngredientWithCount(ingredientOf(CommonItemTags.SILICON), 2),
                new IngredientWithCount(ingredientOf(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStackTemplate(EPItems.REINFORCED_ADVANCED_SOLAR_CELL));

        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(EPItems.BASIC_CIRCUIT), 4),
                new IngredientWithCount(ingredientOf(CommonItemTags.WIRES_ENERGIZED_COPPER), 4),
                new IngredientWithCount(ingredientOf(CommonItemTags.SILICON), 4),
                new IngredientWithCount(ingredientOf(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStackTemplate(EPItems.ADVANCED_CIRCUIT));

        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(EPItems.ADVANCED_CIRCUIT), 4),
                new IngredientWithCount(ingredientOf(CommonItemTags.WIRES_ENERGIZED_GOLD), 6),
                new IngredientWithCount(ingredientOf(CommonItemTags.SILICON), 6)
        }, new ItemStackTemplate(EPItems.PROCESSING_UNIT));

        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(EPItems.PROCESSING_UNIT), 4),
                new IngredientWithCount(ingredientOf(EPItems.TELEPORTER_MATRIX), 4),
                new IngredientWithCount(ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX), 2),
                new IngredientWithCount(ingredientOf(CommonItemTags.SILICON), 2)
        }, new ItemStackTemplate(EPItems.TELEPORTER_PROCESSING_UNIT));

        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(ConventionalItemTags.AMETHYST_GEMS), 6),
                new IngredientWithCount(ingredientOf(ConventionalItemTags.DIAMOND_GEMS), 2),
                new IngredientWithCount(ingredientOf(ConventionalItemTags.EMERALD_GEMS), 2),
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY), 1)
        }, new ItemStackTemplate(EPItems.CRYSTAL_MATRIX));
    }

    private void buildFluidFreezerRecipe() {
        addWaterInputFluidFreezerRecipe(FluidUtils.convertMilliBucketsToDroplets(125), new ItemStackTemplate(Items.SNOWBALL));

        addWaterInputFluidFreezerRecipe(FluidUtils.convertMilliBucketsToDroplets(500), new ItemStackTemplate(Items.SNOW_BLOCK));

        addWaterInputFluidFreezerRecipe(FluidUtils.convertMilliBucketsToDroplets(1000), new ItemStackTemplate(Items.ICE));
    }

    private void buildStoneLiquefierRecipes() {
        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.STONE), FluidUtils.convertMilliBucketsToDroplets(50), "stone");

        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.COBBLESTONE), FluidUtils.convertMilliBucketsToDroplets(50), "cobblestone");

        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.DEEPSLATE), FluidUtils.convertMilliBucketsToDroplets(150), "deepslate");

        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.COBBLED_DEEPSLATE), FluidUtils.convertMilliBucketsToDroplets(150), "cobbled_deepslate");

        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.GRANITE), FluidUtils.convertMilliBucketsToDroplets(50), "granite");

        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.DIORITE), FluidUtils.convertMilliBucketsToDroplets(50), "diorite");

        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.ANDESITE), FluidUtils.convertMilliBucketsToDroplets(50), "andesite");

        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.BLACKSTONE), FluidUtils.convertMilliBucketsToDroplets(250), "blackstone");

        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.OBSIDIAN), FluidUtils.convertMilliBucketsToDroplets(1000), "obsidian");

        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.NETHERRACK), FluidUtils.convertMilliBucketsToDroplets(250), "netherrack");

        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.MAGMA_CREAM), FluidUtils.convertMilliBucketsToDroplets(250), "magma_cream");
        addLavaOutputStoneLiquefierRecipe(ingredientOf(Items.MAGMA_BLOCK), FluidUtils.convertMilliBucketsToDroplets(1000), "magma_block");

        addWaterOutputStoneLiquefierRecipe(ingredientOf(Items.SNOWBALL), FluidUtils.convertMilliBucketsToDroplets(125), "snowball");

        addWaterOutputStoneLiquefierRecipe(ingredientOf(Items.SNOW_BLOCK), FluidUtils.convertMilliBucketsToDroplets(500), "snow_block");

        addWaterOutputStoneLiquefierRecipe(ingredientOf(Items.ICE), FluidUtils.convertMilliBucketsToDroplets(1000), "ice");
    }

    private void buildStoneSolidifierRecipes() {
        addStoneSolidifierRecipe(1000, 50, new ItemStackTemplate(Items.STONE));

        addStoneSolidifierRecipe(50, 50, new ItemStackTemplate(Items.COBBLESTONE));

        addStoneSolidifierRecipe(1000, 150, new ItemStackTemplate(Items.DEEPSLATE));

        addStoneSolidifierRecipe(150, 150, new ItemStackTemplate(Items.COBBLED_DEEPSLATE));

        addStoneSolidifierRecipe(1000, 50, new ItemStackTemplate(Items.GRANITE));

        addStoneSolidifierRecipe(1000, 50, new ItemStackTemplate(Items.DIORITE));

        addStoneSolidifierRecipe(1000, 50, new ItemStackTemplate(Items.ANDESITE));

        addStoneSolidifierRecipe(1000, 250, new ItemStackTemplate(Items.BLACKSTONE));

        addStoneSolidifierRecipe(1000, 1000, new ItemStackTemplate(Items.OBSIDIAN));
    }

    private void buildFiltrationPlantRecipes() {
        addOreFiltrationRecipe(new ItemStackTemplate(EPItems.RAW_TIN), 0.05, "tin");
        addOreFiltrationRecipe(new ItemStackTemplate(Items.RAW_COPPER), 0.05, "copper");
        addOreFiltrationRecipe(new ItemStackTemplate(Items.RAW_IRON), 0.05, "iron");
        addOreFiltrationRecipe(new ItemStackTemplate(Items.RAW_GOLD), 0.005, "gold");
    }

    private void buildFluidTransposerRecipes() {
        addConcreteFluidTransposerRecipe(ingredientOf(Items.WHITE_CONCRETE_POWDER), new ItemStackTemplate(Items.WHITE_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.ORANGE_CONCRETE_POWDER), new ItemStackTemplate(Items.ORANGE_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.MAGENTA_CONCRETE_POWDER), new ItemStackTemplate(Items.MAGENTA_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.LIGHT_BLUE_CONCRETE_POWDER), new ItemStackTemplate(Items.LIGHT_BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.YELLOW_CONCRETE_POWDER), new ItemStackTemplate(Items.YELLOW_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.LIME_CONCRETE_POWDER), new ItemStackTemplate(Items.LIME_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.PINK_CONCRETE_POWDER), new ItemStackTemplate(Items.PINK_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.GRAY_CONCRETE_POWDER), new ItemStackTemplate(Items.GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.LIGHT_GRAY_CONCRETE_POWDER), new ItemStackTemplate(Items.LIGHT_GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.CYAN_CONCRETE_POWDER), new ItemStackTemplate(Items.CYAN_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.PURPLE_CONCRETE_POWDER), new ItemStackTemplate(Items.PURPLE_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.BLUE_CONCRETE_POWDER), new ItemStackTemplate(Items.BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.BROWN_CONCRETE_POWDER), new ItemStackTemplate(Items.BROWN_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.GREEN_CONCRETE_POWDER), new ItemStackTemplate(Items.GREEN_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.RED_CONCRETE_POWDER), new ItemStackTemplate(Items.RED_CONCRETE));
        addConcreteFluidTransposerRecipe(ingredientOf(Items.BLACK_CONCRETE_POWDER), new ItemStackTemplate(Items.BLACK_CONCRETE));

        addFluidTransposerRecipe(ingredientOf(Items.SPONGE), new ItemStackTemplate(Items.WET_SPONGE), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));
        addFluidTransposerRecipe(ingredientOf(Items.WET_SPONGE), new ItemStackTemplate(Items.SPONGE), FluidTransposerBlockEntity.Mode.EMPTYING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));

        addFluidTransposerRecipe(ingredientOf(Items.DIRT), new ItemStackTemplate(Items.MUD), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(250)));
    }

    private void buildChargerRecipes() {
        addChargerRecipe(ingredientOf(ConventionalItemTags.COPPER_INGOTS),
                new ItemStackTemplate(EPItems.ENERGIZED_COPPER_INGOT), 4194304);
    }

    private void buildEnergizerRecipes() {
        addEnergizerRecipe(ingredientOf(ConventionalItemTags.COPPER_INGOTS),
                new ItemStackTemplate(EPItems.ENERGIZED_COPPER_INGOT), 32768);
        addEnergizerRecipe(ingredientOf(ConventionalItemTags.GOLD_INGOTS),
                new ItemStackTemplate(EPItems.ENERGIZED_GOLD_INGOT), 131072);
        addEnergizerRecipe(ingredientOf(EPItems.CRYSTAL_MATRIX),
                new ItemStackTemplate(EPItems.ENERGIZED_CRYSTAL_MATRIX), 524288);
    }

    private void buildCrystalGrowthChamberRecipes() {
        addCrystalGrowthChamberRecipe(ingredientOf(ConventionalItemTags.AMETHYST_GEMS),
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.AMETHYST_SHARD), new double[] {
                        1., 1., .67, .5, .25, .125
                }), 16000);
        addCrystalGrowthChamberRecipe(new IngredientWithCount(ingredientOf(Items.AMETHYST_BLOCK), 4),
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(Items.BUDDING_AMETHYST), .25),
                32000);
    }

    private void add3x3PackingCraftingRecipe(Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                    Ingredient unpackedInput, ItemLike packedItem, CraftingBookCategory category,
                                                    String group, String recipeIdSuffix) {
        addShapedCraftingRecipe(hasIngredientTrigger, Map.of(
                '#', unpackedInput
        ), new String[] {
                "###",
                "###",
                "###"
        }, new ItemStackTemplate(packedItem.asItem()), category, group, recipeIdSuffix);
    }
    private void add3x3UnpackingCraftingRecipe(Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                      Ingredient packedInput, ItemLike unpackedItem, CraftingBookCategory category,
                                                      String group, String recipeIdSuffix) {
        addShapelessCraftingRecipe(hasIngredientTrigger, List.of(
                packedInput
        ), new ItemStackTemplate(unpackedItem.asItem(), 9), category, group, recipeIdSuffix);
    }
    private void addMetalIngotCraftingRecipes(TagKey<Item> nuggetInput,
                                                     TagKey<Item> blockInput, ItemLike ingotItem, String metalName) {
        add3x3PackingCraftingRecipe(has(nuggetInput), ingredientOf(nuggetInput), ingotItem,
                CraftingBookCategory.MISC, metalName + "_ingot", "_from_nuggets");
        add3x3UnpackingCraftingRecipe(has(blockInput), ingredientOf(blockInput), ingotItem,
                CraftingBookCategory.MISC, metalName + "_ingot", "_from_" + metalName + "_block");
    }
    private void addMetalNuggetCraftingRecipe(TagKey<Item> ingotInput, ItemLike nuggetItem) {
        addShapelessCraftingRecipe(has(ingotInput), List.of(
                ingredientOf(ingotInput)
        ), new ItemStackTemplate(nuggetItem.asItem(), 9), CraftingBookCategory.MISC);
    }
    private void addMetalPlateCraftingRecipe(TagKey<Item> ingotInput, ItemLike plateItem) {
        addShapelessCraftingRecipe(has(ingotInput), List.of(
                ingredientOf(CommonItemTags.TOOLS_HAMMERS),
                ingredientOf(ingotInput)
        ), new ItemStackTemplate(plateItem.asItem()), CraftingBookCategory.MISC);
    }
    private void addMetalWireCraftingRecipe(TagKey<Item> plateInput, ItemLike wireItem) {
        addShapelessCraftingRecipe(has(plateInput), List.of(
                ingredientOf(CommonItemTags.TOOLS_CUTTERS),
                ingredientOf(plateInput)
        ), new ItemStackTemplate(wireItem.asItem(), 2), CraftingBookCategory.MISC);
    }
    private void addHammerCraftingRecipe(TagKey<Item> materialInput, ItemLike hammerItem) {
        addShapedCraftingRecipe(has(materialInput), Map.of(
                'S', ingredientOf(ConventionalItemTags.WOODEN_RODS),
                'M', ingredientOf(materialInput)
        ), new String[] {
                " M ",
                " SM",
                "S  "
        }, new ItemStackTemplate(hammerItem.asItem()), CraftingBookCategory.MISC);
    }
    private void addBasicCableCraftingRecipes(TagKey<Item> ingotInput, TagKey<Item> wireInput,
                                              ItemStackTemplate cableItem) {
        addCableCraftingRecipe(ingotInput, cableItem);
        addCableUsingWireCraftingRecipe(wireInput, cableItem);
    }
    private void addCableUsingWireCraftingRecipe(TagKey<Item> wireInput,
                                                 ItemStackTemplate cableItem) {
        addShapedCraftingRecipe(has(wireInput), Map.of(
                'W', ingredientOf(wireInput),
                'I', ingredientOf(EPItems.CABLE_INSULATOR)
        ), new String[] {
                "IWI",
                "IWI",
                "IWI"
        }, cableItem, CraftingBookCategory.MISC, getItemName(cableItem.item().value()), "_using_wire");
    }
    private void addCableCraftingRecipe(TagKey<Item> ingotInput,
                                        ItemStackTemplate cableItem) {
        addShapedCraftingRecipe(has(ingotInput), Map.of(
                'I', ingredientOf(ingotInput),
                'i', ingredientOf(EPItems.CABLE_INSULATOR)
        ), new String[] {
                "iIi",
                "iIi",
                "iIi"
        }, cableItem, CraftingBookCategory.MISC, getItemName(cableItem.item().value()));
    }
    private void addShapedCraftingRecipe(Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                         Map<Character, Ingredient> key, String[] pattern,
                                         ItemStackTemplate result, CraftingBookCategory category) {
        addShapedCraftingRecipe(hasIngredientTrigger, key, pattern, result, category, "");
    }
    private void addShapedCraftingRecipe(Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                         Map<Character, Ingredient> key, String[] pattern,
                                         ItemStackTemplate result, CraftingBookCategory category,
                                         String group) {
        addShapedCraftingRecipe(hasIngredientTrigger, key, pattern, result, category, group, "");
    }
    private void addShapedCraftingRecipe(Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                         Map<Character, Ingredient> key, String[] pattern,
                                         ItemStackTemplate result, CraftingBookCategory category,
                                         String group, String recipeIdSuffix) {
        addShapedCraftingRecipe(hasIngredientTrigger, key, pattern, result, category, group, recipeIdSuffix, "");
    }
    private void addShapedCraftingRecipe(Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                         Map<Character, Ingredient> key, String[] pattern,
                                         ItemStackTemplate result, CraftingBookCategory category,
                                         String group, String recipeIdSuffix, String recipeIdPrefix) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdPrefix + getItemName(result.item().value()) + recipeIdSuffix);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", hasIngredientTrigger)
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        ShapedRecipe recipe = new ShapedRecipe(new Recipe.CommonInfo(true),
                new CraftingRecipe.CraftingBookInfo(category, Objects.requireNonNullElse(group, "")),
                ShapedRecipePattern.of(key, pattern), result);
        this.output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }
    private void addShapelessCraftingRecipe(Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                            List<Ingredient> inputs, ItemStackTemplate result, CraftingBookCategory category) {
        addShapelessCraftingRecipe(hasIngredientTrigger, inputs, result, category, "");
    }
    private void addShapelessCraftingRecipe(Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                            List<Ingredient> inputs, ItemStackTemplate result, CraftingBookCategory category,
                                            String group) {
        addShapelessCraftingRecipe(hasIngredientTrigger, inputs, result, category, group, "");
    }
    private void addShapelessCraftingRecipe(Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                            List<Ingredient> inputs, ItemStackTemplate result, CraftingBookCategory category,
                                            String group, String recipeIdSuffix) {
        addShapelessCraftingRecipe(hasIngredientTrigger, inputs, result, category, group, recipeIdSuffix, "");
    }
    private void addShapelessCraftingRecipe(Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                            List<Ingredient> inputs, ItemStackTemplate result, CraftingBookCategory category,
                                            String group, String recipeIdSuffix, String recipeIdPrefix) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdPrefix + getItemName(result.item().value()) + recipeIdSuffix);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", hasIngredientTrigger)
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        ShapelessRecipe recipe = new ShapelessRecipe(new Recipe.CommonInfo(true),
                new CraftingRecipe.CraftingBookInfo(category, Objects.requireNonNullElse(group, "")), result,
                NonNullList.of(null, inputs.toArray(Ingredient[]::new)));
        this.output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }
    private void addCustomCraftingRecipe(Supplier<? extends CustomRecipe> customRecipeFactory,
                                         String recipeIdString) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdString);

        CustomRecipe recipe = customRecipeFactory.get();
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addBlastingAndSmeltingRecipes(ItemLike ingredient, ItemStackTemplate result,
                                               CookingBookCategory category, int time, float xp, String group,
                                               String recipeIngredientName) {
        addBlastingRecipe(ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }
    private void addBlastingAndSmeltingRecipes(TagKey<Item> ingredient, ItemStackTemplate result,
                                               CookingBookCategory category, int time, float xp, String group,
                                               String recipeIngredientName) {
        addBlastingRecipe(ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }

    private void addSmeltingRecipe(ItemLike ingredient, ItemStackTemplate result, CookingBookCategory category,
                                   int time, float xp, String group) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemName(result.item().value()));

        addSmeltingRecipe(ingredient, result, category, time, xp, group, recipeId);
    }
    private void addSmeltingRecipe(ItemLike ingredient, ItemStackTemplate result, CookingBookCategory category,
                                   int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemName(result.item().value()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(ingredient, result, category, time, xp, group, recipeId);
    }
    private void addSmeltingRecipe(ItemLike ingredient, ItemStackTemplate result, CookingBookCategory category,
                                   int time, float xp, String group, Identifier recipeId) {
        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        AbstractCookingRecipe recipe = new SmeltingRecipe(new Recipe.CommonInfo(true),
                new AbstractCookingRecipe.CookingBookInfo(category, Objects.requireNonNullElse(group, "")),
                ingredientOf(ingredient), result, xp, time);
        this.output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }
    private void addSmeltingRecipe(TagKey<Item> ingredient, ItemStackTemplate result, CookingBookCategory category,
                                   int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemName(result.item().value()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        AbstractCookingRecipe recipe = new SmeltingRecipe(new Recipe.CommonInfo(true),
                new AbstractCookingRecipe.CookingBookInfo(category, Objects.requireNonNullElse(group, "")),
                ingredientOf(ingredient), result, xp, time);
        this.output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }

    private void addBlastingRecipe(ItemLike ingredient, ItemStackTemplate result, CookingBookCategory category,
                                   int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("blasting/" +
                getItemName(result.item().value()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        AbstractCookingRecipe recipe = new BlastingRecipe(new Recipe.CommonInfo(true),
                new AbstractCookingRecipe.CookingBookInfo(category, Objects.requireNonNullElse(group, "")),
                ingredientOf(ingredient), result, xp, time);
        this.output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }
    private void addBlastingRecipe(TagKey<Item> ingredient, ItemStackTemplate result, CookingBookCategory category,
                                   int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("blasting/" +
                getItemName(result.item().value()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        AbstractCookingRecipe recipe = new BlastingRecipe(new Recipe.CommonInfo(true),
                new AbstractCookingRecipe.CookingBookInfo(category, Objects.requireNonNullElse(group, "")),
                ingredientOf(ingredient), result, xp, time);
        this.output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }

    private void addNetheriteSmithingUpgradeRecipe(Ingredient base, ItemStackTemplate output) {
        Identifier recipeId = EPAPI.id("smithing/" +
                getItemName(output.item().value()));

        Advancement.Builder advancementBuilder = this.output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", has(ConventionalItemTags.NETHERITE_INGOTS))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        SmithingTransformRecipe recipe = new SmithingTransformRecipe(new Recipe.CommonInfo(true),
                Optional.of(ingredientOf(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)), base,
                Optional.of(ingredientOf(ConventionalItemTags.NETHERITE_INGOTS)), output);
        this.output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }

    private void addAlloyFurnaceRecipe(IngredientWithCount[] inputs, ItemStackTemplate output,
                                       int ticks) {
        addAlloyFurnaceRecipe(inputs, output, OutputItemStackTemplateWithPercentages.EMPTY, ticks);
    }
    private void addAlloyFurnaceRecipe(IngredientWithCount[] inputs, ItemStackTemplate output,
                                       OutputItemStackTemplateWithPercentages secondaryOutput, int ticks) {
        Identifier recipeId = EPAPI.id("alloy_furnace/" +
                getItemName(output.item().value()));

        AlloyFurnaceRecipe recipe = new AlloyFurnaceRecipe(output, secondaryOutput, inputs, ticks);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addPressMoldMakerRecipe(int clayCount, ItemStackTemplate output) {
        Identifier recipeId = EPAPI.id("press_mold_maker/" +
                getItemName(output.item().value()));

        PressMoldMakerRecipe recipe = new PressMoldMakerRecipe(output, clayCount);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addPlateCompressorRecipes(Ingredient ingotInput,
                                           Ingredient blockInput, ItemStackTemplate output, String metalName) {
        addPlateCompressorIngotRecipe(ingotInput, output, metalName);
        addCompressorRecipe(blockInput, output.withCount(9), metalName + "_block");
    }
    private void addPlateCompressorIngotRecipe(Ingredient ingotInput,
                                               ItemStackTemplate output, String metalName) {
        addCompressorRecipe(ingotInput, output, metalName + "_ingot");
    }
    private void addCompressorRecipe(Ingredient input, ItemStackTemplate output, String recipeIngredientName) {
        addCompressorRecipe(new IngredientWithCount(input), output, recipeIngredientName);
    }
    private void addCompressorRecipe(IngredientWithCount input, ItemStackTemplate output, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("compressing/" +
                getItemName(output.item().value()) + "_from_compressing_" + recipeIngredientName);

        CompressorRecipe recipe = new CompressorRecipe(output, input);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addCrusherRecipe(Ingredient input, ItemStackTemplate output,
                                  String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("crusher/" +
                getItemName(output.item().value()) + "_from_crushing_" + recipeIngredientName);

        CrusherRecipe recipe = new CrusherRecipe(output, input);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addBasicMetalPulverizerRecipes(Ingredient oreInput,
                                                Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                Ingredient ingotInput, ItemStackTemplate output, String metalName) {
        addPulverizerRecipe(oreInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., .25
        }, new double[] {
                1., 1, .5, .25
        }), metalName + "_ores");

        addRawMetalAndIngotPulverizerRecipes(rawMetalInput, rawMetalBlockInput, ingotInput, output, metalName);
    }
    private void addRawMetalAndIngotPulverizerRecipes(Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                      Ingredient ingotInput, ItemStackTemplate output, String metalName) {
        addPulverizerRecipe(rawMetalInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., .25
        }, new double[] {
                1., .5
        }), "raw_" + metalName);

        addPulverizerRecipe(rawMetalBlockInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., 1., 1., 1., 1., 1., 1., 1., .5, .5, .25
        }, new double[] {
                1., 1., 1., 1., 1., 1., 1., 1., 1., .75, .5, .25, .25
        }), "raw_" + metalName + "_blocks");

        addPulverizerRecipe(ingotInput, new PulverizerRecipe.OutputItemStackWithPercentages(output,
                1., 1.), metalName + "_ingots");
    }
    private void addPulverizerRecipe(Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            String recipeIngredientName) {
        addPulverizerRecipe(input, output,
                new PulverizerRecipe.OutputItemStackWithPercentages(null, new double[0], new double[0]), recipeIngredientName);
    }
    private void addPulverizerRecipe(Ingredient input,
                                     PulverizerRecipe.OutputItemStackWithPercentages output,
                                     PulverizerRecipe.OutputItemStackWithPercentages secondaryOutput,
                                     String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("pulverizer/" +
                getItemName(output.output().item().value()) + "_from_pulverizer_" + recipeIngredientName);

        PulverizerRecipe recipe = new PulverizerRecipe(output, secondaryOutput, input);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addBasicWoodSawmillRecipe(ItemStackTemplate planksItem,
                                           Ingredient logsInput, Ingredient fenceInput, Ingredient fenceGateInput,
                                           Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                           Ingredient signInput, Ingredient shelfInput, Ingredient boatInput,
                                           Ingredient chestBoatInput, boolean isRaft, String woodName) {
        addSawmillRecipe(logsInput, planksItem.withCount(6), 1, getItemName(planksItem.item().value()),
                woodName + "_logs");

        addBasicWoodWithoutLogsSawmillRecipe(planksItem, fenceInput, fenceGateInput, doorInput, trapdoorInput,
                pressurePlateInput, signInput, shelfInput, boatInput, chestBoatInput, isRaft, woodName);
    }
    private void addBasicWoodWithoutLogsSawmillRecipe(ItemStackTemplate planksItem,
                                                      Ingredient fenceInput, Ingredient fenceGateInput,
                                                      Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                      Ingredient signInput, Ingredient shelfInput, Ingredient boatInput,
                                                      Ingredient chestBoatInput, boolean isRaft, String woodName) {
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(planksItem, fenceInput, fenceGateInput, doorInput,
                trapdoorInput, pressurePlateInput, signInput, shelfInput, woodName);

        addSawmillRecipe(boatInput, planksItem.withCount(4), 3, getItemName(planksItem.item().value()),
                woodName + (isRaft?"_raft":"_boat"));
        addSawmillRecipe(chestBoatInput, planksItem.withCount(5), 7, getItemName(planksItem.item().value()),
                woodName + (isRaft?"_chest_raft":"_chest_boat"));
    }
    private void addBasicWoodWithoutLogsAndBoatsSawmillRecipe(ItemStackTemplate planksItem,
                                                              Ingredient fenceInput, Ingredient fenceGateInput,
                                                              Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                              Ingredient signInput, Ingredient shelfInput, String woodName) {
        addSawmillRecipe(fenceInput, planksItem, 2, getItemName(planksItem.item().value()),
                woodName + "_fence");
        addSawmillRecipe(fenceGateInput, planksItem.withCount(2), 3, getItemName(planksItem.item().value()),
                woodName + "_fence_gate");
        addSawmillRecipe(doorInput, planksItem, 3, getItemName(planksItem.item().value()),
                woodName + "_door");
        addSawmillRecipe(trapdoorInput, planksItem.withCount(2), 3, getItemName(planksItem.item().value()),
                woodName + "_trapdoor");
        addSawmillRecipe(pressurePlateInput, planksItem, 2, getItemName(planksItem.item().value()),
                woodName + "_pressure_plate");
        addSawmillRecipe(signInput, planksItem.withCount(2), 1, getItemName(planksItem.item().value()),
                woodName + "_sign");
        addSawmillRecipe(shelfInput, planksItem.withCount(3), 1, getItemName(planksItem.item().value()),
                woodName + "_shelf");
    }
    private void addSawmillRecipe(Ingredient input, ItemStackTemplate output,
                                  int sawdustAmount, String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillRecipe recipe = new SawmillRecipe(output, input, sawdustAmount);
        this.output.accept(getKey(recipeId), recipe, null);
    }
    private void addSawmillRecipe(Ingredient input, ItemStackTemplate output,
                                  ItemStackTemplate secondaryOutput, String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillRecipe recipe = new SawmillRecipe(output, secondaryOutput, input);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addBasicFlowerGrowingRecipe(ItemLike flowerItem,
                                                    String outputName) {
        addPlantGrowthChamberRecipe(ingredientOf(flowerItem), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(flowerItem.asItem()), new double[] {
                        1., 1., .33
                })
        }, 16000, outputName, getItemName(flowerItem));
    }
    private void addBasicMushroomsGrowingRecipe(ItemLike mushroomItem,
                                                       String outputName) {
        addPlantGrowthChamberRecipe(ingredientOf(mushroomItem), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(mushroomItem.asItem()), new double[] {
                        1., 1., .5, .25
                })
        }, 16000, outputName, getItemName(mushroomItem));
    }
    private void addBasicAncientFlowerGrowingRecipe(ItemLike seedItem,
                                                           ItemLike flowerItem, String outputName) {
        addPlantGrowthChamberRecipe(ingredientOf(seedItem), new OutputItemStackTemplateWithPercentages[] {
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(seedItem.asItem()), new double[] {
                        1., .33, .15
                }),
                new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(flowerItem.asItem()), new double[] {
                        1., .15
                })
        }, 16000, outputName, getItemName(seedItem));
    }
    private void addPlantGrowthChamberRecipe(Ingredient input,
                                             OutputItemStackTemplateWithPercentages[] outputs, int ticks,
                                             String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("growing/" +
                outputName + "_from_growing_" + recipeIngredientName);

        PlantGrowthChamberRecipe recipe = new PlantGrowthChamberRecipe(outputs, input, ticks);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addPlantGrowthChamberFertilizerRecipe(Ingredient input,
                                                       double speedMultiplier, double energyConsumptionMultiplier,
                                                       String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("growing/fertilizer/" +
                recipeIngredientName);

        PlantGrowthChamberFertilizerRecipe recipe = new PlantGrowthChamberFertilizerRecipe(input, speedMultiplier,
                energyConsumptionMultiplier);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addGearMetalPressRecipe(Ingredient input, ItemStackTemplate output) {
        addMetalPressRecipe(new IngredientWithCount(input, 2), output, new ItemStackTemplate(EPItems.GEAR_PRESS_MOLD));
    }
    private void addRodMetalPressRecipe(Ingredient input, ItemStackTemplate output) {
        addMetalPressRecipe(input, output.withCount(2), new ItemStackTemplate(EPItems.ROD_PRESS_MOLD));
    }
    private void addWireMetalPressRecipe(Ingredient input, ItemStackTemplate output) {
        addMetalPressRecipe(input, output.withCount(3), new ItemStackTemplate(EPItems.WIRE_PRESS_MOLD));
    }
    private void addMetalPressRecipe(Ingredient input, ItemStackTemplate output,
                                     ItemStackTemplate pressMold) {
        addMetalPressRecipe(new IngredientWithCount(input), output, pressMold);
    }
    private void addMetalPressRecipe(IngredientWithCount input, ItemStackTemplate output,
                                     ItemStackTemplate pressMold) {
        Identifier recipeId = EPAPI.id("metal_press/" +
                getItemName(output.item().value()));

        MetalPressRecipe recipe = new MetalPressRecipe(output, pressMold, input);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addHeatGeneratorRecipe(Fluid input, int energyProduction,
                                               String recipeIngredientName) {
        addHeatGeneratorRecipe(new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private void addHeatGeneratorRecipe(Fluid[] input, int energyProduction,
                                               String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("heat_generator/" +
                "energy_production_from_" + recipeIngredientName);

        HeatGeneratorRecipe recipe = new HeatGeneratorRecipe(input, energyProduction);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addThermalGeneratorRecipe(Fluid input, int energyProduction,
                                                  String recipeIngredientName) {
        addThermalGeneratorRecipe(new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private void addThermalGeneratorRecipe(Fluid[] input, int energyProduction,
                                                  String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("thermal_generator/" +
                "energy_production_from_" + recipeIngredientName);

        ThermalGeneratorRecipe recipe = new ThermalGeneratorRecipe(input, energyProduction);
        this.output.accept(getKey(recipeId), recipe, null);
    }
    private void addWaterInputFluidFreezerRecipe(long waterAmount, ItemStackTemplate output) {
        addFluidFreezerRecipe(new FluidStack(Fluids.WATER, waterAmount), output);
    }
    private void addFluidFreezerRecipe(FluidStack input, ItemStackTemplate output) {
        Identifier recipeId = EPAPI.id("fluid_freezer/" + getItemName(output.item().value()));

        FluidFreezerRecipe recipe = new FluidFreezerRecipe(input, output);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addLavaOutputStoneLiquefierRecipe(Ingredient input, long lavaAmount, String recipeIngredientName) {
        addStoneLiquefierRecipe(input, new FluidStack(Fluids.LAVA, lavaAmount), recipeIngredientName);
    }
    private void addWaterOutputStoneLiquefierRecipe(Ingredient input, long waterAmount, String recipeIngredientName) {
        addStoneLiquefierRecipe(input, new FluidStack(Fluids.WATER, waterAmount), recipeIngredientName);
    }
    private void addStoneLiquefierRecipe(Ingredient input, FluidStack output, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("stone_liquefier/" + recipeIngredientName);

        StoneLiquefierRecipe recipe = new StoneLiquefierRecipe(input, output);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addStoneSolidifierRecipe(int waterAmount, int lavaAmount, ItemStackTemplate output) {
        Identifier recipeId = EPAPI.id("stone_solidifier/" +
                getItemName(output.item().value()));

        StoneSolidifierRecipe recipe = new StoneSolidifierRecipe(output, waterAmount, lavaAmount);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addAssemblingMachineRecipe(IngredientWithCount[] inputs, ItemStackTemplate output) {
        Identifier recipeId = EPAPI.id("assembling/" +
                getItemName(output.item().value()));

        AssemblingMachineRecipe recipe = new AssemblingMachineRecipe(output, inputs);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addOreFiltrationRecipe(ItemStackTemplate oreOutput, double oreOutputPercentage,
                                               String oreName) {
        addFiltrationPlantRecipe(new OutputItemStackTemplateWithPercentages(new ItemStackTemplate(EPItems.STONE_PEBBLE), .33),
                new OutputItemStackTemplateWithPercentages(oreOutput, oreOutputPercentage), BuiltInRegistries.ITEM.getKey(oreOutput.item().value()),
                oreName + "_ore_filtration");
    }
    private void addFiltrationPlantRecipe(OutputItemStackTemplateWithPercentages output,
                                          Identifier icon, String recipeName) {
        addFiltrationPlantRecipe(output, OutputItemStackTemplateWithPercentages.EMPTY, icon, recipeName);
    }
    private void addFiltrationPlantRecipe(OutputItemStackTemplateWithPercentages output,
                                          OutputItemStackTemplateWithPercentages secondaryOutput, Identifier icon,
                                          String recipeName) {
        Identifier recipeId = EPAPI.id("filtration_plant/" +
                recipeName);

        FiltrationPlantRecipe recipe = new FiltrationPlantRecipe(output, secondaryOutput, icon);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addConcreteFluidTransposerRecipe(Ingredient input, ItemStackTemplate output) {
        addFluidTransposerRecipe(input, output, FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));
    }
    private void addFluidTransposerRecipe(Ingredient input, ItemStackTemplate output,
                                                 FluidTransposerBlockEntity.Mode mode, FluidStack fluid) {
        Identifier recipeId = EPAPI.id("fluid_transposer/" +
                getItemName(output.item().value()));

        FluidTransposerRecipe recipe = new FluidTransposerRecipe(mode, output, input, fluid);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addChargerRecipe(Ingredient input, ItemStackTemplate output, int energyConsumption) {
        Identifier recipeId = EPAPI.id("charger/" +
                getItemName(output.item().value()));

        ChargerRecipe recipe = new ChargerRecipe(output, input, energyConsumption);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addEnergizerRecipe(Ingredient input, ItemStackTemplate output, int energyConsumption) {
        Identifier recipeId = EPAPI.id("energizer/" +
                getItemName(output.item().value()));

        EnergizerRecipe recipe = new EnergizerRecipe(output, input, energyConsumption);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private void addCrystalGrowthChamberRecipe(Ingredient input, OutputItemStackTemplateWithPercentages output,
                                                      int ticks) {
        addCrystalGrowthChamberRecipe(new IngredientWithCount(input, 1), output, ticks);
    }
    private void addCrystalGrowthChamberRecipe(IngredientWithCount input, OutputItemStackTemplateWithPercentages output,
                                                      int ticks) {
        Identifier recipeId = EPAPI.id("crystal_growing/" +
                getItemName(output.output().item().value()));

        CrystalGrowthChamberRecipe recipe = new CrystalGrowthChamberRecipe(output, input, ticks);
        this.output.accept(getKey(recipeId), recipe, null);
    }

    private Ingredient ingredientOf(ItemLike item) {
        return Ingredient.of(item);
    }

    private Ingredient ingredientOf(ItemLike... items) {
        return Ingredient.of(items);
    }

    private Ingredient ingredientOf(TagKey<Item> tagKey) {
        return Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(tagKey));
    }

    private static ResourceKey<Recipe<?>> getKey(Identifier recipeId) {
        return ResourceKey.create(Registries.RECIPE, recipeId);
    }
}
