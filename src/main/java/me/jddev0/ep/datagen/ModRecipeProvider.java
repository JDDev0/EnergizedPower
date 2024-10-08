package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.datagen.recipe.*;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import me.jddev0.ep.recipe.PulverizerRecipe;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> output) {
        buildCraftingRecipes(output);
        buildCookingRecipes(output);
        buildSmithingRecipes(output);
        buildPressMoldMakerRecipes(output);
        buildAlloyFurnaceRecipes(output);
        buildCompressorRecipes(output);
        buildCrusherRecipes(output);
        buildPulverizerRecipes(output);
        buildSawmillRecipes(output);
        buildPlantGrowthChamberRecipes(output);
        buildPlantGrowthChamberFertilizerRecipes(output);
        buildMetalPressRecipes(output);
        buildHeatGeneratorRecipes(output);
        buildThermalGeneratorRecipes(output);
        buildStoneSolidifierRecipes(output);
        buildAssemblingMachineRecipes(output);
        buildFiltrationPlantRecipes(output);
        buildFluidTransposerRecipes(output);
        buildChargerRecipes(output);
        buildEnergizerRecipes(output);
        buildCrystalGrowthChamberRecipes(output);
    }

    private void buildCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        buildItemIngredientsCraftingRecipes(output);

        buildFertilizerCraftingRecipes(output);

        buildUpgradeModuleCraftingRecipes(output);

        buildToolsCraftingRecipes(output);

        buildEnergyItemsCraftingRecipes(output);

        buildItemTransportCraftingRecipes(output);
        buildFluidTransportCraftingRecipes(output);
        buildEnergyTransportCraftingRecipes(output);

        buildMachineCraftingRecipes(output);

        buildMiscCraftingRecipes(output);

        buildCustomCraftingRecipes(output);
    }
    private void buildItemIngredientsCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        add3x3UnpackingCraftingRecipe(output, conditionsFromItem(ModBlocks.SAWDUST_BLOCK),
                Ingredient.ofItems(ModBlocks.SAWDUST_BLOCK), ModItems.SAWDUST,
                CraftingRecipeCategory.MISC, "", "_from_sawdust_block");
        add3x3PackingCraftingRecipe(output, conditionsFromTag(CommonItemTags.SAW_DUSTS),
                Ingredient.fromTag(CommonItemTags.SAW_DUSTS), ModBlocks.SAWDUST_BLOCK_ITEM,
                CraftingRecipeCategory.MISC, "", "");

        add3x3UnpackingCraftingRecipe(output, conditionsFromTag(CommonItemTags.SILICON_BLOCKS),
                Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS), ModItems.SILICON,
                CraftingRecipeCategory.MISC, "", "_from_silicon_block");
        add3x3PackingCraftingRecipe(output, conditionsFromTag(CommonItemTags.SILICON),
                Ingredient.fromTag(CommonItemTags.SILICON), ModBlocks.SILICON_BLOCK_ITEM,
                CraftingRecipeCategory.MISC, "", "");

        addMetalNuggetCraftingRecipe(output, CommonItemTags.TIN_NUGGETS, ModItems.TIN_NUGGET);
        addMetalIngotCraftingRecipes(output, CommonItemTags.TIN_NUGGETS, CommonItemTags.TIN_BLOCKS,
                ModItems.TIN_INGOT, "tin");

        add3x3PackingCraftingRecipe(output, conditionsFromTag(CommonItemTags.TIN_INGOTS),
                Ingredient.fromTag(CommonItemTags.TIN_INGOTS), ModBlocks.TIN_BLOCK_ITEM,
                CraftingRecipeCategory.MISC, "", "");

        add3x3UnpackingCraftingRecipe(output, conditionsFromTag(CommonItemTags.RAW_TIN_BLOCKS),
                Ingredient.fromTag(CommonItemTags.RAW_TIN_BLOCKS), ModItems.RAW_TIN,
                CraftingRecipeCategory.MISC, "", "");
        add3x3PackingCraftingRecipe(output, conditionsFromTag(CommonItemTags.RAW_TIN_ORES),
                Ingredient.fromTag(CommonItemTags.RAW_TIN_ORES), ModBlocks.RAW_TIN_BLOCK_ITEM,
                CraftingRecipeCategory.MISC, "", "");

        addMetalPlateCraftingRecipe(output, CommonItemTags.TIN_INGOTS, ModItems.TIN_PLATE);
        addMetalPlateCraftingRecipe(output, ConventionalItemTags.COPPER_INGOTS, ModItems.COPPER_PLATE);
        addMetalPlateCraftingRecipe(output, ConventionalItemTags.IRON_INGOTS, ModItems.IRON_PLATE);
        addMetalPlateCraftingRecipe(output, ConventionalItemTags.GOLD_INGOTS, ModItems.GOLD_PLATE);

        addMetalWireCraftingRecipe(output, CommonItemTags.TIN_PLATES, ModItems.TIN_WIRE);
        addMetalWireCraftingRecipe(output, CommonItemTags.COPPER_PLATES, ModItems.COPPER_WIRE);
        addMetalWireCraftingRecipe(output, CommonItemTags.GOLD_PLATES, ModItems.GOLD_WIRE);

        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.SILICON), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'Q', Ingredient.fromTag(ConventionalItemTags.QUARTZ),
                'T', Ingredient.fromTag(CommonItemTags.TIN_INGOTS),
                'C', Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS)
        ), new String[] {
                " C ",
                "SQS",
                " T "
        }, new ItemStack(ModItems.BASIC_SOLAR_CELL), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.COPPER_WIRES), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_WIRES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'S', Ingredient.fromTag(CommonItemTags.SILICON)
        ), new String[] {
                "RCR",
                "CIC",
                "SCS"
        }, new ItemStack(ModItems.BASIC_CIRCUIT), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BASIC_CIRCUIT), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GOLD_WIRES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'B', Ingredient.ofItems(ModItems.BASIC_CIRCUIT)
        ), new String[] {
                "GGG",
                "CBC",
                "CBC"
        }, new ItemStack(ModItems.BASIC_UPGRADE_MODULE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ADVANCED_CIRCUIT), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_WIRES),
                'C', Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_PLATES),
                'A', Ingredient.ofItems(ModItems.ADVANCED_CIRCUIT),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "GGG",
                "CBC",
                "CAC"
        }, new ItemStack(ModItems.ADVANCED_UPGRADE_MODULE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE), Map.of(
                'g', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_WIRES),
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'P', Ingredient.ofItems(ModItems.PROCESSING_UNIT),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "ggg",
                "GAG",
                "GPG"
        }, new ItemStack(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromTag(ConventionalItemTags.IRON_INGOTS), Map.of(
                'i', Ingredient.fromTag(CommonItemTags.IRON_NUGGETS),
                'I', Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS)
        ), new String[] {
                " i ",
                "iIi",
                " i "
        }, new ItemStack(ModItems.SAW_BLADE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.SILICON), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'I', Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS),
                'C', Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS)
        ), new String[] {
                "CIC",
                "ISI",
                "CIC"
        }, new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.IRON_GEARS), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                's', Ingredient.fromTag(CommonItemTags.STEEL_INGOTS),
                'I', Ingredient.fromTag(CommonItemTags.IRON_GEARS),
                'R', Ingredient.fromTag(CommonItemTags.IRON_RODS),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "IsR",
                "SBS",
                "RsI"
        }, new ItemStack(ModBlocks.HARDENED_MACHINE_FRAME_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.ENERGIZED_COPPER_INGOTS), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'A', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_INGOTS),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_INGOTS),
                'H', Ingredient.ofItems(ModBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "ESE",
                "AHA",
                "ESE"
        }, new ItemStack(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'A', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSC",
                "SAS",
                "CSC"
        }, new ItemStack(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), CraftingRecipeCategory.MISC);
    }
    private void buildFertilizerCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        addShapedCraftingRecipe(output, conditionsFromItem(Items.BONE_MEAL), Map.of(
                'B', Ingredient.ofItems(Items.BONE_MEAL),
                'D', Ingredient.ofItems(Items.DANDELION),
                'b', Ingredient.ofItems(Items.BLUE_ORCHID),
                'L', Ingredient.fromTag(ConventionalItemTags.LAPIS),
                'A', Ingredient.ofItems(Items.ALLIUM),
                'P', Ingredient.ofItems(Items.POPPY)
        ), new String[] {
                "DBb",
                "BLB",
                "ABP"
        }, new ItemStack(ModItems.BASIC_FERTILIZER, 4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BASIC_FERTILIZER), Map.of(
                'B', Ingredient.ofItems(ModItems.BASIC_FERTILIZER),
                'S', Ingredient.ofItems(Items.SUGAR_CANE),
                'K', Ingredient.ofItems(Items.KELP),
                's', Ingredient.ofItems(Items.SUGAR),
                'b', Ingredient.ofItems(Items.BAMBOO),
                'W', Ingredient.ofItems(Items.WHEAT_SEEDS)
        ), new String[] {
                "SBK",
                "BsB",
                "bBW"
        }, new ItemStack(ModItems.GOOD_FERTILIZER, 4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.GOOD_FERTILIZER), Map.of(
                'G', Ingredient.ofItems(ModItems.GOOD_FERTILIZER),
                'M', Ingredient.ofItems(Items.RED_MUSHROOM),
                'S', Ingredient.ofItems(Items.SWEET_BERRIES),
                'r', Ingredient.fromTag(ConventionalItemTags.RED_DYES),
                'T', Ingredient.ofItems(Items.RED_TULIP),
                'R', Ingredient.ofItems(Items.ROSE_BUSH)
        ), new String[] {
                "MGS",
                "GrG",
                "TGR"
        }, new ItemStack(ModItems.ADVANCED_FERTILIZER, 4), CraftingRecipeCategory.MISC);
    }
    private void buildUpgradeModuleCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CSC",
                "RBR",
                "CSC"
        }, new ItemStack(ModItems.SPEED_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.SPEED_UPGRADE_MODULE_1), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE),
                's', Ingredient.ofItems(ModItems.SPEED_UPGRADE_MODULE_1)
        ), new String[] {
                "CSC",
                "RBR",
                "CsC"
        }, new ItemStack(ModItems.SPEED_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.SPEED_UPGRADE_MODULE_2), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE),
                's', Ingredient.ofItems(ModItems.SPEED_UPGRADE_MODULE_2)
        ), new String[] {
                "CSC",
                "RAR",
                "CsC"
        }, new ItemStack(ModItems.SPEED_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.SPEED_UPGRADE_MODULE_3), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE),
                's', Ingredient.ofItems(ModItems.SPEED_UPGRADE_MODULE_3)
        ), new String[] {
                "CSC",
                "RAR",
                "CsC"
        }, new ItemStack(ModItems.SPEED_UPGRADE_MODULE_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.SPEED_UPGRADE_MODULE_4), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'r', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                's', Ingredient.ofItems(ModItems.SPEED_UPGRADE_MODULE_4)
        ), new String[] {
                "CSC",
                "rRr",
                "CsC"
        }, new ItemStack(ModItems.SPEED_UPGRADE_MODULE_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'G', Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                'R', Ingredient.fromTag(CommonItemTags.REDSTONE_ALLOY_INGOTS),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CGC",
                "RBR",
                "CGC"
        }, new ItemStack(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'G', Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                'R', Ingredient.fromTag(CommonItemTags.REDSTONE_ALLOY_INGOTS),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE),
                'E', Ingredient.ofItems(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1)
        ), new String[] {
                "CGC",
                "RBR",
                "CEC"
        }, new ItemStack(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'G', Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                'R', Ingredient.fromTag(CommonItemTags.REDSTONE_ALLOY_INGOTS),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2)
        ), new String[] {
                "CGC",
                "RAR",
                "CEC"
        }, new ItemStack(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'G', Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                'R', Ingredient.fromTag(CommonItemTags.REDSTONE_ALLOY_INGOTS),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3)
        ), new String[] {
                "CGC",
                "RAR",
                "CEC"
        }, new ItemStack(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'G', Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                'r', Ingredient.fromTag(CommonItemTags.REDSTONE_ALLOY_INGOTS),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4)
        ), new String[] {
                "CGC",
                "rRr",
                "CEC"
        }, new ItemStack(ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'T', Ingredient.fromTag(CommonItemTags.TIN_PLATES),
                'c', Ingredient.fromTag(ItemTags.COALS),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CTC",
                "cBc",
                "CTC"
        }, new ItemStack(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_1), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'T', Ingredient.fromTag(CommonItemTags.TIN_PLATES),
                'c', Ingredient.fromTag(ItemTags.COALS),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE),
                'E', Ingredient.ofItems(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_1)
        ), new String[] {
                "CTC",
                "cBc",
                "CEC"
        }, new ItemStack(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_2), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'T', Ingredient.fromTag(CommonItemTags.TIN_PLATES),
                'c', Ingredient.fromTag(ItemTags.COALS),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_2)
        ), new String[] {
                "CTC",
                "cAc",
                "CEC"
        }, new ItemStack(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'T', Ingredient.fromTag(CommonItemTags.TIN_PLATES),
                'c', Ingredient.fromTag(ItemTags.COALS),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3)
        ), new String[] {
                "CTC",
                "cAc",
                "CEC"
        }, new ItemStack(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'T', Ingredient.fromTag(CommonItemTags.TIN_PLATES),
                'c', Ingredient.fromTag(ItemTags.COALS),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', Ingredient.ofItems(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4)
        ), new String[] {
                "CTC",
                "cRc",
                "CEC"
        }, new ItemStack(ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "GCG",
                "rRr",
                "GCG"
        }, new ItemStack(ModItems.DURATION_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.DURATION_UPGRADE_MODULE_1), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', Ingredient.ofItems(ModItems.DURATION_UPGRADE_MODULE_1)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(ModItems.DURATION_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.DURATION_UPGRADE_MODULE_2), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', Ingredient.ofItems(ModItems.DURATION_UPGRADE_MODULE_2)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(ModItems.DURATION_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.DURATION_UPGRADE_MODULE_3), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', Ingredient.ofItems(ModItems.DURATION_UPGRADE_MODULE_3)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(ModItems.DURATION_UPGRADE_MODULE_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.DURATION_UPGRADE_MODULE_4), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', Ingredient.ofItems(ModItems.DURATION_UPGRADE_MODULE_4)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(ModItems.DURATION_UPGRADE_MODULE_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.DURATION_UPGRADE_MODULE_5), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', Ingredient.ofItems(ModItems.DURATION_UPGRADE_MODULE_5)
        ), new String[] {
                "GDG",
                "CRC",
                "GDG"
        }, new ItemStack(ModItems.DURATION_UPGRADE_MODULE_6), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ADVANCED_UPGRADE_MODULE), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "GRG",
                "RAR",
                "GRG"
        }, new ItemStack(ModItems.RANGE_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.RANGE_UPGRADE_MODULE_1), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'r', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE),
                'R', Ingredient.ofItems(ModItems.RANGE_UPGRADE_MODULE_1)
        ), new String[] {
                "GrG",
                "rAr",
                "GRG"
        }, new ItemStack(ModItems.RANGE_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.RANGE_UPGRADE_MODULE_2), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'r', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE),
                'R', Ingredient.ofItems(ModItems.RANGE_UPGRADE_MODULE_2)
        ), new String[] {
                "GrG",
                "rAr",
                "GRG"
        }, new ItemStack(ModItems.RANGE_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BASIC_UPGRADE_MODULE), Map.of(
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE),
                'F', Ingredient.ofItems(ModBlocks.IRON_FLUID_PIPE_ITEM)
        ), new String[] {
                "IFI",
                "RBR",
                "IFI"
        }, new ItemStack(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1), Map.of(
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE),
                'F', Ingredient.ofItems(ModBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1)
        ), new String[] {
                "IFI",
                "RBR",
                "IEI"
        }, new ItemStack(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2), Map.of(
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE),
                'F', Ingredient.ofItems(ModBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2)
        ), new String[] {
                "IFI",
                "RAR",
                "IEI"
        }, new ItemStack(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3), Map.of(
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE),
                'F', Ingredient.ofItems(ModBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3)
        ), new String[] {
                "IFI",
                "RAR",
                "IEI"
        }, new ItemStack(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4), Map.of(
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'r', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'F', Ingredient.ofItems(ModBlocks.IRON_FLUID_PIPE_ITEM),
                'E', Ingredient.ofItems(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4)
        ), new String[] {
                "IFI",
                "rRr",
                "IEI"
        }, new ItemStack(ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE),
                'b', Ingredient.ofItems(Items.BLAST_FURNACE)
        ), new String[] {
                "CSC",
                "RBR",
                "CbC"
        }, new ItemStack(ModItems.BLAST_FURNACE_UPGRADE_MODULE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                's', Ingredient.fromTag(CommonItemTags.SILICON),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE),
                'S', Ingredient.ofItems(Items.SMOKER)
        ), new String[] {
                "CsC",
                "RBR",
                "CSC"
        }, new ItemStack(ModItems.SMOKER_UPGRADE_MODULE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BASIC_UPGRADE_MODULE), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GOLD_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'b', Ingredient.ofItems(ModItems.BASIC_SOLAR_CELL),
                'B', Ingredient.ofItems(ModItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "GSG",
                "bBb",
                "GSG"
        }, new ItemStack(ModItems.MOON_LIGHT_UPGRADE_MODULE_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.MOON_LIGHT_UPGRADE_MODULE_1), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GOLD_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(ModItems.BASIC_SOLAR_CELL),
                'A', Ingredient.ofItems(ModItems.ADVANCED_UPGRADE_MODULE),
                'M', Ingredient.ofItems(ModItems.MOON_LIGHT_UPGRADE_MODULE_1)
        ), new String[] {
                "GSG",
                "BAB",
                "GMG"
        }, new ItemStack(ModItems.MOON_LIGHT_UPGRADE_MODULE_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.MOON_LIGHT_UPGRADE_MODULE_2), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GOLD_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(ModItems.BASIC_SOLAR_CELL),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'M', Ingredient.ofItems(ModItems.MOON_LIGHT_UPGRADE_MODULE_2)
        ), new String[] {
                "GSG",
                "BRB",
                "GMG"
        }, new ItemStack(ModItems.MOON_LIGHT_UPGRADE_MODULE_3), CraftingRecipeCategory.MISC);
    }
    private void buildToolsCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        addHammerCraftingRecipe(output, ItemTags.PLANKS, ModItems.WOODEN_HAMMER);
        addHammerCraftingRecipe(output, ItemTags.STONE_TOOL_MATERIALS, ModItems.STONE_HAMMER);
        addHammerCraftingRecipe(output, ConventionalItemTags.IRON_INGOTS, ModItems.IRON_HAMMER);
        addHammerCraftingRecipe(output, ConventionalItemTags.GOLD_INGOTS, ModItems.GOLDEN_HAMMER);
        addHammerCraftingRecipe(output, ConventionalItemTags.DIAMONDS, ModItems.DIAMOND_HAMMER);

        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.IRON_PLATES), Map.of(
                'i', Ingredient.fromTag(CommonItemTags.IRON_NUGGETS),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.WOODEN_RODS)
        ), new String[] {
                "I I",
                " i ",
                "S S"
        }, new ItemStack(ModItems.CUTTER), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromTag(ConventionalItemTags.IRON_INGOTS), Map.of(
                'i', Ingredient.fromTag(CommonItemTags.IRON_NUGGETS),
                'I', Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS)
        ), new String[] {
                " I ",
                " iI",
                "i  "
        }, new ItemStack(ModItems.WRENCH), CraftingRecipeCategory.MISC);
    }
    private void buildEnergyItemsCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        addShapedCraftingRecipe(output, conditionsFromTag(ConventionalItemTags.COPPER_INGOTS), Map.of(
                'T', Ingredient.fromTag(CommonItemTags.TIN_NUGGETS),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'c', Ingredient.fromTag(ItemTags.COALS)
        ), new String[] {
                "T T",
                "CRC",
                "CcC"
        }, new ItemStack(ModItems.BATTERY_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BATTERY_1), Map.of(
                'T', Ingredient.fromTag(CommonItemTags.TIN_NUGGETS),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'c', Ingredient.fromTag(ItemTags.COALS),
                'B', Ingredient.ofItems(ModItems.BATTERY_1)
        ), new String[] {
                "T T",
                "CBC",
                "IcI"
        }, new ItemStack(ModItems.BATTERY_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BATTERY_2), Map.of(
                'T', Ingredient.fromTag(CommonItemTags.TIN_NUGGETS),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(ModItems.BATTERY_2)
        ), new String[] {
                "T T",
                "BCB",
                "CSC"
        }, new ItemStack(ModItems.BATTERY_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BATTERY_3), Map.of(
                'T', Ingredient.fromTag(CommonItemTags.TIN_NUGGETS),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(ModItems.BATTERY_3)
        ), new String[] {
                "T T",
                "CBC",
                "SIS"
        }, new ItemStack(ModItems.BATTERY_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BATTERY_4), Map.of(
                'T', Ingredient.fromTag(CommonItemTags.TIN_NUGGETS),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(CommonItemTags.REDSTONE_ALLOY_INGOTS),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(ModItems.BATTERY_4)
        ), new String[] {
                "T T",
                "BSB",
                "IRI"
        }, new ItemStack(ModItems.BATTERY_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BATTERY_5), Map.of(
                'T', Ingredient.fromTag(CommonItemTags.TIN_NUGGETS),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_PLATES),
                'B', Ingredient.ofItems(ModItems.BATTERY_5)
        ), new String[] {
                "T T",
                "EBE",
                "EBE"
        }, new ItemStack(ModItems.BATTERY_6), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BATTERY_6), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GOLD_NUGGETS),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'B', Ingredient.ofItems(ModItems.BATTERY_6)
        ), new String[] {
                "G G",
                "EBE",
                "SBS"
        }, new ItemStack(ModItems.BATTERY_7), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BATTERY_7), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GOLD_NUGGETS),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'A', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'B', Ingredient.ofItems(ModItems.BATTERY_7)
        ), new String[] {
                "G G",
                "EBE",
                "ABA"
        }, new ItemStack(ModItems.BATTERY_8), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.COAL_ENGINE_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'c', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'C', Ingredient.ofItems(ModBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "SIS",
                "RCR",
                "cIc"
        }, new ItemStack(ModItems.INVENTORY_COAL_ENGINE), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.CHARGER_ITEM), Map.of(
                'c', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'C', Ingredient.ofItems(ModBlocks.CHARGER_ITEM)
        ), new String[] {
                "SIS",
                "RCR",
                "cIc"
        }, new ItemStack(ModItems.INVENTORY_CHARGER), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.TELEPORTER_ITEM), Map.of(
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'c', Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_PLATES),
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'T', Ingredient.ofItems(ModBlocks.TELEPORTER_ITEM)
        ), new String[] {
                "CcC",
                "RTR",
                "GcG"
        }, new ItemStack(ModItems.INVENTORY_TELEPORTER), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BATTERY_3), Map.of(
                'b', Ingredient.ofItems(ModItems.BASIC_CIRCUIT),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'B', Ingredient.ofItems(ModItems.BATTERY_3)
        ), new String[] {
                "S S",
                "bRb",
                "CBC"
        }, new ItemStack(ModItems.ENERGY_ANALYZER), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BATTERY_3), Map.of(
                'b', Ingredient.ofItems(ModItems.BASIC_CIRCUIT),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'L', Ingredient.fromTag(ConventionalItemTags.LAPIS),
                'B', Ingredient.ofItems(ModItems.BATTERY_3)
        ), new String[] {
                "S S",
                "bLb",
                "CBC"
        }, new ItemStack(ModItems.FLUID_ANALYZER), CraftingRecipeCategory.MISC);
    }
    private void buildItemTransportCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.IRON_PLATES), Map.of(
                        'L', Ingredient.fromTag(CommonItemTags.LEATHER),
                        'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                        'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS)
                ), new String[] {
                        "   ",
                        "LLL",
                        "IRI"
                }, new ItemStack(ModBlocks.ITEM_CONVEYOR_BELT_ITEM, 6), CraftingRecipeCategory.MISC,
                "item_conveyor_belt", "_from_leather");

        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.IRON_PLATES), Map.of(
                        'K', Ingredient.ofItems(Items.DRIED_KELP),
                        'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                        'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS)
                ), new String[] {
                        "   ",
                        "KKK",
                        "IRI"
                }, new ItemStack(ModBlocks.ITEM_CONVEYOR_BELT_ITEM, 6), CraftingRecipeCategory.MISC,
                "item_conveyor_belt", "_from_dried_kelp");

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COBBLESTONES),
                'c', Ingredient.ofItems(ModBlocks.ITEM_CONVEYOR_BELT_ITEM),
                'H', Ingredient.ofItems(Items.HOPPER)
        ), new String[] {
                "CCC",
                "CHC",
                "CcC"
        }, new ItemStack(ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COBBLESTONES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                'R', Ingredient.fromTag(CommonItemTags.REDSTONE_BLOCKS),
                'L', Ingredient.ofItems(ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CRC",
                "ILI",
                "CRC"
        }, new ItemStack(ModBlocks.ITEM_CONVEYOR_BELT_SORTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COBBLESTONES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'l', Ingredient.ofItems(Items.LEVER),
                'L', Ingredient.ofItems(ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "ClC",
                "ILI",
                "CRC"
        }, new ItemStack(ModBlocks.ITEM_CONVEYOR_BELT_SWITCH_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COBBLESTONES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'L', Ingredient.ofItems(ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CIC",
                "ILI",
                "CRC"
        }, new ItemStack(ModBlocks.ITEM_CONVEYOR_BELT_SPLITTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COBBLESTONES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'L', Ingredient.ofItems(ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CRC",
                "ILI",
                "CIC"
        }, new ItemStack(ModBlocks.ITEM_CONVEYOR_BELT_MERGER_ITEM), CraftingRecipeCategory.MISC);
    }
    private void buildFluidTransportCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.IRON_PLATES), Map.of(
                        'I', Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS),
                        'i', Ingredient.fromTag(CommonItemTags.IRON_PLATES)
                ), new String[] {
                        "IiI",
                        "IiI",
                        "IiI"
                }, new ItemStack(ModBlocks.IRON_FLUID_PIPE_ITEM, 12), CraftingRecipeCategory.MISC,
                "", "", "iron_");

        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.GOLD_PLATES), Map.of(
                'G', Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                'g', Ingredient.fromTag(CommonItemTags.GOLD_PLATES)
        ), new String[] {
                "GgG",
                "GgG",
                "GgG"
        }, new ItemStack(ModBlocks.GOLDEN_FLUID_PIPE_ITEM, 12), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.IRON_PLATES), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GLASS_PANES_COLORLESS),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES)
        ), new String[] {
                "IGI",
                "IGI",
                "IGI"
        }, new ItemStack(ModBlocks.FLUID_TANK_SMALL_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.FLUID_TANK_SMALL_ITEM), Map.of(
                'F', Ingredient.ofItems(ModBlocks.FLUID_TANK_SMALL_ITEM),
                'S', Ingredient.fromTag(CommonItemTags.STEEL_INGOTS)
        ), new String[] {
                "SFS",
                "SFS",
                "SFS"
        }, new ItemStack(ModBlocks.FLUID_TANK_MEDIUM_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.FLUID_TANK_MEDIUM_ITEM), Map.of(
                'F', Ingredient.ofItems(ModBlocks.FLUID_TANK_SMALL_ITEM),
                'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS)
        ), new String[] {
                "IFI",
                "IFI",
                "IFI"
        }, new ItemStack(ModBlocks.FLUID_TANK_LARGE_ITEM), CraftingRecipeCategory.MISC);
    }
    private void buildEnergyTransportCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        addBasicCableCraftingRecipes(output, CommonItemTags.TIN_INGOTS, CommonItemTags.TIN_WIRES,
                new ItemStack(ModBlocks.TIN_CABLE_ITEM, 9));

        addBasicCableCraftingRecipes(output, ConventionalItemTags.COPPER_INGOTS, CommonItemTags.COPPER_WIRES,
                new ItemStack(ModBlocks.COPPER_CABLE_ITEM, 6));
        addBasicCableCraftingRecipes(output, ConventionalItemTags.GOLD_INGOTS, CommonItemTags.GOLD_WIRES,
                new ItemStack(ModBlocks.GOLD_CABLE_ITEM, 6));

        addBasicCableCraftingRecipes(output, CommonItemTags.ENERGIZED_COPPER_INGOTS, CommonItemTags.ENERGIZED_COPPER_WIRES,
                new ItemStack(ModBlocks.ENERGIZED_COPPER_CABLE_ITEM, 3));
        addBasicCableCraftingRecipes(output, CommonItemTags.ENERGIZED_GOLD_INGOTS, CommonItemTags.ENERGIZED_GOLD_WIRES,
                new ItemStack(ModBlocks.ENERGIZED_GOLD_CABLE_ITEM, 3));

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'I', Ingredient.ofItems(ModItems.CABLE_INSULATOR),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX)
        ), new String[] {
                "ICI",
                "ICI",
                "ICI"
        }, new ItemStack(ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'M', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSI",
                "RMR",
                "CSI"
        }, new ItemStack(ModBlocks.LV_TRANSFORMER_1_TO_N_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'M', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSI",
                "SMR",
                "CRI"
        }, new ItemStack(ModBlocks.LV_TRANSFORMER_3_TO_3_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'M', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CRI",
                "SMS",
                "CRI"
        }, new ItemStack(ModBlocks.LV_TRANSFORMER_N_TO_1_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                        'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                        'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                        'M', Ingredient.ofItems(ModBlocks.HARDENED_MACHINE_FRAME_ITEM),
                        'T', Ingredient.ofItems(ModBlocks.LV_TRANSFORMER_1_TO_N_ITEM)
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStack(ModBlocks.MV_TRANSFORMER_1_TO_N_ITEM), CraftingRecipeCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                        'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                        'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                        'M', Ingredient.ofItems(ModBlocks.HARDENED_MACHINE_FRAME_ITEM),
                        'T', Ingredient.ofItems(ModBlocks.LV_TRANSFORMER_3_TO_3_ITEM)
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStack(ModBlocks.MV_TRANSFORMER_3_TO_3_ITEM), CraftingRecipeCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                        'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                        'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                        'M', Ingredient.ofItems(ModBlocks.HARDENED_MACHINE_FRAME_ITEM),
                        'T', Ingredient.ofItems(ModBlocks.LV_TRANSFORMER_N_TO_1_ITEM)
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStack(ModBlocks.MV_TRANSFORMER_N_TO_1_ITEM), CraftingRecipeCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'M', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(ModBlocks.MV_TRANSFORMER_1_TO_N_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'M', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(ModBlocks.MV_TRANSFORMER_3_TO_3_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(ModBlocks.HV_TRANSFORMER_3_TO_3_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'M', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(ModBlocks.MV_TRANSFORMER_N_TO_1_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(ModBlocks.HV_TRANSFORMER_N_TO_1_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', Ingredient.ofItems(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', Ingredient.ofItems(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(ModBlocks.HV_TRANSFORMER_3_TO_3_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(ModBlocks.EHV_TRANSFORMER_3_TO_3_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', Ingredient.ofItems(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(ModBlocks.HV_TRANSFORMER_N_TO_1_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(ModBlocks.EHV_TRANSFORMER_N_TO_1_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'B', Ingredient.ofItems(ModItems.BATTERY_5),
                'M', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CBC",
                "BMB",
                "SBS"
        }, new ItemStack(ModBlocks.BATTERY_BOX_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'B', Ingredient.ofItems(ModItems.BATTERY_8),
                'M', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "EBE",
                "BMB",
                "BSB"
        }, new ItemStack(ModBlocks.ADVANCED_BATTERY_BOX_ITEM), CraftingRecipeCategory.MISC);

        addShapelessCraftingRecipe(output, conditionsFromItem(ModBlocks.BATTERY_BOX_ITEM), List.of(
                Ingredient.ofItems(ModBlocks.BATTERY_BOX_ITEM),
                Ingredient.ofItems(Items.MINECART)
        ), new ItemStack(ModItems.BATTERY_BOX_MINECART), CraftingRecipeCategory.MISC);

        addShapelessCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_BATTERY_BOX_ITEM), List.of(
                Ingredient.ofItems(ModBlocks.ADVANCED_BATTERY_BOX_ITEM),
                Ingredient.ofItems(Items.MINECART)
        ), new ItemStack(ModItems.ADVANCED_BATTERY_BOX_MINECART), CraftingRecipeCategory.MISC);
    }
    private void buildMachineCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        addShapedCraftingRecipe(output, conditionsFromItem(Items.SMOOTH_STONE), Map.of(
                'S', Ingredient.ofItems(Items.SMOOTH_STONE),
                'B', Ingredient.ofItems(Items.BRICKS),
                's', Ingredient.fromTag(ItemTags.SHOVELS)
        ), new String[] {
                "BBB",
                "BsB",
                "SSS"
        }, new ItemStack(ModBlocks.PRESS_MOLD_MAKER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(Items.FURNACE), Map.of(
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'B', Ingredient.ofItems(Items.BRICKS),
                'F', Ingredient.ofItems(Items.FURNACE)
        ), new String[] {
                "III",
                "FIF",
                "BBB"
        }, new ItemStack(ModBlocks.ALLOY_FURNACE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'b', Ingredient.ofItems(ModItems.BASIC_CIRCUIT),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                'T', Ingredient.ofItems(Items.CRAFTING_TABLE)
        ), new String[] {
                "CTC",
                "bBb",
                "ITI"
        }, new ItemStack(ModBlocks.AUTO_CRAFTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME), Map.of(
                'c', Ingredient.ofItems(ModItems.ADVANCED_CIRCUIT),
                'P', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'A', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'a', Ingredient.ofItems(ModBlocks.AUTO_CRAFTER_ITEM)
        ), new String[] {
                "GaG",
                "cAc",
                "PaP"
        }, new ItemStack(ModBlocks.ADVANCED_AUTO_CRAFTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', Ingredient.fromTag(CommonItemTags.SILICON),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', Ingredient.ofItems(Items.STONECUTTER)
        ), new String[] {
                "CsC",
                "SBS",
                "CIC"
        }, new ItemStack(ModBlocks.CRUSHER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'c', Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_PLATES),
                'a', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'F', Ingredient.ofItems(ModBlocks.FLUID_TANK_SMALL_ITEM),
                'A', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'C', Ingredient.ofItems(ModBlocks.CRUSHER_ITEM)
        ), new String[] {
                "aCa",
                "FAF",
                "cCc"
        }, new ItemStack(ModBlocks.ADVANCED_CRUSHER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', Ingredient.ofItems(Items.STONECUTTER)
        ), new String[] {
                "ISI",
                "SBS",
                "ISI"
        }, new ItemStack(ModBlocks.PULVERIZER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_PLATES),
                'a', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'F', Ingredient.ofItems(ModBlocks.FLUID_TANK_SMALL_ITEM),
                'A', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'P', Ingredient.ofItems(ModBlocks.PULVERIZER_ITEM)
        ), new String[] {
                "aPa",
                "FAF",
                "CPC"
        }, new ItemStack(ModBlocks.ADVANCED_PULVERIZER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.ofItems(ModItems.SAW_BLADE),
                's', Ingredient.ofItems(ModItems.SILICON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "sSs",
                "CBC",
                "sIs"
        }, new ItemStack(ModBlocks.SAWMILL_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.ofItems(Items.PISTON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "IPI",
                "PBP",
                "IPI"
        }, new ItemStack(ModBlocks.COMPRESSOR_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.ofItems(Items.PISTON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                'S', Ingredient.fromTag(CommonItemTags.STEEL_INGOTS),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SPS",
                "IBI",
                "SIS"
        }, new ItemStack(ModBlocks.METAL_PRESS_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'I', Ingredient.fromTag(CommonItemTags.IRON_GEARS),
                'i', Ingredient.fromTag(CommonItemTags.IRON_RODS),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                'P', Ingredient.ofItems(ModBlocks.PRESS_MOLD_MAKER_ITEM)
        ), new String[] {
                "IPI",
                "iBi",
                "IPI"
        }, new ItemStack(ModBlocks.AUTO_PRESS_MOLD_MAKER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', Ingredient.fromTag(CommonItemTags.SILICON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', Ingredient.ofItems(Items.STONECUTTER)
        ), new String[] {
                "CSC",
                "sBs",
                "ISI"
        }, new ItemStack(ModBlocks.AUTO_STONECUTTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GLASS_PANES_COLORLESS),
                'D', Ingredient.ofItems(Items.DIRT),
                'W', Ingredient.ofItems(Items.WATER_BUCKET),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SWS",
                "GBG",
                "IDI"
        }, new ItemStack(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                'D', Ingredient.ofItems(Items.DISPENSER)
        ), new String[] {
                "IDS",
                "DBD",
                "SDC"
        }, new ItemStack(ModBlocks.BLOCK_PLACER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                's', Ingredient.fromTag(CommonItemTags.STEEL_INGOTS),
                'i', Ingredient.fromTag(CommonItemTags.IRON_GEARS),
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'B', Ingredient.ofItems(ModItems.BASIC_CIRCUIT),
                'H', Ingredient.ofItems(ModBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iRi",
                "BHB",
                "SsS"
        }, new ItemStack(ModBlocks.ASSEMBLING_MACHINE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'B', Ingredient.ofItems(ModItems.BASIC_CIRCUIT),
                'S', Ingredient.fromTag(CommonItemTags.STEEL_INGOTS),
                'H', Ingredient.ofItems(ModBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'A', Ingredient.ofItems(ModBlocks.ALLOY_FURNACE_ITEM)
        ), new String[] {
                "SAS",
                "BHB",
                "SAS"
        }, new ItemStack(ModBlocks.INDUCTION_SMELTER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'F', Ingredient.ofItems(ModBlocks.FLUID_TANK_SMALL_ITEM),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SIS",
                "IBI",
                "CFC"
        }, new ItemStack(ModBlocks.FLUID_FILLER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'F', Ingredient.ofItems(ModBlocks.FLUID_TANK_SMALL_ITEM),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'i', Ingredient.fromTag(CommonItemTags.IRON_GEARS),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                'H', Ingredient.ofItems(ModBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iSi",
                "FHF",
                "CIC"
        }, new ItemStack(ModBlocks.STONE_SOLIDIFIER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'i', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'D', Ingredient.ofItems(ModBlocks.FLUID_DRAINER_ITEM),
                'F', Ingredient.ofItems(ModBlocks.FLUID_FILLER_ITEM),
                'f', Ingredient.ofItems(ModBlocks.FLUID_TANK_SMALL_ITEM),
                'H', Ingredient.ofItems(ModBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CiC",
                "DHF",
                "IfI"
        }, new ItemStack(ModBlocks.FLUID_TRANSPOSER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'i', Ingredient.fromTag(CommonItemTags.IRON_GEARS),
                'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                'F', Ingredient.ofItems(ModBlocks.FLUID_TANK_SMALL_ITEM),
                'B', Ingredient.ofItems(Items.IRON_BARS),
                'f', Ingredient.ofItems(ModBlocks.IRON_FLUID_PIPE_ITEM),
                'H', Ingredient.ofItems(ModBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iBi",
                "FHF",
                "IfI"
        }, new ItemStack(ModBlocks.FILTRATION_PLANT_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'F', Ingredient.ofItems(ModBlocks.FLUID_TANK_SMALL_ITEM),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CFC",
                "IBI",
                "SIS"
        }, new ItemStack(ModBlocks.FLUID_DRAINER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'F', Ingredient.ofItems(ModBlocks.FLUID_TANK_SMALL_ITEM),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                'P', Ingredient.ofItems(Items.PISTON),
                'p', Ingredient.ofItems(ModBlocks.IRON_FLUID_PIPE_ITEM)
        ), new String[] {
                "RPR",
                "FBF",
                "IpI"
        }, new ItemStack(ModBlocks.FLUID_PUMP_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.IRON_BLOCKS), Map.of(
                'i', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                'B', Ingredient.ofItems(Items.IRON_BARS),
                'G', Ingredient.fromTag(CommonItemTags.GLASS_BLOCKS_COLORLESS)
        ), new String[] {
                "IBI",
                "iGi",
                "IiI"
        }, new ItemStack(ModBlocks.DRAIN_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', Ingredient.fromTag(CommonItemTags.SILICON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS)
        ), new String[] {
                "sCs",
                "IBI",
                "CSC"
        }, new ItemStack(ModBlocks.CHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'a', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'A', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'C', Ingredient.ofItems(ModBlocks.CHARGER_ITEM)
        ), new String[] {
                "SGS",
                "aAa",
                "GCG"
        }, new ItemStack(ModBlocks.ADVANCED_CHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', Ingredient.fromTag(CommonItemTags.SILICON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS)
        ), new String[] {
                "CSC",
                "IBI",
                "sCs"
        }, new ItemStack(ModBlocks.UNCHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'a', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'A', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'U', Ingredient.ofItems(ModBlocks.UNCHARGER_ITEM)
        ), new String[] {
                "GUG",
                "aAa",
                "SGS"
        }, new ItemStack(ModBlocks.ADVANCED_UNCHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.CHARGER_ITEM), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                'c', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                'i', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'H', Ingredient.ofItems(ModBlocks.CHARGER_ITEM)
        ), new String[] {
                "cCc",
                "SHS",
                "iIi"
        }, new ItemStack(ModBlocks.MINECART_CHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_CHARGER_ITEM), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GOLD_BLOCKS),
                'g', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                'c', Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'H', Ingredient.ofItems(ModBlocks.ADVANCED_CHARGER_ITEM)
        ), new String[] {
                "gGg",
                "SHS",
                "cCc"
        }, new ItemStack(ModBlocks.ADVANCED_MINECART_CHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.UNCHARGER_ITEM), Map.of(
                'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                'c', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                'i', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'U', Ingredient.ofItems(ModBlocks.UNCHARGER_ITEM)
        ), new String[] {
                "iIi",
                "SUS",
                "cCc"
        }, new ItemStack(ModBlocks.MINECART_UNCHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_UNCHARGER_ITEM), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GOLD_BLOCKS),
                'g', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                'c', Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'U', Ingredient.ofItems(ModBlocks.ADVANCED_UNCHARGER_ITEM)
        ), new String[] {
                "cCc",
                "SUS",
                "gGg"
        }, new ItemStack(ModBlocks.ADVANCED_MINECART_UNCHARGER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.BASIC_SOLAR_CELL), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GLASS_PANES_COLORLESS),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'I', Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS),
                'C', Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS),
                'B', Ingredient.ofItems(ModItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "BRB",
                "ICI"
        }, new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_1), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.SOLAR_PANEL_ITEM_1), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GLASS_PANES_COLORLESS),
                'C', Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'S', Ingredient.ofItems(ModBlocks.SOLAR_PANEL_ITEM_1),
                'B', Ingredient.ofItems(ModItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SBS",
                "CRC"
        }, new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_2), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.SOLAR_PANEL_ITEM_2), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GLASS_PANES_COLORLESS),
                'R', Ingredient.fromTag(CommonItemTags.REDSTONE_ALLOY_INGOTS),
                's', Ingredient.fromTag(CommonItemTags.SILICON),
                'S', Ingredient.ofItems(ModBlocks.SOLAR_PANEL_ITEM_2),
                'B', Ingredient.ofItems(ModItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SBS",
                "sRs"
        }, new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_3), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.SOLAR_PANEL_ITEM_3), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GLASS_PANES_COLORLESS),
                's', Ingredient.fromTag(CommonItemTags.SILICON),
                'R', Ingredient.fromTag(CommonItemTags.REDSTONE_ALLOY_INGOTS),
                'S', Ingredient.ofItems(ModBlocks.SOLAR_PANEL_ITEM_3),
                'A', Ingredient.ofItems(ModItems.ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SsS",
                "ARA"
        }, new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_4), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.SOLAR_PANEL_ITEM_4), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GLASS_PANES_COLORLESS),
                'a', Ingredient.fromTag(CommonItemTags.AMETHYSTS),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_INGOTS),
                'S', Ingredient.ofItems(ModBlocks.SOLAR_PANEL_ITEM_4),
                'A', Ingredient.ofItems(ModItems.ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SaS",
                "AEA"
        }, new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_5), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.SOLAR_PANEL_ITEM_5), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GLASS_PANES_COLORLESS),
                'A', Ingredient.fromTag(CommonItemTags.AMETHYSTS),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_INGOTS),
                'S', Ingredient.ofItems(ModBlocks.SOLAR_PANEL_ITEM_5),
                'R', Ingredient.ofItems(ModItems.REINFORCED_ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SAS",
                "RER"
        }, new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_6), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                'F', Ingredient.ofItems(Items.FURNACE)
        ), new String[] {
                "ISI",
                "CBC",
                "IFI"
        }, new ItemStack(ModBlocks.COAL_ENGINE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(Items.REDSTONE_LAMP), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'C', Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS),
                'R', Ingredient.ofItems(Items.REDSTONE_LAMP)
        ), new String[] {
                "CSC",
                "SRS",
                "CSC"
        }, new ItemStack(ModBlocks.POWERED_LAMP_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'B', Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                'F', Ingredient.ofItems(Items.FURNACE)
        ), new String[] {
                "CFC",
                "SBS",
                "IFI"
        }, new ItemStack(ModBlocks.POWERED_FURNACE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'a', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'G', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'A', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'P', Ingredient.ofItems(ModBlocks.POWERED_FURNACE_ITEM)
        ), new String[] {
                "GPG",
                "SAS",
                "aPa"
        }, new ItemStack(ModBlocks.ADVANCED_POWERED_FURNACE_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'G', Ingredient.fromTag(CommonItemTags.GOLD_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_PLATES),
                'a', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                'A', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "RaR",
                "GAG",
                "ECE"
        }, new ItemStack(ModBlocks.LIGHTNING_GENERATOR_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'G', Ingredient.fromTag(CommonItemTags.GOLD_PLATES),
                'R', Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_PLATES),
                'a', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'A', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "RaR",
                "GAG",
                "ESE"
        }, new ItemStack(ModBlocks.ENERGIZER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'I', Ingredient.fromTag(CommonItemTags.IRON_BLOCKS),
                'R', Ingredient.fromTag(CommonItemTags.REDSTONE_BLOCKS),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                'a', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'A', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "aRa",
                "IAI",
                "ECE"
        }, new ItemStack(ModBlocks.CHARGING_STATION_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.COAL_ENGINE_ITEM), Map.of(
                's', Ingredient.fromTag(CommonItemTags.SILICON),
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'c', Ingredient.fromTag(CommonItemTags.COPPER_WIRES),
                'C', Ingredient.ofItems(ModBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "cSc",
                "sCs",
                "cSc"
        }, new ItemStack(ModBlocks.HEAT_GENERATOR_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.SILICON),
                'F', Ingredient.ofItems(ModBlocks.FLUID_TANK_SMALL_ITEM),
                'c', Ingredient.fromTag(CommonItemTags.COPPER_PLATES),
                'C', Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS),
                'H', Ingredient.ofItems(ModBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'E', Ingredient.ofItems(ModBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "cHc",
                "SES",
                "CFC"
        }, new ItemStack(ModBlocks.THERMAL_GENERATOR_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'a', Ingredient.ofItems(Items.AMETHYST_BLOCK),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_PLATES),
                'P', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'A', Ingredient.ofItems(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "aPa",
                "EAE",
                "aPa"
        }, new ItemStack(ModBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.ofItems(ModItems.PROCESSING_UNIT),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'a', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'A', Ingredient.fromTag(CommonItemTags.AMETHYSTS),
                'R', Ingredient.ofItems(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CAC",
                "PRP",
                "aEa"
        }, new ItemStack(ModBlocks.WEATHER_CONTROLLER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.ofItems(ModItems.PROCESSING_UNIT),
                'c', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'C', Ingredient.ofItems(Items.CLOCK),
                'A', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'R', Ingredient.ofItems(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "cCc",
                "PRP",
                "AEA"
        }, new ItemStack(ModBlocks.TIME_CONTROLLER_ITEM), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'T', Ingredient.ofItems(ModItems.TELEPORTER_PROCESSING_UNIT),
                'C', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'A', Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_PLATES),
                'E', Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES),
                'S', Ingredient.fromTag(CommonItemTags.SILICON_BLOCKS),
                'R', Ingredient.ofItems(ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CEC",
                "TRT",
                "ASA"
        }, new ItemStack(ModBlocks.TELEPORTER_ITEM), CraftingRecipeCategory.MISC);
    }
    private void buildMiscCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        addShapelessCraftingRecipe(output, InventoryChangedCriterion.Conditions.items(
                Items.BOOK,
                ModBlocks.BASIC_MACHINE_FRAME_ITEM
        ), List.of(
                Ingredient.ofItems(Items.BOOK),
                Ingredient.ofItems(ModBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new ItemStack(ModItems.ENERGIZED_POWER_BOOK), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromTag(CommonItemTags.CHARCOAL_DUSTS), Map.of(
                'P', Ingredient.ofItems(Items.PAPER),
                'C', Ingredient.fromTag(CommonItemTags.CHARCOAL_DUSTS),
                'I', Ingredient.fromTag(CommonItemTags.IRON_PLATES)
        ), new String[] {
                "PCP",
                "CIC",
                "PCP"
        }, new ItemStack(ModItems.CHARCOAL_FILTER), CraftingRecipeCategory.MISC);

        addShapedCraftingRecipe(output, conditionsFromItem(ModItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'A', Ingredient.fromTag(CommonItemTags.AMETHYSTS),
                'E', Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX),
                'e', Ingredient.fromTag(CommonItemTags.ENDER_PEARLS)
        ), new String[] {
                "AEA",
                "EeE",
                "AEA"
        }, new ItemStack(ModItems.TELEPORTER_MATRIX), CraftingRecipeCategory.MISC);
    }
    private void buildCustomCraftingRecipes(Consumer<RecipeJsonProvider> output) {
        addCustomCraftingRecipe(output, ModRecipes.TELEPORTER_MATRIX_SETTINGS_COPY_SERIALIZER, CraftingRecipeCategory.MISC,
                "teleporter_matrix_settings_copy");
    }

    private void buildCookingRecipes(Consumer<RecipeJsonProvider> output) {
        addBlastingAndSmeltingRecipes(output, CommonItemTags.RAW_TIN_ORES, new ItemStack(ModItems.TIN_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "tin_ingot", "raw_tin");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.TIN_ORES, new ItemStack(ModItems.TIN_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "tin_ingot", "tin_ores");

        addBlastingAndSmeltingRecipes(output, CommonItemTags.TIN_DUSTS, new ItemStack(ModItems.TIN_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "tin_ingot", "tin_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.COPPER_DUSTS, new ItemStack(Items.COPPER_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "copper_ingot", "copper_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.IRON_DUSTS, new ItemStack(Items.IRON_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "iron_ingot", "iron_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.GOLD_DUSTS, new ItemStack(Items.GOLD_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "gold_ingot", "gold_dust");

        addBlastingAndSmeltingRecipes(output, ModItems.IRON_HAMMER, new ItemStack(Items.IRON_NUGGET), CookingRecipeCategory.MISC,
                100, .1f, "iron_nugget", "iron_hammer");
        addBlastingAndSmeltingRecipes(output, ModItems.GOLDEN_HAMMER, new ItemStack(Items.GOLD_NUGGET), CookingRecipeCategory.MISC,
                100, .1f, "gold_nugget", "golden_hammer");

        addBlastingAndSmeltingRecipes(output, ConventionalItemTags.QUARTZ, new ItemStack(ModItems.SILICON), CookingRecipeCategory.MISC,
                250, 4.1f, "silicon", "nether_quartz");

        addSmeltingRecipe(output, ModBlocks.SAWDUST_BLOCK_ITEM, new ItemStack(Items.CHARCOAL), CookingRecipeCategory.MISC,
                200, .15f, null, "sawdust_block");

        addSmeltingRecipe(output, ModItems.RAW_GEAR_PRESS_MOLD, new ItemStack(ModItems.GEAR_PRESS_MOLD), CookingRecipeCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_ROD_PRESS_MOLD, new ItemStack(ModItems.ROD_PRESS_MOLD), CookingRecipeCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_WIRE_PRESS_MOLD, new ItemStack(ModItems.WIRE_PRESS_MOLD), CookingRecipeCategory.MISC,
                200, .3f, null);
    }

    private void buildSmithingRecipes(Consumer<RecipeJsonProvider> output) {
        addNetheriteSmithingUpgradeRecipe(output, Ingredient.ofItems(ModItems.DIAMOND_HAMMER),
                new ItemStack(ModItems.NETHERITE_HAMMER));
    }

    private void buildPressMoldMakerRecipes(Consumer<RecipeJsonProvider> output) {
        addPressMoldMakerRecipe(output, 4, new ItemStack(ModItems.RAW_GEAR_PRESS_MOLD));
        addPressMoldMakerRecipe(output, 9, new ItemStack(ModItems.RAW_ROD_PRESS_MOLD));
        addPressMoldMakerRecipe(output, 6, new ItemStack(ModItems.RAW_WIRE_PRESS_MOLD));
    }

    private void buildAlloyFurnaceRecipes(Consumer<RecipeJsonProvider> output) {
        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS)),
                new IngredientWithCount(Ingredient.fromTag(ItemTags.COALS), 3)
        }, new ItemStack(ModItems.STEEL_INGOT), 500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.TIN_INGOTS)),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON)),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStack(ModItems.REDSTONE_ALLOY_INGOT), 2500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.STEEL_INGOTS), 3),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS), 3),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.TIN_INGOTS), 3)
        }, new ItemStack(ModItems.ADVANCED_ALLOY_INGOT), 10000);
    }

    private void buildCompressorRecipes(Consumer<RecipeJsonProvider> output) {
        addCompressorRecipe(output, Ingredient.ofItems(ModItems.STONE_PEBBLE), new ItemStack(Items.COBBLESTONE),
                16, "stone_pebbles");

        addPlateCompressorRecipes(output, Ingredient.fromTag(CommonItemTags.TIN_INGOTS),
                Ingredient.fromTag(CommonItemTags.TIN_BLOCKS), new ItemStack(ModItems.TIN_PLATE),
                "tin");
        addPlateCompressorRecipes(output, Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS),
                Ingredient.fromTag(CommonItemTags.COPPER_BLOCKS), new ItemStack(ModItems.COPPER_PLATE),
                "copper");
        addPlateCompressorRecipes(output, Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS),
                Ingredient.fromTag(CommonItemTags.IRON_BLOCKS), new ItemStack(ModItems.IRON_PLATE),
                "iron");
        addPlateCompressorRecipes(output, Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                Ingredient.fromTag(CommonItemTags.GOLD_BLOCKS), new ItemStack(ModItems.GOLD_PLATE),
                "gold");

        addPlateCompressorIngotRecipe(output, Ingredient.fromTag(CommonItemTags.ADVANCED_ALLOY_INGOTS),
                new ItemStack(ModItems.ADVANCED_ALLOY_PLATE), "advanced_alloy");
        addPlateCompressorIngotRecipe(output, Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_INGOTS),
                new ItemStack(ModItems.ENERGIZED_COPPER_PLATE), "energized_copper");
        addPlateCompressorIngotRecipe(output, Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_INGOTS),
                new ItemStack(ModItems.ENERGIZED_GOLD_PLATE), "energized_gold");
    }

    private void buildCrusherRecipes(Consumer<RecipeJsonProvider> output) {
        addCrusherRecipe(output, Ingredient.ofItems(Items.STONE), new ItemStack(Items.COBBLESTONE),
                "stone");
        addCrusherRecipe(output, Ingredient.ofItems(Items.STONE_BRICKS, Items.CHISELED_STONE_BRICKS, Items.CRACKED_STONE_BRICKS,
                        Items.SMOOTH_STONE), new ItemStack(Items.COBBLESTONE),
                "stone_variants");

        addCrusherRecipe(output, Ingredient.ofItems(Items.MOSSY_STONE_BRICKS), new ItemStack(Items.MOSSY_COBBLESTONE),
                "mossy_stone_bricks");

        addCrusherRecipe(output, Ingredient.ofItems(Items.DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate");
        addCrusherRecipe(output, Ingredient.ofItems(Items.DEEPSLATE_BRICKS, Items.CHISELED_DEEPSLATE, Items.CRACKED_DEEPSLATE_BRICKS,
                        Items.DEEPSLATE_TILES, Items.CRACKED_DEEPSLATE_TILES, Items.POLISHED_DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate_variants");

        addCrusherRecipe(output, Ingredient.ofItems(Items.POLISHED_GRANITE), new ItemStack(Items.GRANITE),
                "polished_granite");
        addCrusherRecipe(output, Ingredient.ofItems(Items.POLISHED_DIORITE), new ItemStack(Items.DIORITE),
                "polished_diorite");
        addCrusherRecipe(output, Ingredient.ofItems(Items.POLISHED_ANDESITE), new ItemStack(Items.ANDESITE),
                "polished_andesite");

        addCrusherRecipe(output, Ingredient.fromTag(CommonItemTags.COBBLESTONES), new ItemStack(Items.GRAVEL),
                "cobblestone");

        addCrusherRecipe(output, Ingredient.fromTag(CommonItemTags.GRAVELS), new ItemStack(Items.SAND),
                "gravel");

        addCrusherRecipe(output, Ingredient.ofItems(Items.SANDSTONE), new ItemStack(Items.SAND),
                "sandstone");
        addCrusherRecipe(output, Ingredient.ofItems(Items.SMOOTH_SANDSTONE, Items.CUT_SANDSTONE,
                        Items.CHISELED_SANDSTONE), new ItemStack(Items.SAND),
                "sandstone_variants");

        addCrusherRecipe(output, Ingredient.ofItems(Items.RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone");
        addCrusherRecipe(output, Ingredient.ofItems(Items.SMOOTH_RED_SANDSTONE, Items.CUT_RED_SANDSTONE,
                        Items.CHISELED_RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone_variants");

        addCrusherRecipe(output, Ingredient.ofItems(Items.POLISHED_BLACKSTONE, Items.POLISHED_BLACKSTONE_BRICKS,
                        Items.CHISELED_POLISHED_BLACKSTONE, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                        Items.GILDED_BLACKSTONE), new ItemStack(Items.BLACKSTONE),
                "blackstone_variants");

        addCrusherRecipe(output, Ingredient.ofItems(Items.SMOOTH_BASALT, Items.POLISHED_BASALT), new ItemStack(Items.BASALT),
                "basalt_variants");
    }

    private void buildPulverizerRecipes(Consumer<RecipeJsonProvider> output) {
        addBasicMetalPulverizerRecipes(output,
                Ingredient.fromTag(CommonItemTags.TIN_ORES), Ingredient.fromTag(CommonItemTags.RAW_TIN_ORES),
                Ingredient.fromTag(CommonItemTags.RAW_TIN_BLOCKS), Ingredient.fromTag(CommonItemTags.TIN_INGOTS),
                new ItemStack(ModItems.TIN_DUST), "tin");
        addBasicMetalPulverizerRecipes(output,
                Ingredient.fromTag(CommonItemTags.IRON_ORES), Ingredient.fromTag(ConventionalItemTags.RAW_IRON_ORES),
                Ingredient.fromTag(ConventionalItemTags.RAW_IRON_BLOCKS), Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS),
                new ItemStack(ModItems.IRON_DUST), "iron");
        addBasicMetalPulverizerRecipes(output,
                Ingredient.fromTag(CommonItemTags.GOLD_ORES), Ingredient.fromTag(ConventionalItemTags.RAW_GOLD_ORES),
                Ingredient.fromTag(ConventionalItemTags.RAW_GOLD_BLOCKS), Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                new ItemStack(ModItems.GOLD_DUST), "gold");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.COPPER_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(ModItems.COPPER_DUST), new double[] {
                        1., 1., 1., 1., .5, .5
                }, new double[] {
                        1., 1., 1., 1., .75, .5, .25
                }),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(ModItems.GOLD_DUST),
                        .1, .2), "copper_ores");
        addRawMetalAndIngotPulverizerRecipes(output,
                Ingredient.fromTag(ConventionalItemTags.RAW_COPPER_ORES),
                Ingredient.fromTag(ConventionalItemTags.RAW_COPPER_BLOCKS), Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS),
                new ItemStack(ModItems.COPPER_DUST), "copper");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.COAL_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.COAL), new double[] {
                        1., 1., .25
                }, new double[] {
                        1., 1., .5, .25
                }), "coal_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.REDSTONE_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.REDSTONE), new double[] {
                        1., 1., 1., 1., 1., .67, .33, .33, .17
                }, new double[] {
                        1., 1., 1., 1., 1., .67, .67, .33, .33, .17
                }), "redstone_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.LAPIS_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.LAPIS_LAZULI), new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .5, .5, .25, .125
                }, new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .75, .5, .5, .25, .125
                }), "lapis_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.EMERALD_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.EMERALD), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "emerald_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.DIAMOND_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.DIAMOND), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "diamond_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(ConventionalItemTags.QUARTZ_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.QUARTZ), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "nether_quartz_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.NETHERITE_SCRAP_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.NETHERITE_SCRAP), new double[] {
                        1., .125, .125
                }, new double[] {
                        1., .25, .25, .125
                }), "ancient_debris");

        addPulverizerRecipe(output, Ingredient.ofItems(Items.CHARCOAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(ModItems.CHARCOAL_DUST),
                        1., 1.), "charcoal");
    }

    private void buildSawmillRecipes(Consumer<RecipeJsonProvider> output) {
        addBasicWoodSawmillRecipe(output, new ItemStack(Items.OAK_PLANKS),
                Ingredient.fromTag(ItemTags.OAK_LOGS), Ingredient.ofItems(Items.OAK_FENCE),
                Ingredient.ofItems(Items.OAK_FENCE_GATE), Ingredient.ofItems(Items.OAK_DOOR),
                Ingredient.ofItems(Items.OAK_TRAPDOOR), Ingredient.ofItems(Items.OAK_PRESSURE_PLATE),
                Ingredient.ofItems(Items.OAK_SIGN), Ingredient.ofItems(Items.OAK_BOAT), Ingredient.ofItems(Items.OAK_CHEST_BOAT),
                false, "oak");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.SPRUCE_PLANKS),
                Ingredient.fromTag(ItemTags.SPRUCE_LOGS), Ingredient.ofItems(Items.SPRUCE_FENCE),
                Ingredient.ofItems(Items.SPRUCE_FENCE_GATE), Ingredient.ofItems(Items.SPRUCE_DOOR),
                Ingredient.ofItems(Items.SPRUCE_TRAPDOOR), Ingredient.ofItems(Items.SPRUCE_PRESSURE_PLATE),
                Ingredient.ofItems(Items.SPRUCE_SIGN), Ingredient.ofItems(Items.SPRUCE_BOAT), Ingredient.ofItems(Items.SPRUCE_CHEST_BOAT),
                false, "spruce");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.BIRCH_PLANKS),
                Ingredient.fromTag(ItemTags.BIRCH_LOGS), Ingredient.ofItems(Items.BIRCH_FENCE),
                Ingredient.ofItems(Items.BIRCH_FENCE_GATE), Ingredient.ofItems(Items.BIRCH_DOOR),
                Ingredient.ofItems(Items.BIRCH_TRAPDOOR), Ingredient.ofItems(Items.BIRCH_PRESSURE_PLATE),
                Ingredient.ofItems(Items.BIRCH_SIGN), Ingredient.ofItems(Items.BIRCH_BOAT), Ingredient.ofItems(Items.BIRCH_CHEST_BOAT),
                false, "birch");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.JUNGLE_PLANKS),
                Ingredient.fromTag(ItemTags.JUNGLE_LOGS), Ingredient.ofItems(Items.JUNGLE_FENCE),
                Ingredient.ofItems(Items.JUNGLE_FENCE_GATE), Ingredient.ofItems(Items.JUNGLE_DOOR),
                Ingredient.ofItems(Items.JUNGLE_TRAPDOOR), Ingredient.ofItems(Items.JUNGLE_PRESSURE_PLATE),
                Ingredient.ofItems(Items.JUNGLE_SIGN), Ingredient.ofItems(Items.JUNGLE_BOAT), Ingredient.ofItems(Items.JUNGLE_CHEST_BOAT),
                false, "jungle");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.ACACIA_PLANKS),
                Ingredient.fromTag(ItemTags.ACACIA_LOGS), Ingredient.ofItems(Items.ACACIA_FENCE),
                Ingredient.ofItems(Items.ACACIA_FENCE_GATE), Ingredient.ofItems(Items.ACACIA_DOOR),
                Ingredient.ofItems(Items.ACACIA_TRAPDOOR), Ingredient.ofItems(Items.ACACIA_PRESSURE_PLATE),
                Ingredient.ofItems(Items.ACACIA_SIGN), Ingredient.ofItems(Items.ACACIA_BOAT), Ingredient.ofItems(Items.ACACIA_CHEST_BOAT),
                false, "acacia");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.DARK_OAK_PLANKS),
                Ingredient.fromTag(ItemTags.DARK_OAK_LOGS), Ingredient.ofItems(Items.DARK_OAK_FENCE),
                Ingredient.ofItems(Items.DARK_OAK_FENCE_GATE), Ingredient.ofItems(Items.DARK_OAK_DOOR),
                Ingredient.ofItems(Items.DARK_OAK_TRAPDOOR), Ingredient.ofItems(Items.DARK_OAK_PRESSURE_PLATE),
                Ingredient.ofItems(Items.DARK_OAK_SIGN), Ingredient.ofItems(Items.DARK_OAK_BOAT), Ingredient.ofItems(Items.DARK_OAK_CHEST_BOAT),
                false, "dark_oak");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.MANGROVE_PLANKS),
                Ingredient.fromTag(ItemTags.MANGROVE_LOGS), Ingredient.ofItems(Items.MANGROVE_FENCE),
                Ingredient.ofItems(Items.MANGROVE_FENCE_GATE), Ingredient.ofItems(Items.MANGROVE_DOOR),
                Ingredient.ofItems(Items.MANGROVE_TRAPDOOR), Ingredient.ofItems(Items.MANGROVE_PRESSURE_PLATE),
                Ingredient.ofItems(Items.MANGROVE_SIGN), Ingredient.ofItems(Items.MANGROVE_BOAT), Ingredient.ofItems(Items.MANGROVE_CHEST_BOAT),
                false, "mangrove");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.CHERRY_PLANKS),
                Ingredient.fromTag(ItemTags.CHERRY_LOGS), Ingredient.ofItems(Items.CHERRY_FENCE),
                Ingredient.ofItems(Items.CHERRY_FENCE_GATE), Ingredient.ofItems(Items.CHERRY_DOOR),
                Ingredient.ofItems(Items.CHERRY_TRAPDOOR), Ingredient.ofItems(Items.CHERRY_PRESSURE_PLATE),
                Ingredient.ofItems(Items.CHERRY_SIGN), Ingredient.ofItems(Items.CHERRY_BOAT), Ingredient.ofItems(Items.CHERRY_CHEST_BOAT),
                false, "cherry");

        addSawmillRecipe(output, Ingredient.fromTag(ItemTags.BAMBOO_BLOCKS), new ItemStack(Items.BAMBOO_PLANKS, 3),
                1, "bamboo_planks", "bamboo_blocks");
        addBasicWoodWithoutLogsSawmillRecipe(output, new ItemStack(Items.BAMBOO_PLANKS),
                Ingredient.ofItems(Items.BAMBOO_FENCE), Ingredient.ofItems(Items.BAMBOO_FENCE_GATE), Ingredient.ofItems(Items.BAMBOO_DOOR),
                Ingredient.ofItems(Items.BAMBOO_TRAPDOOR), Ingredient.ofItems(Items.BAMBOO_PRESSURE_PLATE),
                Ingredient.ofItems(Items.BAMBOO_SIGN), Ingredient.ofItems(Items.BAMBOO_RAFT), Ingredient.ofItems(Items.BAMBOO_CHEST_RAFT),
                true, "bamboo");

        addSawmillRecipe(output, Ingredient.fromTag(ItemTags.CRIMSON_STEMS), new ItemStack(Items.CRIMSON_PLANKS, 6),
                1, "crimson_planks", "crimson_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(output, new ItemStack(Items.CRIMSON_PLANKS),
                Ingredient.ofItems(Items.CRIMSON_FENCE), Ingredient.ofItems(Items.CRIMSON_FENCE_GATE), Ingredient.ofItems(Items.CRIMSON_DOOR),
                Ingredient.ofItems(Items.CRIMSON_TRAPDOOR), Ingredient.ofItems(Items.CRIMSON_PRESSURE_PLATE),
                Ingredient.ofItems(Items.CRIMSON_SIGN), "crimson");

        addSawmillRecipe(output, Ingredient.fromTag(ItemTags.WARPED_STEMS), new ItemStack(Items.WARPED_PLANKS, 6),
                1, "warped_planks", "warped_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(output, new ItemStack(Items.WARPED_PLANKS),
                Ingredient.ofItems(Items.WARPED_FENCE), Ingredient.ofItems(Items.WARPED_FENCE_GATE), Ingredient.ofItems(Items.WARPED_DOOR),
                Ingredient.ofItems(Items.WARPED_TRAPDOOR), Ingredient.ofItems(Items.WARPED_PRESSURE_PLATE),
                Ingredient.ofItems(Items.WARPED_SIGN), "warped");

        addSawmillRecipe(output, Ingredient.ofItems(Items.CRAFTING_TABLE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "crafting_table");
        addSawmillRecipe(output, Ingredient.ofItems(Items.CARTOGRAPHY_TABLE), new ItemStack(Items.OAK_PLANKS, 4),
                new ItemStack(Items.PAPER, 2), "oak_planks", "cartography_table");
        addSawmillRecipe(output, Ingredient.ofItems(Items.FLETCHING_TABLE), new ItemStack(Items.OAK_PLANKS, 4),
                new ItemStack(Items.FLINT, 2), "oak_planks", "fletching_table");
        addSawmillRecipe(output, Ingredient.ofItems(Items.LOOM), new ItemStack(Items.OAK_PLANKS, 2),
                new ItemStack(Items.STRING, 2), "oak_planks", "loom");
        addSawmillRecipe(output, Ingredient.ofItems(Items.COMPOSTER), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "composter");
        addSawmillRecipe(output, Ingredient.ofItems(Items.NOTE_BLOCK), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.REDSTONE), "oak_planks", "note_block");
        addSawmillRecipe(output, Ingredient.ofItems(Items.JUKEBOX), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.DIAMOND), "oak_planks", "jukebox");

        addSawmillRecipe(output, Ingredient.ofItems(Items.BOOKSHELF), new ItemStack(Items.OAK_PLANKS, 6),
                new ItemStack(Items.BOOK, 3), "oak_planks", "bookshelf");
        addSawmillRecipe(output, Ingredient.ofItems(Items.CHISELED_BOOKSHELF), new ItemStack(Items.OAK_PLANKS, 6),
                5, "oak_planks", "chiseled_bookshelf");
        addSawmillRecipe(output, Ingredient.ofItems(Items.LECTERN), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.BOOK, 3), "oak_planks", "lectern");

        addSawmillRecipe(output, Ingredient.ofItems(Items.CHEST), new ItemStack(Items.OAK_PLANKS, 7),
                3, "oak_planks", "chest");
        addSawmillRecipe(output, Ingredient.ofItems(Items.BARREL), new ItemStack(Items.OAK_PLANKS, 6),
                5, "oak_planks", "barrel");

        addSawmillRecipe(output, Ingredient.ofItems(Items.WOODEN_SWORD), new ItemStack(Items.OAK_PLANKS, 2),
                1, "oak_planks", "wooden_sword");
        addSawmillRecipe(output, Ingredient.ofItems(Items.WOODEN_SHOVEL), new ItemStack(Items.OAK_PLANKS),
                2, "oak_planks", "wooden_shovel");
        addSawmillRecipe(output, Ingredient.ofItems(Items.WOODEN_PICKAXE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_pickaxe");
        addSawmillRecipe(output, Ingredient.ofItems(Items.WOODEN_AXE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_axe");
        addSawmillRecipe(output, Ingredient.ofItems(Items.WOODEN_HOE), new ItemStack(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hoe");
        addSawmillRecipe(output, Ingredient.ofItems(ModItems.WOODEN_HAMMER), new ItemStack(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hammer");

        addSawmillRecipe(output, Ingredient.fromTag(ItemTags.PLANKS), new ItemStack(Items.STICK, 3),
                1, "sticks", "planks");
        addSawmillRecipe(output, Ingredient.ofItems(Items.BAMBOO_MOSAIC), new ItemStack(Items.STICK, 3),
                3, "sticks", "bamboo_mosaic");

        addSawmillRecipe(output, Ingredient.ofEntries(Stream.of(new Ingredient.TagEntry(ItemTags.WOODEN_STAIRS),
                        new Ingredient.StackEntry(new ItemStack(Items.BAMBOO_MOSAIC_STAIRS)))),
                new ItemStack(Items.STICK, 3), 1, "sticks", "stairs");
        addSawmillRecipe(output, Ingredient.ofEntries(Stream.of(new Ingredient.TagEntry(ItemTags.WOODEN_SLABS),
                        new Ingredient.StackEntry(new ItemStack(Items.BAMBOO_MOSAIC_SLAB)))),
                new ItemStack(Items.STICK, 1), 1, "sticks", "slabs");
        addSawmillRecipe(output, Ingredient.fromTag(ItemTags.WOODEN_BUTTONS), new ItemStack(Items.STICK, 3),
                1, "sticks", "buttons");

        addSawmillRecipe(output, Ingredient.ofItems(Items.LADDER), new ItemStack(Items.STICK, 2),
                1, "sticks", "ladder");

        addSawmillRecipe(output, Ingredient.ofItems(Items.BOWL), new ItemStack(Items.STICK),
                2, "sticks", "bowl");
        addSawmillRecipe(output, Ingredient.ofItems(Items.BOW), new ItemStack(Items.STICK, 3),
                new ItemStack(Items.STRING, 3), "sticks", "bow");
        addSawmillRecipe(output, Ingredient.ofItems(Items.FISHING_ROD), new ItemStack(Items.STICK, 3),
                new ItemStack(Items.STRING, 2), "sticks", "fishing_rod");

        addSawmillRecipe(output, Ingredient.fromTag(CommonItemTags.WOODEN_RODS), new ItemStack(ModItems.SAWDUST),
                0, "sawdust", "sticks");
    }

    private void buildPlantGrowthChamberRecipes(Consumer<RecipeJsonProvider> output) {
        addBasicFlowerGrowingRecipe(output, Items.DANDELION, "dandelions");
        addBasicFlowerGrowingRecipe(output, Items.POPPY, "poppies");
        addBasicFlowerGrowingRecipe(output, Items.BLUE_ORCHID, "blue_orchids");
        addBasicFlowerGrowingRecipe(output, Items.ALLIUM, "alliums");
        addBasicFlowerGrowingRecipe(output, Items.AZURE_BLUET, "azure_bluets");
        addBasicFlowerGrowingRecipe(output, Items.RED_TULIP, "red_tulips");
        addBasicFlowerGrowingRecipe(output, Items.ORANGE_TULIP, "orange_tulips");
        addBasicFlowerGrowingRecipe(output, Items.WHITE_TULIP, "white_tulips");
        addBasicFlowerGrowingRecipe(output, Items.PINK_TULIP, "pink_tulips");
        addBasicFlowerGrowingRecipe(output, Items.OXEYE_DAISY, "oxeye_daisies");
        addBasicFlowerGrowingRecipe(output, Items.CORNFLOWER, "cornflowers");
        addBasicFlowerGrowingRecipe(output, Items.LILY_OF_THE_VALLEY, "lily_of_the_valley");

        addBasicFlowerGrowingRecipe(output, Items.SUNFLOWER, "sunflowers");
        addBasicFlowerGrowingRecipe(output, Items.LILAC, "lilacs");
        addBasicFlowerGrowingRecipe(output, Items.ROSE_BUSH, "rose_bushes");
        addBasicFlowerGrowingRecipe(output, Items.PEONY, "peonies");

        addBasicMushroomsGrowingRecipe(output, Items.BROWN_MUSHROOM, "brown_mushrooms");
        addBasicMushroomsGrowingRecipe(output, Items.RED_MUSHROOM, "red_mushrooms");

        addBasicAncientFlowerGrowingRecipe(output, Items.TORCHFLOWER_SEEDS, Items.TORCHFLOWER, "torchflowers");
        addBasicAncientFlowerGrowingRecipe(output, Items.PITCHER_POD, Items.PITCHER_PLANT, "pitcher_plants");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.PINK_PETALS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.PINK_PETALS), new double[] {
                        1., 1., 1., .67, .33, .33, .15
                })
        }, 16000, "pink_petals", "pink_petals");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.SWEET_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SWEET_BERRIES), new double[] {
                        1., 1., .33, .17
                })
        }, 16000, "sweet_berries", "sweet_berries");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.GLOW_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.GLOW_BERRIES), new double[] {
                        1., 1., .67, .33, .17, .17
                })
        }, 16000, "glow_berries", "glow_berries");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.WHEAT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT), new double[] {
                        1., .75, .25
                })
        }, 16000, "wheat", "wheat_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.BEETROOT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "beetroots", "beetroot_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.POTATO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.POTATO), new double[] {
                        1., .75, .25, .25
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.POISONOUS_POTATO), new double[] {
                        .125
                })
        }, 16000, "potatoes", "potato");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.CARROT), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.CARROT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "carrots", "carrot");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.MELON_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.MELON_SLICE), new double[] {
                        1., 1., .75, .25, .25
                })
        }, 16000, "melon_slices", "melon_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.PUMPKIN_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.PUMPKIN), new double[] {
                        1.
                })
        }, 16000, "pumpkin", "pumpkin_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.SUGAR_CANE), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SUGAR_CANE), new double[] {
                        1., 1., .67, .67, .33, .17, .17
                })
        }, 16000, "sugar_canes", "sugar_cane");
        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.BAMBOO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BAMBOO), new double[] {
                        1., 1., .67, .17
                })
        }, 16000, "bamboo", "bamboo");
    }

    private void buildPlantGrowthChamberFertilizerRecipes(Consumer<RecipeJsonProvider> output) {
        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.ofItems(Items.BONE_MEAL),
                1.5, 3., "bone_meal");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.ofItems(ModItems.BASIC_FERTILIZER),
                2.5, 3.5, "basic_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.ofItems(ModItems.GOOD_FERTILIZER),
                3.5, 5., "good_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.ofItems(ModItems.ADVANCED_FERTILIZER),
                5., 6.5, "advanced_fertilizer");
    }

    private void buildMetalPressRecipes(Consumer<RecipeJsonProvider> output) {
        addGearMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.IRON_PLATES), new ItemStack(ModItems.IRON_GEAR));

        addRodMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.IRON_PLATES), new ItemStack(ModItems.IRON_ROD));

        addWireMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.TIN_PLATES), new ItemStack(ModItems.TIN_WIRE));
        addWireMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.COPPER_PLATES), new ItemStack(ModItems.COPPER_WIRE));
        addWireMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.GOLD_PLATES), new ItemStack(ModItems.GOLD_WIRE));

        addWireMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_PLATES), new ItemStack(ModItems.ENERGIZED_COPPER_WIRE));
        addWireMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_PLATES), new ItemStack(ModItems.ENERGIZED_GOLD_WIRE));
    }

    private void buildHeatGeneratorRecipes(Consumer<RecipeJsonProvider> output) {
        addHeatGeneratorRecipe(output, Fluids.FLOWING_LAVA, 15, "flowing_lava");
        addHeatGeneratorRecipe(output, Fluids.LAVA, 25, "still_lava");
    }

    private void buildThermalGeneratorRecipes(Consumer<RecipeJsonProvider> output) {
        addThermalGeneratorRecipe(output, Fluids.LAVA, 20000, "lava");
    }

    private void buildAssemblingMachineRecipes(Consumer<RecipeJsonProvider> output) {
        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(ModItems.BASIC_SOLAR_CELL), 2),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_INGOTS), 4),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.TIN_INGOTS), 2),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.REDSTONE_ALLOY_INGOTS), 1)
        }, new ItemStack(ModItems.ADVANCED_SOLAR_CELL));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(ModItems.ADVANCED_SOLAR_CELL), 2),
                new IngredientWithCount(Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX), 4),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON), 2),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStack(ModItems.REINFORCED_ADVANCED_SOLAR_CELL));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(ModItems.BASIC_CIRCUIT), 4),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.ENERGIZED_COPPER_WIRES), 4),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON), 4),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStack(ModItems.ADVANCED_CIRCUIT));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(ModItems.ADVANCED_CIRCUIT), 4),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.ENERGIZED_GOLD_WIRES), 6),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON), 6)
        }, new ItemStack(ModItems.PROCESSING_UNIT));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(ModItems.PROCESSING_UNIT), 4),
                new IngredientWithCount(Ingredient.ofItems(ModItems.TELEPORTER_MATRIX), 4),
                new IngredientWithCount(Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX), 2),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON), 2)
        }, new ItemStack(ModItems.TELEPORTER_PROCESSING_UNIT));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.AMETHYSTS), 6),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.DIAMONDS), 2),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.EMERALDS), 2),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.REDSTONE_ALLOY_INGOTS), 1)
        }, new ItemStack(ModItems.CRYSTAL_MATRIX));
    }

    private void buildStoneSolidifierRecipes(Consumer<RecipeJsonProvider> output) {
        addStoneSolidifierRecipe(output, 1000, 50, new ItemStack(Items.STONE));

        addStoneSolidifierRecipe(output, 50, 50, new ItemStack(Items.COBBLESTONE));

        addStoneSolidifierRecipe(output, 1000, 150, new ItemStack(Items.DEEPSLATE));

        addStoneSolidifierRecipe(output, 150, 150, new ItemStack(Items.COBBLED_DEEPSLATE));

        addStoneSolidifierRecipe(output, 1000, 50, new ItemStack(Items.GRANITE));

        addStoneSolidifierRecipe(output, 1000, 50, new ItemStack(Items.DIORITE));

        addStoneSolidifierRecipe(output, 1000, 50, new ItemStack(Items.ANDESITE));

        addStoneSolidifierRecipe(output, 1000, 250, new ItemStack(Items.BLACKSTONE));

        addStoneSolidifierRecipe(output, 1000, 1000, new ItemStack(Items.OBSIDIAN));
    }

    private void buildFiltrationPlantRecipes(Consumer<RecipeJsonProvider> output) {
        addOreFiltrationRecipe(output, new ItemStack(ModItems.RAW_TIN), 0.05, "tin");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_COPPER), 0.05, "copper");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_IRON), 0.05, "iron");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_GOLD), 0.005, "gold");
    }

    private void buildFluidTransposerRecipes(Consumer<RecipeJsonProvider> output) {
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.WHITE_CONCRETE_POWDER), new ItemStack(Items.WHITE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.ORANGE_CONCRETE_POWDER), new ItemStack(Items.ORANGE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.MAGENTA_CONCRETE_POWDER), new ItemStack(Items.MAGENTA_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.LIGHT_BLUE_CONCRETE_POWDER), new ItemStack(Items.LIGHT_BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.YELLOW_CONCRETE_POWDER), new ItemStack(Items.YELLOW_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.LIME_CONCRETE_POWDER), new ItemStack(Items.LIME_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.PINK_CONCRETE_POWDER), new ItemStack(Items.PINK_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.GRAY_CONCRETE_POWDER), new ItemStack(Items.GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.LIGHT_GRAY_CONCRETE_POWDER), new ItemStack(Items.LIGHT_GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.CYAN_CONCRETE_POWDER), new ItemStack(Items.CYAN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.PURPLE_CONCRETE_POWDER), new ItemStack(Items.PURPLE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.BLUE_CONCRETE_POWDER), new ItemStack(Items.BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.BROWN_CONCRETE_POWDER), new ItemStack(Items.BROWN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.GREEN_CONCRETE_POWDER), new ItemStack(Items.GREEN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.RED_CONCRETE_POWDER), new ItemStack(Items.RED_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.BLACK_CONCRETE_POWDER), new ItemStack(Items.BLACK_CONCRETE));

        addFluidTransposerRecipe(output, Ingredient.ofItems(Items.SPONGE), new ItemStack(Items.WET_SPONGE), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));
        addFluidTransposerRecipe(output, Ingredient.ofItems(Items.WET_SPONGE), new ItemStack(Items.SPONGE), FluidTransposerBlockEntity.Mode.EMPTYING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));

        addFluidTransposerRecipe(output, Ingredient.ofItems(Items.DIRT), new ItemStack(Items.MUD), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(250)));
    }

    private void buildChargerRecipes(Consumer<RecipeJsonProvider> output) {
        addChargerRecipe(output, Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS),
                new ItemStack(ModItems.ENERGIZED_COPPER_INGOT), 4194304);
    }

    private void buildEnergizerRecipes(Consumer<RecipeJsonProvider> output) {
        addEnergizerRecipe(output, Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS),
                new ItemStack(ModItems.ENERGIZED_COPPER_INGOT), 32768);
        addEnergizerRecipe(output, Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                new ItemStack(ModItems.ENERGIZED_GOLD_INGOT), 131072);
        addEnergizerRecipe(output, Ingredient.ofItems(ModItems.CRYSTAL_MATRIX),
                new ItemStack(ModItems.ENERGIZED_CRYSTAL_MATRIX), 524288);
    }

    private void buildCrystalGrowthChamberRecipes(Consumer<RecipeJsonProvider> output) {
        addCrystalGrowthChamberRecipe(output, Ingredient.fromTag(CommonItemTags.AMETHYSTS),
                new OutputItemStackWithPercentages(new ItemStack(Items.AMETHYST_SHARD), new double[] {
                        1., 1., .67, .5, .25, .125
                }), 16000);
        addCrystalGrowthChamberRecipe(output, Ingredient.ofItems(Items.AMETHYST_BLOCK),
                new OutputItemStackWithPercentages(new ItemStack(Items.BUDDING_AMETHYST), .25), 4,
                32000);
    }

    private static void add3x3PackingCraftingRecipe(Consumer<RecipeJsonProvider> output, InventoryChangedCriterion.Conditions hasIngredientTrigger,
                                                    Ingredient unpackedInput, ItemConvertible packedItem, CraftingRecipeCategory category,
                                                    String group, String recipeIdSuffix) {
        addShapedCraftingRecipe(output, hasIngredientTrigger, Map.of(
                '#', unpackedInput
        ), new String[] {
                "###",
                "###",
                "###"
        }, new ItemStack(packedItem), category, group, recipeIdSuffix);
    }
    private static void add3x3UnpackingCraftingRecipe(Consumer<RecipeJsonProvider> output, InventoryChangedCriterion.Conditions hasIngredientTrigger,
                                                      Ingredient packedInput, ItemConvertible unpackedItem, CraftingRecipeCategory category,
                                                      String group, String recipeIdSuffix) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, List.of(
                packedInput
        ), new ItemStack(unpackedItem, 9), category, group, recipeIdSuffix);
    }
    private static void addMetalIngotCraftingRecipes(Consumer<RecipeJsonProvider> output, TagKey<Item> nuggetInput,
                                                     TagKey<Item> blockInput, ItemConvertible ingotItem, String metalName) {
        add3x3PackingCraftingRecipe(output, conditionsFromTag(nuggetInput), Ingredient.fromTag(nuggetInput), ingotItem,
                CraftingRecipeCategory.MISC, metalName + "_ingot", "_from_nuggets");
        add3x3UnpackingCraftingRecipe(output, conditionsFromTag(blockInput), Ingredient.fromTag(blockInput), ingotItem,
                CraftingRecipeCategory.MISC, metalName + "_ingot", "_from_" + metalName + "_block");
    }
    private static void addMetalNuggetCraftingRecipe(Consumer<RecipeJsonProvider> output, TagKey<Item> ingotInput, ItemConvertible nuggetItem) {
        addShapelessCraftingRecipe(output, conditionsFromTag(ingotInput), List.of(
                Ingredient.fromTag(ingotInput)
        ), new ItemStack(nuggetItem, 9), CraftingRecipeCategory.MISC);
    }
    private static void addMetalPlateCraftingRecipe(Consumer<RecipeJsonProvider> output, TagKey<Item> ingotInput, ItemConvertible plateItem) {
        addShapelessCraftingRecipe(output, conditionsFromTag(ingotInput), List.of(
                Ingredient.fromTag(CommonItemTags.HAMMERS),
                Ingredient.fromTag(ingotInput)
        ), new ItemStack(plateItem), CraftingRecipeCategory.MISC);
    }
    private static void addMetalWireCraftingRecipe(Consumer<RecipeJsonProvider> output, TagKey<Item> plateInput, ItemConvertible wireItem) {
        addShapelessCraftingRecipe(output, conditionsFromTag(plateInput), List.of(
                Ingredient.fromTag(CommonItemTags.CUTTERS),
                Ingredient.fromTag(plateInput)
        ), new ItemStack(wireItem, 2), CraftingRecipeCategory.MISC);
    }
    private static void addHammerCraftingRecipe(Consumer<RecipeJsonProvider> output, TagKey<Item> materialInput, ItemConvertible hammerItem) {
        addShapedCraftingRecipe(output, conditionsFromTag(materialInput), Map.of(
                'S', Ingredient.fromTag(CommonItemTags.WOODEN_RODS),
                'M', Ingredient.fromTag(materialInput)
        ), new String[] {
                " M ",
                " SM",
                "S  "
        }, new ItemStack(hammerItem), CraftingRecipeCategory.MISC);
    }
    private static void addBasicCableCraftingRecipes(Consumer<RecipeJsonProvider> output, TagKey<Item> ingotInput, TagKey<Item> wireInput,
                                                     ItemStack cableItem) {
        addCableCraftingRecipe(output, ingotInput, cableItem);
        addCableUsingWireCraftingRecipe(output, wireInput, cableItem);
    }
    private static void addCableUsingWireCraftingRecipe(Consumer<RecipeJsonProvider> output, TagKey<Item> wireInput,
                                                        ItemStack cableItem) {
        addShapedCraftingRecipe(output, conditionsFromTag(wireInput), Map.of(
                'W', Ingredient.fromTag(wireInput),
                'I', Ingredient.ofItems(ModItems.CABLE_INSULATOR)
        ), new String[] {
                "IWI",
                "IWI",
                "IWI"
        }, cableItem, CraftingRecipeCategory.MISC, getItemPath(cableItem.getItem()), "_using_wire");
    }
    private static void addCableCraftingRecipe(Consumer<RecipeJsonProvider> output, TagKey<Item> ingotInput,
                                               ItemStack cableItem) {
        addShapedCraftingRecipe(output, conditionsFromTag(ingotInput), Map.of(
                'I', Ingredient.fromTag(ingotInput),
                'i', Ingredient.ofItems(ModItems.CABLE_INSULATOR)
        ), new String[] {
                "iIi",
                "iIi",
                "iIi"
        }, cableItem, CraftingRecipeCategory.MISC, getItemPath(cableItem.getItem()));
    }
    private static void addShapedCraftingRecipe(Consumer<RecipeJsonProvider> output, InventoryChangedCriterion.Conditions hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingRecipeCategory category) {
        addShapedCraftingRecipe(output, hasIngredientTrigger, key, pattern, result, category, "");
    }
    private static void addShapedCraftingRecipe(Consumer<RecipeJsonProvider> output, InventoryChangedCriterion.Conditions hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingRecipeCategory category,
                                                String group) {
        addShapedCraftingRecipe(output, hasIngredientTrigger, key, pattern, result, category, group, "");
    }
    private static void addShapedCraftingRecipe(Consumer<RecipeJsonProvider> output, InventoryChangedCriterion.Conditions hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingRecipeCategory category,
                                                String group, String recipeIdSuffix) {
        addShapedCraftingRecipe(output, hasIngredientTrigger, key, pattern, result, category, group, recipeIdSuffix, "");
    }
    private static void addShapedCraftingRecipe(Consumer<RecipeJsonProvider> output, InventoryChangedCriterion.Conditions hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingRecipeCategory category,
                                                String group, String recipeIdSuffix, String recipeIdPrefix) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdPrefix + getItemPath(result.getItem()) + recipeIdSuffix);

        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", hasIngredientTrigger)
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        ShapedFinishedRecipe recipe = new ShapedFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                category, key, pattern, result,
                advancementBuilder,
                recipeId.withPrefixedPath("recipes/")
        );
        output.accept(recipe);
    }
    private static void addShapelessCraftingRecipe(Consumer<RecipeJsonProvider> output, InventoryChangedCriterion.Conditions hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingRecipeCategory category) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, inputs, result, category, "");
    }
    private static void addShapelessCraftingRecipe(Consumer<RecipeJsonProvider> output, InventoryChangedCriterion.Conditions hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingRecipeCategory category,
                                                   String group) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, inputs, result, category, group, "");
    }
    private static void addShapelessCraftingRecipe(Consumer<RecipeJsonProvider> output, InventoryChangedCriterion.Conditions hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingRecipeCategory category,
                                                   String group, String recipeIdSuffix) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, inputs, result, category, group, recipeIdSuffix, "");
    }
    private static void addShapelessCraftingRecipe(Consumer<RecipeJsonProvider> output, InventoryChangedCriterion.Conditions hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingRecipeCategory category,
                                                   String group, String recipeIdSuffix, String recipeIdPrefix) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdPrefix + getItemPath(result.getItem()) + recipeIdSuffix);

        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", hasIngredientTrigger)
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        ShapelessFinishedRecipe recipe = new ShapelessFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category, result,
                DefaultedList.copyOf(Ingredient.EMPTY, inputs.toArray(Ingredient[]::new)),
                advancementBuilder,
                recipeId.withPrefixedPath("recipes/")
        );
        output.accept(recipe);
    }
    private static void addCustomCraftingRecipe(Consumer<RecipeJsonProvider> output, RecipeSerializer<? extends SpecialCraftingRecipe> customRecipeSerializer,
                                                CraftingRecipeCategory category, String recipeIdString) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdString);

        CustomFinishedRecipe recipe = new CustomFinishedRecipe(
                recipeId,
                category,
                customRecipeSerializer
        );
        output.accept(recipe);
    }

    private static void addBlastingAndSmeltingRecipes(Consumer<RecipeJsonProvider> output, ItemConvertible ingredient, ItemStack result,
                                                      CookingRecipeCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }
    private static void addBlastingAndSmeltingRecipes(Consumer<RecipeJsonProvider> output, TagKey<Item> ingredient, ItemStack result,
                                                      CookingRecipeCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addSmeltingRecipe(Consumer<RecipeJsonProvider> output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemPath(result.getItem()));

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(Consumer<RecipeJsonProvider> output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemPath(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(Consumer<RecipeJsonProvider> output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, Identifier recipeId) {
        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromItem(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.ofItems(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                recipeId.withPrefixedPath("recipes/"),
                RecipeSerializer.SMELTING
        );
        output.accept(recipe);
    }
    private static void addSmeltingRecipe(Consumer<RecipeJsonProvider> output, TagKey<Item> ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemPath(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromTag(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.fromTag(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                recipeId.withPrefixedPath("recipes/"),
                RecipeSerializer.SMELTING
        );
        output.accept(recipe);
    }

    private static void addBlastingRecipe(Consumer<RecipeJsonProvider> output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("blasting/" +
                getItemPath(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromItem(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.ofItems(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                recipeId.withPrefixedPath("recipes/"),
                RecipeSerializer.BLASTING
        );
        output.accept(recipe);
    }
    private static void addBlastingRecipe(Consumer<RecipeJsonProvider> output, TagKey<Item> ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("blasting/" +
                getItemPath(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromTag(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.fromTag(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                recipeId.withPrefixedPath("recipes/"),
                RecipeSerializer.BLASTING
        );
        output.accept(recipe);
    }

    private static void addNetheriteSmithingUpgradeRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient base, ItemStack output) {
        Identifier recipeId = EPAPI.id("smithing/" +
                getItemPath(output.getItem()));

        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromTag(ConventionalItemTags.NETHERITE_INGOTS))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        SmithingTransformFinishedRecipe recipe = new SmithingTransformFinishedRecipe(
                recipeId,
                Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                base,
                Ingredient.fromTag(ConventionalItemTags.NETHERITE_INGOTS),
                output.getItem(),
                advancementBuilder,
                recipeId.withPrefixedPath("recipes/")
        );
        recipeOutput.accept(recipe);
    }

    private static void addAlloyFurnaceRecipe(Consumer<RecipeJsonProvider> recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              int ticks) {
        addAlloyFurnaceRecipe(recipeOutput, inputs, output, OutputItemStackWithPercentages.EMPTY, ticks);
    }
    private static void addAlloyFurnaceRecipe(Consumer<RecipeJsonProvider> recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              OutputItemStackWithPercentages secondaryOutput, int ticks) {
        Identifier recipeId = EPAPI.id("alloy_furnace/" +
                getItemPath(output.getItem()));

        AlloyFurnaceFinishedRecipe recipe = new AlloyFurnaceFinishedRecipe(
                recipeId,
                output, secondaryOutput, inputs, ticks
        );
        recipeOutput.accept(recipe);
    }

    private static void addPressMoldMakerRecipe(Consumer<RecipeJsonProvider> recipeOutput, int clayCount, ItemStack output) {
        Identifier recipeId = EPAPI.id("press_mold_maker/" +
                getItemPath(output.getItem()));

        PressMoldMakerFinishedRecipe recipe = new PressMoldMakerFinishedRecipe(
                recipeId,
                output, clayCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addPlateCompressorRecipes(Consumer<RecipeJsonProvider> recipeOutput, Ingredient ingotInput,
                                                  Ingredient blockInput, ItemStack output, String metalName) {
        addPlateCompressorIngotRecipe(recipeOutput, ingotInput, output, metalName);
        addCompressorRecipe(recipeOutput, blockInput, output.copyWithCount(9), metalName + "_block");
    }
    private static void addPlateCompressorIngotRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient ingotInput,
                                                      ItemStack output, String metalName) {
        addCompressorRecipe(recipeOutput, ingotInput, output, metalName + "_ingot");
    }
    private static void addCompressorRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output, String recipeIngredientName) {
        addCompressorRecipe(recipeOutput, input, output, 1, recipeIngredientName);
    }
    private static void addCompressorRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output, int inputCount,
                                            String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("compressing/" +
                getItemPath(output.getItem()) + "_from_compressing_" + recipeIngredientName);

        CompressorFinishedRecipe recipe = new CompressorFinishedRecipe(
                recipeId,
                output, input, inputCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addCrusherRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output,
                                         String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("crusher/" +
                getItemPath(output.getItem()) + "_from_crushing_" + recipeIngredientName);

        CrusherFinishedRecipe recipe = new CrusherFinishedRecipe(
                recipeId,
                output, input
        );
        recipeOutput.accept(recipe);
    }

    private static void addBasicMetalPulverizerRecipes(Consumer<RecipeJsonProvider> recipeOutput, Ingredient oreInput,
                                                       Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                       Ingredient ingotInput, ItemStack output, String metalName) {
        addPulverizerRecipe(recipeOutput, oreInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., .25
        }, new double[] {
                1., 1, .5, .25
        }), metalName + "_ores");

        addRawMetalAndIngotPulverizerRecipes(recipeOutput, rawMetalInput, rawMetalBlockInput, ingotInput, output, metalName);
    }
    private static void addRawMetalAndIngotPulverizerRecipes(Consumer<RecipeJsonProvider> recipeOutput,
                                                             Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                             Ingredient ingotInput, ItemStack output, String metalName) {
        addPulverizerRecipe(recipeOutput, rawMetalInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., .25
        }, new double[] {
                1., .5
        }), "raw_" + metalName);

        addPulverizerRecipe(recipeOutput, rawMetalBlockInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., 1., 1., 1., 1., 1., 1., 1., .5, .5, .25
        }, new double[] {
                1., 1., 1., 1., 1., 1., 1., 1., 1., .75, .5, .25, .25
        }), "raw_" + metalName + "_blocks");

        addPulverizerRecipe(recipeOutput, ingotInput, new PulverizerRecipe.OutputItemStackWithPercentages(output,
                1., 1.), metalName + "_ingots");
    }
    private static void addPulverizerRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            String recipeIngredientName) {
        addPulverizerRecipe(recipeOutput, input, output,
                new PulverizerRecipe.OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0], new double[0]), recipeIngredientName);
    }
    private static void addPulverizerRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            PulverizerRecipe.OutputItemStackWithPercentages secondaryOutput,
                                            String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("pulverizer/" +
                getItemPath(output.output().getItem()) + "_from_pulverizer_" + recipeIngredientName);

        PulverizerFinishedRecipe recipe = new PulverizerFinishedRecipe(
                recipeId,
                output, secondaryOutput, input
        );
        recipeOutput.accept(recipe);
    }

    private static void addBasicWoodSawmillRecipe(Consumer<RecipeJsonProvider> recipeOutput, ItemStack planksItem,
                                                  Ingredient logsInput, Ingredient fenceInput, Ingredient fenceGateInput,
                                                  Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                  Ingredient signInput, Ingredient boatInput, Ingredient chestBoatInput,
                                                  boolean isRaft, String woodName) {
        addSawmillRecipe(recipeOutput, logsInput, planksItem.copyWithCount(6), 1, getItemPath(planksItem.getItem()),
                woodName + "_logs");

        addBasicWoodWithoutLogsSawmillRecipe(recipeOutput, planksItem, fenceInput, fenceGateInput, doorInput, trapdoorInput,
                pressurePlateInput, signInput, boatInput, chestBoatInput, isRaft, woodName);
    }
    private static void addBasicWoodWithoutLogsSawmillRecipe(Consumer<RecipeJsonProvider> recipeOutput, ItemStack planksItem,
                                                             Ingredient fenceInput, Ingredient fenceGateInput,
                                                             Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                             Ingredient signInput, Ingredient boatInput, Ingredient chestBoatInput,
                                                             boolean isRaft, String woodName) {
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(recipeOutput, planksItem, fenceInput, fenceGateInput, doorInput,
                trapdoorInput, pressurePlateInput, signInput, woodName);

        addSawmillRecipe(recipeOutput, boatInput, planksItem.copyWithCount(4), 3, getItemPath(planksItem.getItem()),
                woodName + (isRaft?"_raft":"_boat"));
        addSawmillRecipe(recipeOutput, chestBoatInput, planksItem.copyWithCount(5), 7, getItemPath(planksItem.getItem()),
                woodName + (isRaft?"_chest_raft":"_chest_boat"));
    }
    private static void addBasicWoodWithoutLogsAndBoatsSawmillRecipe(Consumer<RecipeJsonProvider> recipeOutput, ItemStack planksItem,
                                                                     Ingredient fenceInput, Ingredient fenceGateInput,
                                                                     Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                                     Ingredient signInput, String woodName) {
        addSawmillRecipe(recipeOutput, fenceInput, planksItem, 2, getItemPath(planksItem.getItem()),
                woodName + "_fence");
        addSawmillRecipe(recipeOutput, fenceGateInput, planksItem.copyWithCount(2), 3, getItemPath(planksItem.getItem()),
                woodName + "_fence_gate");
        addSawmillRecipe(recipeOutput, doorInput, planksItem, 3, getItemPath(planksItem.getItem()),
                woodName + "_door");
        addSawmillRecipe(recipeOutput, trapdoorInput, planksItem.copyWithCount(2), 3, getItemPath(planksItem.getItem()),
                woodName + "_trapdoor");
        addSawmillRecipe(recipeOutput, pressurePlateInput, planksItem, 2, getItemPath(planksItem.getItem()),
                woodName + "_pressure_plate");
        addSawmillRecipe(recipeOutput, signInput, planksItem.copyWithCount(2), 1, getItemPath(planksItem.getItem()),
                woodName + "_sign");
    }
    private static void addSawmillRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output,
                                         int sawdustAmount, String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillFinishedRecipe recipe = new SawmillFinishedRecipe(
                recipeId,
                output, input, sawdustAmount
        );
        recipeOutput.accept(recipe);
    }
    private static void addSawmillRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output,
                                         ItemStack secondaryOutput, String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillFinishedRecipe recipe = new SawmillFinishedRecipe(
                recipeId,
                output, secondaryOutput, input
        );
        recipeOutput.accept(recipe);
    }

    private static void addBasicFlowerGrowingRecipe(Consumer<RecipeJsonProvider> recipeOutput, ItemConvertible flowerItem,
                                                    String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, Ingredient.ofItems(flowerItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., 1., .33
                })
        }, 16000, outputName, getItemPath(flowerItem));
    }
    private static void addBasicMushroomsGrowingRecipe(Consumer<RecipeJsonProvider> recipeOutput, ItemConvertible mushroomItem,
                                                       String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, Ingredient.ofItems(mushroomItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(mushroomItem), new double[] {
                        1., 1., .5, .25
                })
        }, 16000, outputName, getItemPath(mushroomItem));
    }
    private static void addBasicAncientFlowerGrowingRecipe(Consumer<RecipeJsonProvider> recipeOutput, ItemConvertible seedItem,
                                                           ItemConvertible flowerItem, String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, Ingredient.ofItems(seedItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(seedItem), new double[] {
                        1., .33, .15
                }),
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., .15
                })
        }, 16000, outputName, getItemPath(seedItem));
    }
    private static void addPlantGrowthChamberRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input,
                                                    OutputItemStackWithPercentages[] outputs, int ticks,
                                                    String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("growing/" +
                outputName + "_from_growing_" + recipeIngredientName);

        PlantGrowthChamberFinishedRecipe recipe = new PlantGrowthChamberFinishedRecipe(
                recipeId,
                outputs, input, ticks
        );
        recipeOutput.accept(recipe);
    }

    private static void addPlantGrowthChamberFertilizerRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input,
                                                              double speedMultiplier, double energyConsumptionMultiplier,
                                                              String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("growing/fertilizer/" +
                recipeIngredientName);

        PlantGrowthChamberFertilizerFinishedRecipe recipe = new PlantGrowthChamberFertilizerFinishedRecipe(
                recipeId,
                input, speedMultiplier, energyConsumptionMultiplier
        );
        recipeOutput.accept(recipe);
    }

    private static void addGearMetalPressRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output, new ItemStack(ModItems.GEAR_PRESS_MOLD), 2);
    }
    private static void addRodMetalPressRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output.copyWithCount(2), new ItemStack(ModItems.ROD_PRESS_MOLD));
    }
    private static void addWireMetalPressRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output.copyWithCount(3), new ItemStack(ModItems.WIRE_PRESS_MOLD));
    }
    private static void addMetalPressRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output,
                                            ItemStack pressMold) {
        addMetalPressRecipe(recipeOutput, input, output, pressMold, 1);
    }
    private static void addMetalPressRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output,
                                            ItemStack pressMold, int inputCount) {
        Identifier recipeId = EPAPI.id("metal_press/" +
                getItemPath(output.getItem()));

        MetalPressFinishedRecipe recipe = new MetalPressFinishedRecipe(
                recipeId,
                output, pressMold, input, inputCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addHeatGeneratorRecipe(Consumer<RecipeJsonProvider> recipeOutput, Fluid input, int energyProduction,
                                               String recipeIngredientName) {
        addHeatGeneratorRecipe(recipeOutput, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private static void addHeatGeneratorRecipe(Consumer<RecipeJsonProvider> recipeOutput, Fluid[] input, int energyProduction,
                                               String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("heat_generator/" +
                "energy_production_from_" + recipeIngredientName);

        HeatGeneratorFinishedRecipe recipe = new HeatGeneratorFinishedRecipe(
                recipeId,
                input, energyProduction
        );
        recipeOutput.accept(recipe);
    }

    private static void addThermalGeneratorRecipe(Consumer<RecipeJsonProvider> recipeOutput, Fluid input, int energyProduction,
                                                  String recipeIngredientName) {
        addThermalGeneratorRecipe(recipeOutput, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private static void addThermalGeneratorRecipe(Consumer<RecipeJsonProvider> recipeOutput, Fluid[] input, int energyProduction,
                                                  String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("thermal_generator/" +
                "energy_production_from_" + recipeIngredientName);

        ThermalGeneratorFinishedRecipe recipe = new ThermalGeneratorFinishedRecipe(
                recipeId,
                input, energyProduction
        );
        recipeOutput.accept(recipe);
    }

    private static void addStoneSolidifierRecipe(Consumer<RecipeJsonProvider> recipeOutput, int waterAmount, int lavaAmount, ItemStack output) {
        Identifier recipeId = EPAPI.id("stone_solidifier/" +
                getItemPath(output.getItem()));

        StoneSolidifierFinishedRecipe recipe = new StoneSolidifierFinishedRecipe(
                recipeId,
                output, waterAmount, lavaAmount
        );
        recipeOutput.accept(recipe);
    }

    private static void addAssemblingMachineRecipe(Consumer<RecipeJsonProvider> recipeOutput, IngredientWithCount[] inputs, ItemStack output) {
        Identifier recipeId = EPAPI.id("assembling/" +
                getItemPath(output.getItem()));

        AssemblingMachineFinishedRecipe recipe = new AssemblingMachineFinishedRecipe(
                recipeId,
                output, inputs
        );
        recipeOutput.accept(recipe);
    }

    private static void addOreFiltrationRecipe(Consumer<RecipeJsonProvider> recipeOutput, ItemStack oreOutput, double oreOutputPercentage,
                                               String oreName) {
        addFiltrationPlantRecipe(recipeOutput, new OutputItemStackWithPercentages(new ItemStack(ModItems.STONE_PEBBLE), .33),
                new OutputItemStackWithPercentages(oreOutput, oreOutputPercentage), Registries.ITEM.getId(oreOutput.getItem()),
                oreName + "_ore_filtration");
    }
    private static void addFiltrationPlantRecipe(Consumer<RecipeJsonProvider> recipeOutput, OutputItemStackWithPercentages output,
                                                 Identifier icon, String recipeName) {
        addFiltrationPlantRecipe(recipeOutput, output, OutputItemStackWithPercentages.EMPTY, icon, recipeName);
    }
    private static void addFiltrationPlantRecipe(Consumer<RecipeJsonProvider> recipeOutput, OutputItemStackWithPercentages output,
                                                 OutputItemStackWithPercentages secondaryOutput, Identifier icon,
                                                 String recipeName) {
        Identifier recipeId = EPAPI.id("filtration_plant/" +
                recipeName);

        FiltrationPlantFinishedRecipe recipe = new FiltrationPlantFinishedRecipe(
                recipeId,
                output, secondaryOutput, icon
        );
        recipeOutput.accept(recipe);
    }

    private static void addConcreteFluidTransposerRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output) {
        addFluidTransposerRecipe(recipeOutput, input, output, FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));
    }
    private static void addFluidTransposerRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output,
                                                 FluidTransposerBlockEntity.Mode mode, FluidStack fluid) {
        Identifier recipeId = EPAPI.id("fluid_transposer/" +
                getItemPath(output.getItem()));

        FluidTransposerFinishedRecipe recipe = new FluidTransposerFinishedRecipe(
                recipeId,
                mode, output, input, fluid
        );
        recipeOutput.accept(recipe);
    }

    private static void addChargerRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        Identifier recipeId = EPAPI.id("charger/" +
                getItemPath(output.getItem()));

        ChargerFinishedRecipe recipe = new ChargerFinishedRecipe(
                recipeId,
                output, input, energyConsumption
        );
        recipeOutput.accept(recipe);
    }

    private static void addEnergizerRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        Identifier recipeId = EPAPI.id("energizer/" +
                getItemPath(output.getItem()));

        EnergizerFinishedRecipe recipe = new EnergizerFinishedRecipe(
                recipeId,
                output, input, energyConsumption
        );
        recipeOutput.accept(recipe);
    }

    private static void addCrystalGrowthChamberRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, OutputItemStackWithPercentages output,
                                                      int ticks) {
        addCrystalGrowthChamberRecipe(recipeOutput, input, output, 1, ticks);
    }
    private static void addCrystalGrowthChamberRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, OutputItemStackWithPercentages output,
                                                      int inputCount, int ticks) {
        Identifier recipeId = EPAPI.id("crystal_growing/" +
                getItemPath(output.output().getItem()));

        CrystalGrowthChamberFinishedRecipe recipe = new CrystalGrowthChamberFinishedRecipe(
                recipeId,
                output, input, inputCount, ticks
        );
        recipeOutput.accept(recipe);
    }
}
