package me.jddev0.ep.datagen.recipe;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.registry.tags.CommonItemTags;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ModRecipeGenerator extends RecipeProvider {
    public ModRecipeGenerator(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        super(registries, recipeOutput);
    }

    @Override
    protected void buildRecipes() {
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
        add3x3UnpackingCraftingRecipe(output, has(EPBlocks.SAWDUST_BLOCK),
                ingredientOf(EPBlocks.SAWDUST_BLOCK), EPItems.SAWDUST,
                CraftingBookCategory.MISC, "", "_from_sawdust_block");
        add3x3PackingCraftingRecipe(output, has(CommonItemTags.DUSTS_WOOD),
                ingredientOf(CommonItemTags.DUSTS_WOOD), EPBlocks.SAWDUST_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        add3x3UnpackingCraftingRecipe(output, has(CommonItemTags.STORAGE_BLOCKS_SILICON),
                ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON), EPItems.SILICON,
                CraftingBookCategory.MISC, "", "_from_silicon_block");
        add3x3PackingCraftingRecipe(output, has(CommonItemTags.SILICON),
                ingredientOf(CommonItemTags.SILICON), EPBlocks.SILICON_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        addMetalNuggetCraftingRecipe(output, CommonItemTags.INGOTS_TIN, EPItems.TIN_NUGGET);
        addMetalIngotCraftingRecipes(output, CommonItemTags.NUGGETS_TIN, CommonItemTags.STORAGE_BLOCKS_TIN,
                EPItems.TIN_INGOT, "tin");

        add3x3PackingCraftingRecipe(output, has(CommonItemTags.INGOTS_TIN),
                ingredientOf(CommonItemTags.INGOTS_TIN), EPBlocks.TIN_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        add3x3UnpackingCraftingRecipe(output, has(CommonItemTags.STORAGE_BLOCKS_RAW_TIN),
                ingredientOf(CommonItemTags.STORAGE_BLOCKS_RAW_TIN), EPItems.RAW_TIN,
                CraftingBookCategory.MISC, "", "");
        add3x3PackingCraftingRecipe(output, has(CommonItemTags.RAW_MATERIALS_TIN),
                ingredientOf(CommonItemTags.RAW_MATERIALS_TIN), EPBlocks.RAW_TIN_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        addMetalPlateCraftingRecipe(output, CommonItemTags.INGOTS_TIN, EPItems.TIN_PLATE);
        addMetalPlateCraftingRecipe(output, Tags.Items.INGOTS_COPPER, EPItems.COPPER_PLATE);
        addMetalPlateCraftingRecipe(output, Tags.Items.INGOTS_IRON, EPItems.IRON_PLATE);
        addMetalPlateCraftingRecipe(output, Tags.Items.INGOTS_GOLD, EPItems.GOLD_PLATE);

        addMetalWireCraftingRecipe(output, CommonItemTags.PLATES_TIN, EPItems.TIN_WIRE);
        addMetalWireCraftingRecipe(output, CommonItemTags.PLATES_COPPER, EPItems.COPPER_WIRE);
        addMetalWireCraftingRecipe(output, CommonItemTags.PLATES_GOLD, EPItems.GOLD_WIRE);

        addShapedCraftingRecipe(output, has(CommonItemTags.SILICON), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'Q', ingredientOf(Tags.Items.GEMS_QUARTZ),
                'T', ingredientOf(CommonItemTags.INGOTS_TIN),
                'C', ingredientOf(Tags.Items.INGOTS_COPPER)
        ), new String[] {
                " C ",
                "SQS",
                " T "
        }, new ItemStack(EPItems.BASIC_SOLAR_CELL.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.WIRES_COPPER), Map.of(
                'C', ingredientOf(CommonItemTags.WIRES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'S', ingredientOf(CommonItemTags.SILICON)
        ), new String[] {
                "RCR",
                "CIC",
                "SCS"
        }, new ItemStack(EPItems.BASIC_CIRCUIT.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_CIRCUIT), Map.of(
                'G', ingredientOf(CommonItemTags.WIRES_GOLD),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPItems.BASIC_CIRCUIT)
        ), new String[] {
                "GGG",
                "CBC",
                "CBC"
        }, new ItemStack(EPItems.BASIC_UPGRADE_MODULE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ADVANCED_CIRCUIT), Map.of(
                'G', ingredientOf(CommonItemTags.WIRES_ENERGIZED_GOLD),
                'C', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'A', ingredientOf(EPItems.ADVANCED_CIRCUIT),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "GGG",
                "CBC",
                "CAC"
        }, new ItemStack(EPItems.ADVANCED_UPGRADE_MODULE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE), Map.of(
                'g', ingredientOf(CommonItemTags.WIRES_ENERGIZED_GOLD),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'P', ingredientOf(EPItems.PROCESSING_UNIT),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "ggg",
                "GAG",
                "GPG"
        }, new ItemStack(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(Tags.Items.INGOTS_IRON), Map.of(
                'i', ingredientOf(Tags.Items.NUGGETS_IRON),
                'I', ingredientOf(Tags.Items.INGOTS_IRON)
        ), new String[] {
                " i ",
                "iIi",
                " i "
        }, new ItemStack(EPItems.SAW_BLADE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.SILICON), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'I', ingredientOf(Tags.Items.INGOTS_IRON),
                'C', ingredientOf(Tags.Items.INGOTS_COPPER)
        ), new String[] {
                "CIC",
                "ISI",
                "CIC"
        }, new ItemStack(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.GEARS_IRON), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'I', ingredientOf(CommonItemTags.GEARS_IRON),
                'R', ingredientOf(CommonItemTags.RODS_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "IsR",
                "SBS",
                "RsI"
        }, new ItemStack(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.INGOTS_ENERGIZED_COPPER), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'A', ingredientOf(CommonItemTags.INGOTS_ADVANCED_ALLOY),
                'E', ingredientOf(CommonItemTags.INGOTS_ENERGIZED_COPPER),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "ESE",
                "AHA",
                "ESE"
        }, new ItemStack(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSC",
                "SAS",
                "CSC"
        }, new ItemStack(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM.get()), CraftingBookCategory.MISC);
    }
    private void buildFertilizerCraftingRecipes() {
        addShapedCraftingRecipe(output, has(Items.BONE_MEAL), Map.of(
                'B', ingredientOf(Items.BONE_MEAL),
                'D', ingredientOf(Items.DANDELION),
                'b', ingredientOf(Items.BLUE_ORCHID),
                'L', ingredientOf(Tags.Items.GEMS_LAPIS),
                'A', ingredientOf(Items.ALLIUM),
                'P', ingredientOf(Items.POPPY)
        ), new String[] {
                "DBb",
                "BLB",
                "ABP"
        }, new ItemStack(EPItems.BASIC_FERTILIZER.get(), 4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_FERTILIZER), Map.of(
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
        }, new ItemStack(EPItems.GOOD_FERTILIZER.get(), 4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.GOOD_FERTILIZER), Map.of(
                'G', ingredientOf(EPItems.GOOD_FERTILIZER),
                'M', ingredientOf(Items.RED_MUSHROOM),
                'S', ingredientOf(Items.SWEET_BERRIES),
                'r', ingredientOf(Tags.Items.DYES_RED),
                'T', ingredientOf(Items.RED_TULIP),
                'R', ingredientOf(Items.ROSE_BUSH)
        ), new String[] {
                "MGS",
                "GrG",
                "TGR"
        }, new ItemStack(EPItems.ADVANCED_FERTILIZER.get(), 4), CraftingBookCategory.MISC);
    }
    private void buildUpgradeModuleCraftingRecipes() {
        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CSC",
                "RBR",
                "CSC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.SPEED_UPGRADE_MODULE_1), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                's', ingredientOf(EPItems.SPEED_UPGRADE_MODULE_1)
        ), new String[] {
                "CSC",
                "RBR",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.SPEED_UPGRADE_MODULE_2), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                's', ingredientOf(EPItems.SPEED_UPGRADE_MODULE_2)
        ), new String[] {
                "CSC",
                "RAR",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.SPEED_UPGRADE_MODULE_3), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                's', ingredientOf(EPItems.SPEED_UPGRADE_MODULE_3)
        ), new String[] {
                "CSC",
                "RAR",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.SPEED_UPGRADE_MODULE_4), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'r', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                's', ingredientOf(EPItems.SPEED_UPGRADE_MODULE_4)
        ), new String[] {
                "CSC",
                "rRr",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'G', ingredientOf(Tags.Items.INGOTS_GOLD),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CGC",
                "RBR",
                "CGC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'G', ingredientOf(Tags.Items.INGOTS_GOLD),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1)
        ), new String[] {
                "CGC",
                "RBR",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'G', ingredientOf(Tags.Items.INGOTS_GOLD),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2)
        ), new String[] {
                "CGC",
                "RAR",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'G', ingredientOf(Tags.Items.INGOTS_GOLD),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3)
        ), new String[] {
                "CGC",
                "RAR",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'G', ingredientOf(Tags.Items.INGOTS_GOLD),
                'r', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4)
        ), new String[] {
                "CGC",
                "rRr",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'c', ingredientOf(ItemTags.COALS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "CTC",
                "cBc",
                "CTC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'c', ingredientOf(ItemTags.COALS),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1)
        ), new String[] {
                "CTC",
                "cBc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'c', ingredientOf(ItemTags.COALS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2)
        ), new String[] {
                "CTC",
                "cAc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'c', ingredientOf(ItemTags.COALS),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3)
        ), new String[] {
                "CTC",
                "cAc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'T', ingredientOf(CommonItemTags.PLATES_TIN),
                'c', ingredientOf(ItemTags.COALS),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'E', ingredientOf(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4)
        ), new String[] {
                "CTC",
                "cRc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "GCG",
                "rRr",
                "GCG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.DURATION_UPGRADE_MODULE_1), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', ingredientOf(EPItems.DURATION_UPGRADE_MODULE_1)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.DURATION_UPGRADE_MODULE_2), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', ingredientOf(EPItems.DURATION_UPGRADE_MODULE_2)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.DURATION_UPGRADE_MODULE_3), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', ingredientOf(EPItems.DURATION_UPGRADE_MODULE_3)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.DURATION_UPGRADE_MODULE_4), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'r', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', ingredientOf(EPItems.DURATION_UPGRADE_MODULE_4)
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.DURATION_UPGRADE_MODULE_5), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'D', ingredientOf(EPItems.DURATION_UPGRADE_MODULE_5)
        ), new String[] {
                "GDG",
                "CRC",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_6.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ADVANCED_UPGRADE_MODULE), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE)
        ), new String[] {
                "GRG",
                "RAR",
                "GRG"
        }, new ItemStack(EPItems.RANGE_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.RANGE_UPGRADE_MODULE_1), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'r', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'R', ingredientOf(EPItems.RANGE_UPGRADE_MODULE_1)
        ), new String[] {
                "GrG",
                "rAr",
                "GRG"
        }, new ItemStack(EPItems.RANGE_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.RANGE_UPGRADE_MODULE_2), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'r', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'R', ingredientOf(EPItems.RANGE_UPGRADE_MODULE_2)
        ), new String[] {
                "GrG",
                "rAr",
                "GRG"
        }, new ItemStack(EPItems.RANGE_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM)
        ), new String[] {
                "IFI",
                "RBR",
                "IFI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1)
        ), new String[] {
                "IFI",
                "RBR",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2)
        ), new String[] {
                "IFI",
                "RAR",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3)
        ), new String[] {
                "IFI",
                "RAR",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'r', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4)
        ), new String[] {
                "IFI",
                "rRr",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'b', ingredientOf(Items.BLAST_FURNACE)
        ), new String[] {
                "CSC",
                "RBR",
                "CbC"
        }, new ItemStack(EPItems.BLAST_FURNACE_UPGRADE_MODULE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM)
        ), new String[] {
                "IRI",
                "FBF",
                "IRI"
        }, new ItemStack(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1)
        ), new String[] {
                "IRI",
                "FBF",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2)
        ), new String[] {
                "IRI",
                "FAF",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3)
        ), new String[] {
                "IRI",
                "FAF",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'r', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'F', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'E', ingredientOf(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4)
        ), new String[] {
                "IrI",
                "FRF",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                's', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE),
                'S', ingredientOf(Items.SMOKER)
        ), new String[] {
                "CsC",
                "RBR",
                "CSC"
        }, new ItemStack(EPItems.SMOKER_UPGRADE_MODULE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'S', ingredientOf(CommonItemTags.SILICON),
                'b', ingredientOf(EPItems.BASIC_SOLAR_CELL),
                'B', ingredientOf(EPItems.BASIC_UPGRADE_MODULE)
        ), new String[] {
                "GSG",
                "bBb",
                "GSG"
        }, new ItemStack(EPItems.MOON_LIGHT_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.MOON_LIGHT_UPGRADE_MODULE_1), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BASIC_SOLAR_CELL),
                'A', ingredientOf(EPItems.ADVANCED_UPGRADE_MODULE),
                'M', ingredientOf(EPItems.MOON_LIGHT_UPGRADE_MODULE_1)
        ), new String[] {
                "GSG",
                "BAB",
                "GMG"
        }, new ItemStack(EPItems.MOON_LIGHT_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.MOON_LIGHT_UPGRADE_MODULE_2), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BASIC_SOLAR_CELL),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE),
                'M', ingredientOf(EPItems.MOON_LIGHT_UPGRADE_MODULE_2)
        ), new String[] {
                "GSG",
                "BRB",
                "GMG"
        }, new ItemStack(EPItems.MOON_LIGHT_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);
    }
    private void buildToolsCraftingRecipes() {
        addHammerCraftingRecipe(output, ItemTags.PLANKS, EPItems.WOODEN_HAMMER);
        addHammerCraftingRecipe(output, ItemTags.STONE_TOOL_MATERIALS, EPItems.STONE_HAMMER);
        addHammerCraftingRecipe(output, ItemTags.COPPER_TOOL_MATERIALS, EPItems.COPPER_HAMMER);
        addHammerCraftingRecipe(output, Tags.Items.INGOTS_IRON, EPItems.IRON_HAMMER);
        addHammerCraftingRecipe(output, Tags.Items.INGOTS_GOLD, EPItems.GOLDEN_HAMMER);
        addHammerCraftingRecipe(output, Tags.Items.GEMS_DIAMOND, EPItems.DIAMOND_HAMMER);

        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_IRON), Map.of(
                'i', ingredientOf(Tags.Items.NUGGETS_IRON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'S', ingredientOf(Tags.Items.RODS_WOODEN)
        ), new String[] {
                "I I",
                " i ",
                "S S"
        }, new ItemStack(EPItems.CUTTER.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(Tags.Items.INGOTS_IRON), Map.of(
                'i', ingredientOf(Tags.Items.NUGGETS_IRON),
                'I', ingredientOf(Tags.Items.INGOTS_IRON)
        ), new String[] {
                " I ",
                " iI",
                "i  "
        }, new ItemStack(EPItems.WRENCH.get()), CraftingBookCategory.MISC);
    }
    private void buildEnergyItemsCraftingRecipes() {
        addShapedCraftingRecipe(output, has(Tags.Items.INGOTS_COPPER), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'c', ingredientOf(ItemTags.COALS)
        ), new String[] {
                "T T",
                "CRC",
                "CcC"
        }, new ItemStack(EPItems.BATTERY_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_1), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'c', ingredientOf(ItemTags.COALS),
                'B', ingredientOf(EPItems.BATTERY_1)
        ), new String[] {
                "T T",
                "CBC",
                "IcI"
        }, new ItemStack(EPItems.BATTERY_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_2), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BATTERY_2)
        ), new String[] {
                "T T",
                "BCB",
                "CSC"
        }, new ItemStack(EPItems.BATTERY_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_3), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BATTERY_3)
        ), new String[] {
                "T T",
                "CBC",
                "SIS"
        }, new ItemStack(EPItems.BATTERY_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_4), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BATTERY_4)
        ), new String[] {
                "T T",
                "BSB",
                "IRI"
        }, new ItemStack(EPItems.BATTERY_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_5), Map.of(
                'T', ingredientOf(CommonItemTags.NUGGETS_TIN),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'B', ingredientOf(EPItems.BATTERY_5)
        ), new String[] {
                "T T",
                "EBE",
                "EBE"
        }, new ItemStack(EPItems.BATTERY_6.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_6), Map.of(
                'G', ingredientOf(Tags.Items.NUGGETS_GOLD),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'S', ingredientOf(CommonItemTags.SILICON),
                'B', ingredientOf(EPItems.BATTERY_6)
        ), new String[] {
                "G G",
                "EBE",
                "SBS"
        }, new ItemStack(EPItems.BATTERY_7.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_7), Map.of(
                'G', ingredientOf(Tags.Items.NUGGETS_GOLD),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'B', ingredientOf(EPItems.BATTERY_7)
        ), new String[] {
                "G G",
                "EBE",
                "ABA"
        }, new ItemStack(EPItems.BATTERY_8.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.COAL_ENGINE_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'c', ingredientOf(CommonItemTags.PLATES_COPPER),
                'C', ingredientOf(EPBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "SIS",
                "RCR",
                "cIc"
        }, new ItemStack(EPItems.INVENTORY_COAL_ENGINE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.CHARGER_ITEM), Map.of(
                'c', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(EPBlocks.CHARGER_ITEM)
        ), new String[] {
                "SIS",
                "RCR",
                "cIc"
        }, new ItemStack(EPItems.INVENTORY_CHARGER.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.TELEPORTER_ITEM), Map.of(
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'c', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'T', ingredientOf(EPBlocks.TELEPORTER_ITEM)
        ), new String[] {
                "CcC",
                "RTR",
                "GcG"
        }, new ItemStack(EPItems.INVENTORY_TELEPORTER.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_3), Map.of(
                'b', ingredientOf(EPItems.BASIC_CIRCUIT),
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'B', ingredientOf(EPItems.BATTERY_3)
        ), new String[] {
                "S S",
                "bRb",
                "CBC"
        }, new ItemStack(EPItems.ENERGY_ANALYZER.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_3), Map.of(
                'b', ingredientOf(EPItems.BASIC_CIRCUIT),
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'L', ingredientOf(Tags.Items.GEMS_LAPIS),
                'B', ingredientOf(EPItems.BATTERY_3)
        ), new String[] {
                "S S",
                "bLb",
                "CBC"
        }, new ItemStack(EPItems.FLUID_ANALYZER.get()), CraftingBookCategory.MISC);
    }
    private void buildItemTransportCraftingRecipes() {
        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_IRON), Map.of(
                'L', ingredientOf(Tags.Items.LEATHERS),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE)
        ), new String[] {
                "   ",
                "LLL",
                "IRI"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM.get(), 6), CraftingBookCategory.MISC,
                "item_conveyor_belt", "_from_leather");

        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_IRON), Map.of(
                'K', ingredientOf(Items.DRIED_KELP),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE)
        ), new String[] {
                "   ",
                "KKK",
                "IRI"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM.get(), 6), CraftingBookCategory.MISC,
                "item_conveyor_belt", "_from_dried_kelp");

        addShapedCraftingRecipe(output, has(CommonItemTags.INGOTS_STEEL), Map.of(
                'S', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM)
        ), new String[] {
                "RIR",
                "SBS",
                "RIR"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.GEARS_IRON), Map.of(
                'G', ingredientOf(CommonItemTags.GEARS_IRON),
                'R', ingredientOf(CommonItemTags.RODS_IRON),
                'r', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM)
        ), new String[] {
                "GRG",
                "rFr",
                "GRG"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'C', ingredientOf(Tags.Items.COBBLESTONES_NORMAL),
                'c', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM),
                'H', ingredientOf(Items.HOPPER)
        ), new String[] {
                "CCC",
                "CHC",
                "CcC"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', ingredientOf(Items.BRICKS),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientOf(Tags.Items.COBBLESTONES_NORMAL),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'R', ingredientOf(Tags.Items.STORAGE_BLOCKS_REDSTONE),
                'L', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CRC",
                "ILI",
                "CRC"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', ingredientOf(Items.BRICKS),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientOf(Tags.Items.COBBLESTONES_NORMAL),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'l', ingredientOf(Items.LEVER),
                'L', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "ClC",
                "ILI",
                "CRC"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', ingredientOf(Items.BRICKS),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientOf(Tags.Items.COBBLESTONES_NORMAL),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'L', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CIC",
                "ILI",
                "CRC"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', ingredientOf(Items.BRICKS),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', ingredientOf(Tags.Items.COBBLESTONES_NORMAL),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'L', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM)
        ), new String[] {
                "CRC",
                "ILI",
                "CIC"
        }, new ItemStack(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'b', ingredientOf(Items.BRICKS),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM)
        ), new String[] {
                "bbb",
                "bBb",
                "SFS"
        }, new ItemStack(EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'S', ingredientOf(Items.SMOOTH_STONE),
                'E', ingredientOf(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM),
                'F', ingredientOf(EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM)
        ), new String[] {
                "IiI",
                "sFs",
                "SES"
        }, new ItemStack(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_IRON), Map.of(
                'B', ingredientOf(Tags.Items.BARRELS_WOODEN),
                'I', ingredientOf(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "III",
                "IBI",
                "III"
        }, new ItemStack(EPBlocks.ITEM_SILO_TINY_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ITEM_SILO_TINY_ITEM), Map.of(
                'S', ingredientOf(EPBlocks.ITEM_SILO_TINY_ITEM),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "IiI",
                "iSi",
                "IiI"
        }, new ItemStack(EPBlocks.ITEM_SILO_SMALL_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ITEM_SILO_SMALL_ITEM), Map.of(
                'S', ingredientOf(EPBlocks.ITEM_SILO_SMALL_ITEM),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.PLATES_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL)
        ), new String[] {
                "IsI",
                "iSi",
                "IsI"
        }, new ItemStack(EPBlocks.ITEM_SILO_MEDIUM_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ITEM_SILO_MEDIUM_ITEM), Map.of(
                'S', ingredientOf(EPBlocks.ITEM_SILO_MEDIUM_ITEM),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL)
        ), new String[] {
                "IsI",
                "sSs",
                "IsI"
        }, new ItemStack(EPBlocks.ITEM_SILO_LARGE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ITEM_SILO_LARGE_ITEM), Map.of(
                'S', ingredientOf(EPBlocks.ITEM_SILO_LARGE_ITEM),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON)
        ), new String[] {
                "III",
                "ISI",
                "III"
        }, new ItemStack(EPBlocks.ITEM_SILO_GIANT_ITEM.get()), CraftingBookCategory.MISC);
    }
    private void buildFluidTransportCraftingRecipes() {
        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_IRON), Map.of(
                'I', ingredientOf(Tags.Items.INGOTS_IRON),
                'i', ingredientOf(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "IiI",
                "IiI",
                "IiI"
        }, new ItemStack(EPBlocks.IRON_FLUID_PIPE_ITEM.get(), 12), CraftingBookCategory.MISC,
                "", "", "iron_");

        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_GOLD), Map.of(
                'G', ingredientOf(Tags.Items.INGOTS_GOLD),
                'g', ingredientOf(CommonItemTags.PLATES_GOLD)
        ), new String[] {
                "GgG",
                "GgG",
                "GgG"
        }, new ItemStack(EPBlocks.GOLDEN_FLUID_PIPE_ITEM.get(), 12), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_IRON), Map.of(
                'G', ingredientOf(Tags.Items.GLASS_PANES_COLORLESS),
                'I', ingredientOf(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "IGI",
                "IGI",
                "IGI"
        }, new ItemStack(EPBlocks.FLUID_TANK_SMALL_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.FLUID_TANK_SMALL_ITEM), Map.of(
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'S', ingredientOf(CommonItemTags.INGOTS_STEEL)
        ), new String[] {
                "SFS",
                "SFS",
                "SFS"
        }, new ItemStack(EPBlocks.FLUID_TANK_MEDIUM_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.FLUID_TANK_MEDIUM_ITEM), Map.of(
                'F', ingredientOf(EPBlocks.FLUID_TANK_MEDIUM_ITEM.get()),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON)
        ), new String[] {
                "IFI",
                "IFI",
                "IFI"
        }, new ItemStack(EPBlocks.FLUID_TANK_LARGE_ITEM.get()), CraftingBookCategory.MISC);
    }
    private void buildEnergyTransportCraftingRecipes() {
        addBasicCableCraftingRecipes(output, CommonItemTags.INGOTS_TIN, CommonItemTags.WIRES_TIN,
                new ItemStack(EPBlocks.TIN_CABLE_ITEM.get(), 9));

        addBasicCableCraftingRecipes(output, Tags.Items.INGOTS_COPPER, CommonItemTags.WIRES_COPPER,
                new ItemStack(EPBlocks.COPPER_CABLE_ITEM.get(), 6));
        addBasicCableCraftingRecipes(output, Tags.Items.INGOTS_GOLD, CommonItemTags.WIRES_GOLD,
                new ItemStack(EPBlocks.GOLD_CABLE_ITEM.get(), 6));

        addBasicCableCraftingRecipes(output, CommonItemTags.INGOTS_ENERGIZED_COPPER, CommonItemTags.WIRES_ENERGIZED_COPPER,
                new ItemStack(EPBlocks.ENERGIZED_COPPER_CABLE_ITEM.get(), 3));
        addBasicCableCraftingRecipes(output, CommonItemTags.INGOTS_ENERGIZED_GOLD, CommonItemTags.WIRES_ENERGIZED_GOLD,
                new ItemStack(EPBlocks.ENERGIZED_GOLD_CABLE_ITEM.get(), 3));

        addShapedCraftingRecipe(output, has(EPItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'I', ingredientOf(EPItems.CABLE_INSULATOR),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX)
        ), new String[] {
                "ICI",
                "ICI",
                "ICI"
        }, new ItemStack(EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'M', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSI",
                "RMR",
                "CSI"
        }, new ItemStack(EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'M', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSI",
                "SMR",
                "CRI"
        }, new ItemStack(EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'M', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CRI",
                "SMS",
                "CRI"
        }, new ItemStack(EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'M', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM)
        ), new String[] {
                "CTI",
                "SMS",
                "CTI"
        }, new ItemStack(EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM.get()), CraftingBookCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'M', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM)
        ), new String[] {
                "CTI",
                "SMS",
                "CTI"
        }, new ItemStack(EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM.get()), CraftingBookCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'M', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM)
        ), new String[] {
                "CTI",
                "SMS",
                "CTI"
        }, new ItemStack(EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM.get()), CraftingBookCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM)
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(EPBlocks.EHV_TRANSFORMER_3_TO_3_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM)
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(EPBlocks.EHV_TRANSFORMER_N_TO_1_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(EPItems.BASIC_CIRCUIT),
                'M', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CSI",
                "BMB",
                "CSI"
        }, new ItemStack(EPBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'B', ingredientOf(EPItems.BASIC_CIRCUIT),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'M', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM)
        ), new String[] {
                "CTI",
                "BMB",
                "CTI"
        }, new ItemStack(EPBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'A', ingredientOf(EPItems.ADVANCED_CIRCUIT),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM)
        ), new String[] {
                "GTG",
                "AMA",
                "GTG"
        }, new ItemStack(EPBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'R', ingredientOf(EPItems.PROCESSING_UNIT),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'M', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM),
                'T', ingredientOf(EPBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM)
        ), new String[] {
                "CTC",
                "RMR",
                "CTC"
        }, new ItemStack(EPBlocks.CONFIGURABLE_EHV_TRANSFORMER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPItems.BATTERY_5),
                'M', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CBC",
                "BMB",
                "SBS"
        }, new ItemStack(EPBlocks.BATTERY_BOX_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'B', ingredientOf(EPItems.BATTERY_8),
                'M', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "EBE",
                "BMB",
                "BSB"
        }, new ItemStack(EPBlocks.ADVANCED_BATTERY_BOX_ITEM.get()), CraftingBookCategory.MISC);

        addShapelessCraftingRecipe(output, has(EPBlocks.BATTERY_BOX_ITEM), List.of(
                ingredientOf(EPBlocks.BATTERY_BOX_ITEM),
                ingredientOf(Items.MINECART)
        ), new ItemStack(EPItems.BATTERY_BOX_MINECART.get()), CraftingBookCategory.MISC);

        addShapelessCraftingRecipe(output, has(EPBlocks.ADVANCED_BATTERY_BOX_ITEM), List.of(
                ingredientOf(EPBlocks.ADVANCED_BATTERY_BOX_ITEM),
                ingredientOf(Items.MINECART)
        ), new ItemStack(EPItems.ADVANCED_BATTERY_BOX_MINECART.get()), CraftingBookCategory.MISC);
    }
    private void buildMachineCraftingRecipes() {
        addShapedCraftingRecipe(output, has(Items.SMOOTH_STONE), Map.of(
                'S', ingredientOf(Items.SMOOTH_STONE),
                'B', ingredientOf(Items.BRICKS),
                's', ingredientOf(ItemTags.SHOVELS)
        ), new String[] {
                "BBB",
                "BsB",
                "SSS"
        }, new ItemStack(EPBlocks.PRESS_MOLD_MAKER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(Items.FURNACE), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(Items.BRICKS),
                'F', ingredientOf(Items.FURNACE)
        ), new String[] {
                "III",
                "FIF",
                "BBB"
        }, new ItemStack(EPBlocks.ALLOY_FURNACE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'b', ingredientOf(EPItems.BASIC_CIRCUIT),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'T', ingredientOf(Items.CRAFTING_TABLE)
        ), new String[] {
                "CTC",
                "bBb",
                "ITI"
        }, new ItemStack(EPBlocks.AUTO_CRAFTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME), Map.of(
                'c', ingredientOf(EPItems.ADVANCED_CIRCUIT),
                'P', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'a', ingredientOf(EPBlocks.AUTO_CRAFTER_ITEM)
        ), new String[] {
                "GaG",
                "cAc",
                "PaP"
        }, new ItemStack(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientOf(Items.STONECUTTER)
        ), new String[] {
                "CsC",
                "SBS",
                "CIC"
        }, new ItemStack(EPBlocks.CRUSHER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'c', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'C', ingredientOf(EPBlocks.CRUSHER_ITEM)
        ), new String[] {
                "aCa",
                "FAF",
                "cCc"
        }, new ItemStack(EPBlocks.ADVANCED_CRUSHER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientOf(Items.STONECUTTER)
        ), new String[] {
                "ISI",
                "SBS",
                "ISI"
        }, new ItemStack(EPBlocks.PULVERIZER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'C', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'P', ingredientOf(EPBlocks.PULVERIZER_ITEM)
        ), new String[] {
                "aPa",
                "FAF",
                "CPC"
        }, new ItemStack(EPBlocks.ADVANCED_PULVERIZER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(EPItems.SAW_BLADE),
                's', ingredientOf(EPItems.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "sSs",
                "CBC",
                "sIs"
        }, new ItemStack(EPBlocks.SAWMILL_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'P', ingredientOf(Items.PISTON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "IPI",
                "PBP",
                "IPI"
        }, new ItemStack(EPBlocks.COMPRESSOR_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'P', ingredientOf(Items.PISTON),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'S', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SPS",
                "IBI",
                "SIS"
        }, new ItemStack(EPBlocks.METAL_PRESS_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'I', ingredientOf(CommonItemTags.GEARS_IRON),
                'i', ingredientOf(CommonItemTags.RODS_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'P', ingredientOf(EPBlocks.PRESS_MOLD_MAKER_ITEM)
        ), new String[] {
                "IPI",
                "iBi",
                "IPI"
        }, new ItemStack(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientOf(Items.STONECUTTER)
        ), new String[] {
                "CSC",
                "sBs",
                "ISI"
        }, new ItemStack(EPBlocks.AUTO_STONECUTTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'G', ingredientOf(Tags.Items.GLASS_PANES_COLORLESS),
                'D', ingredientOf(Items.DIRT),
                'W', ingredientOf(Items.WATER_BUCKET),
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SWS",
                "GBG",
                "IDI"
        }, new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'D', ingredientOf(Items.DISPENSER)
        ), new String[] {
                "IDS",
                "DBD",
                "SDC"
        }, new ItemStack(EPBlocks.BLOCK_PLACER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                's', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'B', ingredientOf(EPItems.BASIC_CIRCUIT),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iRi",
                "BHB",
                "SsS"
        }, new ItemStack(EPBlocks.ASSEMBLING_MACHINE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'B', ingredientOf(EPItems.BASIC_CIRCUIT),
                'S', ingredientOf(CommonItemTags.INGOTS_STEEL),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'A', ingredientOf(EPBlocks.ALLOY_FURNACE_ITEM)
        ), new String[] {
                "SAS",
                "BHB",
                "SAS"
        }, new ItemStack(EPBlocks.INDUCTION_SMELTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SIS",
                "IBI",
                "CFC"
        }, new ItemStack(EPBlocks.FLUID_FILLER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "SiS",
                "IHI",
                "CFC"
        }, new ItemStack(EPBlocks.STONE_LIQUEFIER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iSi",
                "FHF",
                "CIC"
        }, new ItemStack(EPBlocks.STONE_SOLIDIFIER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'i', ingredientOf(CommonItemTags.PLATES_IRON),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'D', ingredientOf(EPBlocks.FLUID_DRAINER_ITEM),
                'F', ingredientOf(EPBlocks.FLUID_FILLER_ITEM),
                'f', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CiC",
                "DHF",
                "IfI"
        }, new ItemStack(EPBlocks.FLUID_TRANSPOSER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'i', ingredientOf(CommonItemTags.GEARS_IRON),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'B', ingredientOf(Items.IRON_BARS),
                'f', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "iBi",
                "FHF",
                "IfI"
        }, new ItemStack(EPBlocks.FILTRATION_PLANT_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CFC",
                "IBI",
                "SIS"
        }, new ItemStack(EPBlocks.FLUID_DRAINER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'P', ingredientOf(Items.PISTON),
                'p', ingredientOf(EPBlocks.IRON_FLUID_PIPE_ITEM)
        ), new String[] {
                "RPR",
                "FBF",
                "IpI"
        }, new ItemStack(EPBlocks.FLUID_PUMP_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'F', ingredientOf(EPBlocks.FLUID_PUMP_ITEM),
                'f', ingredientOf(EPBlocks.FLUID_TANK_LARGE_ITEM)
        ), new String[] {
                "GFG",
                "fAf",
                "aFa"
        }, new ItemStack(EPBlocks.ADVANCED_FLUID_PUMP_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(Tags.Items.STORAGE_BLOCKS_IRON), Map.of(
                'i', ingredientOf(CommonItemTags.PLATES_IRON),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'B', ingredientOf(Items.IRON_BARS),
                'G', ingredientOf(Tags.Items.GLASS_BLOCKS_COLORLESS)
        ), new String[] {
                "IBI",
                "iGi",
                "IiI"
        }, new ItemStack(EPBlocks.DRAIN_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON)
        ), new String[] {
                "sCs",
                "IBI",
                "CSC"
        }, new ItemStack(EPBlocks.CHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'C', ingredientOf(EPBlocks.CHARGER_ITEM)
        ), new String[] {
                "SGS",
                "aAa",
                "GCG"
        }, new ItemStack(EPBlocks.ADVANCED_CHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON)
        ), new String[] {
                "CSC",
                "IBI",
                "sCs"
        }, new ItemStack(EPBlocks.UNCHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'U', ingredientOf(EPBlocks.UNCHARGER_ITEM)
        ), new String[] {
                "GUG",
                "aAa",
                "SGS"
        }, new ItemStack(EPBlocks.ADVANCED_UNCHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.CHARGER_ITEM), Map.of(
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'c', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.PLATES_IRON),
                'S', ingredientOf(CommonItemTags.SILICON),
                'H', ingredientOf(EPBlocks.CHARGER_ITEM)
        ), new String[] {
                "cCc",
                "SHS",
                "iIi"
        }, new ItemStack(EPBlocks.MINECART_CHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_CHARGER_ITEM), Map.of(
                'G', ingredientOf(Tags.Items.STORAGE_BLOCKS_GOLD),
                'g', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'c', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'H', ingredientOf(EPBlocks.ADVANCED_CHARGER_ITEM)
        ), new String[] {
                "gGg",
                "SHS",
                "cCc"
        }, new ItemStack(EPBlocks.ADVANCED_MINECART_CHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.UNCHARGER_ITEM), Map.of(
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'c', ingredientOf(CommonItemTags.PLATES_COPPER),
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'i', ingredientOf(CommonItemTags.PLATES_IRON),
                'S', ingredientOf(CommonItemTags.SILICON),
                'U', ingredientOf(EPBlocks.UNCHARGER_ITEM)
        ), new String[] {
                "iIi",
                "SUS",
                "cCc"
        }, new ItemStack(EPBlocks.MINECART_UNCHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_UNCHARGER_ITEM), Map.of(
                'G', ingredientOf(Tags.Items.STORAGE_BLOCKS_GOLD),
                'g', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'c', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'S', ingredientOf(CommonItemTags.SILICON),
                'U', ingredientOf(EPBlocks.ADVANCED_UNCHARGER_ITEM)
        ), new String[] {
                "cCc",
                "SUS",
                "gGg"
        }, new ItemStack(EPBlocks.ADVANCED_MINECART_UNCHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_SOLAR_CELL), Map.of(
                'G', ingredientOf(Tags.Items.GLASS_PANES_COLORLESS),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'I', ingredientOf(Tags.Items.INGOTS_IRON),
                'C', ingredientOf(Tags.Items.INGOTS_COPPER),
                'B', ingredientOf(EPItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "BRB",
                "ICI"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.SOLAR_PANEL_ITEM_1), Map.of(
                'G', ingredientOf(Tags.Items.GLASS_PANES_COLORLESS),
                'C', ingredientOf(Tags.Items.INGOTS_COPPER),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'S', ingredientOf(EPBlocks.SOLAR_PANEL_ITEM_1),
                'B', ingredientOf(EPItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SBS",
                "CRC"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.SOLAR_PANEL_ITEM_2), Map.of(
                'G', ingredientOf(Tags.Items.GLASS_PANES_COLORLESS),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                's', ingredientOf(CommonItemTags.SILICON),
                'S', ingredientOf(EPBlocks.SOLAR_PANEL_ITEM_2),
                'B', ingredientOf(EPItems.BASIC_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SBS",
                "sRs"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.SOLAR_PANEL_ITEM_3), Map.of(
                'G', ingredientOf(Tags.Items.GLASS_PANES_COLORLESS),
                's', ingredientOf(CommonItemTags.SILICON),
                'R', ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'S', ingredientOf(EPBlocks.SOLAR_PANEL_ITEM_3),
                'A', ingredientOf(EPItems.ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SsS",
                "ARA"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.SOLAR_PANEL_ITEM_4), Map.of(
                'G', ingredientOf(Tags.Items.GLASS_PANES_COLORLESS),
                'a', ingredientOf(Tags.Items.GEMS_AMETHYST),
                'E', ingredientOf(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                'S', ingredientOf(EPBlocks.SOLAR_PANEL_ITEM_4),
                'A', ingredientOf(EPItems.ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SaS",
                "AEA"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.SOLAR_PANEL_ITEM_5), Map.of(
                'G', ingredientOf(Tags.Items.GLASS_PANES_COLORLESS),
                'A', ingredientOf(Tags.Items.GEMS_AMETHYST),
                'E', ingredientOf(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                'S', ingredientOf(EPBlocks.SOLAR_PANEL_ITEM_5),
                'R', ingredientOf(EPItems.REINFORCED_ADVANCED_SOLAR_CELL)
        ), new String[] {
                "GGG",
                "SAS",
                "RER"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_6.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'F', ingredientOf(Items.FURNACE)
        ), new String[] {
                "ISI",
                "CBC",
                "IFI"
        }, new ItemStack(EPBlocks.COAL_ENGINE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(Items.REDSTONE_LAMP), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'C', ingredientOf(Tags.Items.INGOTS_COPPER),
                'R', ingredientOf(Items.REDSTONE_LAMP)
        ), new String[] {
                "CSC",
                "SRS",
                "CSC"
        }, new ItemStack(EPBlocks.POWERED_LAMP_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'I', ingredientOf(CommonItemTags.PLATES_IRON),
                'C', ingredientOf(CommonItemTags.PLATES_COPPER),
                'B', ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                'F', ingredientOf(Items.FURNACE)
        ), new String[] {
                "CFC",
                "SBS",
                "IFI"
        }, new ItemStack(EPBlocks.POWERED_FURNACE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                'P', ingredientOf(EPBlocks.POWERED_FURNACE_ITEM)
        ), new String[] {
                "GPG",
                "SAS",
                "aPa"
        }, new ItemStack(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "RaR",
                "GAG",
                "ECE"
        }, new ItemStack(EPBlocks.LIGHTNING_GENERATOR_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', ingredientOf(CommonItemTags.PLATES_GOLD),
                'R', ingredientOf(Tags.Items.DUSTS_REDSTONE),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "RaR",
                "GAG",
                "ESE"
        }, new ItemStack(EPBlocks.ENERGIZER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'I', ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON),
                'R', ingredientOf(Tags.Items.STORAGE_BLOCKS_REDSTONE),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "aRa",
                "IAI",
                "ECE"
        }, new ItemStack(EPBlocks.CHARGING_STATION_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.COAL_ENGINE_ITEM), Map.of(
                's', ingredientOf(CommonItemTags.SILICON),
                'S', ingredientOf(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'c', ingredientOf(CommonItemTags.WIRES_COPPER),
                'C', ingredientOf(EPBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "cSc",
                "sCs",
                "cSc"
        }, new ItemStack(EPBlocks.HEAT_GENERATOR_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', ingredientOf(CommonItemTags.SILICON),
                'F', ingredientOf(EPBlocks.FLUID_TANK_SMALL_ITEM),
                'c', ingredientOf(CommonItemTags.PLATES_COPPER),
                'C', ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER),
                'H', ingredientOf(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                'E', ingredientOf(EPBlocks.COAL_ENGINE_ITEM)
        ), new String[] {
                "cHc",
                "SES",
                "CFC"
        }, new ItemStack(EPBlocks.THERMAL_GENERATOR_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'a', ingredientOf(Items.AMETHYST_BLOCK),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'P', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', ingredientOf(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "aPa",
                "EAE",
                "aPa"
        }, new ItemStack(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'P', ingredientOf(EPItems.PROCESSING_UNIT),
                'C', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'a', ingredientOf(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'E', ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', ingredientOf(Tags.Items.GEMS_AMETHYST),
                'R', ingredientOf(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM)
        ), new String[] {
                "CAC",
                "PRP",
                "aEa"
        }, new ItemStack(EPBlocks.WEATHER_CONTROLLER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
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
        }, new ItemStack(EPBlocks.TIME_CONTROLLER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
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
        }, new ItemStack(EPBlocks.TELEPORTER_ITEM.get()), CraftingBookCategory.MISC);
    }
    private void buildMiscCraftingRecipes() {
        addShapelessCraftingRecipe(output, InventoryChangeTrigger.TriggerInstance.hasItems(
                Items.BOOK,
                EPBlocks.BASIC_MACHINE_FRAME_ITEM
        ), List.of(
                ingredientOf(Items.BOOK),
                ingredientOf(EPBlocks.BASIC_MACHINE_FRAME_ITEM)
        ), new ItemStack(EPItems.ENERGIZED_POWER_BOOK.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.DUSTS_CHARCOAL), Map.of(
                'P', ingredientOf(Items.PAPER),
                'C', ingredientOf(CommonItemTags.DUSTS_CHARCOAL),
                'I', ingredientOf(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "PCP",
                "CIC",
                "PCP"
        }, new ItemStack(EPItems.CHARCOAL_FILTER.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'A', ingredientOf(Tags.Items.GEMS_AMETHYST),
                'E', ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX),
                'e', ingredientOf(Tags.Items.ENDER_PEARLS)
        ), new String[] {
                "AEA",
                "EeE",
                "AEA"
        }, new ItemStack(EPItems.TELEPORTER_MATRIX.get()), CraftingBookCategory.MISC);
    }
    private void buildCustomCraftingRecipes() {
        addCustomCraftingRecipe(output, TeleporterMatrixSettingsCopyRecipe::new, CraftingBookCategory.MISC,
                "teleporter_matrix_settings_copy");
    }

    private void buildCookingRecipes() {
        addBlastingAndSmeltingRecipes(output, CommonItemTags.RAW_MATERIALS_TIN, new ItemStack(EPItems.TIN_INGOT.get()), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "raw_tin");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.ORES_TIN, new ItemStack(EPItems.TIN_INGOT.get()), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "tin_ores");

        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_TIN, new ItemStack(EPItems.TIN_INGOT.get()), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "tin_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_COPPER, new ItemStack(Items.COPPER_INGOT), CookingBookCategory.MISC,
                100, .7f, "copper_ingot", "copper_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_IRON, new ItemStack(Items.IRON_INGOT), CookingBookCategory.MISC,
                100, .7f, "iron_ingot", "iron_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_GOLD, new ItemStack(Items.GOLD_INGOT), CookingBookCategory.MISC,
                100, .7f, "gold_ingot", "gold_dust");

        addBlastingAndSmeltingRecipes(output, EPItems.COPPER_HAMMER.get(), new ItemStack(Items.COPPER_NUGGET), CookingBookCategory.MISC,
                100, .1f, "copper_nugget", "copper_hammer");
        addBlastingAndSmeltingRecipes(output, EPItems.IRON_HAMMER.get(), new ItemStack(Items.IRON_NUGGET), CookingBookCategory.MISC,
                100, .1f, "iron_nugget", "iron_hammer");
        addBlastingAndSmeltingRecipes(output, EPItems.GOLDEN_HAMMER.get(), new ItemStack(Items.GOLD_NUGGET), CookingBookCategory.MISC,
                100, .1f, "gold_nugget", "golden_hammer");

        addBlastingAndSmeltingRecipes(output, Tags.Items.GEMS_QUARTZ, new ItemStack(EPItems.SILICON.get()), CookingBookCategory.MISC,
                250, 4.1f, "silicon", "nether_quartz");

        addSmeltingRecipe(output, EPBlocks.SAWDUST_BLOCK_ITEM.get(), new ItemStack(Items.CHARCOAL), CookingBookCategory.MISC,
                200, .15f, null, "sawdust_block");

        addSmeltingRecipe(output, EPItems.RAW_GEAR_PRESS_MOLD.get(), new ItemStack(EPItems.GEAR_PRESS_MOLD.get()), CookingBookCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(output, EPItems.RAW_ROD_PRESS_MOLD.get(), new ItemStack(EPItems.ROD_PRESS_MOLD.get()), CookingBookCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(output, EPItems.RAW_WIRE_PRESS_MOLD.get(), new ItemStack(EPItems.WIRE_PRESS_MOLD.get()), CookingBookCategory.MISC,
                200, .3f, null);
    }

    private void buildSmithingRecipes() {
        addNetheriteSmithingUpgradeRecipe(output, ingredientOf(EPItems.DIAMOND_HAMMER),
                new ItemStack(EPItems.NETHERITE_HAMMER.get()));
    }

    private void buildPressMoldMakerRecipes() {
        addPressMoldMakerRecipe(output, 4, new ItemStack(EPItems.RAW_GEAR_PRESS_MOLD.get()));
        addPressMoldMakerRecipe(output, 9, new ItemStack(EPItems.RAW_ROD_PRESS_MOLD.get()));
        addPressMoldMakerRecipe(output, 6, new ItemStack(EPItems.RAW_WIRE_PRESS_MOLD.get()));
    }

    private void buildAlloyFurnaceRecipes() {
        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(Tags.Items.INGOTS_IRON)),
                new IngredientWithCount(ingredientOf(ItemTags.COALS), 3)
        }, new ItemStack(EPItems.STEEL_INGOT.get()), 500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_TIN)),
                new IngredientWithCount(ingredientOf(CommonItemTags.SILICON)),
                new IngredientWithCount(ingredientOf(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(EPItems.REDSTONE_ALLOY_INGOT.get()), 2500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_STEEL), 3),
                new IngredientWithCount(ingredientOf(Tags.Items.INGOTS_COPPER), 3),
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_TIN), 3)
        }, new ItemStack(EPItems.ADVANCED_ALLOY_INGOT.get()), 10000);
    }

    private void buildCompressorRecipes() {
        addCompressorRecipe(output, new IngredientWithCount(ingredientOf(EPItems.STONE_PEBBLE), 16), new ItemStack(Items.COBBLESTONE),
                "stone_pebbles");

        addPlateCompressorRecipes(output, ingredientOf(CommonItemTags.INGOTS_TIN),
                ingredientOf(CommonItemTags.STORAGE_BLOCKS_TIN), new ItemStack(EPItems.TIN_PLATE.get()),
                "tin");
        addPlateCompressorRecipes(output, ingredientOf(Tags.Items.INGOTS_COPPER),
                ingredientOf(Tags.Items.STORAGE_BLOCKS_COPPER), new ItemStack(EPItems.COPPER_PLATE.get()),
                "copper");
        addPlateCompressorRecipes(output, ingredientOf(Tags.Items.INGOTS_IRON),
                ingredientOf(Tags.Items.STORAGE_BLOCKS_IRON), new ItemStack(EPItems.IRON_PLATE.get()),
                "iron");
        addPlateCompressorRecipes(output, ingredientOf(Tags.Items.INGOTS_GOLD),
                ingredientOf(Tags.Items.STORAGE_BLOCKS_GOLD), new ItemStack(EPItems.GOLD_PLATE.get()),
                "gold");

        addPlateCompressorIngotRecipe(output, ingredientOf(CommonItemTags.INGOTS_ADVANCED_ALLOY),
                new ItemStack(EPItems.ADVANCED_ALLOY_PLATE.get()), "advanced_alloy");
        addPlateCompressorIngotRecipe(output, ingredientOf(CommonItemTags.INGOTS_ENERGIZED_COPPER),
                new ItemStack(EPItems.ENERGIZED_COPPER_PLATE.get()), "energized_copper");
        addPlateCompressorIngotRecipe(output, ingredientOf(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                new ItemStack(EPItems.ENERGIZED_GOLD_PLATE.get()), "energized_gold");
    }

    private void buildCrusherRecipes() {
        addCrusherRecipe(output, ingredientOf(Items.STONE), new ItemStack(Items.COBBLESTONE),
                "stone");
        addCrusherRecipe(output, ingredientOf(Items.STONE_BRICKS, Items.CHISELED_STONE_BRICKS, Items.CRACKED_STONE_BRICKS,
                        Items.SMOOTH_STONE), new ItemStack(Items.COBBLESTONE),
                "stone_variants");

        addCrusherRecipe(output, ingredientOf(Items.MOSSY_STONE_BRICKS), new ItemStack(Items.MOSSY_COBBLESTONE),
                "mossy_stone_bricks");

        addCrusherRecipe(output, ingredientOf(Items.TUFF_BRICKS, Items.CHISELED_TUFF_BRICKS, Items.CHISELED_TUFF,
                        Items.POLISHED_TUFF), new ItemStack(Items.TUFF),
                "tuff_variants");

        addCrusherRecipe(output, ingredientOf(Items.DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate");
        addCrusherRecipe(output, ingredientOf(Items.DEEPSLATE_BRICKS, Items.CHISELED_DEEPSLATE, Items.CRACKED_DEEPSLATE_BRICKS,
                        Items.DEEPSLATE_TILES, Items.CRACKED_DEEPSLATE_TILES, Items.POLISHED_DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate_variants");

        addCrusherRecipe(output, ingredientOf(Items.POLISHED_GRANITE), new ItemStack(Items.GRANITE),
                "polished_granite");
        addCrusherRecipe(output, ingredientOf(Items.POLISHED_DIORITE), new ItemStack(Items.DIORITE),
                "polished_diorite");
        addCrusherRecipe(output, ingredientOf(Items.POLISHED_ANDESITE), new ItemStack(Items.ANDESITE),
                "polished_andesite");

        addCrusherRecipe(output, ingredientOf(Tags.Items.COBBLESTONES_NORMAL), new ItemStack(Items.GRAVEL),
                "cobblestone");

        addCrusherRecipe(output, ingredientOf(Tags.Items.GRAVELS), new ItemStack(Items.SAND),
                "gravel");

        addCrusherRecipe(output, ingredientOf(Items.SANDSTONE), new ItemStack(Items.SAND),
                "sandstone");
        addCrusherRecipe(output, ingredientOf(Items.SMOOTH_SANDSTONE, Items.CUT_SANDSTONE,
                        Items.CHISELED_SANDSTONE), new ItemStack(Items.SAND),
                "sandstone_variants");

        addCrusherRecipe(output, ingredientOf(Items.RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone");
        addCrusherRecipe(output, ingredientOf(Items.SMOOTH_RED_SANDSTONE, Items.CUT_RED_SANDSTONE,
                        Items.CHISELED_RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone_variants");

        addCrusherRecipe(output, ingredientOf(Items.POLISHED_BLACKSTONE, Items.POLISHED_BLACKSTONE_BRICKS,
                        Items.CHISELED_POLISHED_BLACKSTONE, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                        Items.GILDED_BLACKSTONE), new ItemStack(Items.BLACKSTONE),
                "blackstone_variants");

        addCrusherRecipe(output, ingredientOf(Items.SMOOTH_BASALT, Items.POLISHED_BASALT), new ItemStack(Items.BASALT),
                "basalt_variants");
    }

    private void buildPulverizerRecipes() {
        addBasicMetalPulverizerRecipes(output,
                ingredientOf(CommonItemTags.ORES_TIN), ingredientOf(CommonItemTags.RAW_MATERIALS_TIN),
                ingredientOf(CommonItemTags.STORAGE_BLOCKS_RAW_TIN), ingredientOf(CommonItemTags.INGOTS_TIN),
                new ItemStack(EPItems.TIN_DUST.get()), "tin");
        addBasicMetalPulverizerRecipes(output,
                ingredientOf(Tags.Items.ORES_IRON), ingredientOf(Tags.Items.RAW_MATERIALS_IRON),
                ingredientOf(Tags.Items.STORAGE_BLOCKS_RAW_IRON), ingredientOf(Tags.Items.INGOTS_IRON),
                new ItemStack(EPItems.IRON_DUST.get()), "iron");
        addBasicMetalPulverizerRecipes(output,
                ingredientOf(Tags.Items.ORES_GOLD), ingredientOf(Tags.Items.RAW_MATERIALS_GOLD),
                ingredientOf(Tags.Items.STORAGE_BLOCKS_RAW_GOLD), ingredientOf(Tags.Items.INGOTS_GOLD),
                new ItemStack(EPItems.GOLD_DUST.get()), "gold");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.ORES_COPPER),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(EPItems.COPPER_DUST.get()), new double[] {
                        1., 1., 1., 1., .5, .5
                }, new double[] {
                        1., 1., 1., 1., .75, .5, .25
                }),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(EPItems.GOLD_DUST.get()),
                        .1, .2), "copper_ores");
        addRawMetalAndIngotPulverizerRecipes(output,
                ingredientOf(Tags.Items.RAW_MATERIALS_COPPER),
                ingredientOf(Tags.Items.STORAGE_BLOCKS_RAW_COPPER), ingredientOf(Tags.Items.INGOTS_COPPER),
                new ItemStack(EPItems.COPPER_DUST.get()), "copper");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.ORES_COAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.COAL), new double[] {
                        1., 1., .25
                }, new double[] {
                        1., 1., .5, .25
                }), "coal_ores");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.ORES_REDSTONE),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.REDSTONE), new double[] {
                        1., 1., 1., 1., 1., .67, .33, .33, .17
                }, new double[] {
                        1., 1., 1., 1., 1., .67, .67, .33, .33, .17
                }), "redstone_ores");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.ORES_LAPIS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.LAPIS_LAZULI), new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .5, .5, .25, .125
                }, new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .75, .5, .5, .25, .125
                }), "lapis_ores");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.ORES_EMERALD),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.EMERALD), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "emerald_ores");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.ORES_DIAMOND),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.DIAMOND), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "diamond_ores");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.ORES_QUARTZ),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.QUARTZ), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "nether_quartz_ores");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.ORES_NETHERITE_SCRAP),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.NETHERITE_SCRAP), new double[] {
                        1., .125, .125
                }, new double[] {
                        1., .25, .25, .125
                }), "ancient_debris");

        addPulverizerRecipe(output, ingredientOf(Items.CHARCOAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(EPItems.CHARCOAL_DUST.get()),
                        1., 1.), "charcoal");

        addPulverizerRecipe(output, ingredientOf(Items.CLAY),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.CLAY_BALL), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "clay");

        addPulverizerRecipe(output, ingredientOf(Items.GLOWSTONE),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.GLOWSTONE_DUST), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "glowstone");

        addPulverizerRecipe(output, ingredientOf(Items.MAGMA_BLOCK),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.MAGMA_CREAM), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "magma_block");

        addPulverizerRecipe(output, ingredientOf(Items.QUARTZ_BLOCK),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.QUARTZ), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "quartz_block");

        addPulverizerRecipe(output, ingredientOf(ItemTags.WOOL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.STRING), new double[] {
                        1., 1., 1., 1.
                }, new double[] {
                        1., 1., 1., 1.
                }), "wool");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.GRAVELS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.FLINT),
                        1., 1.), "gravels");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.BONES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.BONE_MEAL), new double[] {
                        1., 1., 1., .25, .25
                }, new double[] {
                        1., 1., 1., .5, .25, .125
                }), "bones");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.RODS_BLAZE),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.BLAZE_POWDER), new double[] {
                        1., 1., .5
                }, new double[] {
                        1., 1., .75, .25
                }), "blaze_rods");

        addPulverizerRecipe(output, ingredientOf(Tags.Items.RODS_BREEZE),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.WIND_CHARGE), new double[] {
                        1., 1., 1., 1., .5, .5
                }, new double[] {
                        1., 1., 1., 1., .75, .5, .25
                }), "breeze_rods");
    }

    private void buildSawmillRecipes() {
        addBasicWoodSawmillRecipe(output, new ItemStack(Items.OAK_PLANKS),
                ingredientOf(ItemTags.OAK_LOGS), ingredientOf(Items.OAK_FENCE),
                ingredientOf(Items.OAK_FENCE_GATE), ingredientOf(Items.OAK_DOOR),
                ingredientOf(Items.OAK_TRAPDOOR), ingredientOf(Items.OAK_PRESSURE_PLATE),
                ingredientOf(Items.OAK_SIGN), ingredientOf(Items.OAK_SHELF),
                ingredientOf(Items.OAK_BOAT), ingredientOf(Items.OAK_CHEST_BOAT),
                false, "oak");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.SPRUCE_PLANKS),
                ingredientOf(ItemTags.SPRUCE_LOGS), ingredientOf(Items.SPRUCE_FENCE),
                ingredientOf(Items.SPRUCE_FENCE_GATE), ingredientOf(Items.SPRUCE_DOOR),
                ingredientOf(Items.SPRUCE_TRAPDOOR), ingredientOf(Items.SPRUCE_PRESSURE_PLATE),
                ingredientOf(Items.SPRUCE_SIGN), ingredientOf(Items.SPRUCE_SHELF),
                ingredientOf(Items.SPRUCE_BOAT), ingredientOf(Items.SPRUCE_CHEST_BOAT),
                false, "spruce");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.BIRCH_PLANKS),
                ingredientOf(ItemTags.BIRCH_LOGS), ingredientOf(Items.BIRCH_FENCE),
                ingredientOf(Items.BIRCH_FENCE_GATE), ingredientOf(Items.BIRCH_DOOR),
                ingredientOf(Items.BIRCH_TRAPDOOR), ingredientOf(Items.BIRCH_PRESSURE_PLATE),
                ingredientOf(Items.BIRCH_SIGN), ingredientOf(Items.BIRCH_SHELF),
                ingredientOf(Items.BIRCH_BOAT), ingredientOf(Items.BIRCH_CHEST_BOAT),
                false, "birch");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.JUNGLE_PLANKS),
                ingredientOf(ItemTags.JUNGLE_LOGS), ingredientOf(Items.JUNGLE_FENCE),
                ingredientOf(Items.JUNGLE_FENCE_GATE), ingredientOf(Items.JUNGLE_DOOR),
                ingredientOf(Items.JUNGLE_TRAPDOOR), ingredientOf(Items.JUNGLE_PRESSURE_PLATE),
                ingredientOf(Items.JUNGLE_SIGN), ingredientOf(Items.JUNGLE_SHELF),
                ingredientOf(Items.JUNGLE_BOAT), ingredientOf(Items.JUNGLE_CHEST_BOAT),
                false, "jungle");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.ACACIA_PLANKS),
                ingredientOf(ItemTags.ACACIA_LOGS), ingredientOf(Items.ACACIA_FENCE),
                ingredientOf(Items.ACACIA_FENCE_GATE), ingredientOf(Items.ACACIA_DOOR),
                ingredientOf(Items.ACACIA_TRAPDOOR), ingredientOf(Items.ACACIA_PRESSURE_PLATE),
                ingredientOf(Items.ACACIA_SIGN), ingredientOf(Items.ACACIA_SHELF),
                ingredientOf(Items.ACACIA_BOAT), ingredientOf(Items.ACACIA_CHEST_BOAT),
                false, "acacia");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.DARK_OAK_PLANKS),
                ingredientOf(ItemTags.DARK_OAK_LOGS), ingredientOf(Items.DARK_OAK_FENCE),
                ingredientOf(Items.DARK_OAK_FENCE_GATE), ingredientOf(Items.DARK_OAK_DOOR),
                ingredientOf(Items.DARK_OAK_TRAPDOOR), ingredientOf(Items.DARK_OAK_PRESSURE_PLATE),
                ingredientOf(Items.DARK_OAK_SIGN), ingredientOf(Items.DARK_OAK_SHELF),
                ingredientOf(Items.DARK_OAK_BOAT), ingredientOf(Items.DARK_OAK_CHEST_BOAT),
                false, "dark_oak");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.MANGROVE_PLANKS),
                ingredientOf(ItemTags.MANGROVE_LOGS), ingredientOf(Items.MANGROVE_FENCE),
                ingredientOf(Items.MANGROVE_FENCE_GATE), ingredientOf(Items.MANGROVE_DOOR),
                ingredientOf(Items.MANGROVE_TRAPDOOR), ingredientOf(Items.MANGROVE_PRESSURE_PLATE),
                ingredientOf(Items.MANGROVE_SIGN), ingredientOf(Items.MANGROVE_SHELF),
                ingredientOf(Items.MANGROVE_BOAT), ingredientOf(Items.MANGROVE_CHEST_BOAT),
                false, "mangrove");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.CHERRY_PLANKS),
                ingredientOf(ItemTags.CHERRY_LOGS), ingredientOf(Items.CHERRY_FENCE),
                ingredientOf(Items.CHERRY_FENCE_GATE), ingredientOf(Items.CHERRY_DOOR),
                ingredientOf(Items.CHERRY_TRAPDOOR), ingredientOf(Items.CHERRY_PRESSURE_PLATE),
                ingredientOf(Items.CHERRY_SIGN), ingredientOf(Items.CHERRY_SHELF),
                ingredientOf(Items.CHERRY_BOAT), ingredientOf(Items.CHERRY_CHEST_BOAT),
                false, "cherry");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.PALE_OAK_PLANKS),
                ingredientOf(ItemTags.PALE_OAK_LOGS), ingredientOf(Items.PALE_OAK_FENCE),
                ingredientOf(Items.PALE_OAK_FENCE_GATE), ingredientOf(Items.PALE_OAK_DOOR),
                ingredientOf(Items.PALE_OAK_TRAPDOOR), ingredientOf(Items.PALE_OAK_PRESSURE_PLATE),
                ingredientOf(Items.PALE_OAK_SIGN), ingredientOf(Items.PALE_OAK_SHELF),
                ingredientOf(Items.PALE_OAK_BOAT), ingredientOf(Items.PALE_OAK_CHEST_BOAT),
                false, "pale_oak");

        addSawmillRecipe(output, ingredientOf(ItemTags.BAMBOO_BLOCKS), new ItemStack(Items.BAMBOO_PLANKS, 3),
                1, "bamboo_planks", "bamboo_blocks");
        addBasicWoodWithoutLogsSawmillRecipe(output, new ItemStack(Items.BAMBOO_PLANKS),
                ingredientOf(Items.BAMBOO_FENCE), ingredientOf(Items.BAMBOO_FENCE_GATE), ingredientOf(Items.BAMBOO_DOOR),
                ingredientOf(Items.BAMBOO_TRAPDOOR), ingredientOf(Items.BAMBOO_PRESSURE_PLATE),
                ingredientOf(Items.BAMBOO_SIGN), ingredientOf(Items.BAMBOO_SHELF),
                ingredientOf(Items.BAMBOO_RAFT), ingredientOf(Items.BAMBOO_CHEST_RAFT),
                true, "bamboo");

        addSawmillRecipe(output, ingredientOf(ItemTags.CRIMSON_STEMS), new ItemStack(Items.CRIMSON_PLANKS, 6),
                1, "crimson_planks", "crimson_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(output, new ItemStack(Items.CRIMSON_PLANKS),
                ingredientOf(Items.CRIMSON_FENCE), ingredientOf(Items.CRIMSON_FENCE_GATE), ingredientOf(Items.CRIMSON_DOOR),
                ingredientOf(Items.CRIMSON_TRAPDOOR), ingredientOf(Items.CRIMSON_PRESSURE_PLATE),
                ingredientOf(Items.CRIMSON_SIGN), ingredientOf(Items.CRIMSON_SHELF), "crimson");

        addSawmillRecipe(output, ingredientOf(ItemTags.WARPED_STEMS), new ItemStack(Items.WARPED_PLANKS, 6),
                1, "warped_planks", "warped_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(output, new ItemStack(Items.WARPED_PLANKS),
                ingredientOf(Items.WARPED_FENCE), ingredientOf(Items.WARPED_FENCE_GATE), ingredientOf(Items.WARPED_DOOR),
                ingredientOf(Items.WARPED_TRAPDOOR), ingredientOf(Items.WARPED_PRESSURE_PLATE),
                ingredientOf(Items.WARPED_SIGN), ingredientOf(Items.WARPED_SHELF), "warped");

        addSawmillRecipe(output, ingredientOf(Items.CRAFTING_TABLE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "crafting_table");
        addSawmillRecipe(output, ingredientOf(Items.CARTOGRAPHY_TABLE), new ItemStack(Items.OAK_PLANKS, 4),
                new ItemStack(Items.PAPER, 2), "oak_planks", "cartography_table");
        addSawmillRecipe(output, ingredientOf(Items.FLETCHING_TABLE), new ItemStack(Items.OAK_PLANKS, 4),
                new ItemStack(Items.FLINT, 2), "oak_planks", "fletching_table");
        addSawmillRecipe(output, ingredientOf(Items.LOOM), new ItemStack(Items.OAK_PLANKS, 2),
                new ItemStack(Items.STRING, 2), "oak_planks", "loom");
        addSawmillRecipe(output, ingredientOf(Items.COMPOSTER), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "composter");
        addSawmillRecipe(output, ingredientOf(Items.NOTE_BLOCK), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.REDSTONE), "oak_planks", "note_block");
        addSawmillRecipe(output, ingredientOf(Items.JUKEBOX), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.DIAMOND), "oak_planks", "jukebox");

        addSawmillRecipe(output, ingredientOf(Items.BOOKSHELF), new ItemStack(Items.OAK_PLANKS, 6),
                new ItemStack(Items.BOOK, 3), "oak_planks", "bookshelf");
        addSawmillRecipe(output, ingredientOf(Items.CHISELED_BOOKSHELF), new ItemStack(Items.OAK_PLANKS, 6),
                5, "oak_planks", "chiseled_bookshelf");
        addSawmillRecipe(output, ingredientOf(Items.LECTERN), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.BOOK, 3), "oak_planks", "lectern");

        addSawmillRecipe(output, ingredientOf(Items.CHEST), new ItemStack(Items.OAK_PLANKS, 7),
                3, "oak_planks", "chest");
        addSawmillRecipe(output, ingredientOf(Items.BARREL), new ItemStack(Items.OAK_PLANKS, 6),
                5, "oak_planks", "barrel");

        addSawmillRecipe(output, ingredientOf(Items.WOODEN_SWORD), new ItemStack(Items.OAK_PLANKS, 2),
                1, "oak_planks", "wooden_sword");
        addSawmillRecipe(output, ingredientOf(Items.WOODEN_SHOVEL), new ItemStack(Items.OAK_PLANKS),
                2, "oak_planks", "wooden_shovel");
        addSawmillRecipe(output, ingredientOf(Items.WOODEN_PICKAXE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_pickaxe");
        addSawmillRecipe(output, ingredientOf(Items.WOODEN_AXE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_axe");
        addSawmillRecipe(output, ingredientOf(Items.WOODEN_HOE), new ItemStack(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hoe");
        addSawmillRecipe(output, ingredientOf(EPItems.WOODEN_HAMMER.get()), new ItemStack(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hammer");

        addSawmillRecipe(output, ingredientOf(ItemTags.PLANKS), new ItemStack(Items.STICK, 3),
                1, "sticks", "planks");
        addSawmillRecipe(output, ingredientOf(Items.BAMBOO_MOSAIC), new ItemStack(Items.STICK, 3),
                3, "sticks", "bamboo_mosaic");

        addSawmillRecipe(output, ingredientOf(ItemTags.WOODEN_STAIRS),
                new ItemStack(Items.STICK, 3), 1, "sticks", "stairs");
        addSawmillRecipe(output, ingredientOf(Items.BAMBOO_MOSAIC_STAIRS),
                new ItemStack(Items.STICK, 3), 1, "sticks", "bamboo_mosaic_stairs");
        addSawmillRecipe(output, ingredientOf(ItemTags.WOODEN_SLABS),
                new ItemStack(Items.STICK, 1), 1, "sticks", "slabs");
        addSawmillRecipe(output, ingredientOf(Items.BAMBOO_MOSAIC_SLAB),
                new ItemStack(Items.STICK, 1), 1, "sticks", "bamboo_mosaic_slabs");
        addSawmillRecipe(output, ingredientOf(ItemTags.WOODEN_BUTTONS), new ItemStack(Items.STICK, 3),
                1, "sticks", "buttons");

        addSawmillRecipe(output, ingredientOf(Items.LADDER), new ItemStack(Items.STICK, 2),
                1, "sticks", "ladder");

        addSawmillRecipe(output, ingredientOf(Items.BOWL), new ItemStack(Items.STICK),
                2, "sticks", "bowl");
        addSawmillRecipe(output, ingredientOf(Items.BOW), new ItemStack(Items.STICK, 3),
                new ItemStack(Items.STRING, 3), "sticks", "bow");
        addSawmillRecipe(output, ingredientOf(Items.FISHING_ROD), new ItemStack(Items.STICK, 3),
                new ItemStack(Items.STRING, 2), "sticks", "fishing_rod");

        addSawmillRecipe(output, ingredientOf(Tags.Items.RODS_WOODEN), new ItemStack(EPItems.SAWDUST.get()),
                0, "sawdust", "sticks");
    }

    private void buildPlantGrowthChamberRecipes() {
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
        addBasicFlowerGrowingRecipe(output, Items.OPEN_EYEBLOSSOM, "open_eyeblossoms");
        addBasicFlowerGrowingRecipe(output, Items.CLOSED_EYEBLOSSOM, "closed_eyeblossoms");

        addBasicFlowerGrowingRecipe(output, Items.SUNFLOWER, "sunflowers");
        addBasicFlowerGrowingRecipe(output, Items.LILAC, "lilacs");
        addBasicFlowerGrowingRecipe(output, Items.ROSE_BUSH, "rose_bushes");
        addBasicFlowerGrowingRecipe(output, Items.PEONY, "peonies");

        addBasicMushroomsGrowingRecipe(output, Items.BROWN_MUSHROOM, "brown_mushrooms");
        addBasicMushroomsGrowingRecipe(output, Items.RED_MUSHROOM, "red_mushrooms");

        addBasicAncientFlowerGrowingRecipe(output, Items.TORCHFLOWER_SEEDS, Items.TORCHFLOWER, "torchflowers");
        addBasicAncientFlowerGrowingRecipe(output, Items.PITCHER_POD, Items.PITCHER_PLANT, "pitcher_plants");

        addPlantGrowthChamberRecipe(output, ingredientOf(Items.PINK_PETALS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.PINK_PETALS), new double[] {
                        1., 1., 1., .67, .33, .33, .15
                })
        }, 16000, "pink_petals", "pink_petals");

        addPlantGrowthChamberRecipe(output, ingredientOf(Items.WILDFLOWERS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.WILDFLOWERS), new double[] {
                        1., 1., 1., .67, .33, .33, .15
                })
        }, 16000, "wildflowers", "wildflowers");

        addPlantGrowthChamberRecipe(output, ingredientOf(Items.SWEET_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SWEET_BERRIES), new double[] {
                        1., 1., .33, .17
                })
        }, 16000, "sweet_berries", "sweet_berries");

        addPlantGrowthChamberRecipe(output, ingredientOf(Items.GLOW_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.GLOW_BERRIES), new double[] {
                        1., 1., .67, .33, .17, .17
                })
        }, 16000, "glow_berries", "glow_berries");

        addPlantGrowthChamberRecipe(output, ingredientOf(Items.WHEAT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT), new double[] {
                        1., .75, .25
                })
        }, 16000, "wheat", "wheat_seeds");

        addPlantGrowthChamberRecipe(output, ingredientOf(Items.BEETROOT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "beetroots", "beetroot_seeds");

        addPlantGrowthChamberRecipe(output, ingredientOf(Items.POTATO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.POTATO), new double[] {
                        1., .75, .25, .25
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.POISONOUS_POTATO), new double[] {
                        .125
                })
        }, 16000, "potatoes", "potato");

        addPlantGrowthChamberRecipe(output, ingredientOf(Items.CARROT), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.CARROT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "carrots", "carrot");

        addPlantGrowthChamberRecipe(output, ingredientOf(Items.MELON_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.MELON_SLICE), new double[] {
                        1., 1., .75, .25, .25
                })
        }, 16000, "melon_slices", "melon_seeds");

        addPlantGrowthChamberRecipe(output, ingredientOf(Items.PUMPKIN_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.PUMPKIN), new double[] {
                        1.
                })
        }, 16000, "pumpkin", "pumpkin_seeds");

        addPlantGrowthChamberRecipe(output, ingredientOf(Items.SUGAR_CANE), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SUGAR_CANE), new double[] {
                        1., 1., .67, .67, .33, .17, .17
                })
        }, 16000, "sugar_canes", "sugar_cane");
        addPlantGrowthChamberRecipe(output, ingredientOf(Items.BAMBOO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BAMBOO), new double[] {
                        1., 1., .67, .17
                })
        }, 16000, "bamboo", "bamboo");
    }

    private void buildPlantGrowthChamberFertilizerRecipes() {
        addPlantGrowthChamberFertilizerRecipe(output, ingredientOf(Items.BONE_MEAL),
                1.5, 3., "bone_meal");

        addPlantGrowthChamberFertilizerRecipe(output, ingredientOf(EPItems.BASIC_FERTILIZER.get()),
                2.5, 3.5, "basic_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(output, ingredientOf(EPItems.GOOD_FERTILIZER.get()),
                3.5, 5., "good_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(output, ingredientOf(EPItems.ADVANCED_FERTILIZER.get()),
                5., 6.5, "advanced_fertilizer");
    }

    private void buildMetalPressRecipes() {
        addGearMetalPressRecipe(output, ingredientOf(CommonItemTags.PLATES_IRON), new ItemStack(EPItems.IRON_GEAR.get()));

        addRodMetalPressRecipe(output, ingredientOf(CommonItemTags.PLATES_IRON), new ItemStack(EPItems.IRON_ROD.get()));

        addWireMetalPressRecipe(output, ingredientOf(CommonItemTags.PLATES_TIN), new ItemStack(EPItems.TIN_WIRE.get()));
        addWireMetalPressRecipe(output, ingredientOf(CommonItemTags.PLATES_COPPER), new ItemStack(EPItems.COPPER_WIRE.get()));
        addWireMetalPressRecipe(output, ingredientOf(CommonItemTags.PLATES_GOLD), new ItemStack(EPItems.GOLD_WIRE.get()));

        addWireMetalPressRecipe(output, ingredientOf(CommonItemTags.PLATES_ENERGIZED_COPPER), new ItemStack(EPItems.ENERGIZED_COPPER_WIRE.get()));
        addWireMetalPressRecipe(output, ingredientOf(CommonItemTags.PLATES_ENERGIZED_GOLD), new ItemStack(EPItems.ENERGIZED_GOLD_WIRE.get()));
    }

    private void buildHeatGeneratorRecipes() {
        addHeatGeneratorRecipe(output, Fluids.FLOWING_LAVA, 15, "flowing_lava");
        addHeatGeneratorRecipe(output, Fluids.LAVA, 25, "still_lava");
    }

    private void buildThermalGeneratorRecipes() {
        addThermalGeneratorRecipe(output, Fluids.LAVA, 20000, "lava");
    }

    private void buildAssemblingMachineRecipes() {
        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(EPItems.BASIC_SOLAR_CELL), 2),
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_ENERGIZED_COPPER), 4),
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_TIN), 2),
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY), 1)
        }, new ItemStack(EPItems.ADVANCED_SOLAR_CELL.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(EPItems.ADVANCED_SOLAR_CELL), 2),
                new IngredientWithCount(ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX), 4),
                new IngredientWithCount(ingredientOf(CommonItemTags.SILICON), 2),
                new IngredientWithCount(ingredientOf(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(EPItems.REINFORCED_ADVANCED_SOLAR_CELL.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(EPItems.BASIC_CIRCUIT), 4),
                new IngredientWithCount(ingredientOf(CommonItemTags.WIRES_ENERGIZED_COPPER), 4),
                new IngredientWithCount(ingredientOf(CommonItemTags.SILICON), 4),
                new IngredientWithCount(ingredientOf(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(EPItems.ADVANCED_CIRCUIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(EPItems.ADVANCED_CIRCUIT), 4),
                new IngredientWithCount(ingredientOf(CommonItemTags.WIRES_ENERGIZED_GOLD), 6),
                new IngredientWithCount(ingredientOf(CommonItemTags.SILICON), 6)
        }, new ItemStack(EPItems.PROCESSING_UNIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(EPItems.PROCESSING_UNIT), 4),
                new IngredientWithCount(ingredientOf(EPItems.TELEPORTER_MATRIX), 4),
                new IngredientWithCount(ingredientOf(EPItems.ENERGIZED_CRYSTAL_MATRIX), 2),
                new IngredientWithCount(ingredientOf(CommonItemTags.SILICON), 2)
        }, new ItemStack(EPItems.TELEPORTER_PROCESSING_UNIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(ingredientOf(Tags.Items.GEMS_AMETHYST), 6),
                new IngredientWithCount(ingredientOf(Tags.Items.GEMS_DIAMOND), 2),
                new IngredientWithCount(ingredientOf(Tags.Items.GEMS_EMERALD), 2),
                new IngredientWithCount(ingredientOf(CommonItemTags.INGOTS_REDSTONE_ALLOY))
        }, new ItemStack(EPItems.CRYSTAL_MATRIX.get()));
    }

    private void buildStoneLiquefierRecipes() {
        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.STONE), 50, "stone");

        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.COBBLESTONE), 50, "cobblestone");

        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.DEEPSLATE), 150, "deepslate");

        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.COBBLED_DEEPSLATE), 150, "cobbled_deepslate");

        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.GRANITE), 50, "granite");

        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.DIORITE), 50, "diorite");

        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.ANDESITE), 50, "andesite");

        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.BLACKSTONE), 250, "blackstone");

        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.OBSIDIAN), 1000, "obsidian");

        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.NETHERRACK), 250, "netherrack");

        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.MAGMA_CREAM), 250, "magma_cream");
        addLavaOutputStoneLiquefierRecipe(output, Ingredient.of(Items.MAGMA_BLOCK), 1000, "magma_block");

        addWaterOutputStoneLiquefierRecipe(output, Ingredient.of(Items.SNOWBALL), 125, "snowball");

        addWaterOutputStoneLiquefierRecipe(output, Ingredient.of(Items.SNOW_BLOCK), 500, "snow_block");

        addWaterOutputStoneLiquefierRecipe(output, Ingredient.of(Items.ICE), 1000, "ice");
    }

    private void buildStoneSolidifierRecipes() {
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

    private void buildFiltrationPlantRecipes() {
        addOreFiltrationRecipe(output, new ItemStack(EPItems.RAW_TIN.get()), 0.05, "tin");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_COPPER), 0.05, "copper");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_IRON), 0.05, "iron");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_GOLD), 0.005, "gold");
    }

    private void buildFluidTransposerRecipes() {
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.WHITE_CONCRETE_POWDER), new ItemStack(Items.WHITE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.ORANGE_CONCRETE_POWDER), new ItemStack(Items.ORANGE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.MAGENTA_CONCRETE_POWDER), new ItemStack(Items.MAGENTA_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.LIGHT_BLUE_CONCRETE_POWDER), new ItemStack(Items.LIGHT_BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.YELLOW_CONCRETE_POWDER), new ItemStack(Items.YELLOW_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.LIME_CONCRETE_POWDER), new ItemStack(Items.LIME_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.PINK_CONCRETE_POWDER), new ItemStack(Items.PINK_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.GRAY_CONCRETE_POWDER), new ItemStack(Items.GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.LIGHT_GRAY_CONCRETE_POWDER), new ItemStack(Items.LIGHT_GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.CYAN_CONCRETE_POWDER), new ItemStack(Items.CYAN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.PURPLE_CONCRETE_POWDER), new ItemStack(Items.PURPLE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.BLUE_CONCRETE_POWDER), new ItemStack(Items.BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.BROWN_CONCRETE_POWDER), new ItemStack(Items.BROWN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.GREEN_CONCRETE_POWDER), new ItemStack(Items.GREEN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.RED_CONCRETE_POWDER), new ItemStack(Items.RED_CONCRETE));
        addConcreteFluidTransposerRecipe(output, ingredientOf(Items.BLACK_CONCRETE_POWDER), new ItemStack(Items.BLACK_CONCRETE));

        addFluidTransposerRecipe(output, ingredientOf(Items.SPONGE), new ItemStack(Items.WET_SPONGE), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, 1000));
        addFluidTransposerRecipe(output, ingredientOf(Items.WET_SPONGE), new ItemStack(Items.SPONGE), FluidTransposerBlockEntity.Mode.EMPTYING,
                new FluidStack(Fluids.WATER, 1000));

        addFluidTransposerRecipe(output, ingredientOf(Items.DIRT), new ItemStack(Items.MUD), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, 250));
    }

    private void buildChargerRecipes() {
        addChargerRecipe(output, ingredientOf(Tags.Items.INGOTS_COPPER),
                new ItemStack(EPItems.ENERGIZED_COPPER_INGOT.get()), 4194304);
    }

    private void buildEnergizerRecipes() {
        addEnergizerRecipe(output, ingredientOf(Tags.Items.INGOTS_COPPER),
                new ItemStack(EPItems.ENERGIZED_COPPER_INGOT.get()), 32768);
        addEnergizerRecipe(output, ingredientOf(Tags.Items.INGOTS_GOLD),
                new ItemStack(EPItems.ENERGIZED_GOLD_INGOT.get()), 131072);
        addEnergizerRecipe(output, ingredientOf(EPItems.CRYSTAL_MATRIX),
                new ItemStack(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()), 524288);
    }

    private void buildCrystalGrowthChamberRecipes() {
        addCrystalGrowthChamberRecipe(output, ingredientOf(Tags.Items.GEMS_AMETHYST),
                new OutputItemStackWithPercentages(new ItemStack(Items.AMETHYST_SHARD), new double[] {
                    1., 1., .67, .5, .25, .125
                }), 16000);
        addCrystalGrowthChamberRecipe(output, new IngredientWithCount(ingredientOf(Items.AMETHYST_BLOCK), 4),
                new OutputItemStackWithPercentages(new ItemStack(Items.BUDDING_AMETHYST), .25),
                32000);
    }

    private void add3x3PackingCraftingRecipe(RecipeOutput output, Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                    Ingredient unpackedInput, ItemLike packedItem, CraftingBookCategory category,
                                                    String group, String recipeIdSuffix) {
        addShapedCraftingRecipe(output, hasIngredientTrigger, Map.of(
                '#', unpackedInput
        ), new String[] {
                "###",
                "###",
                "###"
        }, new ItemStack(packedItem), category, group, recipeIdSuffix);
    }
    private void add3x3UnpackingCraftingRecipe(RecipeOutput output, Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                      Ingredient packedInput, ItemLike unpackedItem, CraftingBookCategory category,
                                                      String group, String recipeIdSuffix) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, List.of(
                packedInput
        ), new ItemStack(unpackedItem, 9), category, group, recipeIdSuffix);
    }
    private void addMetalIngotCraftingRecipes(RecipeOutput output, TagKey<Item> nuggetInput,
                                                     TagKey<Item> blockInput, ItemLike ingotItem, String metalName) {
        add3x3PackingCraftingRecipe(output, has(nuggetInput), ingredientOf(nuggetInput), ingotItem,
                CraftingBookCategory.MISC, metalName + "_ingot", "_from_nuggets");
        add3x3UnpackingCraftingRecipe(output, has(blockInput), ingredientOf(blockInput), ingotItem,
                CraftingBookCategory.MISC, metalName + "_ingot", "_from_" + metalName + "_block");
    }
    private void addMetalNuggetCraftingRecipe(RecipeOutput output, TagKey<Item> ingotInput, ItemLike nuggetItem) {
        addShapelessCraftingRecipe(output, has(ingotInput), List.of(
                ingredientOf(ingotInput)
        ), new ItemStack(nuggetItem, 9), CraftingBookCategory.MISC);
    }
    private void addMetalPlateCraftingRecipe(RecipeOutput output, TagKey<Item> ingotInput, ItemLike plateItem) {
        addShapelessCraftingRecipe(output, has(ingotInput), List.of(
                ingredientOf(CommonItemTags.TOOLS_HAMMERS),
                ingredientOf(ingotInput)
        ), new ItemStack(plateItem), CraftingBookCategory.MISC);
    }
    private void addMetalWireCraftingRecipe(RecipeOutput output, TagKey<Item> plateInput, ItemLike wireItem) {
        addShapelessCraftingRecipe(output, has(plateInput), List.of(
                ingredientOf(CommonItemTags.TOOLS_CUTTERS),
                ingredientOf(plateInput)
        ), new ItemStack(wireItem, 2), CraftingBookCategory.MISC);
    }
    private void addHammerCraftingRecipe(RecipeOutput output, TagKey<Item> materialInput, ItemLike hammerItem) {
        addShapedCraftingRecipe(output, has(materialInput), Map.of(
                'S', ingredientOf(Tags.Items.RODS_WOODEN),
                'M', ingredientOf(materialInput)
        ), new String[] {
                " M ",
                " SM",
                "S  "
        }, new ItemStack(hammerItem), CraftingBookCategory.MISC);
    }
    private void addBasicCableCraftingRecipes(RecipeOutput output, TagKey<Item> ingotInput, TagKey<Item> wireInput,
                                                     ItemStack cableItem) {
        addCableCraftingRecipe(output, ingotInput, cableItem);
        addCableUsingWireCraftingRecipe(output, wireInput, cableItem);
    }
    private void addCableUsingWireCraftingRecipe(RecipeOutput output, TagKey<Item> wireInput,
                                                        ItemStack cableItem) {
        addShapedCraftingRecipe(output, has(wireInput), Map.of(
                'W', ingredientOf(wireInput),
                'I', ingredientOf(EPItems.CABLE_INSULATOR)
        ), new String[] {
                "IWI",
                "IWI",
                "IWI"
        }, cableItem, CraftingBookCategory.MISC, getItemName(cableItem.getItem()), "_using_wire");
    }
    private void addCableCraftingRecipe(RecipeOutput output, TagKey<Item> ingotInput,
                                               ItemStack cableItem) {
        addShapedCraftingRecipe(output, has(ingotInput), Map.of(
                'I', ingredientOf(ingotInput),
                'i', ingredientOf(EPItems.CABLE_INSULATOR)
        ), new String[] {
                "iIi",
                "iIi",
                "iIi"
        }, cableItem, CraftingBookCategory.MISC, getItemName(cableItem.getItem()));
    }
    private void addShapedCraftingRecipe(RecipeOutput output, Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingBookCategory category) {
        addShapedCraftingRecipe(output, hasIngredientTrigger, key, pattern, result, category, "");
    }
    private void addShapedCraftingRecipe(RecipeOutput output, Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingBookCategory category,
                                                String group) {
        addShapedCraftingRecipe(output, hasIngredientTrigger, key, pattern, result, category, group, "");
    }
    private void addShapedCraftingRecipe(RecipeOutput output, Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingBookCategory category,
                                                String group, String recipeIdSuffix) {
        addShapedCraftingRecipe(output, hasIngredientTrigger, key, pattern, result, category, group, recipeIdSuffix, "");
    }
    private void addShapedCraftingRecipe(RecipeOutput output, Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingBookCategory category,
                                                String group, String recipeIdSuffix, String recipeIdPrefix) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdPrefix + getItemName(result.getItem()) + recipeIdSuffix);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", hasIngredientTrigger)
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        ShapedRecipe recipe = new ShapedRecipe(Objects.requireNonNullElse(group, ""),
                category, ShapedRecipePattern.of(key, pattern), result);
        output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }
    private void addShapelessCraftingRecipe(RecipeOutput output, Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingBookCategory category) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, inputs, result, category, "");
    }
    private void addShapelessCraftingRecipe(RecipeOutput output, Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingBookCategory category,
                                                   String group) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, inputs, result, category, group, "");
    }
    private void addShapelessCraftingRecipe(RecipeOutput output, Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingBookCategory category,
                                                   String group, String recipeIdSuffix) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, inputs, result, category, group, recipeIdSuffix, "");
    }
    private void addShapelessCraftingRecipe(RecipeOutput output, Criterion<InventoryChangeTrigger.TriggerInstance> hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingBookCategory category,
                                                   String group, String recipeIdSuffix, String recipeIdPrefix) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdPrefix + getItemName(result.getItem()) + recipeIdSuffix);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", hasIngredientTrigger)
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        ShapelessRecipe recipe = new ShapelessRecipe(Objects.requireNonNullElse(group, ""), category, result,
                NonNullList.of(null, inputs.toArray(Ingredient[]::new)));
        output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }
    private void addCustomCraftingRecipe(RecipeOutput output, Function<CraftingBookCategory, ? extends CustomRecipe> customRecipeFactory,
                                                CraftingBookCategory category, String recipeIdString) {
        Identifier recipeId = EPAPI.id("crafting/" +
                recipeIdString);

        CustomRecipe recipe = customRecipeFactory.apply(category);
        output.accept(getKey(recipeId), recipe, null);
    }

    private void addBlastingAndSmeltingRecipes(RecipeOutput output, ItemLike ingredient, ItemStack result,
                                                      CookingBookCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }
    private void addBlastingAndSmeltingRecipes(RecipeOutput output, TagKey<Item> ingredient, ItemStack result,
                                                      CookingBookCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }

    private void addSmeltingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemName(result.getItem()));

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }
    private void addSmeltingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }
    private void addSmeltingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, Identifier recipeId) {
        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        AbstractCookingRecipe recipe = new SmeltingRecipe(Objects.requireNonNullElse(group, ""),
                category, ingredientOf(ingredient), result, xp, time);
        output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }
    private void addSmeltingRecipe(RecipeOutput output, TagKey<Item> ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        AbstractCookingRecipe recipe = new SmeltingRecipe(Objects.requireNonNullElse(group, ""),
                category, ingredientOf(ingredient), result, xp, time);
        output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }

    private void addBlastingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        AbstractCookingRecipe recipe = new BlastingRecipe(Objects.requireNonNullElse(group, ""),
                category, ingredientOf(ingredient), result, xp, time);
        output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }
    private void addBlastingRecipe(RecipeOutput output, TagKey<Item> ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        AbstractCookingRecipe recipe = new BlastingRecipe(Objects.requireNonNullElse(group, ""),
                category, ingredientOf(ingredient), result, xp, time);
        output.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }

    private void addNetheriteSmithingUpgradeRecipe(RecipeOutput recipeOutput, Ingredient base, ItemStack output) {
        Identifier recipeId = EPAPI.id("smithing/" +
                getItemName(output.getItem()));

        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey(recipeId)))
                .addCriterion("has_the_ingredient", has(Tags.Items.INGOTS_NETHERITE))
                .rewards(AdvancementRewards.Builder.recipe(getKey(recipeId)))
                .requirements(AdvancementRequirements.Strategy.OR);
        SmithingTransformRecipe recipe = new SmithingTransformRecipe(Optional.of(ingredientOf(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)),
                base, Optional.of(ingredientOf(Tags.Items.INGOTS_NETHERITE)), new TransmuteResult(output.getItemHolder(), output.getCount(), output.getComponentsPatch()));
        recipeOutput.accept(getKey(recipeId), recipe, advancementBuilder.build(recipeId.withPrefix("recipes/")));
    }

    private void addAlloyFurnaceRecipe(RecipeOutput recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              int ticks) {
        addAlloyFurnaceRecipe(recipeOutput, inputs, output, OutputItemStackWithPercentages.EMPTY, ticks);
    }
    private void addAlloyFurnaceRecipe(RecipeOutput recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              OutputItemStackWithPercentages secondaryOutput, int ticks) {
        Identifier recipeId = EPAPI.id("alloy_furnace/" +
                getItemName(output.getItem()));

        AlloyFurnaceRecipe recipe = new AlloyFurnaceRecipe(output, secondaryOutput, inputs, ticks);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addPressMoldMakerRecipe(RecipeOutput recipeOutput, int clayCount, ItemStack output) {
        Identifier recipeId = EPAPI.id("press_mold_maker/" +
                getItemName(output.getItem()));

        PressMoldMakerRecipe recipe = new PressMoldMakerRecipe(output, clayCount);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addPlateCompressorRecipes(RecipeOutput recipeOutput, Ingredient ingotInput,
                                                  Ingredient blockInput, ItemStack output, String metalName) {
        addPlateCompressorIngotRecipe(recipeOutput, ingotInput, output, metalName);
        addCompressorRecipe(recipeOutput, blockInput, output.copyWithCount(9), metalName + "_block");
    }
    private void addPlateCompressorIngotRecipe(RecipeOutput recipeOutput, Ingredient ingotInput,
                                                   ItemStack output, String metalName) {
        addCompressorRecipe(recipeOutput, ingotInput, output, metalName + "_ingot");
    }
    private void addCompressorRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output, String recipeIngredientName) {
        addCompressorRecipe(recipeOutput, new IngredientWithCount(input), output, recipeIngredientName);
    }
    private void addCompressorRecipe(RecipeOutput recipeOutput, IngredientWithCount input, ItemStack output,
                                            String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("compressing/" +
                getItemName(output.getItem()) + "_from_compressing_" + recipeIngredientName);

        CompressorRecipe recipe = new CompressorRecipe(output, input);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addCrusherRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output,
                                         String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("crusher/" +
                getItemName(output.getItem()) + "_from_crushing_" + recipeIngredientName);

        CrusherRecipe recipe = new CrusherRecipe(output, input);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addBasicMetalPulverizerRecipes(RecipeOutput recipeOutput, Ingredient oreInput,
                                                       Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                       Ingredient ingotInput, ItemStack output, String metalName) {
        addPulverizerRecipe(recipeOutput, oreInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., .25
        }, new double[] {
                1., 1, .5, .25
        }), metalName + "_ores");

        addRawMetalAndIngotPulverizerRecipes(recipeOutput, rawMetalInput, rawMetalBlockInput, ingotInput, output, metalName);
    }
    private void addRawMetalAndIngotPulverizerRecipes(RecipeOutput recipeOutput,
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
    private void addPulverizerRecipe(RecipeOutput recipeOutput, Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            String recipeIngredientName) {
        addPulverizerRecipe(recipeOutput, input, output,
                new PulverizerRecipe.OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0], new double[0]), recipeIngredientName);
    }
    private void addPulverizerRecipe(RecipeOutput recipeOutput, Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            PulverizerRecipe.OutputItemStackWithPercentages secondaryOutput,
                                            String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("pulverizer/" +
                getItemName(output.output().getItem()) + "_from_pulverizer_" + recipeIngredientName);

        PulverizerRecipe recipe = new PulverizerRecipe(output, secondaryOutput, input);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addBasicWoodSawmillRecipe(RecipeOutput recipeOutput, ItemStack planksItem,
                                           Ingredient logsInput, Ingredient fenceInput, Ingredient fenceGateInput,
                                           Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                           Ingredient signInput, Ingredient shelfInput,
                                           Ingredient boatInput, Ingredient chestBoatInput, boolean isRaft, String woodName) {
        addSawmillRecipe(recipeOutput, logsInput, planksItem.copyWithCount(6), 1, getItemName(planksItem.getItem()),
                woodName + "_logs");

        addBasicWoodWithoutLogsSawmillRecipe(recipeOutput, planksItem, fenceInput, fenceGateInput, doorInput, trapdoorInput,
                pressurePlateInput, signInput, shelfInput, boatInput, chestBoatInput, isRaft, woodName);
    }
    private void addBasicWoodWithoutLogsSawmillRecipe(RecipeOutput recipeOutput, ItemStack planksItem,
                                                      Ingredient fenceInput, Ingredient fenceGateInput,
                                                      Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                      Ingredient signInput, Ingredient shelfInput,
                                                      Ingredient boatInput, Ingredient chestBoatInput, boolean isRaft, String woodName) {
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(recipeOutput, planksItem, fenceInput, fenceGateInput, doorInput,
                trapdoorInput, pressurePlateInput, signInput, shelfInput, woodName);

        addSawmillRecipe(recipeOutput, boatInput, planksItem.copyWithCount(4), 3, getItemName(planksItem.getItem()),
                woodName + (isRaft?"_raft":"_boat"));
        addSawmillRecipe(recipeOutput, chestBoatInput, planksItem.copyWithCount(5), 7, getItemName(planksItem.getItem()),
                woodName + (isRaft?"_chest_raft":"_chest_boat"));
    }
    private void addBasicWoodWithoutLogsAndBoatsSawmillRecipe(RecipeOutput recipeOutput, ItemStack planksItem,
                                                                     Ingredient fenceInput, Ingredient fenceGateInput,
                                                                     Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                                     Ingredient signInput, Ingredient shelfInput, String woodName) {
        addSawmillRecipe(recipeOutput, fenceInput, planksItem, 2, getItemName(planksItem.getItem()),
                woodName + "_fence");
        addSawmillRecipe(recipeOutput, fenceGateInput, planksItem.copyWithCount(2), 3, getItemName(planksItem.getItem()),
                woodName + "_fence_gate");
        addSawmillRecipe(recipeOutput, doorInput, planksItem, 3, getItemName(planksItem.getItem()),
                woodName + "_door");
        addSawmillRecipe(recipeOutput, trapdoorInput, planksItem.copyWithCount(2), 3, getItemName(planksItem.getItem()),
                woodName + "_trapdoor");
        addSawmillRecipe(recipeOutput, pressurePlateInput, planksItem, 2, getItemName(planksItem.getItem()),
                woodName + "_pressure_plate");
        addSawmillRecipe(recipeOutput, signInput, planksItem.copyWithCount(2), 1, getItemName(planksItem.getItem()),
                woodName + "_sign");
        addSawmillRecipe(recipeOutput, shelfInput, planksItem.copyWithCount(3), 1, getItemName(planksItem.getItem()),
                woodName + "_shelf");
    }
    private void addSawmillRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output,
                                         int sawdustAmount, String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillRecipe recipe = new SawmillRecipe(output, input, sawdustAmount);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }
    private void addSawmillRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output,
                                         ItemStack secondaryOutput, String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillRecipe recipe = new SawmillRecipe(output, secondaryOutput, input);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addBasicFlowerGrowingRecipe(RecipeOutput recipeOutput, ItemLike flowerItem,
                                                    String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, ingredientOf(flowerItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., 1., .33
                })
        }, 16000, outputName, getItemName(flowerItem));
    }
    private void addBasicMushroomsGrowingRecipe(RecipeOutput recipeOutput, ItemLike mushroomItem,
                                                       String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, ingredientOf(mushroomItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(mushroomItem), new double[] {
                        1., 1., .5, .25
                })
        }, 16000, outputName, getItemName(mushroomItem));
    }
    private void addBasicAncientFlowerGrowingRecipe(RecipeOutput recipeOutput, ItemLike seedItem,
                                                           ItemLike flowerItem, String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, ingredientOf(seedItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(seedItem), new double[] {
                        1., .33, .15
                }),
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., .15
                })
        }, 16000, outputName, getItemName(seedItem));
    }
    private void addPlantGrowthChamberRecipe(RecipeOutput recipeOutput, Ingredient input,
                                                    OutputItemStackWithPercentages[] outputs, int ticks,
                                                    String outputName, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("growing/" +
                outputName + "_from_growing_" + recipeIngredientName);

        PlantGrowthChamberRecipe recipe = new PlantGrowthChamberRecipe(outputs, input, ticks);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addPlantGrowthChamberFertilizerRecipe(RecipeOutput recipeOutput, Ingredient input,
                                                              double speedMultiplier, double energyConsumptionMultiplier,
                                                              String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("growing/fertilizer/" +
                recipeIngredientName);

        PlantGrowthChamberFertilizerRecipe recipe = new PlantGrowthChamberFertilizerRecipe(input, speedMultiplier,
                energyConsumptionMultiplier);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addGearMetalPressRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, new IngredientWithCount(input, 2), output, new ItemStack(EPItems.GEAR_PRESS_MOLD.get()));
    }
    private void addRodMetalPressRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output.copyWithCount(2), new ItemStack(EPItems.ROD_PRESS_MOLD.get()));
    }
    private void addWireMetalPressRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output.copyWithCount(3), new ItemStack(EPItems.WIRE_PRESS_MOLD.get()));
    }
    private void addMetalPressRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output,
                                            ItemStack pressMold) {
        addMetalPressRecipe(recipeOutput, new IngredientWithCount(input), output, pressMold);
    }
    private void addMetalPressRecipe(RecipeOutput recipeOutput, IngredientWithCount input, ItemStack output,
                                            ItemStack pressMold) {
        Identifier recipeId = EPAPI.id("metal_press/" +
                getItemName(output.getItem()));

        MetalPressRecipe recipe = new MetalPressRecipe(output, pressMold, input);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addHeatGeneratorRecipe(RecipeOutput recipeOutput, Fluid input, int energyProduction,
                                               String recipeIngredientName) {
        addHeatGeneratorRecipe(recipeOutput, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private void addHeatGeneratorRecipe(RecipeOutput recipeOutput, Fluid[] input, int energyProduction,
                                               String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("heat_generator/" +
                "energy_production_from_" + recipeIngredientName);

        HeatGeneratorRecipe recipe = new HeatGeneratorRecipe(input, energyProduction);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addThermalGeneratorRecipe(RecipeOutput recipeOutput, Fluid input, int energyProduction,
                                                  String recipeIngredientName) {
        addThermalGeneratorRecipe(recipeOutput, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private void addThermalGeneratorRecipe(RecipeOutput recipeOutput, Fluid[] input, int energyProduction,
                                                  String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("thermal_generator/" +
                "energy_production_from_" + recipeIngredientName);

        ThermalGeneratorRecipe recipe = new ThermalGeneratorRecipe(input, energyProduction);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addLavaOutputStoneLiquefierRecipe(RecipeOutput recipeOutput, Ingredient input, int lavaAmount, String recipeIngredientName) {
        addStoneLiquefierRecipe(recipeOutput, input, new FluidStack(Fluids.LAVA, lavaAmount), recipeIngredientName);
    }
    private void addWaterOutputStoneLiquefierRecipe(RecipeOutput recipeOutput, Ingredient input, int waterAmount, String recipeIngredientName) {
        addStoneLiquefierRecipe(recipeOutput, input, new FluidStack(Fluids.WATER, waterAmount), recipeIngredientName);
    }
    private void addStoneLiquefierRecipe(RecipeOutput recipeOutput, Ingredient input, FluidStack output, String recipeIngredientName) {
        Identifier recipeId = EPAPI.id("stone_liquefier/" + recipeIngredientName);

        StoneLiquefierRecipe recipe = new StoneLiquefierRecipe(input, output);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addStoneSolidifierRecipe(RecipeOutput recipeOutput, int waterAmount, int lavaAmount, ItemStack output) {
        Identifier recipeId = EPAPI.id("stone_solidifier/" +
                getItemName(output.getItem()));

        StoneSolidifierRecipe recipe = new StoneSolidifierRecipe(output, waterAmount, lavaAmount);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addAssemblingMachineRecipe(RecipeOutput recipeOutput, IngredientWithCount[] inputs, ItemStack output) {
        Identifier recipeId = EPAPI.id("assembling/" +
                getItemName(output.getItem()));

        AssemblingMachineRecipe recipe = new AssemblingMachineRecipe(output, inputs);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addOreFiltrationRecipe(RecipeOutput recipeOutput, ItemStack oreOutput, double oreOutputPercentage,
                                               String oreName) {
        addFiltrationPlantRecipe(recipeOutput, new OutputItemStackWithPercentages(new ItemStack(EPItems.STONE_PEBBLE.get()), .33),
                new OutputItemStackWithPercentages(oreOutput, oreOutputPercentage), BuiltInRegistries.ITEM.getKey(oreOutput.getItem()),
                oreName + "_ore_filtration");
    }
    private void addFiltrationPlantRecipe(RecipeOutput recipeOutput, OutputItemStackWithPercentages output,
                                                 Identifier icon, String recipeName) {
        addFiltrationPlantRecipe(recipeOutput, output, OutputItemStackWithPercentages.EMPTY, icon, recipeName);
    }
    private void addFiltrationPlantRecipe(RecipeOutput recipeOutput, OutputItemStackWithPercentages output,
                                                 OutputItemStackWithPercentages secondaryOutput, Identifier icon,
                                                 String recipeName) {
        Identifier recipeId = EPAPI.id("filtration_plant/" +
                recipeName);

        FiltrationPlantRecipe recipe = new FiltrationPlantRecipe(output, secondaryOutput, icon);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addConcreteFluidTransposerRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output) {
        addFluidTransposerRecipe(recipeOutput, input, output, FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, 1000));
    }
    private void addFluidTransposerRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output,
                                                 FluidTransposerBlockEntity.Mode mode, FluidStack fluid) {
        Identifier recipeId = EPAPI.id("fluid_transposer/" +
                getItemName(output.getItem()));

        FluidTransposerRecipe recipe = new FluidTransposerRecipe(mode, output, input, fluid);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addChargerRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        Identifier recipeId = EPAPI.id("charger/" +
                getItemName(output.getItem()));

        ChargerRecipe recipe = new ChargerRecipe(output, input, energyConsumption);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addEnergizerRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        Identifier recipeId = EPAPI.id("energizer/" +
                getItemName(output.getItem()));

        EnergizerRecipe recipe = new EnergizerRecipe(output, input, energyConsumption);
        recipeOutput.accept(getKey(recipeId), recipe, null);
    }

    private void addCrystalGrowthChamberRecipe(RecipeOutput recipeOutput, Ingredient input, OutputItemStackWithPercentages output,
                                                      int ticks) {
        addCrystalGrowthChamberRecipe(recipeOutput, new IngredientWithCount(input), output, ticks);
    }
    private void addCrystalGrowthChamberRecipe(RecipeOutput recipeOutput, IngredientWithCount input, OutputItemStackWithPercentages output,
                                                      int ticks) {
        Identifier recipeId = EPAPI.id("crystal_growing/" +
                getItemName(output.output().getItem()));

        CrystalGrowthChamberRecipe recipe = new CrystalGrowthChamberRecipe(output, input, ticks);
        recipeOutput.accept(getKey(recipeId), recipe, null);
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
