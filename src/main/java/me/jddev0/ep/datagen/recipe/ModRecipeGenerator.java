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
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ModRecipeGenerator extends RecipeGenerator {
    public ModRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        super(registries, exporter);
    }

    @Override
    public void generate() {
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
        add3x3UnpackingCraftingRecipe(conditionsFromItem(EPBlocks.SAWDUST_BLOCK),
                Ingredient.ofItems(EPBlocks.SAWDUST_BLOCK), EPItems.SAWDUST,
                CraftingRecipeCategory.MISC, "", "_from_sawdust_block");
        add3x3PackingCraftingRecipe(conditionsFromTag(CommonItemTags.DUSTS_WOOD),
                ingredientFromTag(CommonItemTags.DUSTS_WOOD), EPBlocks.SAWDUST_BLOCK_ITEM,
                CraftingRecipeCategory.MISC, "", "");

        add3x3UnpackingCraftingRecipe(conditionsFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON), EPItems.SILICON,
                CraftingRecipeCategory.MISC, "", "_from_silicon_block");
        add3x3PackingCraftingRecipe(conditionsFromTag(CommonItemTags.SILICON),
                ingredientFromTag(CommonItemTags.SILICON), EPBlocks.SILICON_BLOCK_ITEM,
                CraftingRecipeCategory.MISC, "", "");

        addMetalNuggetCraftingRecipe(CommonItemTags.INGOTS_TIN, EPItems.TIN_NUGGET);
        addMetalIngotCraftingRecipes(CommonItemTags.NUGGETS_TIN, CommonItemTags.STORAGE_BLOCKS_TIN,
                EPItems.TIN_INGOT, "tin");

        add3x3PackingCraftingRecipe(conditionsFromTag(CommonItemTags.INGOTS_TIN),
                ingredientFromTag(CommonItemTags.INGOTS_TIN), EPBlocks.TIN_BLOCK_ITEM,
                CraftingRecipeCategory.MISC, "", "");

        add3x3UnpackingCraftingRecipe(conditionsFromTag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN),
                ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN), EPItems.RAW_TIN,
                CraftingRecipeCategory.MISC, "", "");
        add3x3PackingCraftingRecipe(conditionsFromTag(CommonItemTags.RAW_MATERIALS_TIN),
                ingredientFromTag(CommonItemTags.RAW_MATERIALS_TIN), EPBlocks.RAW_TIN_BLOCK_ITEM,
                CraftingRecipeCategory.MISC, "", "");

        addMetalPlateCraftingRecipe(CommonItemTags.INGOTS_TIN, EPItems.TIN_PLATE);
        addMetalPlateCraftingRecipe(ConventionalItemTags.COPPER_INGOTS, EPItems.COPPER_PLATE);
        addMetalPlateCraftingRecipe(ConventionalItemTags.IRON_INGOTS, EPItems.IRON_PLATE);
        addMetalPlateCraftingRecipe(ConventionalItemTags.GOLD_INGOTS, EPItems.GOLD_PLATE);

        addMetalWireCraftingRecipe(CommonItemTags.PLATES_TIN, EPItems.TIN_WIRE);
        addMetalWireCraftingRecipe(CommonItemTags.PLATES_COPPER, EPItems.COPPER_WIRE);
        addMetalWireCraftingRecipe(CommonItemTags.PLATES_GOLD, EPItems.GOLD_WIRE);

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.SILICON), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'Q', ingredientFromTag(ConventionalItemTags.QUARTZ_GEMS),
                'T', ingredientFromTag(CommonItemTags.INGOTS_TIN),
                'C', ingredientFromTag(ConventionalItemTags.COPPER_INGOTS)
        ), new String[] {
                " C ",
                "SQS",
                " T "
        }, new ItemStack(EPItems.BASIC_SOLAR_CELL), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.WIRES_COPPER), Map.of(
                'C', ingredientFromTag(CommonItemTags.WIRES_COPPER),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'S', ingredientFromTag(CommonItemTags.SILICON)
        ), new String[] {
                "RCR",
                "CIC",
                "SCS"
        }, new ItemStack(EPItems.BASIC_CIRCUIT), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BASIC_CIRCUIT), Map.of(
                'G', ingredientFromTag(CommonItemTags.WIRES_GOLD),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.ofItems(EPItems.BASIC_CIRCUIT)
        ), new String[] {
                "GGG",
                "CBC",
                "CBC"
        }, new ItemStack(EPItems.BASIC_UPGRADE_MODULE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ADVANCED_CIRCUIT), Map.of(
                'G', ingredientFromTag(CommonItemTags.WIRES_ENERGIZED_GOLD),
                'C', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'A', Ingredient.ofItems(EPItems.ADVANCED_CIRCUIT),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "GGG",
                "CBC",
                "CAC"
        }, new ItemStack(EPItems.ADVANCED_UPGRADE_MODULE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE), Map.of(
                'g', ingredientFromTag(CommonItemTags.WIRES_ENERGIZED_GOLD),
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'P', Ingredient.ofItems(EPItems.PROCESSING_UNIT),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "ggg",
                "GAG",
                "GPG"
        }, new ItemStack(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromTag(ConventionalItemTags.IRON_INGOTS), Map.of(
                'i', ingredientFromTag(ConventionalItemTags.IRON_NUGGETS),
                'I', ingredientFromTag(ConventionalItemTags.IRON_INGOTS)
        ), new String[] {
                " i ",
                "iIi",
                " i "
        }, new ItemStack(EPItems.SAW_BLADE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.SILICON), Map.of(
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'I', ingredientFromTag(ConventionalItemTags.IRON_INGOTS),
                'C', ingredientFromTag(ConventionalItemTags.COPPER_INGOTS)
        ), new String[] {
                "CIC",
                "ISI",
                "CIC"
        }, new ItemStack(EPBlocks.BASIC_MACHINE_FRAME_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.GEARS_IRON), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                's', ingredientFromTag(CommonItemTags.INGOTS_STEEL),
                'I', ingredientFromTag(CommonItemTags.GEARS_IRON),
                'R', ingredientFromTag(CommonItemTags.RODS_IRON),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "IsR",
                "SBS",
                "RsI"
        }, new ItemStack(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.INGOTS_ENERGIZED_COPPER), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'A', ingredientFromTag(CommonItemTags.INGOTS_ADVANCED_ALLOY),
                'E', ingredientFromTag(CommonItemTags.INGOTS_ENERGIZED_COPPER),
                'H', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "ESE",
                "AHA",
                "ESE"
        }, new ItemStack(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSC",
                "SAS",
                "CSC"
        }, new ItemStack(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), CraftingRecipeCategory.MISC);
    }
    private void buildFertilizerCraftingRecipes() {
        addShapedCraftingRecipe(conditionsFromItem(Items.BONE_MEAL), Map.of(
                'B', Ingredient.ofItems(Items.BONE_MEAL),
                'D', Ingredient.ofItems(Items.DANDELION),
                'b', Ingredient.ofItems(Items.BLUE_ORCHID),
                'L', ingredientFromTag(ConventionalItemTags.LAPIS_GEMS),
                'A', Ingredient.ofItems(Items.ALLIUM),
                'P', Ingredient.ofItems(Items.POPPY)
        ), new String[] {
                "DBb",
                "BLB",
                "ABP"
        }, new ItemStack(EPItems.BASIC_FERTILIZER, 4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BASIC_FERTILIZER), Map.of(
                'B', Ingredient.ofItems(EPItems.BASIC_FERTILIZER),
                'S', Ingredient.ofItems(Items.SUGAR_CANE),
                'K', Ingredient.ofItems(Items.KELP),
                's', Ingredient.ofItems(Items.SUGAR),
                'b', Ingredient.ofItems(Items.BAMBOO),
                'W', Ingredient.ofItems(Items.WHEAT_SEEDS)
        ), new String[] {
                "SBK",
                "BsB",
                "bBW"
        }, new ItemStack(EPItems.GOOD_FERTILIZER, 4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.GOOD_FERTILIZER), Map.of(
                'G', Ingredient.ofItems(EPItems.GOOD_FERTILIZER),
                'M', Ingredient.ofItems(Items.RED_MUSHROOM),
                'S', Ingredient.ofItems(Items.SWEET_BERRIES),
                'r', ingredientFromTag(ConventionalItemTags.RED_DYES),
                'T', Ingredient.ofItems(Items.RED_TULIP),
                'R', Ingredient.ofItems(Items.ROSE_BUSH)
        ), new String[] {
                "MGS",
                "GrG",
                "TGR"
        }, new ItemStack(EPItems.ADVANCED_FERTILIZER, 4), CraftingRecipeCategory.MISC);
    }
    private void buildUpgradeModuleCraftingRecipes() {
        addShapedCraftingRecipe(conditionsFromItem(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CSC",
                "RBR",
                "CSC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.SPEED_UPGRADE_MODULE_1), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE),
                's', Ingredient.ofItems(EPItems.SPEED_UPGRADE_MODULE_1)
        ), new String[] {
                "CSC",
                "RBR",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.SPEED_UPGRADE_MODULE_2), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                's', Ingredient.ofItems(EPItems.SPEED_UPGRADE_MODULE_2)
        ), new String[] {
                "CSC",
                "RAR",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.SPEED_UPGRADE_MODULE_3), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                's', Ingredient.ofItems(EPItems.SPEED_UPGRADE_MODULE_3)
        ), new String[] {
                "CSC",
                "RAR",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.SPEED_UPGRADE_MODULE_4), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'r', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                's', Ingredient.ofItems(EPItems.SPEED_UPGRADE_MODULE_4)
        ), new String[] {
                "CSC",
                "rRr",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'G', ingredientFromTag(ConventionalItemTags.GOLD_INGOTS),
                'R', ingredientFromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CGC",
                "RBR",
                "CGC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'G', ingredientFromTag(ConventionalItemTags.GOLD_INGOTS),
                'R', ingredientFromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE),
                'E', Ingredient.ofItems(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1)
        ), new String[] {
                "CGC",
                "RBR",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'G', ingredientFromTag(ConventionalItemTags.GOLD_INGOTS),
                'R', ingredientFromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2)
        ), new String[] {
                "CGC",
                "RAR",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'G', ingredientFromTag(ConventionalItemTags.GOLD_INGOTS),
                'R', ingredientFromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3)
        ), new String[] {
                "CGC",
                "RAR",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'G', ingredientFromTag(ConventionalItemTags.GOLD_INGOTS),
                'r', ingredientFromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4)
        ), new String[] {
                "CGC",
                "rRr",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'T', ingredientFromTag(CommonItemTags.PLATES_TIN),
                'c', ingredientFromTag(ItemTags.COALS),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CTC",
                "cBc",
                "CTC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'T', ingredientFromTag(CommonItemTags.PLATES_TIN),
                'c', ingredientFromTag(ItemTags.COALS),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE),
                'E', Ingredient.ofItems(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1)
        ), new String[] {
                "CTC",
                "cBc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'T', ingredientFromTag(CommonItemTags.PLATES_TIN),
                'c', ingredientFromTag(ItemTags.COALS),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2)
        ), new String[] {
                "CTC",
                "cAc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'T', ingredientFromTag(CommonItemTags.PLATES_TIN),
                'c', ingredientFromTag(ItemTags.COALS),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3)
        ), new String[] {
                "CTC",
                "cAc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'T', ingredientFromTag(CommonItemTags.PLATES_TIN),
                'c', ingredientFromTag(ItemTags.COALS),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4)
        ), new String[] {
                "CTC",
                "cRc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "GCG",
                "rRr",
                "GCG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.DURATION_UPGRADE_MODULE_1), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', Ingredient.ofItems(EPItems.DURATION_UPGRADE_MODULE_1)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.DURATION_UPGRADE_MODULE_2), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', Ingredient.ofItems(EPItems.DURATION_UPGRADE_MODULE_2)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.DURATION_UPGRADE_MODULE_3), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', Ingredient.ofItems(EPItems.DURATION_UPGRADE_MODULE_3)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.DURATION_UPGRADE_MODULE_4), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', Ingredient.ofItems(EPItems.DURATION_UPGRADE_MODULE_4)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.DURATION_UPGRADE_MODULE_5), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', Ingredient.ofItems(EPItems.DURATION_UPGRADE_MODULE_5)
        ), new String[] {
                "GDG",
                "CRC",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_6), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ADVANCED_UPGRADE_MODULE), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "GRG",
                "RAR",
                "GRG"
        }, new ItemStack(EPItems.RANGE_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.RANGE_UPGRADE_MODULE_1), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'r', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                'R', Ingredient.ofItems(EPItems.RANGE_UPGRADE_MODULE_1)
        ), new String[] {
                "GrG",
                "rAr",
                "GRG"
        }, new ItemStack(EPItems.RANGE_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.RANGE_UPGRADE_MODULE_2), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'r', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                'R', Ingredient.ofItems(EPItems.RANGE_UPGRADE_MODULE_2)
        ), new String[] {
                "GrG",
                "rAr",
                "GRG"
        }, new ItemStack(EPItems.RANGE_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE),
                'F', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM)
        ), new String[] {
                "IFI",
                "RBR",
                "IFI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE),
                'F', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1)
        ), new String[] {
                "IFI",
                "RBR",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2)
        ), new String[] {
                "IFI",
                "RAR",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3)
        ), new String[] {
                "IFI",
                "RAR",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'r', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'F', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4)
        ), new String[] {
                "IFI",
                "rRr",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE),
                'b', Ingredient.ofItems(Items.BLAST_FURNACE)
        ), new String[] {
                "CSC",
                "RBR",
                "CbC"
        }, new ItemStack(EPItems.BLAST_FURNACE_UPGRADE_MODULE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE),
                'F', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM)
        ), new String[] {
                "IRI",
                "FBF",
                "IRI"
        }, new ItemStack(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE),
                'F', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1)
        ), new String[] {
                "IRI",
                "FBF",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2)
        ), new String[] {
                "IRI",
                "FAF",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3)
        ), new String[] {
                "IRI",
                "FAF",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'r', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'F', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4)
        ), new String[] {
                "IrI",
                "FRF",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                's', ingredientFromTag(CommonItemTags.SILICON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE),
                'S', Ingredient.ofItems(Items.SMOKER)
        ), new String[] {
                "CsC",
                "RBR",
                "CSC"
        }, new ItemStack(EPItems.SMOKER_UPGRADE_MODULE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_GOLD),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'b', Ingredient.ofItems(EPItems.BASIC_SOLAR_CELL),
                'B', Ingredient.ofItems(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "GSG",
                "bBb",
                "GSG"
        }, new ItemStack(EPItems.MOON_LIGHT_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.MOON_LIGHT_UPGRADE_MODULE_1), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_GOLD),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(EPItems.BASIC_SOLAR_CELL),
                'A', Ingredient.ofItems(EPItems.ADVANCED_UPGRADE_MODULE),
                'M', Ingredient.ofItems(EPItems.MOON_LIGHT_UPGRADE_MODULE_1)
        ), new String[] {
                "GSG",
                "BAB",
                "GMG"
        }, new ItemStack(EPItems.MOON_LIGHT_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.MOON_LIGHT_UPGRADE_MODULE_2), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_GOLD),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(EPItems.BASIC_SOLAR_CELL),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'M', Ingredient.ofItems(EPItems.MOON_LIGHT_UPGRADE_MODULE_2)
        ), new String[] {
                "GSG",
                "BRB",
                "GMG"
        }, new ItemStack(EPItems.MOON_LIGHT_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);
    }
    private void buildToolsCraftingRecipes() {
        addHammerCraftingRecipe(ItemTags.WOODEN_TOOL_MATERIALS, EPItems.WOODEN_HAMMER);
        addHammerCraftingRecipe(ItemTags.STONE_TOOL_MATERIALS, EPItems.STONE_HAMMER);
        addHammerCraftingRecipe(ItemTags.COPPER_TOOL_MATERIALS, EPItems.COPPER_HAMMER);
        addHammerCraftingRecipe(ItemTags.IRON_TOOL_MATERIALS, EPItems.IRON_HAMMER);
        addHammerCraftingRecipe(ItemTags.GOLD_TOOL_MATERIALS, EPItems.GOLDEN_HAMMER);
        addHammerCraftingRecipe(ItemTags.DIAMOND_TOOL_MATERIALS, EPItems.DIAMOND_HAMMER);

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.PLATES_IRON), Map.of(
                'i', ingredientFromTag(ConventionalItemTags.IRON_NUGGETS),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'S', ingredientFromTag(ConventionalItemTags.WOODEN_RODS)
        ), new String[] {
                "I I",
                " i ",
                "S S"
        }, new ItemStack(EPItems.CUTTER), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromTag(ConventionalItemTags.IRON_INGOTS), Map.of(
                'i', ingredientFromTag(ConventionalItemTags.IRON_NUGGETS),
                'I', ingredientFromTag(ConventionalItemTags.IRON_INGOTS)
        ), new String[] {
                " I ",
                " iI",
                "i  "
        }, new ItemStack(EPItems.WRENCH), CraftingRecipeCategory.MISC);
    }
    private void buildEnergyItemsCraftingRecipes() {
        addShapedCraftingRecipe(conditionsFromTag(ConventionalItemTags.COPPER_INGOTS), Map.of(
                'T', ingredientFromTag(CommonItemTags.NUGGETS_TIN),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'c', ingredientFromTag(ItemTags.COALS)
        ), new String[] {
                "T T",
                "CRC",
                "CcC"
        }, new ItemStack(EPItems.BATTERY_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BATTERY_1), Map.of(
                'T', ingredientFromTag(CommonItemTags.NUGGETS_TIN),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'c', ingredientFromTag(ItemTags.COALS),
                'B', Ingredient.ofItems(EPItems.BATTERY_1)
        ), new String[] {
                "T T",
                "CBC",
                "IcI"
        }, new ItemStack(EPItems.BATTERY_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BATTERY_2), Map.of(
                'T', ingredientFromTag(CommonItemTags.NUGGETS_TIN),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(EPItems.BATTERY_2)
        ), new String[] {
                "T T",
                "BCB",
                "CSC"
        }, new ItemStack(EPItems.BATTERY_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BATTERY_3), Map.of(
                'T', ingredientFromTag(CommonItemTags.NUGGETS_TIN),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(EPItems.BATTERY_3)
        ), new String[] {
                "T T",
                "CBC",
                "SIS"
        }, new ItemStack(EPItems.BATTERY_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BATTERY_4), Map.of(
                'T', ingredientFromTag(CommonItemTags.NUGGETS_TIN),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(EPItems.BATTERY_4)
        ), new String[] {
                "T T",
                "BSB",
                "IRI"
        }, new ItemStack(EPItems.BATTERY_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BATTERY_5), Map.of(
                'T', ingredientFromTag(CommonItemTags.NUGGETS_TIN),
                'E', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'B', Ingredient.ofItems(EPItems.BATTERY_5)
        ), new String[] {
                "T T",
                "EBE",
                "EBE"
        }, new ItemStack(EPItems.BATTERY_6), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BATTERY_6), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.GOLD_NUGGETS),
                'E', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(EPItems.BATTERY_6)
        ), new String[] {
                "G G",
                "EBE",
                "SBS"
        }, new ItemStack(EPItems.BATTERY_7), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BATTERY_7), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.GOLD_NUGGETS),
                'E', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'B', Ingredient.ofItems(EPItems.BATTERY_7)
        ), new String[] {
                "G G",
                "EBE",
                "ABA"
        }, new ItemStack(EPItems.BATTERY_8), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.COAL_ENGINE_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'c', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'C', Ingredient.ofItems(EPBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "SIS",
                "RCR",
                "cIc"
        }, new ItemStack(EPItems.INVENTORY_COAL_ENGINE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.CHARGER_ITEM), Map.of(
                'c', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'C', Ingredient.ofItems(EPBlocks.CHARGER_ITEM)
        ), new String[] {
                "SIS",
                "RCR",
                "cIc"
        }, new ItemStack(EPItems.INVENTORY_CHARGER), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.TELEPORTER_ITEM), Map.of(
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'c', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'T', Ingredient.ofItems(EPBlocks.TELEPORTER_ITEM)
        ), new String[] {
                "CcC",
                "RTR",
                "GcG"
        }, new ItemStack(EPItems.INVENTORY_TELEPORTER), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BATTERY_3), Map.of(
                'b', Ingredient.ofItems(EPItems.BASIC_CIRCUIT),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(EPItems.BATTERY_3)
        ), new String[] {
                "S S",
                "bRb",
                "CBC"
        }, new ItemStack(EPItems.ENERGY_ANALYZER), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BATTERY_3), Map.of(
                'b', Ingredient.ofItems(EPItems.BASIC_CIRCUIT),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'L', ingredientFromTag(ConventionalItemTags.LAPIS_GEMS),
                'B', Ingredient.ofItems(EPItems.BATTERY_3)
        ), new String[] {
                "S S",
                "bLb",
                "CBC"
        }, new ItemStack(EPItems.FLUID_ANALYZER), CraftingRecipeCategory.MISC);
    }
    private void buildItemTransportCraftingRecipes() {
        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.PLATES_IRON), Map.of(
                'L', ingredientFromTag(ConventionalItemTags.LEATHERS),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS)
        ), new String[] {
                "   ",
                "LLL",
                "IRI"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM, 6), CraftingRecipeCategory.MISC,
                "item_conveyor_belt", "_from_leather");

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.PLATES_IRON), Map.of(
                'K', Ingredient.ofItems(Items.DRIED_KELP),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS)
        ), new String[] {
                "   ",
                "KKK",
                "IRI"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM, 6), CraftingRecipeCategory.MISC,
                "item_conveyor_belt", "_from_dried_kelp");

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.INGOTS_STEEL), Map.of(
                'S', ingredientFromTag(CommonItemTags.INGOTS_STEEL),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM)
        ), new String[] {
                "RIR",
                "SBS",
                "RIR"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.GEARS_IRON), Map.of(
                'G', ingredientFromTag(CommonItemTags.GEARS_IRON),
                'R', ingredientFromTag(CommonItemTags.RODS_IRON),
                'r', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'F', Ingredient.ofItems(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM)
        ), new String[] {
                "GRG",
                "rFr",
                "GRG"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'C', ingredientFromTag(ConventionalItemTags.NORMAL_COBBLESTONES),
                'c', Ingredient.ofItems(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM),
                'H', Ingredient.ofItems(Items.HOPPER)
        ), new String[] {
                "CCC",
                "CHC",
                "CcC"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', Ingredient.ofItems(Items.BRICKS),
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'F', Ingredient.ofItems(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', Ingredient.ofItems(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientFromTag(CommonItemTags.GEARS_IRON),
                's', ingredientFromTag(CommonItemTags.INGOTS_STEEL),
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'E', Ingredient.ofItems(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', Ingredient.ofItems(EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientFromTag(ConventionalItemTags.NORMAL_COBBLESTONES),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'R', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_REDSTONE),
                'L', Ingredient.ofItems(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CRC",
                "ILI",
                "CRC"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', Ingredient.ofItems(Items.BRICKS),
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'F', Ingredient.ofItems(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', Ingredient.ofItems(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientFromTag(CommonItemTags.GEARS_IRON),
                's', ingredientFromTag(CommonItemTags.INGOTS_STEEL),
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'E', Ingredient.ofItems(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', Ingredient.ofItems(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientFromTag(ConventionalItemTags.NORMAL_COBBLESTONES),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'l', Ingredient.ofItems(Items.LEVER),
                'L', Ingredient.ofItems(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "ClC",
                "ILI",
                "CRC"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', Ingredient.ofItems(Items.BRICKS),
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'F', Ingredient.ofItems(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', Ingredient.ofItems(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientFromTag(CommonItemTags.GEARS_IRON),
                's', ingredientFromTag(CommonItemTags.INGOTS_STEEL),
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'E', Ingredient.ofItems(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', Ingredient.ofItems(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientFromTag(ConventionalItemTags.NORMAL_COBBLESTONES),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'L', Ingredient.ofItems(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CIC",
                "ILI",
                "CRC"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', Ingredient.ofItems(Items.BRICKS),
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'F', Ingredient.ofItems(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', Ingredient.ofItems(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientFromTag(CommonItemTags.GEARS_IRON),
                's', ingredientFromTag(CommonItemTags.INGOTS_STEEL),
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'E', Ingredient.ofItems(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', Ingredient.ofItems(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientFromTag(ConventionalItemTags.NORMAL_COBBLESTONES),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'L', Ingredient.ofItems(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CRC",
                "ILI",
                "CIC"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', Ingredient.ofItems(Items.BRICKS),
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'F', Ingredient.ofItems(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', Ingredient.ofItems(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientFromTag(CommonItemTags.GEARS_IRON),
                's', ingredientFromTag(CommonItemTags.INGOTS_STEEL),
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'E', Ingredient.ofItems(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', Ingredient.ofItems(EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.PLATES_IRON), Map.of(
                'B', ingredientFromTag(ConventionalItemTags.WOODEN_BARRELS),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "III",
                "IBI",
                "III"
        }, new ItemStack(EPBlocks.ITEM_SILO_TINY_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ITEM_SILO_TINY_ITEM), Map.of(
                'S', Ingredient.ofItems(EPBlocks.ITEM_SILO_TINY_ITEM),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientFromTag(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "IiI",
                "iSi",
                "IiI"
        }, new ItemStack(EPBlocks.ITEM_SILO_SMALL_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ITEM_SILO_SMALL_ITEM), Map.of(
                'S', Ingredient.ofItems(EPBlocks.ITEM_SILO_SMALL_ITEM),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientFromTag(CommonItemTags.PLATES_IRON),
                's', ingredientFromTag(CommonItemTags.INGOTS_STEEL)
        ), new String[] {
                "IsI",
                "iSi",
                "IsI"
        }, new ItemStack(EPBlocks.ITEM_SILO_MEDIUM_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ITEM_SILO_MEDIUM_ITEM), Map.of(
                'S', Ingredient.ofItems(EPBlocks.ITEM_SILO_MEDIUM_ITEM),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                's', ingredientFromTag(CommonItemTags.INGOTS_STEEL)
        ), new String[] {
                "IsI",
                "sSs",
                "IsI"
        }, new ItemStack(EPBlocks.ITEM_SILO_LARGE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ITEM_SILO_LARGE_ITEM), Map.of(
                'S', Ingredient.ofItems(EPBlocks.ITEM_SILO_LARGE_ITEM),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON)
        ), new String[] {
                "III",
                "ISI",
                "III"
        }, new ItemStack(EPBlocks.ITEM_SILO_GIANT_ITEM), CraftingRecipeCategory.MISC);
    }
    private void buildFluidTransportCraftingRecipes() {
        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.PLATES_IRON), Map.of(
                        'I', ingredientFromTag(ConventionalItemTags.IRON_INGOTS),
                        'i', ingredientFromTag(CommonItemTags.PLATES_IRON)
                ), new String[] {
                        "IiI",
                        "IiI",
                        "IiI"
                }, new ItemStack(EPBlocks.IRON_FLUID_PIPE_ITEM, 12), CraftingRecipeCategory.MISC,
                "", "", "iron_");

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.PLATES_GOLD), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.GOLD_INGOTS),
                'g', ingredientFromTag(CommonItemTags.PLATES_GOLD)
        ), new String[] {
                "GgG",
                "GgG",
                "GgG"
        }, new ItemStack(EPBlocks.GOLDEN_FLUID_PIPE_ITEM, 12), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.PLATES_IRON), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "IGI",
                "IGI",
                "IGI"
        }, new ItemStack(EPBlocks.FLUID_TANK_SMALL_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.FLUID_TANK_SMALL_ITEM), Map.of(
                'F', Ingredient.ofItems(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'S', ingredientFromTag(CommonItemTags.INGOTS_STEEL)
        ), new String[] {
                "SFS",
                "SFS",
                "SFS"
        }, new ItemStack(EPBlocks.FLUID_TANK_MEDIUM_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.FLUID_TANK_MEDIUM_ITEM), Map.of(
                'F', Ingredient.ofItems(EPBlocks.FLUID_TANK_MEDIUM_ITEM),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON)
        ), new String[] {
                "IFI",
                "IFI",
                "IFI"
        }, new ItemStack(EPBlocks.FLUID_TANK_LARGE_ITEM), CraftingRecipeCategory.MISC);
    }
    private void buildEnergyTransportCraftingRecipes() {
        addBasicCableCraftingRecipes(CommonItemTags.INGOTS_TIN, CommonItemTags.WIRES_TIN,
                new ItemStack(EPBlocks.TIN_CABLE_ITEM, 9));

        addBasicCableCraftingRecipes(ConventionalItemTags.COPPER_INGOTS, CommonItemTags.WIRES_COPPER,
                new ItemStack(EPBlocks.COPPER_CABLE_ITEM, 6));
        addBasicCableCraftingRecipes(ConventionalItemTags.GOLD_INGOTS, CommonItemTags.WIRES_GOLD,
                new ItemStack(EPBlocks.GOLD_CABLE_ITEM, 6));

        addBasicCableCraftingRecipes(CommonItemTags.INGOTS_ENERGIZED_COPPER, CommonItemTags.WIRES_ENERGIZED_COPPER,
                new ItemStack(EPBlocks.ENERGIZED_COPPER_CABLE_ITEM, 3));
        addBasicCableCraftingRecipes(CommonItemTags.INGOTS_ENERGIZED_GOLD, CommonItemTags.WIRES_ENERGIZED_GOLD,
                new ItemStack(EPBlocks.ENERGIZED_GOLD_CABLE_ITEM, 3));

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'I', Ingredient.ofItems(EPItems.CABLE_INSULATOR),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX)
        ), new String[] {
                "ICI",
                "ICI",
                "ICI"
        }, new ItemStack(EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'M', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSI",
                "RMR",
                "CSI"
        }, new ItemStack(EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'M', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSI",
                "SMR",
                "CRI"
        }, new ItemStack(EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'M', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CRI",
                "SMS",
                "CRI"
        }, new ItemStack(EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                        'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                        'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                        'M', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                        'T', Ingredient.ofItems(EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM)
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStack(EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM), CraftingRecipeCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                        'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                        'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                        'M', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                        'T', Ingredient.ofItems(EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM)
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStack(EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM), CraftingRecipeCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                        'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                        'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                        'M', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                        'T', Ingredient.ofItems(EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM)
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStack(EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM), CraftingRecipeCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', Ingredient.ofItems(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', Ingredient.ofItems(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(EPBlocks.EHV_TRANSFORMER_3_TO_3_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', Ingredient.ofItems(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(EPBlocks.EHV_TRANSFORMER_N_TO_1_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'B', Ingredient.ofItems(EPItems.BASIC_CIRCUIT),
                'M', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSI",
                "BMB",
                "CSI"
        }, new ItemStack(EPBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'B', Ingredient.ofItems(EPItems.BASIC_CIRCUIT),
                'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'M', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(EPBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM)
        ), new String[] {
                "CTI",
                "BMB",
                "CTI"
        }, new ItemStack(EPBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'A', Ingredient.ofItems(EPItems.ADVANCED_CIRCUIT),
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(EPBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM)
        ), new String[] {
                "GTG",
                "AMA",
                "GTG"
        }, new ItemStack(EPBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'R', Ingredient.ofItems(EPItems.PROCESSING_UNIT),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', Ingredient.ofItems(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(EPBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM)
        ), new String[] {
                "CTC",
                "RMR",
                "CTC"
        }, new ItemStack(EPBlocks.CONFIGURABLE_EHV_TRANSFORMER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.ofItems(EPItems.BATTERY_5),
                'M', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CBC",
                "BMB",
                "SBS"
        }, new ItemStack(EPBlocks.BATTERY_BOX_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'E', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'B', Ingredient.ofItems(EPItems.BATTERY_8),
                'M', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "EBE",
                "BMB",
                "BSB"
        }, new ItemStack(EPBlocks.ADVANCED_BATTERY_BOX_ITEM), CraftingRecipeCategory.MISC);

        addShapelessCraftingRecipe(conditionsFromItem(EPBlocks.BATTERY_BOX_ITEM), List.of(
                Ingredient.ofItems(EPBlocks.BATTERY_BOX_ITEM),
                Ingredient.ofItems(Items.MINECART)
        ), new ItemStack(EPItems.BATTERY_BOX_MINECART), CraftingRecipeCategory.MISC);

        addShapelessCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_BATTERY_BOX_ITEM), List.of(
                Ingredient.ofItems(EPBlocks.ADVANCED_BATTERY_BOX_ITEM),
                Ingredient.ofItems(Items.MINECART)
        ), new ItemStack(EPItems.ADVANCED_BATTERY_BOX_MINECART), CraftingRecipeCategory.MISC);
    }
    private void buildMachineCraftingRecipes() {
        addShapedCraftingRecipe(conditionsFromItem(Items.SMOOTH_STONE), Map.of(
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'B', Ingredient.ofItems(Items.BRICKS),
                's', ingredientFromTag(ItemTags.SHOVELS)
        ), new String[] {
                "BBB",
                "BsB",
                "SSS"
        }, new ItemStack(EPBlocks.PRESS_MOLD_MAKER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(Items.FURNACE), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'B', Ingredient.ofItems(Items.BRICKS),
                'F', Ingredient.ofItems(Items.FURNACE)
        ), new String[] {
                "III",
                "FIF",
                "BBB"
        }, new ItemStack(EPBlocks.ALLOY_FURNACE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'b', Ingredient.ofItems(EPItems.BASIC_CIRCUIT),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(Items.CRAFTING_TABLE)
        ), new String[] {
                "CTC",
                "bBb",
                "ITI"
        }, new ItemStack(EPBlocks.AUTO_CRAFTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME), Map.of(
                'c', Ingredient.ofItems(EPItems.ADVANCED_CIRCUIT),
                'P', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'a', Ingredient.ofItems(EPBlocks.AUTO_CRAFTER_ITEM)
        ), new String[] {
                "GaG",
                "cAc",
                "PaP"
        }, new ItemStack(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientFromTag(CommonItemTags.SILICON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', Ingredient.ofItems(Items.STONECUTTER)
        ), new String[] {
                "CsC",
                "SBS",
                "CIC"
        }, new ItemStack(EPBlocks.CRUSHER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'c', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'F', Ingredient.ofItems(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'C', Ingredient.ofItems(EPBlocks.CRUSHER_ITEM)
        ), new String[] {
                "aCa",
                "FAF",
                "cCc"
        }, new ItemStack(EPBlocks.ADVANCED_CRUSHER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', Ingredient.ofItems(Items.STONECUTTER)
        ), new String[] {
                "ISI",
                "SBS",
                "ISI"
        }, new ItemStack(EPBlocks.PULVERIZER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'C', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'F', Ingredient.ofItems(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'P', Ingredient.ofItems(EPBlocks.PULVERIZER_ITEM)
        ), new String[] {
                "aPa",
                "FAF",
                "CPC"
        }, new ItemStack(EPBlocks.ADVANCED_PULVERIZER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.ofItems(EPItems.SAW_BLADE),
                's', Ingredient.ofItems(EPItems.SILICON),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "sSs",
                "CBC",
                "sIs"
        }, new ItemStack(EPBlocks.SAWMILL_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.ofItems(Items.PISTON),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "IPI",
                "PBP",
                "IPI"
        }, new ItemStack(EPBlocks.COMPRESSOR_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.ofItems(Items.PISTON),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'S', ingredientFromTag(CommonItemTags.INGOTS_STEEL),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SPS",
                "IBI",
                "SIS"
        }, new ItemStack(EPBlocks.METAL_PRESS_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'I', ingredientFromTag(CommonItemTags.GEARS_IRON),
                'i', ingredientFromTag(CommonItemTags.RODS_IRON),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'P', Ingredient.ofItems(EPBlocks.PRESS_MOLD_MAKER_ITEM)
        ), new String[] {
                "IPI",
                "iBi",
                "IPI"
        }, new ItemStack(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientFromTag(CommonItemTags.SILICON),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', Ingredient.ofItems(Items.STONECUTTER)
        ), new String[] {
                "CSC",
                "sBs",
                "ISI"
        }, new ItemStack(EPBlocks.AUTO_STONECUTTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'D', Ingredient.ofItems(Items.DIRT),
                'W', Ingredient.ofItems(Items.WATER_BUCKET),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SWS",
                "GBG",
                "IDI"
        }, new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'D', Ingredient.ofItems(Items.DISPENSER)
        ), new String[] {
                "IDS",
                "DBD",
                "SDC"
        }, new ItemStack(EPBlocks.BLOCK_PLACER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                's', ingredientFromTag(CommonItemTags.INGOTS_STEEL),
                'i', ingredientFromTag(CommonItemTags.GEARS_IRON),
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'B', Ingredient.ofItems(EPItems.BASIC_CIRCUIT),
                'H', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iRi",
                "BHB",
                "SsS"
        }, new ItemStack(EPBlocks.ASSEMBLING_MACHINE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'B', Ingredient.ofItems(EPItems.BASIC_CIRCUIT),
                'S', ingredientFromTag(CommonItemTags.INGOTS_STEEL),
                'H', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'A', Ingredient.ofItems(EPBlocks.ALLOY_FURNACE_ITEM)
        ), new String[] {
                "SAS",
                "BHB",
                "SAS"
        }, new ItemStack(EPBlocks.INDUCTION_SMELTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'F', Ingredient.ofItems(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SIS",
                "IBI",
                "CFC"
        }, new ItemStack(EPBlocks.FLUID_FILLER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'F', Ingredient.ofItems(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'i', ingredientFromTag(CommonItemTags.GEARS_IRON),
                'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'H', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SiS",
                "IHI",
                "CFC"
        }, new ItemStack(EPBlocks.STONE_LIQUEFIER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'F', Ingredient.ofItems(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'i', ingredientFromTag(CommonItemTags.GEARS_IRON),
                'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'H', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iSi",
                "FHF",
                "CIC"
        }, new ItemStack(EPBlocks.STONE_SOLIDIFIER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'i', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'D', Ingredient.ofItems(EPBlocks.FLUID_DRAINER_ITEM),
                'F', Ingredient.ofItems(EPBlocks.FLUID_FILLER_ITEM),
                'f', Ingredient.ofItems(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'H', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CiC",
                "DHF",
                "IfI"
        }, new ItemStack(EPBlocks.FLUID_TRANSPOSER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'i', ingredientFromTag(CommonItemTags.GEARS_IRON),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'F', Ingredient.ofItems(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'B', Ingredient.ofItems(Items.IRON_BARS),
                'f', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'H', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iBi",
                "FHF",
                "IfI"
        }, new ItemStack(EPBlocks.FILTRATION_PLANT_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'F', Ingredient.ofItems(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CFC",
                "IBI",
                "SIS"
        }, new ItemStack(EPBlocks.FLUID_DRAINER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'F', Ingredient.ofItems(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'P', Ingredient.ofItems(Items.PISTON),
                'p', Ingredient.ofItems(EPBlocks.IRON_FLUID_PIPE_ITEM)
        ), new String[] {
                "RPR",
                "FBF",
                "IpI"
        }, new ItemStack(EPBlocks.FLUID_PUMP_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'a', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'F', Ingredient.ofItems(EPBlocks.FLUID_PUMP_ITEM),
                'f', Ingredient.ofItems(EPBlocks.FLUID_TANK_LARGE_ITEM)
        ), new String[] {
                "GFG",
                "fAf",
                "aFa"
        }, new ItemStack(EPBlocks.ADVANCED_FLUID_PUMP_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON), Map.of(
                'i', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'B', Ingredient.ofItems(Items.IRON_BARS),
                'G', ingredientFromTag(ConventionalItemTags.GLASS_BLOCKS_COLORLESS)
        ), new String[] {
                "IBI",
                "iGi",
                "IiI"
        }, new ItemStack(EPBlocks.DRAIN_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientFromTag(CommonItemTags.SILICON),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON)
        ), new String[] {
                "sCs",
                "IBI",
                "CSC"
        }, new ItemStack(EPBlocks.CHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'a', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'C', Ingredient.ofItems(EPBlocks.CHARGER_ITEM)
        ), new String[] {
                "SGS",
                "aAa",
                "GCG"
        }, new ItemStack(EPBlocks.ADVANCED_CHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientFromTag(CommonItemTags.SILICON),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON)
        ), new String[] {
                "CSC",
                "IBI",
                "sCs"
        }, new ItemStack(EPBlocks.UNCHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'a', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'U', Ingredient.ofItems(EPBlocks.UNCHARGER_ITEM)
        ), new String[] {
                "GUG",
                "aAa",
                "SGS"
        }, new ItemStack(EPBlocks.ADVANCED_UNCHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.CHARGER_ITEM), Map.of(
                'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'c', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'H', Ingredient.ofItems(EPBlocks.CHARGER_ITEM)
        ), new String[] {
                "cCc",
                "SHS",
                "iIi"
        }, new ItemStack(EPBlocks.MINECART_CHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_CHARGER_ITEM), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_GOLD),
                'g', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'c', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'H', Ingredient.ofItems(EPBlocks.ADVANCED_CHARGER_ITEM)
        ), new String[] {
                "gGg",
                "SHS",
                "cCc"
        }, new ItemStack(EPBlocks.ADVANCED_MINECART_CHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.UNCHARGER_ITEM), Map.of(
                'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'c', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'i', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'U', Ingredient.ofItems(EPBlocks.UNCHARGER_ITEM)
        ), new String[] {
                "iIi",
                "SUS",
                "cCc"
        }, new ItemStack(EPBlocks.MINECART_UNCHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_UNCHARGER_ITEM), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_GOLD),
                'g', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'c', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'U', Ingredient.ofItems(EPBlocks.ADVANCED_UNCHARGER_ITEM)
        ), new String[] {
                "cCc",
                "SUS",
                "gGg"
        }, new ItemStack(EPBlocks.ADVANCED_MINECART_UNCHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.BASIC_SOLAR_CELL), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'I', ingredientFromTag(ConventionalItemTags.IRON_INGOTS),
                'C', ingredientFromTag(ConventionalItemTags.COPPER_INGOTS),
                'B', Ingredient.ofItems(EPItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "BRB",
                "ICI"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.SOLAR_PANEL_ITEM_1), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'C', ingredientFromTag(ConventionalItemTags.COPPER_INGOTS),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'S', Ingredient.ofItems(EPBlocks.SOLAR_PANEL_ITEM_1),
                'B', Ingredient.ofItems(EPItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SBS",
                "CRC"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.SOLAR_PANEL_ITEM_2), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'R', ingredientFromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                's', ingredientFromTag(CommonItemTags.SILICON),
                'S', Ingredient.ofItems(EPBlocks.SOLAR_PANEL_ITEM_2),
                'B', Ingredient.ofItems(EPItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SBS",
                "sRs"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.SOLAR_PANEL_ITEM_3), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.GLASS_PANES_COLORLESS),
                's', ingredientFromTag(CommonItemTags.SILICON),
                'R', ingredientFromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'S', Ingredient.ofItems(EPBlocks.SOLAR_PANEL_ITEM_3),
                'A', Ingredient.ofItems(EPItems.ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SsS",
                "ARA"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.SOLAR_PANEL_ITEM_4), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'a', ingredientFromTag(ConventionalItemTags.AMETHYST_GEMS),
                'E', ingredientFromTag(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                'S', Ingredient.ofItems(EPBlocks.SOLAR_PANEL_ITEM_4),
                'A', Ingredient.ofItems(EPItems.ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SaS",
                "AEA"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.SOLAR_PANEL_ITEM_5), Map.of(
                'G', ingredientFromTag(ConventionalItemTags.GLASS_PANES_COLORLESS),
                'A', ingredientFromTag(ConventionalItemTags.AMETHYST_GEMS),
                'E', ingredientFromTag(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                'S', Ingredient.ofItems(EPBlocks.SOLAR_PANEL_ITEM_5),
                'R', Ingredient.ofItems(EPItems.REINFORCED_ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SAS",
                "RER"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_6), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'F', Ingredient.ofItems(Items.FURNACE)
        ), new String[] {
                "ISI",
                "CBC",
                "IFI"
        }, new ItemStack(EPBlocks.COAL_ENGINE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(Items.REDSTONE_LAMP), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'C', ingredientFromTag(ConventionalItemTags.COPPER_INGOTS),
                'R', Ingredient.ofItems(Items.REDSTONE_LAMP)
        ), new String[] {
                "CSC",
                "SRS",
                "CSC"
        }, new ItemStack(EPBlocks.POWERED_LAMP_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON),
                'C', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'F', Ingredient.ofItems(Items.FURNACE)
        ), new String[] {
                "CFC",
                "SBS",
                "IFI"
        }, new ItemStack(EPBlocks.POWERED_FURNACE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'a', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'P', Ingredient.ofItems(EPBlocks.POWERED_FURNACE_ITEM)
        ), new String[] {
                "GPG",
                "SAS",
                "aPa"
        }, new ItemStack(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'G', ingredientFromTag(CommonItemTags.PLATES_GOLD),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'E', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "RaR",
                "GAG",
                "ECE"
        }, new ItemStack(EPBlocks.LIGHTNING_GENERATOR_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientFromTag(CommonItemTags.PLATES_GOLD),
                'R', ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'E', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "RaR",
                "GAG",
                "ESE"
        }, new ItemStack(EPBlocks.ENERGIZER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'I', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON),
                'R', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_REDSTONE),
                'E', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'a', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "aRa",
                "IAI",
                "ECE"
        }, new ItemStack(EPBlocks.CHARGING_STATION_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.COAL_ENGINE_ITEM), Map.of(
                's', ingredientFromTag(CommonItemTags.SILICON),
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'c', ingredientFromTag(CommonItemTags.WIRES_COPPER),
                'C', Ingredient.ofItems(EPBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "cSc",
                "sCs",
                "cSc"
        }, new ItemStack(EPBlocks.HEAT_GENERATOR_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientFromTag(CommonItemTags.SILICON),
                'F', Ingredient.ofItems(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'c', ingredientFromTag(CommonItemTags.PLATES_COPPER),
                'C', ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER),
                'H', Ingredient.ofItems(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'E', Ingredient.ofItems(EPBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "cHc",
                "SES",
                "CFC"
        }, new ItemStack(EPBlocks.THERMAL_GENERATOR_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'a', Ingredient.ofItems(Items.AMETHYST_BLOCK),
                'E', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'P', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', Ingredient.ofItems(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "aPa",
                "EAE",
                "aPa"
        }, new ItemStack(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.ofItems(EPItems.PROCESSING_UNIT),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'a', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'E', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientFromTag(ConventionalItemTags.AMETHYST_GEMS),
                'R', Ingredient.ofItems(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CAC",
                "PRP",
                "aEa"
        }, new ItemStack(EPBlocks.WEATHER_CONTROLLER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.ofItems(EPItems.PROCESSING_UNIT),
                'c', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'C', Ingredient.ofItems(Items.CLOCK),
                'A', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'E', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'R', Ingredient.ofItems(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "cCc",
                "PRP",
                "AEA"
        }, new ItemStack(EPBlocks.TIME_CONTROLLER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'T', Ingredient.ofItems(EPItems.TELEPORTER_PROCESSING_UNIT),
                'C', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'A', ingredientFromTag(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'E', ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'S', ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'R', Ingredient.ofItems(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CEC",
                "TRT",
                "ASA"
        }, new ItemStack(EPBlocks.TELEPORTER_ITEM), CraftingRecipeCategory.MISC);
    }
    private void buildMiscCraftingRecipes() {
        addShapelessCraftingRecipe(InventoryChangedCriterion.Conditions.items(
                Items.BOOK,
                EPBlocks.BASIC_MACHINE_FRAME_ITEM
        ), List.of(
                Ingredient.ofItems(Items.BOOK),
                Ingredient.ofItems(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new ItemStack(EPItems.ENERGIZED_POWER_BOOK), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromTag(CommonItemTags.DUSTS_CHARCOAL), Map.of(
                'P', Ingredient.ofItems(Items.PAPER),
                'C', ingredientFromTag(CommonItemTags.DUSTS_CHARCOAL),
                'I', ingredientFromTag(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "PCP",
                "CIC",
                "PCP"
        }, new ItemStack(EPItems.CHARCOAL_FILTER), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(conditionsFromItem(EPItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'A', ingredientFromTag(ConventionalItemTags.AMETHYST_GEMS),
                'E', Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'e', ingredientFromTag(ConventionalItemTags.ENDER_PEARLS)
        ), new String[] {
                "AEA",
                "EeE",
                "AEA"
        }, new ItemStack(EPItems.TELEPORTER_MATRIX), CraftingRecipeCategory.MISC);
    }
    private void buildCustomCraftingRecipes() {
        addCustomCraftingRecipe(TeleporterMatrixSettingsCopyRecipe::new, CraftingRecipeCategory.MISC,
                "teleporter_matrix_settings_copy");
    }

    private void buildCookingRecipes() {
        addBlastingAndSmeltingRecipes(CommonItemTags.RAW_MATERIALS_TIN, new ItemStack(EPItems.TIN_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "tin_ingot", "raw_tin");
        addBlastingAndSmeltingRecipes(CommonItemTags.ORES_TIN, new ItemStack(EPItems.TIN_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "tin_ingot", "tin_ores");

        addBlastingAndSmeltingRecipes(CommonItemTags.DUSTS_TIN, new ItemStack(EPItems.TIN_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "tin_ingot", "tin_dust");
        addBlastingAndSmeltingRecipes(CommonItemTags.DUSTS_COPPER, new ItemStack(Items.COPPER_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "copper_ingot", "copper_dust");
        addBlastingAndSmeltingRecipes(CommonItemTags.DUSTS_IRON, new ItemStack(Items.IRON_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "iron_ingot", "iron_dust");
        addBlastingAndSmeltingRecipes(CommonItemTags.DUSTS_GOLD, new ItemStack(Items.GOLD_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "gold_ingot", "gold_dust");

        addBlastingAndSmeltingRecipes(EPItems.COPPER_HAMMER, new ItemStack(Items.COPPER_NUGGET), CookingRecipeCategory.MISC,
                100, .1f, "copper_nugget", "copper_hammer");
        addBlastingAndSmeltingRecipes(EPItems.IRON_HAMMER, new ItemStack(Items.IRON_NUGGET), CookingRecipeCategory.MISC,
                100, .1f, "iron_nugget", "iron_hammer");
        addBlastingAndSmeltingRecipes(EPItems.GOLDEN_HAMMER, new ItemStack(Items.GOLD_NUGGET), CookingRecipeCategory.MISC,
                100, .1f, "gold_nugget", "golden_hammer");

        addBlastingAndSmeltingRecipes(ConventionalItemTags.QUARTZ_GEMS, new ItemStack(EPItems.SILICON), CookingRecipeCategory.MISC,
                250, 4.1f, "silicon", "nether_quartz");

        addSmeltingRecipe(EPBlocks.SAWDUST_BLOCK_ITEM, new ItemStack(Items.CHARCOAL), CookingRecipeCategory.MISC,
                200, .15f, null, "sawdust_block");

        addSmeltingRecipe(EPItems.RAW_GEAR_PRESS_MOLD, new ItemStack(EPItems.GEAR_PRESS_MOLD), CookingRecipeCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(EPItems.RAW_ROD_PRESS_MOLD, new ItemStack(EPItems.ROD_PRESS_MOLD), CookingRecipeCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(EPItems.RAW_WIRE_PRESS_MOLD, new ItemStack(EPItems.WIRE_PRESS_MOLD), CookingRecipeCategory.MISC,
                200, .3f, null);
    }

    private void buildSmithingRecipes() {
        addNetheriteSmithingUpgradeRecipe(Ingredient.ofItems(EPItems.DIAMOND_HAMMER),
                new ItemStack(EPItems.NETHERITE_HAMMER));
    }

    private void buildPressMoldMakerRecipes() {
        addPressMoldMakerRecipe(4, new ItemStack(EPItems.RAW_GEAR_PRESS_MOLD));
        addPressMoldMakerRecipe(9, new ItemStack(EPItems.RAW_ROD_PRESS_MOLD));
        addPressMoldMakerRecipe(6, new ItemStack(EPItems.RAW_WIRE_PRESS_MOLD));
    }

    private void buildAlloyFurnaceRecipes() {
        addAlloyFurnaceRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientFromTag(ConventionalItemTags.IRON_INGOTS)),
                new IngredientWithCount(ingredientFromTag(ItemTags.COALS), 3)
        }, new ItemStack(EPItems.STEEL_INGOT), 500);

        addAlloyFurnaceRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientFromTag(CommonItemTags.INGOTS_TIN)),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.SILICON)),
                new IngredientWithCount(ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStack(EPItems.REDSTONE_ALLOY_INGOT), 2500);

        addAlloyFurnaceRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientFromTag(CommonItemTags.INGOTS_STEEL), 3),
                new IngredientWithCount(ingredientFromTag(ConventionalItemTags.COPPER_INGOTS), 3),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.INGOTS_TIN), 3)
        }, new ItemStack(EPItems.ADVANCED_ALLOY_INGOT), 10000);
    }

    private void buildCompressorRecipes() {
        addCompressorRecipe(new IngredientWithCount(Ingredient.ofItems(EPItems.STONE_PEBBLE), 16), new ItemStack(Items.COBBLESTONE),
                "stone_pebbles");

        addPlateCompressorRecipes(ingredientFromTag(CommonItemTags.INGOTS_TIN),
                ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_TIN), new ItemStack(EPItems.TIN_PLATE),
                "tin");
        addPlateCompressorRecipes(ingredientFromTag(ConventionalItemTags.COPPER_INGOTS),
                ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER), new ItemStack(EPItems.COPPER_PLATE),
                "copper");
        addPlateCompressorRecipes(ingredientFromTag(ConventionalItemTags.IRON_INGOTS),
                ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON), new ItemStack(EPItems.IRON_PLATE),
                "iron");
        addPlateCompressorRecipes(ingredientFromTag(ConventionalItemTags.GOLD_INGOTS),
                ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_GOLD), new ItemStack(EPItems.GOLD_PLATE),
                "gold");

        addPlateCompressorIngotRecipe(ingredientFromTag(CommonItemTags.INGOTS_ADVANCED_ALLOY),
                new ItemStack(EPItems.ADVANCED_ALLOY_PLATE), "advanced_alloy");
        addPlateCompressorIngotRecipe(ingredientFromTag(CommonItemTags.INGOTS_ENERGIZED_COPPER),
                new ItemStack(EPItems.ENERGIZED_COPPER_PLATE), "energized_copper");
        addPlateCompressorIngotRecipe(ingredientFromTag(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                new ItemStack(EPItems.ENERGIZED_GOLD_PLATE), "energized_gold");
    }

    private void buildCrusherRecipes() {
        addCrusherRecipe(Ingredient.ofItems(Items.STONE), new ItemStack(Items.COBBLESTONE),
                "stone");
        addCrusherRecipe(Ingredient.ofItems(Items.STONE_BRICKS, Items.CHISELED_STONE_BRICKS, Items.CRACKED_STONE_BRICKS,
                        Items.SMOOTH_STONE), new ItemStack(Items.COBBLESTONE),
                "stone_variants");

        addCrusherRecipe(Ingredient.ofItems(Items.MOSSY_STONE_BRICKS), new ItemStack(Items.MOSSY_COBBLESTONE),
                "mossy_stone_bricks");

        addCrusherRecipe(Ingredient.ofItems(Items.TUFF_BRICKS, Items.CHISELED_TUFF_BRICKS, Items.CHISELED_TUFF,
                        Items.POLISHED_TUFF), new ItemStack(Items.TUFF),
                "tuff_variants");

        addCrusherRecipe(Ingredient.ofItems(Items.DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate");
        addCrusherRecipe(Ingredient.ofItems(Items.DEEPSLATE_BRICKS, Items.CHISELED_DEEPSLATE, Items.CRACKED_DEEPSLATE_BRICKS,
                        Items.DEEPSLATE_TILES, Items.CRACKED_DEEPSLATE_TILES, Items.POLISHED_DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate_variants");

        addCrusherRecipe(Ingredient.ofItems(Items.POLISHED_GRANITE), new ItemStack(Items.GRANITE),
                "polished_granite");
        addCrusherRecipe(Ingredient.ofItems(Items.POLISHED_DIORITE), new ItemStack(Items.DIORITE),
                "polished_diorite");
        addCrusherRecipe(Ingredient.ofItems(Items.POLISHED_ANDESITE), new ItemStack(Items.ANDESITE),
                "polished_andesite");

        addCrusherRecipe(ingredientFromTag(CommonItemTags.COBBLESTONES_NORMAL), new ItemStack(Items.GRAVEL),
                "cobblestone");

        addCrusherRecipe(ingredientFromTag(CommonItemTags.GRAVELS), new ItemStack(Items.SAND),
                "gravel");

        addCrusherRecipe(Ingredient.ofItems(Items.SANDSTONE), new ItemStack(Items.SAND),
                "sandstone");
        addCrusherRecipe(Ingredient.ofItems(Items.SMOOTH_SANDSTONE, Items.CUT_SANDSTONE,
                        Items.CHISELED_SANDSTONE), new ItemStack(Items.SAND),
                "sandstone_variants");

        addCrusherRecipe(Ingredient.ofItems(Items.RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone");
        addCrusherRecipe(Ingredient.ofItems(Items.SMOOTH_RED_SANDSTONE, Items.CUT_RED_SANDSTONE,
                        Items.CHISELED_RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone_variants");

        addCrusherRecipe(Ingredient.ofItems(Items.POLISHED_BLACKSTONE, Items.POLISHED_BLACKSTONE_BRICKS,
                        Items.CHISELED_POLISHED_BLACKSTONE, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                        Items.GILDED_BLACKSTONE), new ItemStack(Items.BLACKSTONE),
                "blackstone_variants");

        addCrusherRecipe(Ingredient.ofItems(Items.SMOOTH_BASALT, Items.POLISHED_BASALT), new ItemStack(Items.BASALT),
                "basalt_variants");
    }

    private void buildPulverizerRecipes() {
        addBasicMetalPulverizerRecipes(
                ingredientFromTag(CommonItemTags.ORES_TIN), ingredientFromTag(CommonItemTags.RAW_MATERIALS_TIN),
                ingredientFromTag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN), ingredientFromTag(CommonItemTags.INGOTS_TIN),
                new ItemStack(EPItems.TIN_DUST), "tin");
        addBasicMetalPulverizerRecipes(
                ingredientFromTag(CommonItemTags.ORES_IRON), ingredientFromTag(ConventionalItemTags.IRON_RAW_MATERIALS),
                ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_RAW_IRON), ingredientFromTag(ConventionalItemTags.IRON_INGOTS),
                new ItemStack(EPItems.IRON_DUST), "iron");
        addBasicMetalPulverizerRecipes(
                ingredientFromTag(CommonItemTags.ORES_GOLD), ingredientFromTag(ConventionalItemTags.GOLD_RAW_MATERIALS),
                ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_RAW_GOLD), ingredientFromTag(ConventionalItemTags.GOLD_INGOTS),
                new ItemStack(EPItems.GOLD_DUST), "gold");

        addPulverizerRecipe(ingredientFromTag(CommonItemTags.ORES_COPPER),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(EPItems.COPPER_DUST), new double[] {
                        1., 1., 1., 1., .5, .5
                }, new double[] {
                        1., 1., 1., 1., .75, .5, .25
                }),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(EPItems.GOLD_DUST),
                        .1, .2), "copper_ores");
        addRawMetalAndIngotPulverizerRecipes(
                ingredientFromTag(ConventionalItemTags.COPPER_RAW_MATERIALS),
                ingredientFromTag(ConventionalItemTags.STORAGE_BLOCKS_RAW_COPPER), ingredientFromTag(ConventionalItemTags.COPPER_INGOTS),
                new ItemStack(EPItems.COPPER_DUST), "copper");

        addPulverizerRecipe(ingredientFromTag(CommonItemTags.ORES_COAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.COAL), new double[] {
                        1., 1., .25
                }, new double[] {
                        1., 1., .5, .25
                }), "coal_ores");

        addPulverizerRecipe(ingredientFromTag(CommonItemTags.ORES_REDSTONE),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.REDSTONE), new double[] {
                        1., 1., 1., 1., 1., .67, .33, .33, .17
                }, new double[] {
                        1., 1., 1., 1., 1., .67, .67, .33, .33, .17
                }), "redstone_ores");

        addPulverizerRecipe(ingredientFromTag(CommonItemTags.ORES_LAPIS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.LAPIS_LAZULI), new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .5, .5, .25, .125
                }, new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .75, .5, .5, .25, .125
                }), "lapis_ores");

        addPulverizerRecipe(ingredientFromTag(CommonItemTags.ORES_EMERALD),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.EMERALD), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "emerald_ores");

        addPulverizerRecipe(ingredientFromTag(CommonItemTags.ORES_DIAMOND),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.DIAMOND), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "diamond_ores");

        addPulverizerRecipe(ingredientFromTag(ConventionalItemTags.QUARTZ_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.QUARTZ), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "nether_quartz_ores");

        addPulverizerRecipe(ingredientFromTag(ConventionalItemTags.NETHERITE_SCRAP_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.NETHERITE_SCRAP), new double[] {
                        1., .125, .125
                }, new double[] {
                        1., .25, .25, .125
                }), "ancient_debris");

        addPulverizerRecipe(Ingredient.ofItems(Items.CHARCOAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(EPItems.CHARCOAL_DUST),
                        1., 1.), "charcoal");

        addPulverizerRecipe(Ingredient.ofItems(Items.CLAY),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.CLAY_BALL), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "clay");

        addPulverizerRecipe(Ingredient.ofItems(Items.GLOWSTONE),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.GLOWSTONE_DUST), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "glowstone");

        addPulverizerRecipe(Ingredient.ofItems(Items.MAGMA_BLOCK),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.MAGMA_CREAM), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "magma_block");

        addPulverizerRecipe(Ingredient.ofItems(Items.QUARTZ_BLOCK),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.QUARTZ), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "quartz_block");

        addPulverizerRecipe(ingredientFromTag(ItemTags.WOOL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.STRING), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "wool");

        addPulverizerRecipe(ingredientFromTag(ConventionalItemTags.GRAVELS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.FLINT),
                        1., 1.), "gravels");

        addPulverizerRecipe(ingredientFromTag(ConventionalItemTags.BONES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.BONE_MEAL), new double[] {
                        1., 1., 1., .25, .25
                }, new double[] {
                        1., 1., 1., .5, .25, .125
                }), "bones");

        addPulverizerRecipe(ingredientFromTag(ConventionalItemTags.BLAZE_RODS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.BLAZE_POWDER), new double[] {
                        1., 1., .5
                }, new double[] {
                        1., 1., .75, .25
                }), "blaze_rods");

        addPulverizerRecipe(ingredientFromTag(ConventionalItemTags.BREEZE_RODS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.WIND_CHARGE), new double[] {
                        1., 1., 1., 1., .5, .5
                }, new double[] {
                        1., 1., 1., 1., .75, .5, .25
                }), "breeze_rods");
    }

    private void buildSawmillRecipes() {
        addBasicWoodSawmillRecipe(new ItemStack(Items.OAK_PLANKS),
                ingredientFromTag(ItemTags.OAK_LOGS), Ingredient.ofItems(Items.OAK_FENCE),
                Ingredient.ofItems(Items.OAK_FENCE_GATE), Ingredient.ofItems(Items.OAK_DOOR),
                Ingredient.ofItems(Items.OAK_TRAPDOOR), Ingredient.ofItems(Items.OAK_PRESSURE_PLATE),
                Ingredient.ofItems(Items.OAK_SIGN), Ingredient.ofItems(Items.OAK_SHELF),
                Ingredient.ofItems(Items.OAK_BOAT), Ingredient.ofItems(Items.OAK_CHEST_BOAT),
                false, "oak");

        addBasicWoodSawmillRecipe(new ItemStack(Items.SPRUCE_PLANKS),
                ingredientFromTag(ItemTags.SPRUCE_LOGS), Ingredient.ofItems(Items.SPRUCE_FENCE),
                Ingredient.ofItems(Items.SPRUCE_FENCE_GATE), Ingredient.ofItems(Items.SPRUCE_DOOR),
                Ingredient.ofItems(Items.SPRUCE_TRAPDOOR), Ingredient.ofItems(Items.SPRUCE_PRESSURE_PLATE),
                Ingredient.ofItems(Items.SPRUCE_SIGN), Ingredient.ofItems(Items.SPRUCE_SHELF),
                Ingredient.ofItems(Items.SPRUCE_BOAT), Ingredient.ofItems(Items.SPRUCE_CHEST_BOAT),
                false, "spruce");

        addBasicWoodSawmillRecipe(new ItemStack(Items.BIRCH_PLANKS),
                ingredientFromTag(ItemTags.BIRCH_LOGS), Ingredient.ofItems(Items.BIRCH_FENCE),
                Ingredient.ofItems(Items.BIRCH_FENCE_GATE), Ingredient.ofItems(Items.BIRCH_DOOR),
                Ingredient.ofItems(Items.BIRCH_TRAPDOOR), Ingredient.ofItems(Items.BIRCH_PRESSURE_PLATE),
                Ingredient.ofItems(Items.BIRCH_SIGN), Ingredient.ofItems(Items.BIRCH_SHELF),
                Ingredient.ofItems(Items.BIRCH_BOAT), Ingredient.ofItems(Items.BIRCH_CHEST_BOAT),
                false, "birch");

        addBasicWoodSawmillRecipe(new ItemStack(Items.JUNGLE_PLANKS),
                ingredientFromTag(ItemTags.JUNGLE_LOGS), Ingredient.ofItems(Items.JUNGLE_FENCE),
                Ingredient.ofItems(Items.JUNGLE_FENCE_GATE), Ingredient.ofItems(Items.JUNGLE_DOOR),
                Ingredient.ofItems(Items.JUNGLE_TRAPDOOR), Ingredient.ofItems(Items.JUNGLE_PRESSURE_PLATE),
                Ingredient.ofItems(Items.JUNGLE_SIGN), Ingredient.ofItems(Items.JUNGLE_SHELF),
                Ingredient.ofItems(Items.JUNGLE_BOAT), Ingredient.ofItems(Items.JUNGLE_CHEST_BOAT),
                false, "jungle");

        addBasicWoodSawmillRecipe(new ItemStack(Items.ACACIA_PLANKS),
                ingredientFromTag(ItemTags.ACACIA_LOGS), Ingredient.ofItems(Items.ACACIA_FENCE),
                Ingredient.ofItems(Items.ACACIA_FENCE_GATE), Ingredient.ofItems(Items.ACACIA_DOOR),
                Ingredient.ofItems(Items.ACACIA_TRAPDOOR), Ingredient.ofItems(Items.ACACIA_PRESSURE_PLATE),
                Ingredient.ofItems(Items.ACACIA_SIGN), Ingredient.ofItems(Items.ACACIA_SHELF),
                Ingredient.ofItems(Items.ACACIA_BOAT), Ingredient.ofItems(Items.ACACIA_CHEST_BOAT),
                false, "acacia");

        addBasicWoodSawmillRecipe(new ItemStack(Items.DARK_OAK_PLANKS),
                ingredientFromTag(ItemTags.DARK_OAK_LOGS), Ingredient.ofItems(Items.DARK_OAK_FENCE),
                Ingredient.ofItems(Items.DARK_OAK_FENCE_GATE), Ingredient.ofItems(Items.DARK_OAK_DOOR),
                Ingredient.ofItems(Items.DARK_OAK_TRAPDOOR), Ingredient.ofItems(Items.DARK_OAK_PRESSURE_PLATE),
                Ingredient.ofItems(Items.DARK_OAK_SIGN), Ingredient.ofItems(Items.DARK_OAK_SHELF),
                Ingredient.ofItems(Items.DARK_OAK_BOAT), Ingredient.ofItems(Items.DARK_OAK_CHEST_BOAT),
                false, "dark_oak");

        addBasicWoodSawmillRecipe(new ItemStack(Items.MANGROVE_PLANKS),
                ingredientFromTag(ItemTags.MANGROVE_LOGS), Ingredient.ofItems(Items.MANGROVE_FENCE),
                Ingredient.ofItems(Items.MANGROVE_FENCE_GATE), Ingredient.ofItems(Items.MANGROVE_DOOR),
                Ingredient.ofItems(Items.MANGROVE_TRAPDOOR), Ingredient.ofItems(Items.MANGROVE_PRESSURE_PLATE),
                Ingredient.ofItems(Items.MANGROVE_SIGN), Ingredient.ofItems(Items.MANGROVE_SHELF),
                Ingredient.ofItems(Items.MANGROVE_BOAT), Ingredient.ofItems(Items.MANGROVE_CHEST_BOAT),
                false, "mangrove");

        addBasicWoodSawmillRecipe(new ItemStack(Items.CHERRY_PLANKS),
                ingredientFromTag(ItemTags.CHERRY_LOGS), Ingredient.ofItems(Items.CHERRY_FENCE),
                Ingredient.ofItems(Items.CHERRY_FENCE_GATE), Ingredient.ofItems(Items.CHERRY_DOOR),
                Ingredient.ofItems(Items.CHERRY_TRAPDOOR), Ingredient.ofItems(Items.CHERRY_PRESSURE_PLATE),
                Ingredient.ofItems(Items.CHERRY_SIGN), Ingredient.ofItems(Items.CHERRY_SHELF),
                Ingredient.ofItems(Items.CHERRY_BOAT), Ingredient.ofItems(Items.CHERRY_CHEST_BOAT),
                false, "cherry");

        addBasicWoodSawmillRecipe(new ItemStack(Items.PALE_OAK_PLANKS),
                ingredientFromTag(ItemTags.PALE_OAK_LOGS), Ingredient.ofItems(Items.PALE_OAK_FENCE),
                Ingredient.ofItems(Items.PALE_OAK_FENCE_GATE), Ingredient.ofItems(Items.PALE_OAK_DOOR),
                Ingredient.ofItems(Items.PALE_OAK_TRAPDOOR), Ingredient.ofItems(Items.PALE_OAK_PRESSURE_PLATE),
                Ingredient.ofItems(Items.PALE_OAK_SIGN), Ingredient.ofItems(Items.PALE_OAK_SHELF),
                Ingredient.ofItems(Items.PALE_OAK_BOAT), Ingredient.ofItems(Items.PALE_OAK_CHEST_BOAT),
                false, "pale_oak");

        addSawmillRecipe(ingredientFromTag(ItemTags.BAMBOO_BLOCKS), new ItemStack(Items.BAMBOO_PLANKS, 3),
                1, "bamboo_planks", "bamboo_blocks");
        addBasicWoodWithoutLogsSawmillRecipe(new ItemStack(Items.BAMBOO_PLANKS),
                Ingredient.ofItems(Items.BAMBOO_FENCE), Ingredient.ofItems(Items.BAMBOO_FENCE_GATE), Ingredient.ofItems(Items.BAMBOO_DOOR),
                Ingredient.ofItems(Items.BAMBOO_TRAPDOOR), Ingredient.ofItems(Items.BAMBOO_PRESSURE_PLATE),
                Ingredient.ofItems(Items.BAMBOO_SIGN), Ingredient.ofItems(Items.BAMBOO_SHELF),
                Ingredient.ofItems(Items.BAMBOO_RAFT), Ingredient.ofItems(Items.BAMBOO_CHEST_RAFT),
                true, "bamboo");

        addSawmillRecipe(ingredientFromTag(ItemTags.CRIMSON_STEMS), new ItemStack(Items.CRIMSON_PLANKS, 6),
                1, "crimson_planks", "crimson_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(new ItemStack(Items.CRIMSON_PLANKS),
                Ingredient.ofItems(Items.CRIMSON_FENCE), Ingredient.ofItems(Items.CRIMSON_FENCE_GATE), Ingredient.ofItems(Items.CRIMSON_DOOR),
                Ingredient.ofItems(Items.CRIMSON_TRAPDOOR), Ingredient.ofItems(Items.CRIMSON_PRESSURE_PLATE),
                Ingredient.ofItems(Items.CRIMSON_SIGN), Ingredient.ofItems(Items.CRIMSON_SHELF), "crimson");

        addSawmillRecipe(ingredientFromTag(ItemTags.WARPED_STEMS), new ItemStack(Items.WARPED_PLANKS, 6),
                1, "warped_planks", "warped_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(new ItemStack(Items.WARPED_PLANKS),
                Ingredient.ofItems(Items.WARPED_FENCE), Ingredient.ofItems(Items.WARPED_FENCE_GATE), Ingredient.ofItems(Items.WARPED_DOOR),
                Ingredient.ofItems(Items.WARPED_TRAPDOOR), Ingredient.ofItems(Items.WARPED_PRESSURE_PLATE),
                Ingredient.ofItems(Items.WARPED_SIGN), Ingredient.ofItems(Items.WARPED_SHELF), "warped");

        addSawmillRecipe(Ingredient.ofItems(Items.CRAFTING_TABLE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "crafting_table");
        addSawmillRecipe(Ingredient.ofItems(Items.CARTOGRAPHY_TABLE), new ItemStack(Items.OAK_PLANKS, 4),
                new ItemStack(Items.PAPER, 2), "oak_planks", "cartography_table");
        addSawmillRecipe(Ingredient.ofItems(Items.FLETCHING_TABLE), new ItemStack(Items.OAK_PLANKS, 4),
                new ItemStack(Items.FLINT, 2), "oak_planks", "fletching_table");
        addSawmillRecipe(Ingredient.ofItems(Items.LOOM), new ItemStack(Items.OAK_PLANKS, 2),
                new ItemStack(Items.STRING, 2), "oak_planks", "loom");
        addSawmillRecipe(Ingredient.ofItems(Items.COMPOSTER), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "composter");
        addSawmillRecipe(Ingredient.ofItems(Items.NOTE_BLOCK), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.REDSTONE), "oak_planks", "note_block");
        addSawmillRecipe(Ingredient.ofItems(Items.JUKEBOX), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.DIAMOND), "oak_planks", "jukebox");

        addSawmillRecipe(Ingredient.ofItems(Items.BOOKSHELF), new ItemStack(Items.OAK_PLANKS, 6),
                new ItemStack(Items.BOOK, 3), "oak_planks", "bookshelf");
        addSawmillRecipe(Ingredient.ofItems(Items.CHISELED_BOOKSHELF), new ItemStack(Items.OAK_PLANKS, 6),
                5, "oak_planks", "chiseled_bookshelf");
        addSawmillRecipe(Ingredient.ofItems(Items.LECTERN), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.BOOK, 3), "oak_planks", "lectern");

        addSawmillRecipe(Ingredient.ofItems(Items.CHEST), new ItemStack(Items.OAK_PLANKS, 7),
                3, "oak_planks", "chest");
        addSawmillRecipe(Ingredient.ofItems(Items.BARREL), new ItemStack(Items.OAK_PLANKS, 6),
                5, "oak_planks", "barrel");

        addSawmillRecipe(Ingredient.ofItems(Items.WOODEN_SWORD), new ItemStack(Items.OAK_PLANKS, 2),
                1, "oak_planks", "wooden_sword");
        addSawmillRecipe(Ingredient.ofItems(Items.WOODEN_SHOVEL), new ItemStack(Items.OAK_PLANKS),
                2, "oak_planks", "wooden_shovel");
        addSawmillRecipe(Ingredient.ofItems(Items.WOODEN_PICKAXE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_pickaxe");
        addSawmillRecipe(Ingredient.ofItems(Items.WOODEN_AXE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_axe");
        addSawmillRecipe(Ingredient.ofItems(Items.WOODEN_HOE), new ItemStack(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hoe");
        addSawmillRecipe(Ingredient.ofItems(EPItems.WOODEN_HAMMER), new ItemStack(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hammer");

        addSawmillRecipe(ingredientFromTag(ItemTags.PLANKS), new ItemStack(Items.STICK, 3),
                1, "sticks", "planks");
        addSawmillRecipe(Ingredient.ofItems(Items.BAMBOO_MOSAIC), new ItemStack(Items.STICK, 3),
                3, "sticks", "bamboo_mosaic");

        addSawmillRecipe(ingredientFromTag(ItemTags.WOODEN_STAIRS),
                new ItemStack(Items.STICK, 3), 1, "sticks", "stairs");
        addSawmillRecipe(Ingredient.ofItems(Items.BAMBOO_MOSAIC_STAIRS),
                new ItemStack(Items.STICK, 3), 1, "sticks", "bamboo_mosaic_stairs");
        addSawmillRecipe(ingredientFromTag(ItemTags.WOODEN_SLABS),
                new ItemStack(Items.STICK, 1), 1, "sticks", "slabs");
        addSawmillRecipe(Ingredient.ofItems(Items.BAMBOO_MOSAIC_SLAB),
                new ItemStack(Items.STICK, 1), 1, "sticks", "bamboo_mosaic_slabs");
        addSawmillRecipe(ingredientFromTag(ItemTags.WOODEN_BUTTONS), new ItemStack(Items.STICK, 3),
                1, "sticks", "buttons");

        addSawmillRecipe(Ingredient.ofItems(Items.LADDER), new ItemStack(Items.STICK, 2),
                1, "sticks", "ladder");

        addSawmillRecipe(Ingredient.ofItems(Items.BOWL), new ItemStack(Items.STICK),
                2, "sticks", "bowl");
        addSawmillRecipe(Ingredient.ofItems(Items.BOW), new ItemStack(Items.STICK, 3),
                new ItemStack(Items.STRING, 3), "sticks", "bow");
        addSawmillRecipe(Ingredient.ofItems(Items.FISHING_ROD), new ItemStack(Items.STICK, 3),
                new ItemStack(Items.STRING, 2), "sticks", "fishing_rod");

        addSawmillRecipe(ingredientFromTag(ConventionalItemTags.WOODEN_RODS), new ItemStack(EPItems.SAWDUST),
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

        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.PINK_PETALS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.PINK_PETALS), new double[] {
                        1., 1., 1., .67, .33, .33, .15
                })
        }, 16000, "pink_petals", "pink_petals");

        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.WILDFLOWERS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.WILDFLOWERS), new double[] {
                        1., 1., 1., .67, .33, .33, .15
                })
        }, 16000, "wildflowers", "wildflowers");

        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.SWEET_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SWEET_BERRIES), new double[] {
                        1., 1., .33, .17
                })
        }, 16000, "sweet_berries", "sweet_berries");

        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.GLOW_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.GLOW_BERRIES), new double[] {
                        1., 1., .67, .33, .17, .17
                })
        }, 16000, "glow_berries", "glow_berries");

        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.WHEAT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT), new double[] {
                        1., .75, .25
                })
        }, 16000, "wheat", "wheat_seeds");

        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.BEETROOT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "beetroots", "beetroot_seeds");

        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.POTATO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.POTATO), new double[] {
                        1., .75, .25, .25
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.POISONOUS_POTATO), new double[] {
                        .125
                })
        }, 16000, "potatoes", "potato");

        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.CARROT), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.CARROT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "carrots", "carrot");

        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.MELON_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.MELON_SLICE), new double[] {
                        1., 1., .75, .25, .25
                })
        }, 16000, "melon_slices", "melon_seeds");

        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.PUMPKIN_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.PUMPKIN), new double[] {
                        1.
                })
        }, 16000, "pumpkin", "pumpkin_seeds");

        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.SUGAR_CANE), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SUGAR_CANE), new double[] {
                        1., 1., .67, .67, .33, .17, .17
                })
        }, 16000, "sugar_canes", "sugar_cane");
        addPlantGrowthChamberRecipe(Ingredient.ofItems(Items.BAMBOO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BAMBOO), new double[] {
                        1., 1., .67, .17
                })
        }, 16000, "bamboo", "bamboo");
    }

    private void buildPlantGrowthChamberFertilizerRecipes() {
        addPlantGrowthChamberFertilizerRecipe(Ingredient.ofItems(Items.BONE_MEAL),
                1.5, 3., "bone_meal");

        addPlantGrowthChamberFertilizerRecipe(Ingredient.ofItems(EPItems.BASIC_FERTILIZER),
                2.5, 3.5, "basic_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(Ingredient.ofItems(EPItems.GOOD_FERTILIZER),
                3.5, 5., "good_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(Ingredient.ofItems(EPItems.ADVANCED_FERTILIZER),
                5., 6.5, "advanced_fertilizer");
    }

    private void buildMetalPressRecipes() {
        addGearMetalPressRecipe(ingredientFromTag(CommonItemTags.PLATES_IRON), new ItemStack(EPItems.IRON_GEAR));

        addRodMetalPressRecipe(ingredientFromTag(CommonItemTags.PLATES_IRON), new ItemStack(EPItems.IRON_ROD));

        addWireMetalPressRecipe(ingredientFromTag(CommonItemTags.PLATES_TIN), new ItemStack(EPItems.TIN_WIRE));
        addWireMetalPressRecipe(ingredientFromTag(CommonItemTags.PLATES_COPPER), new ItemStack(EPItems.COPPER_WIRE));
        addWireMetalPressRecipe(ingredientFromTag(CommonItemTags.PLATES_GOLD), new ItemStack(EPItems.GOLD_WIRE));

        addWireMetalPressRecipe(ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_COPPER), new ItemStack(EPItems.ENERGIZED_COPPER_WIRE));
        addWireMetalPressRecipe(ingredientFromTag(CommonItemTags.PLATES_ENERGIZED_GOLD), new ItemStack(EPItems.ENERGIZED_GOLD_WIRE));
    }

    private void buildHeatGeneratorRecipes() {
        addHeatGeneratorRecipe(Fluids.FLOWING_LAVA, 15, "flowing_lava");
        addHeatGeneratorRecipe(Fluids.LAVA, 25, "still_lava");
    }

    private void buildThermalGeneratorRecipes() {
        addThermalGeneratorRecipe(Fluids.LAVA, 20000, "lava");
    }

    private void buildAssemblingMachineRecipes() {
        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(EPItems.BASIC_SOLAR_CELL), 2),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.INGOTS_ENERGIZED_COPPER), 4),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.INGOTS_TIN), 2),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY), 1)
        }, new ItemStack(EPItems.ADVANCED_SOLAR_CELL));

        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(EPItems.ADVANCED_SOLAR_CELL), 2),
                new IngredientWithCount(Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX), 4),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.SILICON), 2),
                new IngredientWithCount(ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStack(EPItems.REINFORCED_ADVANCED_SOLAR_CELL));

        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(EPItems.BASIC_CIRCUIT), 4),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.WIRES_ENERGIZED_COPPER), 4),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.SILICON), 4),
                new IngredientWithCount(ingredientFromTag(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStack(EPItems.ADVANCED_CIRCUIT));

        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(EPItems.ADVANCED_CIRCUIT), 4),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.WIRES_ENERGIZED_GOLD), 6),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.SILICON), 6)
        }, new ItemStack(EPItems.PROCESSING_UNIT));

        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(EPItems.PROCESSING_UNIT), 4),
                new IngredientWithCount(Ingredient.ofItems(EPItems.TELEPORTER_MATRIX), 4),
                new IngredientWithCount(Ingredient.ofItems(EPItems.ENERGIZED_CRYSTAL_MATRIX), 2),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.SILICON), 2)
        }, new ItemStack(EPItems.TELEPORTER_PROCESSING_UNIT));

        addAssemblingMachineRecipe(new IngredientWithCount[] {
                new IngredientWithCount(ingredientFromTag(ConventionalItemTags.AMETHYST_GEMS), 6),
                new IngredientWithCount(ingredientFromTag(ConventionalItemTags.DIAMOND_GEMS), 2),
                new IngredientWithCount(ingredientFromTag(ConventionalItemTags.EMERALD_GEMS), 2),
                new IngredientWithCount(ingredientFromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY), 1)
        }, new ItemStack(EPItems.CRYSTAL_MATRIX));
    }

    private void buildStoneLiquefierRecipes() {
        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.STONE), FluidUtils.convertMilliBucketsToDroplets(50), "stone");

        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.COBBLESTONE), FluidUtils.convertMilliBucketsToDroplets(50), "cobblestone");

        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.DEEPSLATE), FluidUtils.convertMilliBucketsToDroplets(150), "deepslate");

        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.COBBLED_DEEPSLATE), FluidUtils.convertMilliBucketsToDroplets(150), "cobbled_deepslate");

        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.GRANITE), FluidUtils.convertMilliBucketsToDroplets(50), "granite");

        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.DIORITE), FluidUtils.convertMilliBucketsToDroplets(50), "diorite");

        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.ANDESITE), FluidUtils.convertMilliBucketsToDroplets(50), "andesite");

        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.BLACKSTONE), FluidUtils.convertMilliBucketsToDroplets(250), "blackstone");

        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.OBSIDIAN), FluidUtils.convertMilliBucketsToDroplets(1000), "obsidian");

        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.NETHERRACK), FluidUtils.convertMilliBucketsToDroplets(250), "netherrack");

        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.MAGMA_CREAM), FluidUtils.convertMilliBucketsToDroplets(250), "magma_cream");
        addLavaOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.MAGMA_BLOCK), FluidUtils.convertMilliBucketsToDroplets(1000), "magma_block");

        addWaterOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.SNOWBALL), FluidUtils.convertMilliBucketsToDroplets(125), "snowball");

        addWaterOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.SNOW_BLOCK), FluidUtils.convertMilliBucketsToDroplets(500), "snow_block");

        addWaterOutputStoneLiquefierRecipe(Ingredient.ofItems(Items.ICE), FluidUtils.convertMilliBucketsToDroplets(1000), "ice");
    }

    private void buildStoneSolidifierRecipes() {
        addStoneSolidifierRecipe(1000, 50, new ItemStack(Items.STONE));

        addStoneSolidifierRecipe(50, 50, new ItemStack(Items.COBBLESTONE));

        addStoneSolidifierRecipe(1000, 150, new ItemStack(Items.DEEPSLATE));

        addStoneSolidifierRecipe(150, 150, new ItemStack(Items.COBBLED_DEEPSLATE));

        addStoneSolidifierRecipe(1000, 50, new ItemStack(Items.GRANITE));

        addStoneSolidifierRecipe(1000, 50, new ItemStack(Items.DIORITE));

        addStoneSolidifierRecipe(1000, 50, new ItemStack(Items.ANDESITE));

        addStoneSolidifierRecipe(1000, 250, new ItemStack(Items.BLACKSTONE));

        addStoneSolidifierRecipe(1000, 1000, new ItemStack(Items.OBSIDIAN));
    }

    private void buildFiltrationPlantRecipes() {
        addOreFiltrationRecipe(new ItemStack(EPItems.RAW_TIN), 0.05, "tin");
        addOreFiltrationRecipe(new ItemStack(Items.RAW_COPPER), 0.05, "copper");
        addOreFiltrationRecipe(new ItemStack(Items.RAW_IRON), 0.05, "iron");
        addOreFiltrationRecipe(new ItemStack(Items.RAW_GOLD), 0.005, "gold");
    }

    private void buildFluidTransposerRecipes() {
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.WHITE_CONCRETE_POWDER), new ItemStack(Items.WHITE_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.ORANGE_CONCRETE_POWDER), new ItemStack(Items.ORANGE_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.MAGENTA_CONCRETE_POWDER), new ItemStack(Items.MAGENTA_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.LIGHT_BLUE_CONCRETE_POWDER), new ItemStack(Items.LIGHT_BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.YELLOW_CONCRETE_POWDER), new ItemStack(Items.YELLOW_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.LIME_CONCRETE_POWDER), new ItemStack(Items.LIME_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.PINK_CONCRETE_POWDER), new ItemStack(Items.PINK_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.GRAY_CONCRETE_POWDER), new ItemStack(Items.GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.LIGHT_GRAY_CONCRETE_POWDER), new ItemStack(Items.LIGHT_GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.CYAN_CONCRETE_POWDER), new ItemStack(Items.CYAN_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.PURPLE_CONCRETE_POWDER), new ItemStack(Items.PURPLE_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.BLUE_CONCRETE_POWDER), new ItemStack(Items.BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.BROWN_CONCRETE_POWDER), new ItemStack(Items.BROWN_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.GREEN_CONCRETE_POWDER), new ItemStack(Items.GREEN_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.RED_CONCRETE_POWDER), new ItemStack(Items.RED_CONCRETE));
        addConcreteFluidTransposerRecipe(Ingredient.ofItems(Items.BLACK_CONCRETE_POWDER), new ItemStack(Items.BLACK_CONCRETE));

        addFluidTransposerRecipe(Ingredient.ofItems(Items.SPONGE), new ItemStack(Items.WET_SPONGE), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));
        addFluidTransposerRecipe(Ingredient.ofItems(Items.WET_SPONGE), new ItemStack(Items.SPONGE), FluidTransposerBlockEntity.Mode.EMPTYING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));

        addFluidTransposerRecipe(Ingredient.ofItems(Items.DIRT), new ItemStack(Items.MUD), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(250)));
    }

    private void buildChargerRecipes() {
        addChargerRecipe(ingredientFromTag(ConventionalItemTags.COPPER_INGOTS),
                new ItemStack(EPItems.ENERGIZED_COPPER_INGOT), 4194304);
    }

    private void buildEnergizerRecipes() {
        addEnergizerRecipe(ingredientFromTag(ConventionalItemTags.COPPER_INGOTS),
                new ItemStack(EPItems.ENERGIZED_COPPER_INGOT), 32768);
        addEnergizerRecipe(ingredientFromTag(ConventionalItemTags.GOLD_INGOTS),
                new ItemStack(EPItems.ENERGIZED_GOLD_INGOT), 131072);
        addEnergizerRecipe(Ingredient.ofItems(EPItems.CRYSTAL_MATRIX),
                new ItemStack(EPItems.ENERGIZED_CRYSTAL_MATRIX), 524288);
    }

    private void buildCrystalGrowthChamberRecipes() {
        addCrystalGrowthChamberRecipe(ingredientFromTag(ConventionalItemTags.AMETHYST_GEMS),
                new OutputItemStackWithPercentages(new ItemStack(Items.AMETHYST_SHARD), new double[] {
                        1., 1., .67, .5, .25, .125
                }), 16000);
        addCrystalGrowthChamberRecipe(new IngredientWithCount(Ingredient.ofItems(Items.AMETHYST_BLOCK), 4),
                new OutputItemStackWithPercentages(new ItemStack(Items.BUDDING_AMETHYST), .25),
                32000);
    }

    private void add3x3PackingCraftingRecipe(AdvancementCriterion<InventoryChangedCriterion.Conditions> hasIngredientTrigger,
                                                    Ingredient unpackedInput, ItemConvertible packedItem, CraftingRecipeCategory category,
                                                    String group, String recipeIdSuffix) {
        addShapedCraftingRecipe(hasIngredientTrigger, Map.of(
                '#', unpackedInput
        ), new String[] {
                "###",
                "###",
                "###"
        }, new ItemStack(packedItem), category, group, recipeIdSuffix);
    }
    private void add3x3UnpackingCraftingRecipe(AdvancementCriterion<InventoryChangedCriterion.Conditions> hasIngredientTrigger,
                                                      Ingredient packedInput, ItemConvertible unpackedItem, CraftingRecipeCategory category,
                                                      String group, String recipeIdSuffix) {
        addShapelessCraftingRecipe(hasIngredientTrigger, List.of(
                packedInput
        ), new ItemStack(unpackedItem, 9), category, group, recipeIdSuffix);
    }
    private void addMetalIngotCraftingRecipes(TagKey<Item> nuggetInput,
                                                     TagKey<Item> blockInput, ItemConvertible ingotItem, String metalName) {
        add3x3PackingCraftingRecipe(conditionsFromTag(nuggetInput), ingredientFromTag(nuggetInput), ingotItem,
                CraftingRecipeCategory.MISC, metalName + "_ingot", "_from_nuggets");
        add3x3UnpackingCraftingRecipe(conditionsFromTag(blockInput), ingredientFromTag(blockInput), ingotItem,
                CraftingRecipeCategory.MISC, metalName + "_ingot", "_from_" + metalName + "_block");
    }
    private void addMetalNuggetCraftingRecipe(TagKey<Item> ingotInput, ItemConvertible nuggetItem) {
        addShapelessCraftingRecipe(conditionsFromTag(ingotInput), List.of(
                ingredientFromTag(ingotInput)
        ), new ItemStack(nuggetItem, 9), CraftingRecipeCategory.MISC);
    }
    private void addMetalPlateCraftingRecipe(TagKey<Item> ingotInput, ItemConvertible plateItem) {
        addShapelessCraftingRecipe(conditionsFromTag(ingotInput), List.of(
                ingredientFromTag(CommonItemTags.TOOLS_HAMMERS),
                ingredientFromTag(ingotInput)
        ), new ItemStack(plateItem), CraftingRecipeCategory.MISC);
    }
    private void addMetalWireCraftingRecipe(TagKey<Item> plateInput, ItemConvertible wireItem) {
        addShapelessCraftingRecipe(conditionsFromTag(plateInput), List.of(
                ingredientFromTag(CommonItemTags.TOOLS_CUTTERS),
                ingredientFromTag(plateInput)
        ), new ItemStack(wireItem, 2), CraftingRecipeCategory.MISC);
    }
    private void addHammerCraftingRecipe(TagKey<Item> materialInput, ItemConvertible hammerItem) {
        addShapedCraftingRecipe(conditionsFromTag(materialInput), Map.of(
                'S', ingredientFromTag(ConventionalItemTags.WOODEN_RODS),
                'M', ingredientFromTag(materialInput)
        ), new String[] {
                " M ",
                " SM",
                "S  "
        }, new ItemStack(hammerItem), CraftingRecipeCategory.MISC);
    }
    private void addBasicCableCraftingRecipes(TagKey<Item> ingotInput, TagKey<Item> wireInput,
                                                     ItemStack cableItem) {
        addCableCraftingRecipe(ingotInput, cableItem);
        addCableUsingWireCraftingRecipe(wireInput, cableItem);
    }
    private void addCableUsingWireCraftingRecipe(TagKey<Item> wireInput,
                                                        ItemStack cableItem) {
        addShapedCraftingRecipe(conditionsFromTag(wireInput), Map.of(
                'W', ingredientFromTag(wireInput),
                'I', Ingredient.ofItems(EPItems.CABLE_INSULATOR)
        ), new String[] {
                "IWI",
                "IWI",
                "IWI"
        }, cableItem, CraftingRecipeCategory.MISC, getItemPath(cableItem.getItem()), "_using_wire");
    }
    private void addCableCraftingRecipe(TagKey<Item> ingotInput,
                                               ItemStack cableItem) {
        addShapedCraftingRecipe(conditionsFromTag(ingotInput), Map.of(
                'I', ingredientFromTag(ingotInput),
                'i', Ingredient.ofItems(EPItems.CABLE_INSULATOR)
        ), new String[] {
                "iIi",
                "iIi",
                "iIi"
        }, cableItem, CraftingRecipeCategory.MISC, getItemPath(cableItem.getItem()));
    }
    private void addShapedCraftingRecipe(AdvancementCriterion<InventoryChangedCriterion.Conditions> hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingRecipeCategory category) {
        addShapedCraftingRecipe(hasIngredientTrigger, key, pattern, result, category, "");
    }
    private void addShapedCraftingRecipe(AdvancementCriterion<InventoryChangedCriterion.Conditions> hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingRecipeCategory category,
                                                String group) {
        addShapedCraftingRecipe(hasIngredientTrigger, key, pattern, result, category, group, "");
    }
    private void addShapedCraftingRecipe(AdvancementCriterion<InventoryChangedCriterion.Conditions> hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingRecipeCategory category,
                                                String group, String recipeIdSuffix) {
        addShapedCraftingRecipe(hasIngredientTrigger, key, pattern, result, category, group, recipeIdSuffix, "");
    }
    private void addShapedCraftingRecipe(AdvancementCriterion<InventoryChangedCriterion.Conditions> hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingRecipeCategory category,
                                                String group, String recipeIdSuffix, String recipeIdPrefix) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdPrefix + getItemPath(result.getItem()) + recipeIdSuffix);

        Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(getKey(recipeId)))
                .criterion("has_the_ingredient", hasIngredientTrigger)
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        ShapedRecipe recipe = new ShapedRecipe(Objects.requireNonNullElse(group, ""),
                category, RawShapedRecipe.create(key, pattern), result);
        exporter.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }
    private void addShapelessCraftingRecipe(AdvancementCriterion<InventoryChangedCriterion.Conditions> hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingRecipeCategory category) {
        addShapelessCraftingRecipe(hasIngredientTrigger, inputs, result, category, "");
    }
    private void addShapelessCraftingRecipe(AdvancementCriterion<InventoryChangedCriterion.Conditions> hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingRecipeCategory category,
                                                   String group) {
        addShapelessCraftingRecipe(hasIngredientTrigger, inputs, result, category, group, "");
    }
    private void addShapelessCraftingRecipe(AdvancementCriterion<InventoryChangedCriterion.Conditions> hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingRecipeCategory category,
                                                   String group, String recipeIdSuffix) {
        addShapelessCraftingRecipe(hasIngredientTrigger, inputs, result, category, group, recipeIdSuffix, "");
    }
    private void addShapelessCraftingRecipe(AdvancementCriterion<InventoryChangedCriterion.Conditions> hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingRecipeCategory category,
                                                   String group, String recipeIdSuffix, String recipeIdPrefix) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdPrefix + getItemPath(result.getItem()) + recipeIdSuffix);

        Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(getKey(recipeId)))
                .criterion("has_the_ingredient", hasIngredientTrigger)
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        ShapelessRecipe recipe = new ShapelessRecipe(Objects.requireNonNullElse(group, ""), category, result,
                DefaultedList.copyOf(null, inputs.toArray(Ingredient[]::new)));
        exporter.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }
    private void addCustomCraftingRecipe(Function<CraftingRecipeCategory, ? extends SpecialCraftingRecipe> customRecipeFactory,
                                                CraftingRecipeCategory category, String recipeIdString) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdString);

        SpecialCraftingRecipe recipe = customRecipeFactory.apply(category);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addBlastingAndSmeltingRecipes(ItemConvertible ingredient, ItemStack result,
                                                      CookingRecipeCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }
    private void addBlastingAndSmeltingRecipes(TagKey<Item> ingredient, ItemStack result,
                                                      CookingRecipeCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }

    private void addSmeltingRecipe(ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemPath(result.getItem()));

        addSmeltingRecipe(ingredient, result, category, time, xp, group, recipeId);
    }
    private void addSmeltingRecipe(ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemPath(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(ingredient, result, category, time, xp, group, recipeId);
    }
    private void addSmeltingRecipe(ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, Identifier recipeId) {
        Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(getKey(recipeId)))
                .criterion("has_the_ingredient", conditionsFromItem(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        AbstractCookingRecipe recipe = new SmeltingRecipe(Objects.requireNonNullElse(group, ""),
                category, Ingredient.ofItems(ingredient), result, xp, time);
        exporter.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }
    private void addSmeltingRecipe(TagKey<Item> ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemPath(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(getKey(recipeId)))
                .criterion("has_the_ingredient", conditionsFromTag(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        AbstractCookingRecipe recipe = new SmeltingRecipe(Objects.requireNonNullElse(group, ""),
                category, ingredientFromTag(ingredient), result, xp, time);
        exporter.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }

    private void addBlastingRecipe(ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("blasting/" +
                getItemPath(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(getKey(recipeId)))
                .criterion("has_the_ingredient", conditionsFromItem(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        AbstractCookingRecipe recipe = new BlastingRecipe(Objects.requireNonNullElse(group, ""),
                category, Ingredient.ofItems(ingredient), result, xp, time);
        exporter.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }
    private void addBlastingRecipe(TagKey<Item> ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("blasting/" +
                getItemPath(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(getKey(recipeId)))
                .criterion("has_the_ingredient", conditionsFromTag(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        AbstractCookingRecipe recipe = new BlastingRecipe(Objects.requireNonNullElse(group, ""),
                category, ingredientFromTag(ingredient), result, xp, time);
        exporter.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }

    private void addNetheriteSmithingUpgradeRecipe(Ingredient base, ItemStack output) {
        Identifier recipeId = EPAPI.id("smithing/" +
                getItemPath(output.getItem()));

        Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(getKey(recipeId)))
                .criterion("has_the_ingredient", conditionsFromTag(ConventionalItemTags.NETHERITE_INGOTS))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        SmithingTransformRecipe recipe = new SmithingTransformRecipe(Optional.of(Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)),
                base, Optional.of(ingredientFromTag(ConventionalItemTags.NETHERITE_INGOTS)),
                new TransmuteRecipeResult(output.getRegistryEntry(), output.getCount(), output.getComponentChanges()));
        exporter.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }

    private void addAlloyFurnaceRecipe(IngredientWithCount[] inputs, ItemStack output,
                                              int ticks) {
        addAlloyFurnaceRecipe(inputs, output, OutputItemStackWithPercentages.EMPTY, ticks);
    }
    private void addAlloyFurnaceRecipe(IngredientWithCount[] inputs, ItemStack output,
                                              OutputItemStackWithPercentages secondaryOutput, int ticks) {
        Identifier recipeId = EPAPI.id("alloy_furnace/" +
                getItemPath(output.getItem()));

        AlloyFurnaceRecipe recipe = new AlloyFurnaceRecipe(output, secondaryOutput, inputs, ticks);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addPressMoldMakerRecipe(int clayCount, ItemStack output) {
        Identifier recipeId = EPAPI.id("press_mold_maker/" +
                getItemPath(output.getItem()));

        PressMoldMakerRecipe recipe = new PressMoldMakerRecipe(output, clayCount);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addPlateCompressorRecipes(Ingredient ingotInput,
                                                  Ingredient blockInput, ItemStack output, String metalName) {
        addPlateCompressorIngotRecipe(ingotInput, output, metalName);
        addCompressorRecipe(blockInput, output.copyWithCount(9), metalName + "_block");
    }
    private void addPlateCompressorIngotRecipe(Ingredient ingotInput,
                                                      ItemStack output, String metalName) {
        addCompressorRecipe(ingotInput, output, metalName + "_ingot");
    }
    private void addCompressorRecipe(Ingredient input, ItemStack output, String recipeIngredientName) {
        addCompressorRecipe(new IngredientWithCount(input), output, recipeIngredientName);
    }
    private void addCompressorRecipe(IngredientWithCount input, ItemStack output, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("compressing/" +
                getItemPath(output.getItem()) + "_from_compressing_" + recipeIngredientName);

        CompressorRecipe recipe = new CompressorRecipe(output, input);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addCrusherRecipe(Ingredient input, ItemStack output,
                                         String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("crusher/" +
                getItemPath(output.getItem()) + "_from_crushing_" + recipeIngredientName);

        CrusherRecipe recipe = new CrusherRecipe(output, input);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addBasicMetalPulverizerRecipes(Ingredient oreInput,
                                                       Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                       Ingredient ingotInput, ItemStack output, String metalName) {
        addPulverizerRecipe(oreInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., .25
        }, new double[] {
                1., 1, .5, .25
        }), metalName + "_ores");

        addRawMetalAndIngotPulverizerRecipes(rawMetalInput, rawMetalBlockInput, ingotInput, output, metalName);
    }
    private void addRawMetalAndIngotPulverizerRecipes(Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                      Ingredient ingotInput, ItemStack output, String metalName) {
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
                new PulverizerRecipe.OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0], new double[0]), recipeIngredientName);
    }
    private void addPulverizerRecipe(Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            PulverizerRecipe.OutputItemStackWithPercentages secondaryOutput,
                                            String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("pulverizer/" +
                getItemPath(output.output().getItem()) + "_from_pulverizer_" + recipeIngredientName);

        PulverizerRecipe recipe = new PulverizerRecipe(output, secondaryOutput, input);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addBasicWoodSawmillRecipe(ItemStack planksItem,
                                           Ingredient logsInput, Ingredient fenceInput, Ingredient fenceGateInput,
                                           Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                           Ingredient signInput, Ingredient shelfInput, Ingredient boatInput,
                                           Ingredient chestBoatInput, boolean isRaft, String woodName) {
        addSawmillRecipe(logsInput, planksItem.copyWithCount(6), 1, getItemPath(planksItem.getItem()),
                woodName + "_logs");

        addBasicWoodWithoutLogsSawmillRecipe(planksItem, fenceInput, fenceGateInput, doorInput, trapdoorInput,
                pressurePlateInput, signInput, shelfInput, boatInput, chestBoatInput, isRaft, woodName);
    }
    private void addBasicWoodWithoutLogsSawmillRecipe(ItemStack planksItem,
                                                      Ingredient fenceInput, Ingredient fenceGateInput,
                                                      Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                      Ingredient signInput, Ingredient shelfInput, Ingredient boatInput,
                                                      Ingredient chestBoatInput, boolean isRaft, String woodName) {
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(planksItem, fenceInput, fenceGateInput, doorInput,
                trapdoorInput, pressurePlateInput, signInput, shelfInput, woodName);

        addSawmillRecipe(boatInput, planksItem.copyWithCount(4), 3, getItemPath(planksItem.getItem()),
                woodName + (isRaft?"_raft":"_boat"));
        addSawmillRecipe(chestBoatInput, planksItem.copyWithCount(5), 7, getItemPath(planksItem.getItem()),
                woodName + (isRaft?"_chest_raft":"_chest_boat"));
    }
    private void addBasicWoodWithoutLogsAndBoatsSawmillRecipe(ItemStack planksItem,
                                                              Ingredient fenceInput, Ingredient fenceGateInput,
                                                              Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                              Ingredient signInput, Ingredient shelfInput, String woodName) {
        addSawmillRecipe(fenceInput, planksItem, 2, getItemPath(planksItem.getItem()),
                woodName + "_fence");
        addSawmillRecipe(fenceGateInput, planksItem.copyWithCount(2), 3, getItemPath(planksItem.getItem()),
                woodName + "_fence_gate");
        addSawmillRecipe(doorInput, planksItem, 3, getItemPath(planksItem.getItem()),
                woodName + "_door");
        addSawmillRecipe(trapdoorInput, planksItem.copyWithCount(2), 3, getItemPath(planksItem.getItem()),
                woodName + "_trapdoor");
        addSawmillRecipe(pressurePlateInput, planksItem, 2, getItemPath(planksItem.getItem()),
                woodName + "_pressure_plate");
        addSawmillRecipe(signInput, planksItem.copyWithCount(2), 1, getItemPath(planksItem.getItem()),
                woodName + "_sign");
        addSawmillRecipe(shelfInput, planksItem.copyWithCount(3), 1, getItemPath(planksItem.getItem()),
                woodName + "_shelf");
    }
    private void addSawmillRecipe(Ingredient input, ItemStack output,
                                         int sawdustAmount, String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillRecipe recipe = new SawmillRecipe(output, input, sawdustAmount);
        exporter.accept(getKey(recipeId), recipe, null);
    }
    private void addSawmillRecipe(Ingredient input, ItemStack output,
                                         ItemStack secondaryOutput, String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillRecipe recipe = new SawmillRecipe(output, secondaryOutput, input);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addBasicFlowerGrowingRecipe(ItemConvertible flowerItem,
                                                    String outputName) {
        addPlantGrowthChamberRecipe(Ingredient.ofItems(flowerItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., 1., .33
                })
        }, 16000, outputName, getItemPath(flowerItem));
    }
    private void addBasicMushroomsGrowingRecipe(ItemConvertible mushroomItem,
                                                       String outputName) {
        addPlantGrowthChamberRecipe(Ingredient.ofItems(mushroomItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(mushroomItem), new double[] {
                        1., 1., .5, .25
                })
        }, 16000, outputName, getItemPath(mushroomItem));
    }
    private void addBasicAncientFlowerGrowingRecipe(ItemConvertible seedItem,
                                                           ItemConvertible flowerItem, String outputName) {
        addPlantGrowthChamberRecipe(Ingredient.ofItems(seedItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(seedItem), new double[] {
                        1., .33, .15
                }),
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., .15
                })
        }, 16000, outputName, getItemPath(seedItem));
    }
    private void addPlantGrowthChamberRecipe(Ingredient input,
                                                    OutputItemStackWithPercentages[] outputs, int ticks,
                                                    String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("growing/" +
                outputName + "_from_growing_" + recipeIngredientName);

        PlantGrowthChamberRecipe recipe = new PlantGrowthChamberRecipe(outputs, input, ticks);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addPlantGrowthChamberFertilizerRecipe(Ingredient input,
                                                              double speedMultiplier, double energyConsumptionMultiplier,
                                                              String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("growing/fertilizer/" +
                recipeIngredientName);

        PlantGrowthChamberFertilizerRecipe recipe = new PlantGrowthChamberFertilizerRecipe(input, speedMultiplier,
                energyConsumptionMultiplier);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addGearMetalPressRecipe(Ingredient input, ItemStack output) {
        addMetalPressRecipe(new IngredientWithCount(input, 2), output, new ItemStack(EPItems.GEAR_PRESS_MOLD));
    }
    private void addRodMetalPressRecipe(Ingredient input, ItemStack output) {
        addMetalPressRecipe(input, output.copyWithCount(2), new ItemStack(EPItems.ROD_PRESS_MOLD));
    }
    private void addWireMetalPressRecipe(Ingredient input, ItemStack output) {
        addMetalPressRecipe(input, output.copyWithCount(3), new ItemStack(EPItems.WIRE_PRESS_MOLD));
    }
    private void addMetalPressRecipe(Ingredient input, ItemStack output,
                                            ItemStack pressMold) {
        addMetalPressRecipe(new IngredientWithCount(input), output, pressMold);
    }
    private void addMetalPressRecipe(IngredientWithCount input, ItemStack output,
                                            ItemStack pressMold) {
        Identifier recipeId = EPAPI.id("metal_press/" +
                getItemPath(output.getItem()));

        MetalPressRecipe recipe = new MetalPressRecipe(output, pressMold, input);
        exporter.accept(getKey(recipeId), recipe, null);
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
        exporter.accept(getKey(recipeId), recipe, null);
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
        exporter.accept(getKey(recipeId), recipe, null);
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
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addStoneSolidifierRecipe(int waterAmount, int lavaAmount, ItemStack output) {
        Identifier recipeId = EPAPI.id("stone_solidifier/" +
                getItemPath(output.getItem()));

        StoneSolidifierRecipe recipe = new StoneSolidifierRecipe(output, waterAmount, lavaAmount);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addAssemblingMachineRecipe(IngredientWithCount[] inputs, ItemStack output) {
        Identifier recipeId = EPAPI.id("assembling/" +
                getItemPath(output.getItem()));

        AssemblingMachineRecipe recipe = new AssemblingMachineRecipe(output, inputs);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addOreFiltrationRecipe(ItemStack oreOutput, double oreOutputPercentage,
                                               String oreName) {
        addFiltrationPlantRecipe(new OutputItemStackWithPercentages(new ItemStack(EPItems.STONE_PEBBLE), .33),
                new OutputItemStackWithPercentages(oreOutput, oreOutputPercentage), Registries.ITEM.getId(oreOutput.getItem()),
                oreName + "_ore_filtration");
    }
    private void addFiltrationPlantRecipe(OutputItemStackWithPercentages output,
                                                 Identifier icon, String recipeName) {
        addFiltrationPlantRecipe(output, OutputItemStackWithPercentages.EMPTY, icon, recipeName);
    }
    private void addFiltrationPlantRecipe(OutputItemStackWithPercentages output,
                                                 OutputItemStackWithPercentages secondaryOutput, Identifier icon,
                                                 String recipeName) {
        Identifier recipeId = EPAPI.id("filtration_plant/" +
                recipeName);

        FiltrationPlantRecipe recipe = new FiltrationPlantRecipe(output, secondaryOutput, icon);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addConcreteFluidTransposerRecipe(Ingredient input, ItemStack output) {
        addFluidTransposerRecipe(input, output, FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));
    }
    private void addFluidTransposerRecipe(Ingredient input, ItemStack output,
                                                 FluidTransposerBlockEntity.Mode mode, FluidStack fluid) {
        Identifier recipeId = EPAPI.id("fluid_transposer/" +
                getItemPath(output.getItem()));

        FluidTransposerRecipe recipe = new FluidTransposerRecipe(mode, output, input, fluid);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addChargerRecipe(Ingredient input, ItemStack output, int energyConsumption) {
        Identifier recipeId = EPAPI.id("charger/" +
                getItemPath(output.getItem()));

        ChargerRecipe recipe = new ChargerRecipe(output, input, energyConsumption);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addEnergizerRecipe(Ingredient input, ItemStack output, int energyConsumption) {
        Identifier recipeId = EPAPI.id("energizer/" +
                getItemPath(output.getItem()));

        EnergizerRecipe recipe = new EnergizerRecipe(output, input, energyConsumption);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private void addCrystalGrowthChamberRecipe(Ingredient input, OutputItemStackWithPercentages output,
                                                      int ticks) {
        addCrystalGrowthChamberRecipe(new IngredientWithCount(input, 1), output, ticks);
    }
    private void addCrystalGrowthChamberRecipe(IngredientWithCount input, OutputItemStackWithPercentages output,
                                                      int ticks) {
        Identifier recipeId = EPAPI.id("crystal_growing/" +
                getItemPath(output.output().getItem()));

        CrystalGrowthChamberRecipe recipe = new CrystalGrowthChamberRecipe(output, input, ticks);
        exporter.accept(getKey(recipeId), recipe, null);
    }

    private static RegistryKey<Recipe<?>> getKey(Identifier recipeId) {
        return RegistryKey.of(RegistryKeys.RECIPE, recipeId);
    }
}
