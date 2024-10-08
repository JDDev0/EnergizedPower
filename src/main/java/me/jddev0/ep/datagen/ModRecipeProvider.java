package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.datagen.recipe.*;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> output) {
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

    private void buildCraftingRecipes(Consumer<FinishedRecipe> output) {
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
    private void buildItemIngredientsCraftingRecipes(Consumer<FinishedRecipe> output) {
        add3x3UnpackingCraftingRecipe(output, has(EPBlocks.SAWDUST_BLOCK_ITEM),
                Ingredient.of(EPBlocks.SAWDUST_BLOCK.get()), EPItems.SAWDUST,
                CraftingBookCategory.MISC, "", "_from_sawdust_block");
        add3x3PackingCraftingRecipe(output, has(CommonItemTags.DUSTS_WOOD),
                Ingredient.of(CommonItemTags.DUSTS_WOOD), EPBlocks.SAWDUST_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        add3x3UnpackingCraftingRecipe(output, has(CommonItemTags.STORAGE_BLOCKS_SILICON),
                Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON), EPItems.SILICON,
                CraftingBookCategory.MISC, "", "_from_silicon_block");
        add3x3PackingCraftingRecipe(output, has(CommonItemTags.SILICON),
                Ingredient.of(CommonItemTags.SILICON), EPBlocks.SILICON_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        addMetalNuggetCraftingRecipe(output, CommonItemTags.INGOTS_TIN, EPItems.TIN_NUGGET);
        addMetalIngotCraftingRecipes(output, CommonItemTags.NUGGETS_TIN, CommonItemTags.STORAGE_BLOCKS_TIN,
                EPItems.TIN_INGOT, "tin");

        add3x3PackingCraftingRecipe(output, has(CommonItemTags.INGOTS_TIN),
                Ingredient.of(CommonItemTags.INGOTS_TIN), EPBlocks.TIN_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        add3x3UnpackingCraftingRecipe(output, has(CommonItemTags.STORAGE_BLOCKS_RAW_TIN),
                Ingredient.of(CommonItemTags.STORAGE_BLOCKS_RAW_TIN), EPItems.RAW_TIN,
                CraftingBookCategory.MISC, "", "");
        add3x3PackingCraftingRecipe(output, has(CommonItemTags.RAW_MATERIALS_TIN),
                Ingredient.of(CommonItemTags.RAW_MATERIALS_TIN), EPBlocks.RAW_TIN_BLOCK_ITEM,
                CraftingBookCategory.MISC, "", "");

        addMetalPlateCraftingRecipe(output, CommonItemTags.INGOTS_TIN, EPItems.TIN_PLATE);
        addMetalPlateCraftingRecipe(output, Tags.Items.INGOTS_COPPER, EPItems.COPPER_PLATE);
        addMetalPlateCraftingRecipe(output, Tags.Items.INGOTS_IRON, EPItems.IRON_PLATE);
        addMetalPlateCraftingRecipe(output, Tags.Items.INGOTS_GOLD, EPItems.GOLD_PLATE);

        addMetalWireCraftingRecipe(output, CommonItemTags.PLATES_TIN, EPItems.TIN_WIRE);
        addMetalWireCraftingRecipe(output, CommonItemTags.PLATES_COPPER, EPItems.COPPER_WIRE);
        addMetalWireCraftingRecipe(output, CommonItemTags.PLATES_GOLD, EPItems.GOLD_WIRE);

        addShapedCraftingRecipe(output, has(CommonItemTags.SILICON), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'Q', Ingredient.of(Tags.Items.GEMS_QUARTZ),
                'T', Ingredient.of(CommonItemTags.INGOTS_TIN),
                'C', Ingredient.of(Tags.Items.INGOTS_COPPER)
        ), new String[] {
                " C ",
                "SQS",
                " T "
        }, new ItemStack(EPItems.BASIC_SOLAR_CELL.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.WIRES_COPPER), Map.of(
                'C', Ingredient.of(CommonItemTags.WIRES_COPPER),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'S', Ingredient.of(CommonItemTags.SILICON)
        ), new String[] {
                "RCR",
                "CIC",
                "SCS"
        }, new ItemStack(EPItems.BASIC_CIRCUIT.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_CIRCUIT), Map.of(
                'G', Ingredient.of(CommonItemTags.WIRES_GOLD),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.of(EPItems.BASIC_CIRCUIT.get())
        ), new String[] {
                "GGG",
                "CBC",
                "CBC"
        }, new ItemStack(EPItems.BASIC_UPGRADE_MODULE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ADVANCED_CIRCUIT), Map.of(
                'G', Ingredient.of(CommonItemTags.WIRES_ENERGIZED_GOLD),
                'C', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'A', Ingredient.of(EPItems.ADVANCED_CIRCUIT.get()),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get())
        ), new String[] {
                "GGG",
                "CBC",
                "CAC"
        }, new ItemStack(EPItems.ADVANCED_UPGRADE_MODULE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE), Map.of(
                'g', Ingredient.of(CommonItemTags.WIRES_ENERGIZED_GOLD),
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'P', Ingredient.of(EPItems.PROCESSING_UNIT.get()),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get())
        ), new String[] {
                "ggg",
                "GAG",
                "GPG"
        }, new ItemStack(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(Tags.Items.INGOTS_IRON), Map.of(
                'i', Ingredient.of(Tags.Items.NUGGETS_IRON),
                'I', Ingredient.of(Tags.Items.INGOTS_IRON)
        ), new String[] {
                " i ",
                "iIi",
                " i "
        }, new ItemStack(EPItems.SAW_BLADE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.SILICON), Map.of(
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'I', Ingredient.of(Tags.Items.INGOTS_IRON),
                'C', Ingredient.of(Tags.Items.INGOTS_COPPER)
        ), new String[] {
                "CIC",
                "ISI",
                "CIC"
        }, new ItemStack(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.GEARS_IRON), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                's', Ingredient.of(CommonItemTags.INGOTS_STEEL),
                'I', Ingredient.of(CommonItemTags.GEARS_IRON),
                'R', Ingredient.of(CommonItemTags.RODS_IRON),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "IsR",
                "SBS",
                "RsI"
        }, new ItemStack(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.INGOTS_ENERGIZED_COPPER), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'A', Ingredient.of(CommonItemTags.INGOTS_ADVANCED_ALLOY),
                'E', Ingredient.of(CommonItemTags.INGOTS_ENERGIZED_COPPER),
                'H', Ingredient.of(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "ESE",
                "AHA",
                "ESE"
        }, new ItemStack(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'A', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "CSC",
                "SAS",
                "CSC"
        }, new ItemStack(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM.get()), CraftingBookCategory.MISC);
    }
    private void buildFertilizerCraftingRecipes(Consumer<FinishedRecipe> output) {
        addShapedCraftingRecipe(output, has(Items.BONE_MEAL), Map.of(
                'B', Ingredient.of(Items.BONE_MEAL),
                'D', Ingredient.of(Items.DANDELION),
                'b', Ingredient.of(Items.BLUE_ORCHID),
                'L', Ingredient.of(Tags.Items.GEMS_LAPIS),
                'A', Ingredient.of(Items.ALLIUM),
                'P', Ingredient.of(Items.POPPY)
        ), new String[] {
                "DBb",
                "BLB",
                "ABP"
        }, new ItemStack(EPItems.BASIC_FERTILIZER.get(), 4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_FERTILIZER), Map.of(
                'B', Ingredient.of(EPItems.BASIC_FERTILIZER.get()),
                'S', Ingredient.of(Items.SUGAR_CANE),
                'K', Ingredient.of(Items.KELP),
                's', Ingredient.of(Items.SUGAR),
                'b', Ingredient.of(Items.BAMBOO),
                'W', Ingredient.of(Items.WHEAT_SEEDS)
        ), new String[] {
                "SBK",
                "BsB",
                "bBW"
        }, new ItemStack(EPItems.GOOD_FERTILIZER.get(), 4), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.GOOD_FERTILIZER), Map.of(
                'G', Ingredient.of(EPItems.GOOD_FERTILIZER.get()),
                'M', Ingredient.of(Items.RED_MUSHROOM),
                'S', Ingredient.of(Items.SWEET_BERRIES),
                'r', Ingredient.of(Tags.Items.DYES_RED),
                'T', Ingredient.of(Items.RED_TULIP),
                'R', Ingredient.of(Items.ROSE_BUSH)
        ), new String[] {
                "MGS",
                "GrG",
                "TGR"
        }, new ItemStack(EPItems.ADVANCED_FERTILIZER.get(), 4), CraftingBookCategory.MISC);
    }
    private void buildUpgradeModuleCraftingRecipes(Consumer<FinishedRecipe> output) {
        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get())
        ), new String[] {
                "CSC",
                "RBR",
                "CSC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.SPEED_UPGRADE_MODULE_1), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get()),
                's', Ingredient.of(EPItems.SPEED_UPGRADE_MODULE_1.get())
        ), new String[] {
                "CSC",
                "RBR",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.SPEED_UPGRADE_MODULE_2), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get()),
                's', Ingredient.of(EPItems.SPEED_UPGRADE_MODULE_2.get())
        ), new String[] {
                "CSC",
                "RAR",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.SPEED_UPGRADE_MODULE_3), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get()),
                's', Ingredient.of(EPItems.SPEED_UPGRADE_MODULE_3.get())
        ), new String[] {
                "CSC",
                "RAR",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.SPEED_UPGRADE_MODULE_4), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'r', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()),
                's', Ingredient.of(EPItems.SPEED_UPGRADE_MODULE_4.get())
        ), new String[] {
                "CSC",
                "rRr",
                "CsC"
        }, new ItemStack(EPItems.SPEED_UPGRADE_MODULE_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'G', Ingredient.of(Tags.Items.INGOTS_GOLD),
                'R', Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get())
        ), new String[] {
                "CGC",
                "RBR",
                "CGC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'G', Ingredient.of(Tags.Items.INGOTS_GOLD),
                'R', Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get()),
                'E', Ingredient.of(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1.get())
        ), new String[] {
                "CGC",
                "RBR",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'G', Ingredient.of(Tags.Items.INGOTS_GOLD),
                'R', Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get()),
                'E', Ingredient.of(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2.get())
        ), new String[] {
                "CGC",
                "RAR",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'G', Ingredient.of(Tags.Items.INGOTS_GOLD),
                'R', Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get()),
                'E', Ingredient.of(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3.get())
        ), new String[] {
                "CGC",
                "RAR",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'G', Ingredient.of(Tags.Items.INGOTS_GOLD),
                'r', Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()),
                'E', Ingredient.of(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4.get())
        ), new String[] {
                "CGC",
                "rRr",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'T', Ingredient.of(CommonItemTags.PLATES_TIN),
                'c', Ingredient.of(ItemTags.COALS),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get())
        ), new String[] {
                "CTC",
                "cBc",
                "CTC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'T', Ingredient.of(CommonItemTags.PLATES_TIN),
                'c', Ingredient.of(ItemTags.COALS),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get()),
                'E', Ingredient.of(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1.get())
        ), new String[] {
                "CTC",
                "cBc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'T', Ingredient.of(CommonItemTags.PLATES_TIN),
                'c', Ingredient.of(ItemTags.COALS),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get()),
                'E', Ingredient.of(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2.get())
        ), new String[] {
                "CTC",
                "cAc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'T', Ingredient.of(CommonItemTags.PLATES_TIN),
                'c', Ingredient.of(ItemTags.COALS),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get()),
                'E', Ingredient.of(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3.get())
        ), new String[] {
                "CTC",
                "cAc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'T', Ingredient.of(CommonItemTags.PLATES_TIN),
                'c', Ingredient.of(ItemTags.COALS),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()),
                'E', Ingredient.of(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4.get())
        ), new String[] {
                "CTC",
                "cRc",
                "CEC"
        }, new ItemStack(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'r', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get())
        ), new String[] {
                "GCG",
                "rRr",
                "GCG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.DURATION_UPGRADE_MODULE_1), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'r', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()),
                'D', Ingredient.of(EPItems.DURATION_UPGRADE_MODULE_1.get())
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.DURATION_UPGRADE_MODULE_2), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'r', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()),
                'D', Ingredient.of(EPItems.DURATION_UPGRADE_MODULE_2.get())
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.DURATION_UPGRADE_MODULE_3), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'r', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()),
                'D', Ingredient.of(EPItems.DURATION_UPGRADE_MODULE_3.get())
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.DURATION_UPGRADE_MODULE_4), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'r', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()),
                'D', Ingredient.of(EPItems.DURATION_UPGRADE_MODULE_4.get())
        ), new String[] {
                "GCG",
                "rRr",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.DURATION_UPGRADE_MODULE_5), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()),
                'D', Ingredient.of(EPItems.DURATION_UPGRADE_MODULE_5.get())
        ), new String[] {
                "GDG",
                "CRC",
                "GDG"
        }, new ItemStack(EPItems.DURATION_UPGRADE_MODULE_6.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ADVANCED_UPGRADE_MODULE), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get())
        ), new String[] {
                "GRG",
                "RAR",
                "GRG"
        }, new ItemStack(EPItems.RANGE_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.RANGE_UPGRADE_MODULE_1), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'r', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get()),
                'R', Ingredient.of(EPItems.RANGE_UPGRADE_MODULE_1.get())
        ), new String[] {
                "GrG",
                "rAr",
                "GRG"
        }, new ItemStack(EPItems.RANGE_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.RANGE_UPGRADE_MODULE_2), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'r', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get()),
                'R', Ingredient.of(EPItems.RANGE_UPGRADE_MODULE_2.get())
        ), new String[] {
                "GrG",
                "rAr",
                "GRG"
        }, new ItemStack(EPItems.RANGE_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get()),
                'F', Ingredient.of(EPBlocks.IRON_FLUID_PIPE_ITEM.get())
        ), new String[] {
                "IFI",
                "RBR",
                "IFI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1), Map.of(
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get()),
                'F', Ingredient.of(EPBlocks.IRON_FLUID_PIPE_ITEM.get()),
                'E', Ingredient.of(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1.get())
        ), new String[] {
                "IFI",
                "RBR",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2), Map.of(
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get()),
                'F', Ingredient.of(EPBlocks.IRON_FLUID_PIPE_ITEM.get()),
                'E', Ingredient.of(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2.get())
        ), new String[] {
                "IFI",
                "RAR",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3), Map.of(
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get()),
                'F', Ingredient.of(EPBlocks.IRON_FLUID_PIPE_ITEM.get()),
                'E', Ingredient.of(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3.get())
        ), new String[] {
                "IFI",
                "RAR",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4), Map.of(
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'r', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()),
                'F', Ingredient.of(EPBlocks.IRON_FLUID_PIPE_ITEM.get()),
                'E', Ingredient.of(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4.get())
        ), new String[] {
                "IFI",
                "rRr",
                "IEI"
        }, new ItemStack(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get()),
                'b', Ingredient.of(Items.BLAST_FURNACE)
        ), new String[] {
                "CSC",
                "RBR",
                "CbC"
        }, new ItemStack(EPItems.BLAST_FURNACE_UPGRADE_MODULE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                's', Ingredient.of(CommonItemTags.SILICON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get()),
                'S', Ingredient.of(Items.SMOKER)
        ), new String[] {
                "CsC",
                "RBR",
                "CSC"
        }, new ItemStack(EPItems.SMOKER_UPGRADE_MODULE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_UPGRADE_MODULE), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_GOLD),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'b', Ingredient.of(EPItems.BASIC_SOLAR_CELL.get()),
                'B', Ingredient.of(EPItems.BASIC_UPGRADE_MODULE.get())
        ), new String[] {
                "GSG",
                "bBb",
                "GSG"
        }, new ItemStack(EPItems.MOON_LIGHT_UPGRADE_MODULE_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.MOON_LIGHT_UPGRADE_MODULE_1), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_GOLD),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'B', Ingredient.of(EPItems.BASIC_SOLAR_CELL.get()),
                'A', Ingredient.of(EPItems.ADVANCED_UPGRADE_MODULE.get()),
                'M', Ingredient.of(EPItems.MOON_LIGHT_UPGRADE_MODULE_1.get())
        ), new String[] {
                "GSG",
                "BAB",
                "GMG"
        }, new ItemStack(EPItems.MOON_LIGHT_UPGRADE_MODULE_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.MOON_LIGHT_UPGRADE_MODULE_2), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_GOLD),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'B', Ingredient.of(EPItems.BASIC_SOLAR_CELL.get()),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE.get()),
                'M', Ingredient.of(EPItems.MOON_LIGHT_UPGRADE_MODULE_2.get())
        ), new String[] {
                "GSG",
                "BRB",
                "GMG"
        }, new ItemStack(EPItems.MOON_LIGHT_UPGRADE_MODULE_3.get()), CraftingBookCategory.MISC);
    }
    private void buildToolsCraftingRecipes(Consumer<FinishedRecipe> output) {
        addHammerCraftingRecipe(output, ItemTags.PLANKS, EPItems.WOODEN_HAMMER);
        addHammerCraftingRecipe(output, ItemTags.STONE_TOOL_MATERIALS, EPItems.STONE_HAMMER);
        addHammerCraftingRecipe(output, Tags.Items.INGOTS_IRON, EPItems.IRON_HAMMER);
        addHammerCraftingRecipe(output, Tags.Items.INGOTS_GOLD, EPItems.GOLDEN_HAMMER);
        addHammerCraftingRecipe(output, Tags.Items.GEMS_DIAMOND, EPItems.DIAMOND_HAMMER);

        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_IRON), Map.of(
                'i', Ingredient.of(Tags.Items.NUGGETS_IRON),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'S', Ingredient.of(Tags.Items.RODS_WOODEN)
        ), new String[] {
                "I I",
                " i ",
                "S S"
        }, new ItemStack(EPItems.CUTTER.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(Tags.Items.INGOTS_IRON), Map.of(
                'i', Ingredient.of(Tags.Items.NUGGETS_IRON),
                'I', Ingredient.of(Tags.Items.INGOTS_IRON)
        ), new String[] {
                " I ",
                " iI",
                "i  "
        }, new ItemStack(EPItems.WRENCH.get()), CraftingBookCategory.MISC);
    }
    private void buildEnergyItemsCraftingRecipes(Consumer<FinishedRecipe> output) {
        addShapedCraftingRecipe(output, has(Tags.Items.INGOTS_COPPER), Map.of(
                'T', Ingredient.of(CommonItemTags.NUGGETS_TIN),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'c', Ingredient.of(ItemTags.COALS)
        ), new String[] {
                "T T",
                "CRC",
                "CcC"
        }, new ItemStack(EPItems.BATTERY_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_1), Map.of(
                'T', Ingredient.of(CommonItemTags.NUGGETS_TIN),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'c', Ingredient.of(ItemTags.COALS),
                'B', Ingredient.of(EPItems.BATTERY_1.get())
        ), new String[] {
                "T T",
                "CBC",
                "IcI"
        }, new ItemStack(EPItems.BATTERY_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_2), Map.of(
                'T', Ingredient.of(CommonItemTags.NUGGETS_TIN),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'B', Ingredient.of(EPItems.BATTERY_2.get())
        ), new String[] {
                "T T",
                "BCB",
                "CSC"
        }, new ItemStack(EPItems.BATTERY_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_3), Map.of(
                'T', Ingredient.of(CommonItemTags.NUGGETS_TIN),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'B', Ingredient.of(EPItems.BATTERY_3.get())
        ), new String[] {
                "T T",
                "CBC",
                "SIS"
        }, new ItemStack(EPItems.BATTERY_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_4), Map.of(
                'T', Ingredient.of(CommonItemTags.NUGGETS_TIN),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'B', Ingredient.of(EPItems.BATTERY_4.get())
        ), new String[] {
                "T T",
                "BSB",
                "IRI"
        }, new ItemStack(EPItems.BATTERY_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_5), Map.of(
                'T', Ingredient.of(CommonItemTags.NUGGETS_TIN),
                'E', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'B', Ingredient.of(EPItems.BATTERY_5.get())
        ), new String[] {
                "T T",
                "EBE",
                "EBE"
        }, new ItemStack(EPItems.BATTERY_6.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_6), Map.of(
                'G', Ingredient.of(Tags.Items.NUGGETS_GOLD),
                'E', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'B', Ingredient.of(EPItems.BATTERY_6.get())
        ), new String[] {
                "G G",
                "EBE",
                "SBS"
        }, new ItemStack(EPItems.BATTERY_7.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_7), Map.of(
                'G', Ingredient.of(Tags.Items.NUGGETS_GOLD),
                'E', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'B', Ingredient.of(EPItems.BATTERY_7.get())
        ), new String[] {
                "G G",
                "EBE",
                "ABA"
        }, new ItemStack(EPItems.BATTERY_8.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.COAL_ENGINE_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'c', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'C', Ingredient.of(EPBlocks.COAL_ENGINE_ITEM.get())
        ), new String[] {
                "SIS",
                "RCR",
                "cIc"
        }, new ItemStack(EPItems.INVENTORY_COAL_ENGINE.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.CHARGER_ITEM), Map.of(
                'c', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'C', Ingredient.of(EPBlocks.CHARGER_ITEM.get())
        ), new String[] {
                "SIS",
                "RCR",
                "cIc"
        }, new ItemStack(EPItems.INVENTORY_CHARGER.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.TELEPORTER_ITEM), Map.of(
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'c', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'T', Ingredient.of(EPBlocks.TELEPORTER_ITEM.get())
        ), new String[] {
                "CcC",
                "RTR",
                "GcG"
        }, new ItemStack(EPItems.INVENTORY_TELEPORTER.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_3), Map.of(
                'b', Ingredient.of(EPItems.BASIC_CIRCUIT.get()),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'B', Ingredient.of(EPItems.BATTERY_3.get())
        ), new String[] {
                "S S",
                "bRb",
                "CBC"
        }, new ItemStack(EPItems.ENERGY_ANALYZER.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BATTERY_3), Map.of(
                'b', Ingredient.of(EPItems.BASIC_CIRCUIT.get()),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'L', Ingredient.of(Tags.Items.GEMS_LAPIS),
                'B', Ingredient.of(EPItems.BATTERY_3.get())
        ), new String[] {
                "S S",
                "bLb",
                "CBC"
        }, new ItemStack(EPItems.FLUID_ANALYZER.get()), CraftingBookCategory.MISC);
    }
    private void buildItemTransportCraftingRecipes(Consumer<FinishedRecipe> output) {
        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_IRON), Map.of(
                        'L', Ingredient.of(Tags.Items.LEATHER),
                        'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                        'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE)
                ), new String[] {
                        "   ",
                        "LLL",
                        "IRI"
                }, new ItemStack(EPBlocks.ITEM_CONVEYOR_BELT_ITEM.get(), 6), CraftingBookCategory.MISC,
                "item_conveyor_belt", "_from_leather");

        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_IRON), Map.of(
                        'K', Ingredient.of(Items.DRIED_KELP),
                        'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                        'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE)
                ), new String[] {
                        "   ",
                        "KKK",
                        "IRI"
                }, new ItemStack(EPBlocks.ITEM_CONVEYOR_BELT_ITEM.get(), 6), CraftingBookCategory.MISC,
                "item_conveyor_belt", "_from_dried_kelp");

        addShapedCraftingRecipe(output, has(EPBlocks.ITEM_CONVEYOR_BELT_ITEM), Map.of(
                'C', Ingredient.of(Tags.Items.COBBLESTONE_NORMAL),
                'c', Ingredient.of(EPBlocks.ITEM_CONVEYOR_BELT_ITEM.get()),
                'H', Ingredient.of(Items.HOPPER)
        ), new String[] {
                "CCC",
                "CHC",
                "CcC"
        }, new ItemStack(EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', Ingredient.of(Tags.Items.COBBLESTONE_NORMAL),
                'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                'R', Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE),
                'L', Ingredient.of(EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM.get())
        ), new String[] {
                "CRC",
                "ILI",
                "CRC"
        }, new ItemStack(EPBlocks.ITEM_CONVEYOR_BELT_SORTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', Ingredient.of(Tags.Items.COBBLESTONE_NORMAL),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'l', Ingredient.of(Items.LEVER),
                'L', Ingredient.of(EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM.get())
        ), new String[] {
                "ClC",
                "ILI",
                "CRC"
        }, new ItemStack(EPBlocks.ITEM_CONVEYOR_BELT_SWITCH_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', Ingredient.of(Tags.Items.COBBLESTONE_NORMAL),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'L', Ingredient.of(EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM.get())
        ), new String[] {
                "CIC",
                "ILI",
                "CRC"
        }, new ItemStack(EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM), Map.of(
                'C', Ingredient.of(Tags.Items.COBBLESTONE_NORMAL),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'L', Ingredient.of(EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM.get())
        ), new String[] {
                "CRC",
                "ILI",
                "CIC"
        }, new ItemStack(EPBlocks.ITEM_CONVEYOR_BELT_MERGER_ITEM.get()), CraftingBookCategory.MISC);
    }
    private void buildFluidTransportCraftingRecipes(Consumer<FinishedRecipe> output) {
        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_IRON), Map.of(
                        'I', Ingredient.of(Tags.Items.INGOTS_IRON),
                        'i', Ingredient.of(CommonItemTags.PLATES_IRON)
                ), new String[] {
                        "IiI",
                        "IiI",
                        "IiI"
                }, new ItemStack(EPBlocks.IRON_FLUID_PIPE_ITEM.get(), 12), CraftingBookCategory.MISC,
                "", "", "iron_");

        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_GOLD), Map.of(
                'G', Ingredient.of(Tags.Items.INGOTS_GOLD),
                'g', Ingredient.of(CommonItemTags.PLATES_GOLD)
        ), new String[] {
                "GgG",
                "GgG",
                "GgG"
        }, new ItemStack(EPBlocks.GOLDEN_FLUID_PIPE_ITEM.get(), 12), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.PLATES_IRON), Map.of(
                'G', Ingredient.of(Tags.Items.GLASS_PANES_COLORLESS),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "IGI",
                "IGI",
                "IGI"
        }, new ItemStack(EPBlocks.FLUID_TANK_SMALL_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.FLUID_TANK_SMALL_ITEM), Map.of(
                'F', Ingredient.of(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'S', Ingredient.of(CommonItemTags.INGOTS_STEEL)
        ), new String[] {
                "SFS",
                "SFS",
                "SFS"
        }, new ItemStack(EPBlocks.FLUID_TANK_MEDIUM_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.FLUID_TANK_MEDIUM_ITEM), Map.of(
                'F', Ingredient.of(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON)
        ), new String[] {
                "IFI",
                "IFI",
                "IFI"
        }, new ItemStack(EPBlocks.FLUID_TANK_LARGE_ITEM.get()), CraftingBookCategory.MISC);
    }
    private void buildEnergyTransportCraftingRecipes(Consumer<FinishedRecipe> output) {
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
                'I', Ingredient.of(EPItems.CABLE_INSULATOR.get()),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get())
        ), new String[] {
                "ICI",
                "ICI",
                "ICI"
        }, new ItemStack(EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'M', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "CSI",
                "RMR",
                "CSI"
        }, new ItemStack(EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'M', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "CSI",
                "SMR",
                "CRI"
        }, new ItemStack(EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'M', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "CRI",
                "SMS",
                "CRI"
        }, new ItemStack(EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                        'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                        'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                        'M', Ingredient.of(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get()),
                        'T', Ingredient.of(EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM.get())
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStack(EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM.get()), CraftingBookCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                        'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                        'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                        'M', Ingredient.of(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get()),
                        'T', Ingredient.of(EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM.get())
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStack(EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM.get()), CraftingBookCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                        'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                        'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                        'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                        'M', Ingredient.of(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get()),
                        'T', Ingredient.of(EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM.get())
                ), new String[] {
                        "CTI",
                        "SMS",
                        "CTI"
                }, new ItemStack(EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM.get()), CraftingBookCategory.MISC,
                "", "", "mv_");

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                'T', Ingredient.of(EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM.get())
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                'T', Ingredient.of(EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM.get())
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'M', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                'T', Ingredient.of(EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM.get())
        ), new String[] {
                "GTG",
                "SMS",
                "GTG"
        }, new ItemStack(EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'M', Ingredient.of(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM.get()),
                'T', Ingredient.of(EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM.get())
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'M', Ingredient.of(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM.get()),
                'T', Ingredient.of(EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM.get())
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(EPBlocks.EHV_TRANSFORMER_3_TO_3_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'M', Ingredient.of(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM.get()),
                'T', Ingredient.of(EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM.get())
        ), new String[] {
                "CTC",
                "SMS",
                "CTC"
        }, new ItemStack(EPBlocks.EHV_TRANSFORMER_N_TO_1_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.of(EPItems.BATTERY_5.get()),
                'M', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "CBC",
                "BMB",
                "SBS"
        }, new ItemStack(EPBlocks.BATTERY_BOX_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'E', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'B', Ingredient.of(EPItems.BATTERY_8.get()),
                'M', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "EBE",
                "BMB",
                "BSB"
        }, new ItemStack(EPBlocks.ADVANCED_BATTERY_BOX_ITEM.get()), CraftingBookCategory.MISC);

        addShapelessCraftingRecipe(output, has(EPBlocks.BATTERY_BOX_ITEM), List.of(
                Ingredient.of(EPBlocks.BATTERY_BOX_ITEM.get()),
                Ingredient.of(Items.MINECART)
        ), new ItemStack(EPItems.BATTERY_BOX_MINECART.get()), CraftingBookCategory.MISC);

        addShapelessCraftingRecipe(output, has(EPBlocks.ADVANCED_BATTERY_BOX_ITEM), List.of(
                Ingredient.of(EPBlocks.ADVANCED_BATTERY_BOX_ITEM.get()),
                Ingredient.of(Items.MINECART)
        ), new ItemStack(EPItems.ADVANCED_BATTERY_BOX_MINECART.get()), CraftingBookCategory.MISC);
    }
    private void buildMachineCraftingRecipes(Consumer<FinishedRecipe> output) {
        addShapedCraftingRecipe(output, has(Items.SMOOTH_STONE), Map.of(
                'S', Ingredient.of(Items.SMOOTH_STONE),
                'B', Ingredient.of(Items.BRICKS),
                's', Ingredient.of(ItemTags.SHOVELS)
        ), new String[] {
                "BBB",
                "BsB",
                "SSS"
        }, new ItemStack(EPBlocks.PRESS_MOLD_MAKER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(Items.FURNACE), Map.of(
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'B', Ingredient.of(Items.BRICKS),
                'F', Ingredient.of(Items.FURNACE)
        ), new String[] {
                "III",
                "FIF",
                "BBB"
        }, new ItemStack(EPBlocks.ALLOY_FURNACE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'b', Ingredient.of(EPItems.BASIC_CIRCUIT.get()),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                'T', Ingredient.of(Items.CRAFTING_TABLE)
        ), new String[] {
                "CTC",
                "bBb",
                "ITI"
        }, new ItemStack(EPBlocks.AUTO_CRAFTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME.get()), Map.of(
                'c', Ingredient.of(EPItems.ADVANCED_CIRCUIT.get()),
                'P', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                'a', Ingredient.of(EPBlocks.AUTO_CRAFTER_ITEM.get())
        ), new String[] {
                "GaG",
                "cAc",
                "PaP"
        }, new ItemStack(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', Ingredient.of(CommonItemTags.SILICON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                'S', Ingredient.of(Items.STONECUTTER)
        ), new String[] {
                "CsC",
                "SBS",
                "CIC"
        }, new ItemStack(EPBlocks.CRUSHER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'c', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'F', Ingredient.of(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'A', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                'C', Ingredient.of(EPBlocks.CRUSHER_ITEM.get())
        ), new String[] {
                "aCa",
                "FAF",
                "cCc"
        }, new ItemStack(EPBlocks.ADVANCED_CRUSHER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                'S', Ingredient.of(Items.STONECUTTER)
        ), new String[] {
                "ISI",
                "SBS",
                "ISI"
        }, new ItemStack(EPBlocks.PULVERIZER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'C', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'F', Ingredient.of(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'A', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                'P', Ingredient.of(EPBlocks.PULVERIZER_ITEM.get())
        ), new String[] {
                "aPa",
                "FAF",
                "CPC"
        }, new ItemStack(EPBlocks.ADVANCED_PULVERIZER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(EPItems.SAW_BLADE.get()),
                's', Ingredient.of(EPItems.SILICON.get()),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "sSs",
                "CBC",
                "sIs"
        }, new ItemStack(EPBlocks.SAWMILL_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.of(Items.PISTON),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "IPI",
                "PBP",
                "IPI"
        }, new ItemStack(EPBlocks.COMPRESSOR_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.of(Items.PISTON),
                'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                'S', Ingredient.of(CommonItemTags.INGOTS_STEEL),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "SPS",
                "IBI",
                "SIS"
        }, new ItemStack(EPBlocks.METAL_PRESS_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'I', Ingredient.of(CommonItemTags.GEARS_IRON),
                'i', Ingredient.of(CommonItemTags.RODS_IRON),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                'P', Ingredient.of(EPBlocks.PRESS_MOLD_MAKER_ITEM.get())
        ), new String[] {
                "IPI",
                "iBi",
                "IPI"
        }, new ItemStack(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', Ingredient.of(CommonItemTags.SILICON),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                'S', Ingredient.of(Items.STONECUTTER)
        ), new String[] {
                "CSC",
                "sBs",
                "ISI"
        }, new ItemStack(EPBlocks.AUTO_STONECUTTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'G', Ingredient.of(Tags.Items.GLASS_PANES_COLORLESS),
                'D', Ingredient.of(Items.DIRT),
                'W', Ingredient.of(Items.WATER_BUCKET),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "SWS",
                "GBG",
                "IDI"
        }, new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                'D', Ingredient.of(Items.DISPENSER)
        ), new String[] {
                "IDS",
                "DBD",
                "SDC"
        }, new ItemStack(EPBlocks.BLOCK_PLACER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                's', Ingredient.of(CommonItemTags.INGOTS_STEEL),
                'i', Ingredient.of(CommonItemTags.GEARS_IRON),
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'B', Ingredient.of(EPItems.BASIC_CIRCUIT.get()),
                'H', Ingredient.of(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "iRi",
                "BHB",
                "SsS"
        }, new ItemStack(EPBlocks.ASSEMBLING_MACHINE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'B', Ingredient.of(EPItems.BASIC_CIRCUIT.get()),
                'S', Ingredient.of(CommonItemTags.INGOTS_STEEL),
                'H', Ingredient.of(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get()),
                'A', Ingredient.of(EPBlocks.ALLOY_FURNACE_ITEM.get())
        ), new String[] {
                "SAS",
                "BHB",
                "SAS"
        }, new ItemStack(EPBlocks.INDUCTION_SMELTER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'F', Ingredient.of(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "SIS",
                "IBI",
                "CFC"
        }, new ItemStack(EPBlocks.FLUID_FILLER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'F', Ingredient.of(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'i', Ingredient.of(CommonItemTags.GEARS_IRON),
                'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                'H', Ingredient.of(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "iSi",
                "FHF",
                "CIC"
        }, new ItemStack(EPBlocks.STONE_SOLIDIFIER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'i', Ingredient.of(CommonItemTags.PLATES_IRON),
                'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'D', Ingredient.of(EPBlocks.FLUID_DRAINER_ITEM.get()),
                'F', Ingredient.of(EPBlocks.FLUID_FILLER_ITEM.get()),
                'f', Ingredient.of(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'H', Ingredient.of(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "CiC",
                "DHF",
                "IfI"
        }, new ItemStack(EPBlocks.FLUID_TRANSPOSER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'i', Ingredient.of(CommonItemTags.GEARS_IRON),
                'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                'F', Ingredient.of(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'B', Ingredient.of(Items.IRON_BARS),
                'f', Ingredient.of(EPBlocks.IRON_FLUID_PIPE_ITEM.get()),
                'H', Ingredient.of(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "iBi",
                "FHF",
                "IfI"
        }, new ItemStack(EPBlocks.FILTRATION_PLANT_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'F', Ingredient.of(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "CFC",
                "IBI",
                "SIS"
        }, new ItemStack(EPBlocks.FLUID_DRAINER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'F', Ingredient.of(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                'P', Ingredient.of(Items.PISTON),
                'p', Ingredient.of(EPBlocks.IRON_FLUID_PIPE_ITEM.get())
        ), new String[] {
                "RPR",
                "FBF",
                "IpI"
        }, new ItemStack(EPBlocks.FLUID_PUMP_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(Tags.Items.STORAGE_BLOCKS_IRON), Map.of(
                'i', Ingredient.of(CommonItemTags.PLATES_IRON),
                'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                'B', Ingredient.of(Items.IRON_BARS),
                'G', Ingredient.of(Tags.Items.GLASS_COLORLESS)
        ), new String[] {
                "IBI",
                "iGi",
                "IiI"
        }, new ItemStack(EPBlocks.DRAIN_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', Ingredient.of(CommonItemTags.SILICON),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON)
        ), new String[] {
                "sCs",
                "IBI",
                "CSC"
        }, new ItemStack(EPBlocks.CHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'a', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                'C', Ingredient.of(EPBlocks.CHARGER_ITEM.get())
        ), new String[] {
                "SGS",
                "aAa",
                "GCG"
        }, new ItemStack(EPBlocks.ADVANCED_CHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                's', Ingredient.of(CommonItemTags.SILICON),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON)
        ), new String[] {
                "CSC",
                "IBI",
                "sCs"
        }, new ItemStack(EPBlocks.UNCHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'a', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                'U', Ingredient.of(EPBlocks.UNCHARGER_ITEM.get())
        ), new String[] {
                "GUG",
                "aAa",
                "SGS"
        }, new ItemStack(EPBlocks.ADVANCED_UNCHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.CHARGER_ITEM), Map.of(
                'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                'c', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                'i', Ingredient.of(CommonItemTags.PLATES_IRON),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'H', Ingredient.of(EPBlocks.CHARGER_ITEM.get())
        ), new String[] {
                "cCc",
                "SHS",
                "iIi"
        }, new ItemStack(EPBlocks.MINECART_CHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_CHARGER_ITEM), Map.of(
                'G', Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD),
                'g', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                'c', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'H', Ingredient.of(EPBlocks.ADVANCED_CHARGER_ITEM.get())
        ), new String[] {
                "gGg",
                "SHS",
                "cCc"
        }, new ItemStack(EPBlocks.ADVANCED_MINECART_CHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.UNCHARGER_ITEM), Map.of(
                'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                'c', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                'i', Ingredient.of(CommonItemTags.PLATES_IRON),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'U', Ingredient.of(EPBlocks.UNCHARGER_ITEM.get())
        ), new String[] {
                "iIi",
                "SUS",
                "cCc"
        }, new ItemStack(EPBlocks.MINECART_UNCHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_UNCHARGER_ITEM), Map.of(
                'G', Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD),
                'g', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                'c', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'S', Ingredient.of(CommonItemTags.SILICON),
                'U', Ingredient.of(EPBlocks.ADVANCED_UNCHARGER_ITEM.get())
        ), new String[] {
                "cCc",
                "SUS",
                "gGg"
        }, new ItemStack(EPBlocks.ADVANCED_MINECART_UNCHARGER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.BASIC_SOLAR_CELL), Map.of(
                'G', Ingredient.of(Tags.Items.GLASS_PANES_COLORLESS),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'I', Ingredient.of(Tags.Items.INGOTS_IRON),
                'C', Ingredient.of(Tags.Items.INGOTS_COPPER),
                'B', Ingredient.of(EPItems.BASIC_SOLAR_CELL.get())
        ), new String[] {
                "GGG",
                "BRB",
                "ICI"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_1.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.SOLAR_PANEL_ITEM_1), Map.of(
                'G', Ingredient.of(Tags.Items.GLASS_PANES_COLORLESS),
                'C', Ingredient.of(Tags.Items.INGOTS_COPPER),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'S', Ingredient.of(EPBlocks.SOLAR_PANEL_ITEM_1.get()),
                'B', Ingredient.of(EPItems.BASIC_SOLAR_CELL.get())
        ), new String[] {
                "GGG",
                "SBS",
                "CRC"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_2.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.SOLAR_PANEL_ITEM_2), Map.of(
                'G', Ingredient.of(Tags.Items.GLASS_PANES_COLORLESS),
                'R', Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                's', Ingredient.of(CommonItemTags.SILICON),
                'S', Ingredient.of(EPBlocks.SOLAR_PANEL_ITEM_2.get()),
                'B', Ingredient.of(EPItems.BASIC_SOLAR_CELL.get())
        ), new String[] {
                "GGG",
                "SBS",
                "sRs"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_3.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.SOLAR_PANEL_ITEM_3), Map.of(
                'G', Ingredient.of(Tags.Items.GLASS_PANES_COLORLESS),
                's', Ingredient.of(CommonItemTags.SILICON),
                'R', Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY),
                'S', Ingredient.of(EPBlocks.SOLAR_PANEL_ITEM_3.get()),
                'A', Ingredient.of(EPItems.ADVANCED_SOLAR_CELL.get())
        ), new String[] {
                "GGG",
                "SsS",
                "ARA"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_4.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.SOLAR_PANEL_ITEM_4), Map.of(
                'G', Ingredient.of(Tags.Items.GLASS_PANES_COLORLESS),
                'a', Ingredient.of(Tags.Items.GEMS_AMETHYST),
                'E', Ingredient.of(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                'S', Ingredient.of(EPBlocks.SOLAR_PANEL_ITEM_4.get()),
                'A', Ingredient.of(EPItems.ADVANCED_SOLAR_CELL.get())
        ), new String[] {
                "GGG",
                "SaS",
                "AEA"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_5.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.SOLAR_PANEL_ITEM_5), Map.of(
                'G', Ingredient.of(Tags.Items.GLASS_PANES_COLORLESS),
                'A', Ingredient.of(Tags.Items.GEMS_AMETHYST),
                'E', Ingredient.of(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                'S', Ingredient.of(EPBlocks.SOLAR_PANEL_ITEM_5.get()),
                'R', Ingredient.of(EPItems.REINFORCED_ADVANCED_SOLAR_CELL.get())
        ), new String[] {
                "GGG",
                "SAS",
                "RER"
        }, new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_6.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                'F', Ingredient.of(Items.FURNACE)
        ), new String[] {
                "ISI",
                "CBC",
                "IFI"
        }, new ItemStack(EPBlocks.COAL_ENGINE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(Items.REDSTONE_LAMP), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'C', Ingredient.of(Tags.Items.INGOTS_COPPER),
                'R', Ingredient.of(Items.REDSTONE_LAMP)
        ), new String[] {
                "CSC",
                "SRS",
                "CSC"
        }, new ItemStack(EPBlocks.POWERED_LAMP_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.BASIC_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON),
                'C', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'B', Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                'F', Ingredient.of(Items.FURNACE)
        ), new String[] {
                "CFC",
                "SBS",
                "IFI"
        }, new ItemStack(EPBlocks.POWERED_FURNACE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'a', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'G', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                'P', Ingredient.of(EPBlocks.POWERED_FURNACE_ITEM.get())
        ), new String[] {
                "GPG",
                "SAS",
                "aPa"
        }, new ItemStack(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'G', Ingredient.of(CommonItemTags.PLATES_GOLD),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'E', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                'A', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "RaR",
                "GAG",
                "ECE"
        }, new ItemStack(EPBlocks.LIGHTNING_GENERATOR_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'G', Ingredient.of(CommonItemTags.PLATES_GOLD),
                'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
                'E', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'a', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "RaR",
                "GAG",
                "ESE"
        }, new ItemStack(EPBlocks.ENERGIZER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'I', Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON),
                'R', Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE),
                'E', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                'a', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "aRa",
                "IAI",
                "ECE"
        }, new ItemStack(EPBlocks.CHARGING_STATION_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.COAL_ENGINE_ITEM), Map.of(
                's', Ingredient.of(CommonItemTags.SILICON),
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'c', Ingredient.of(CommonItemTags.WIRES_COPPER),
                'C', Ingredient.of(EPBlocks.COAL_ENGINE_ITEM.get())
        ), new String[] {
                "cSc",
                "sCs",
                "cSc"
        }, new ItemStack(EPBlocks.HEAT_GENERATOR_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.HARDENED_MACHINE_FRAME_ITEM), Map.of(
                'S', Ingredient.of(CommonItemTags.SILICON),
                'F', Ingredient.of(EPBlocks.FLUID_TANK_SMALL_ITEM.get()),
                'c', Ingredient.of(CommonItemTags.PLATES_COPPER),
                'C', Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
                'H', Ingredient.of(EPBlocks.HARDENED_MACHINE_FRAME_ITEM.get()),
                'E', Ingredient.of(EPBlocks.COAL_ENGINE_ITEM.get())
        ), new String[] {
                "cHc",
                "SES",
                "CFC"
        }, new ItemStack(EPBlocks.THERMAL_GENERATOR_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'a', Ingredient.of(Items.AMETHYST_BLOCK),
                'E', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER),
                'P', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'A', Ingredient.of(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "aPa",
                "EAE",
                "aPa"
        }, new ItemStack(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.of(EPItems.PROCESSING_UNIT.get()),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'a', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'E', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'A', Ingredient.of(Tags.Items.GEMS_AMETHYST),
                'R', Ingredient.of(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "CAC",
                "PRP",
                "aEa"
        }, new ItemStack(EPBlocks.WEATHER_CONTROLLER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'P', Ingredient.of(EPItems.PROCESSING_UNIT.get()),
                'c', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'C', Ingredient.of(Items.CLOCK),
                'A', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'E', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'R', Ingredient.of(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "cCc",
                "PRP",
                "AEA"
        }, new ItemStack(EPBlocks.TIME_CONTROLLER_ITEM.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM), Map.of(
                'T', Ingredient.of(EPItems.TELEPORTER_PROCESSING_UNIT.get()),
                'C', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'A', Ingredient.of(CommonItemTags.PLATES_ADVANCED_ALLOY),
                'E', Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD),
                'S', Ingredient.of(CommonItemTags.STORAGE_BLOCKS_SILICON),
                'R', Ingredient.of(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM.get())
        ), new String[] {
                "CEC",
                "TRT",
                "ASA"
        }, new ItemStack(EPBlocks.TELEPORTER_ITEM.get()), CraftingBookCategory.MISC);
    }
    private void buildMiscCraftingRecipes(Consumer<FinishedRecipe> output) {
        addShapelessCraftingRecipe(output, InventoryChangeTrigger.TriggerInstance.hasItems(
                Items.BOOK,
                EPBlocks.BASIC_MACHINE_FRAME_ITEM.get()
        ), List.of(
                Ingredient.of(Items.BOOK),
                Ingredient.of(EPBlocks.BASIC_MACHINE_FRAME_ITEM.get())
        ), new ItemStack(EPItems.ENERGIZED_POWER_BOOK.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(CommonItemTags.DUSTS_CHARCOAL), Map.of(
                'P', Ingredient.of(Items.PAPER),
                'C', Ingredient.of(CommonItemTags.DUSTS_CHARCOAL),
                'I', Ingredient.of(CommonItemTags.PLATES_IRON)
        ), new String[] {
                "PCP",
                "CIC",
                "PCP"
        }, new ItemStack(EPItems.CHARCOAL_FILTER.get()), CraftingBookCategory.MISC);

        addShapedCraftingRecipe(output, has(EPItems.ENERGIZED_CRYSTAL_MATRIX), Map.of(
                'A', Ingredient.of(Tags.Items.GEMS_AMETHYST),
                'E', Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()),
                'e', Ingredient.of(Tags.Items.ENDER_PEARLS)
        ), new String[] {
                "AEA",
                "EeE",
                "AEA"
        }, new ItemStack(EPItems.TELEPORTER_MATRIX.get()), CraftingBookCategory.MISC);
    }
    private void buildCustomCraftingRecipes(Consumer<FinishedRecipe> output) {
        addCustomCraftingRecipe(output, EPRecipes.TELEPORTER_MATRIX_SETTINGS_COPY_SERIALIZER.get(), CraftingBookCategory.MISC,
                "teleporter_matrix_settings_copy");
    }

    private void buildCookingRecipes(Consumer<FinishedRecipe> output) {
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

    private void buildSmithingRecipes(Consumer<FinishedRecipe> output) {
        addNetheriteSmithingUpgradeRecipe(output, Ingredient.of(EPItems.DIAMOND_HAMMER.get()),
                new ItemStack(EPItems.NETHERITE_HAMMER.get()));
    }

    private void buildPressMoldMakerRecipes(Consumer<FinishedRecipe> output) {
        addPressMoldMakerRecipe(output, 4, new ItemStack(EPItems.RAW_GEAR_PRESS_MOLD.get()));
        addPressMoldMakerRecipe(output, 9, new ItemStack(EPItems.RAW_ROD_PRESS_MOLD.get()));
        addPressMoldMakerRecipe(output, 6, new ItemStack(EPItems.RAW_WIRE_PRESS_MOLD.get()));
    }

    private void buildAlloyFurnaceRecipes(Consumer<FinishedRecipe> output) {
        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(Tags.Items.INGOTS_IRON)),
                new IngredientWithCount(Ingredient.of(ItemTags.COALS), 3)
        }, new ItemStack(EPItems.STEEL_INGOT.get()), 500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_TIN)),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON)),
                new IngredientWithCount(Ingredient.of(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(EPItems.REDSTONE_ALLOY_INGOT.get()), 2500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_STEEL), 3),
                new IngredientWithCount(Ingredient.of(Tags.Items.INGOTS_COPPER), 3),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_TIN), 3)
        }, new ItemStack(EPItems.ADVANCED_ALLOY_INGOT.get()), 10000);
    }

    private void buildCompressorRecipes(Consumer<FinishedRecipe> output) {
        addCompressorRecipe(output, Ingredient.of(EPItems.STONE_PEBBLE.get()), new ItemStack(Items.COBBLESTONE),
                16, "stone_pebbles");

        addPlateCompressorRecipes(output, Ingredient.of(CommonItemTags.INGOTS_TIN),
                Ingredient.of(CommonItemTags.STORAGE_BLOCKS_TIN), new ItemStack(EPItems.TIN_PLATE.get()),
                "tin");
        addPlateCompressorRecipes(output, Ingredient.of(Tags.Items.INGOTS_COPPER),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER), new ItemStack(EPItems.COPPER_PLATE.get()),
                "copper");
        addPlateCompressorRecipes(output, Ingredient.of(Tags.Items.INGOTS_IRON),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON), new ItemStack(EPItems.IRON_PLATE.get()),
                "iron");
        addPlateCompressorRecipes(output, Ingredient.of(Tags.Items.INGOTS_GOLD),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD), new ItemStack(EPItems.GOLD_PLATE.get()),
                "gold");

        addPlateCompressorIngotRecipe(output, Ingredient.of(CommonItemTags.INGOTS_ADVANCED_ALLOY),
                new ItemStack(EPItems.ADVANCED_ALLOY_PLATE.get()), "advanced_alloy");
        addPlateCompressorIngotRecipe(output, Ingredient.of(CommonItemTags.INGOTS_ENERGIZED_COPPER),
                new ItemStack(EPItems.ENERGIZED_COPPER_PLATE.get()), "energized_copper");
        addPlateCompressorIngotRecipe(output, Ingredient.of(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                new ItemStack(EPItems.ENERGIZED_GOLD_PLATE.get()), "energized_gold");
    }

    private void buildCrusherRecipes(Consumer<FinishedRecipe> output) {
        addCrusherRecipe(output, Ingredient.of(Items.STONE), new ItemStack(Items.COBBLESTONE),
                "stone");
        addCrusherRecipe(output, Ingredient.of(Items.STONE_BRICKS, Items.CHISELED_STONE_BRICKS, Items.CRACKED_STONE_BRICKS,
                        Items.SMOOTH_STONE), new ItemStack(Items.COBBLESTONE),
                "stone_variants");

        addCrusherRecipe(output, Ingredient.of(Items.MOSSY_STONE_BRICKS), new ItemStack(Items.MOSSY_COBBLESTONE),
                "mossy_stone_bricks");

        addCrusherRecipe(output, Ingredient.of(Items.DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate");
        addCrusherRecipe(output, Ingredient.of(Items.DEEPSLATE_BRICKS, Items.CHISELED_DEEPSLATE, Items.CRACKED_DEEPSLATE_BRICKS,
                        Items.DEEPSLATE_TILES, Items.CRACKED_DEEPSLATE_TILES, Items.POLISHED_DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate_variants");

        addCrusherRecipe(output, Ingredient.of(Items.POLISHED_GRANITE), new ItemStack(Items.GRANITE),
                "polished_granite");
        addCrusherRecipe(output, Ingredient.of(Items.POLISHED_DIORITE), new ItemStack(Items.DIORITE),
                "polished_diorite");
        addCrusherRecipe(output, Ingredient.of(Items.POLISHED_ANDESITE), new ItemStack(Items.ANDESITE),
                "polished_andesite");

        addCrusherRecipe(output, Ingredient.of(Tags.Items.COBBLESTONE_NORMAL), new ItemStack(Items.GRAVEL),
                "cobblestone");

        addCrusherRecipe(output, Ingredient.of(Tags.Items.GRAVEL), new ItemStack(Items.SAND),
                "gravel");

        addCrusherRecipe(output, Ingredient.of(Items.SANDSTONE), new ItemStack(Items.SAND),
                "sandstone");
        addCrusherRecipe(output, Ingredient.of(Items.SMOOTH_SANDSTONE, Items.CUT_SANDSTONE,
                        Items.CHISELED_SANDSTONE), new ItemStack(Items.SAND),
                "sandstone_variants");

        addCrusherRecipe(output, Ingredient.of(Items.RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone");
        addCrusherRecipe(output, Ingredient.of(Items.SMOOTH_RED_SANDSTONE, Items.CUT_RED_SANDSTONE,
                        Items.CHISELED_RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone_variants");

        addCrusherRecipe(output, Ingredient.of(Items.POLISHED_BLACKSTONE, Items.POLISHED_BLACKSTONE_BRICKS,
                        Items.CHISELED_POLISHED_BLACKSTONE, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                        Items.GILDED_BLACKSTONE), new ItemStack(Items.BLACKSTONE),
                "blackstone_variants");

        addCrusherRecipe(output, Ingredient.of(Items.SMOOTH_BASALT, Items.POLISHED_BASALT), new ItemStack(Items.BASALT),
                "basalt_variants");
    }

    private void buildPulverizerRecipes(Consumer<FinishedRecipe> output) {
        addBasicMetalPulverizerRecipes(output,
                Ingredient.of(CommonItemTags.ORES_TIN), Ingredient.of(CommonItemTags.RAW_MATERIALS_TIN),
                Ingredient.of(CommonItemTags.STORAGE_BLOCKS_RAW_TIN), Ingredient.of(CommonItemTags.INGOTS_TIN),
                new ItemStack(EPItems.TIN_DUST.get()), "tin");
        addBasicMetalPulverizerRecipes(output,
                Ingredient.of(Tags.Items.ORES_IRON), Ingredient.of(Tags.Items.RAW_MATERIALS_IRON),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_RAW_IRON), Ingredient.of(Tags.Items.INGOTS_IRON),
                new ItemStack(EPItems.IRON_DUST.get()), "iron");
        addBasicMetalPulverizerRecipes(output,
                Ingredient.of(Tags.Items.ORES_GOLD), Ingredient.of(Tags.Items.RAW_MATERIALS_GOLD),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_RAW_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD),
                new ItemStack(EPItems.GOLD_DUST.get()), "gold");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_COPPER),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(EPItems.COPPER_DUST.get()), new double[] {
                        1., 1., 1., 1., .5, .5
                }, new double[] {
                        1., 1., 1., 1., .75, .5, .25
                }),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(EPItems.GOLD_DUST.get()),
                        .1, .2), "copper_ores");
        addRawMetalAndIngotPulverizerRecipes(output,
                Ingredient.of(Tags.Items.RAW_MATERIALS_COPPER),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_RAW_COPPER), Ingredient.of(Tags.Items.INGOTS_COPPER),
                new ItemStack(EPItems.COPPER_DUST.get()), "copper");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_COAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.COAL), new double[] {
                        1., 1., .25
                }, new double[] {
                        1., 1., .5, .25
                }), "coal_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_REDSTONE),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.REDSTONE), new double[] {
                        1., 1., 1., 1., 1., .67, .33, .33, .17
                }, new double[] {
                        1., 1., 1., 1., 1., .67, .67, .33, .33, .17
                }), "redstone_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_LAPIS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.LAPIS_LAZULI), new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .5, .5, .25, .125
                }, new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .75, .5, .5, .25, .125
                }), "lapis_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_EMERALD),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.EMERALD), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "emerald_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_DIAMOND),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.DIAMOND), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "diamond_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_QUARTZ),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.QUARTZ), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "nether_quartz_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_NETHERITE_SCRAP),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.NETHERITE_SCRAP), new double[] {
                        1., .125, .125
                }, new double[] {
                        1., .25, .25, .125
                }), "ancient_debris");

        addPulverizerRecipe(output, Ingredient.of(Items.CHARCOAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(EPItems.CHARCOAL_DUST.get()),
                        1., 1.), "charcoal");
    }

    private void buildSawmillRecipes(Consumer<FinishedRecipe> output) {
        addBasicWoodSawmillRecipe(output, new ItemStack(Items.OAK_PLANKS),
                Ingredient.of(ItemTags.OAK_LOGS), Ingredient.of(Items.OAK_FENCE),
                Ingredient.of(Items.OAK_FENCE_GATE), Ingredient.of(Items.OAK_DOOR),
                Ingredient.of(Items.OAK_TRAPDOOR), Ingredient.of(Items.OAK_PRESSURE_PLATE),
                Ingredient.of(Items.OAK_SIGN), Ingredient.of(Items.OAK_BOAT), Ingredient.of(Items.OAK_CHEST_BOAT),
                false, "oak");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.SPRUCE_PLANKS),
                Ingredient.of(ItemTags.SPRUCE_LOGS), Ingredient.of(Items.SPRUCE_FENCE),
                Ingredient.of(Items.SPRUCE_FENCE_GATE), Ingredient.of(Items.SPRUCE_DOOR),
                Ingredient.of(Items.SPRUCE_TRAPDOOR), Ingredient.of(Items.SPRUCE_PRESSURE_PLATE),
                Ingredient.of(Items.SPRUCE_SIGN), Ingredient.of(Items.SPRUCE_BOAT), Ingredient.of(Items.SPRUCE_CHEST_BOAT),
                false, "spruce");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.BIRCH_PLANKS),
                Ingredient.of(ItemTags.BIRCH_LOGS), Ingredient.of(Items.BIRCH_FENCE),
                Ingredient.of(Items.BIRCH_FENCE_GATE), Ingredient.of(Items.BIRCH_DOOR),
                Ingredient.of(Items.BIRCH_TRAPDOOR), Ingredient.of(Items.BIRCH_PRESSURE_PLATE),
                Ingredient.of(Items.BIRCH_SIGN), Ingredient.of(Items.BIRCH_BOAT), Ingredient.of(Items.BIRCH_CHEST_BOAT),
                false, "birch");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.JUNGLE_PLANKS),
                Ingredient.of(ItemTags.JUNGLE_LOGS), Ingredient.of(Items.JUNGLE_FENCE),
                Ingredient.of(Items.JUNGLE_FENCE_GATE), Ingredient.of(Items.JUNGLE_DOOR),
                Ingredient.of(Items.JUNGLE_TRAPDOOR), Ingredient.of(Items.JUNGLE_PRESSURE_PLATE),
                Ingredient.of(Items.JUNGLE_SIGN), Ingredient.of(Items.JUNGLE_BOAT), Ingredient.of(Items.JUNGLE_CHEST_BOAT),
                false, "jungle");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.ACACIA_PLANKS),
                Ingredient.of(ItemTags.ACACIA_LOGS), Ingredient.of(Items.ACACIA_FENCE),
                Ingredient.of(Items.ACACIA_FENCE_GATE), Ingredient.of(Items.ACACIA_DOOR),
                Ingredient.of(Items.ACACIA_TRAPDOOR), Ingredient.of(Items.ACACIA_PRESSURE_PLATE),
                Ingredient.of(Items.ACACIA_SIGN), Ingredient.of(Items.ACACIA_BOAT), Ingredient.of(Items.ACACIA_CHEST_BOAT),
                false, "acacia");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.DARK_OAK_PLANKS),
                Ingredient.of(ItemTags.DARK_OAK_LOGS), Ingredient.of(Items.DARK_OAK_FENCE),
                Ingredient.of(Items.DARK_OAK_FENCE_GATE), Ingredient.of(Items.DARK_OAK_DOOR),
                Ingredient.of(Items.DARK_OAK_TRAPDOOR), Ingredient.of(Items.DARK_OAK_PRESSURE_PLATE),
                Ingredient.of(Items.DARK_OAK_SIGN), Ingredient.of(Items.DARK_OAK_BOAT), Ingredient.of(Items.DARK_OAK_CHEST_BOAT),
                false, "dark_oak");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.MANGROVE_PLANKS),
                Ingredient.of(ItemTags.MANGROVE_LOGS), Ingredient.of(Items.MANGROVE_FENCE),
                Ingredient.of(Items.MANGROVE_FENCE_GATE), Ingredient.of(Items.MANGROVE_DOOR),
                Ingredient.of(Items.MANGROVE_TRAPDOOR), Ingredient.of(Items.MANGROVE_PRESSURE_PLATE),
                Ingredient.of(Items.MANGROVE_SIGN), Ingredient.of(Items.MANGROVE_BOAT), Ingredient.of(Items.MANGROVE_CHEST_BOAT),
                false, "mangrove");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.CHERRY_PLANKS),
                Ingredient.of(ItemTags.CHERRY_LOGS), Ingredient.of(Items.CHERRY_FENCE),
                Ingredient.of(Items.CHERRY_FENCE_GATE), Ingredient.of(Items.CHERRY_DOOR),
                Ingredient.of(Items.CHERRY_TRAPDOOR), Ingredient.of(Items.CHERRY_PRESSURE_PLATE),
                Ingredient.of(Items.CHERRY_SIGN), Ingredient.of(Items.CHERRY_BOAT), Ingredient.of(Items.CHERRY_CHEST_BOAT),
                false, "cherry");

        addSawmillRecipe(output, Ingredient.of(ItemTags.BAMBOO_BLOCKS), new ItemStack(Items.BAMBOO_PLANKS, 3),
                1, "bamboo_planks", "bamboo_blocks");
        addBasicWoodWithoutLogsSawmillRecipe(output, new ItemStack(Items.BAMBOO_PLANKS),
                Ingredient.of(Items.BAMBOO_FENCE), Ingredient.of(Items.BAMBOO_FENCE_GATE), Ingredient.of(Items.BAMBOO_DOOR),
                Ingredient.of(Items.BAMBOO_TRAPDOOR), Ingredient.of(Items.BAMBOO_PRESSURE_PLATE),
                Ingredient.of(Items.BAMBOO_SIGN), Ingredient.of(Items.BAMBOO_RAFT), Ingredient.of(Items.BAMBOO_CHEST_RAFT),
                true, "bamboo");

        addSawmillRecipe(output, Ingredient.of(ItemTags.CRIMSON_STEMS), new ItemStack(Items.CRIMSON_PLANKS, 6),
                1, "crimson_planks", "crimson_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(output, new ItemStack(Items.CRIMSON_PLANKS),
                Ingredient.of(Items.CRIMSON_FENCE), Ingredient.of(Items.CRIMSON_FENCE_GATE), Ingredient.of(Items.CRIMSON_DOOR),
                Ingredient.of(Items.CRIMSON_TRAPDOOR), Ingredient.of(Items.CRIMSON_PRESSURE_PLATE),
                Ingredient.of(Items.CRIMSON_SIGN), "crimson");

        addSawmillRecipe(output, Ingredient.of(ItemTags.WARPED_STEMS), new ItemStack(Items.WARPED_PLANKS, 6),
                1, "warped_planks", "warped_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(output, new ItemStack(Items.WARPED_PLANKS),
                Ingredient.of(Items.WARPED_FENCE), Ingredient.of(Items.WARPED_FENCE_GATE), Ingredient.of(Items.WARPED_DOOR),
                Ingredient.of(Items.WARPED_TRAPDOOR), Ingredient.of(Items.WARPED_PRESSURE_PLATE),
                Ingredient.of(Items.WARPED_SIGN), "warped");

        addSawmillRecipe(output, Ingredient.of(Items.CRAFTING_TABLE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "crafting_table");
        addSawmillRecipe(output, Ingredient.of(Items.CARTOGRAPHY_TABLE), new ItemStack(Items.OAK_PLANKS, 4),
                new ItemStack(Items.PAPER, 2), "oak_planks", "cartography_table");
        addSawmillRecipe(output, Ingredient.of(Items.FLETCHING_TABLE), new ItemStack(Items.OAK_PLANKS, 4),
                new ItemStack(Items.FLINT, 2), "oak_planks", "fletching_table");
        addSawmillRecipe(output, Ingredient.of(Items.LOOM), new ItemStack(Items.OAK_PLANKS, 2),
                new ItemStack(Items.STRING, 2), "oak_planks", "loom");
        addSawmillRecipe(output, Ingredient.of(Items.COMPOSTER), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "composter");
        addSawmillRecipe(output, Ingredient.of(Items.NOTE_BLOCK), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.REDSTONE), "oak_planks", "note_block");
        addSawmillRecipe(output, Ingredient.of(Items.JUKEBOX), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.DIAMOND), "oak_planks", "jukebox");

        addSawmillRecipe(output, Ingredient.of(Items.BOOKSHELF), new ItemStack(Items.OAK_PLANKS, 6),
                new ItemStack(Items.BOOK, 3), "oak_planks", "bookshelf");
        addSawmillRecipe(output, Ingredient.of(Items.CHISELED_BOOKSHELF), new ItemStack(Items.OAK_PLANKS, 6),
                5, "oak_planks", "chiseled_bookshelf");
        addSawmillRecipe(output, Ingredient.of(Items.LECTERN), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.BOOK, 3), "oak_planks", "lectern");

        addSawmillRecipe(output, Ingredient.of(Items.CHEST), new ItemStack(Items.OAK_PLANKS, 7),
                3, "oak_planks", "chest");
        addSawmillRecipe(output, Ingredient.of(Items.BARREL), new ItemStack(Items.OAK_PLANKS, 6),
                5, "oak_planks", "barrel");

        addSawmillRecipe(output, Ingredient.of(Items.WOODEN_SWORD), new ItemStack(Items.OAK_PLANKS, 2),
                1, "oak_planks", "wooden_sword");
        addSawmillRecipe(output, Ingredient.of(Items.WOODEN_SHOVEL), new ItemStack(Items.OAK_PLANKS),
                2, "oak_planks", "wooden_shovel");
        addSawmillRecipe(output, Ingredient.of(Items.WOODEN_PICKAXE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_pickaxe");
        addSawmillRecipe(output, Ingredient.of(Items.WOODEN_AXE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_axe");
        addSawmillRecipe(output, Ingredient.of(Items.WOODEN_HOE), new ItemStack(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hoe");
        addSawmillRecipe(output, Ingredient.of(EPItems.WOODEN_HAMMER.get()), new ItemStack(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hammer");

        addSawmillRecipe(output, Ingredient.of(ItemTags.PLANKS), new ItemStack(Items.STICK, 3),
                1, "sticks", "planks");
        addSawmillRecipe(output, Ingredient.of(Items.BAMBOO_MOSAIC), new ItemStack(Items.STICK, 3),
                3, "sticks", "bamboo_mosaic");

        addSawmillRecipe(output, Ingredient.fromValues(Stream.of(new Ingredient.TagValue(ItemTags.WOODEN_STAIRS),
                        new Ingredient.ItemValue(new ItemStack(Items.BAMBOO_MOSAIC_STAIRS)))),
                new ItemStack(Items.STICK, 3), 1, "sticks", "stairs");
        addSawmillRecipe(output, Ingredient.fromValues(Stream.of(new Ingredient.TagValue(ItemTags.WOODEN_SLABS),
                        new Ingredient.ItemValue(new ItemStack(Items.BAMBOO_MOSAIC_SLAB)))),
                new ItemStack(Items.STICK, 1), 1, "sticks", "slabs");
        addSawmillRecipe(output, Ingredient.of(ItemTags.WOODEN_BUTTONS), new ItemStack(Items.STICK, 3),
                1, "sticks", "buttons");

        addSawmillRecipe(output, Ingredient.of(Items.LADDER), new ItemStack(Items.STICK, 2),
                1, "sticks", "ladder");

        addSawmillRecipe(output, Ingredient.of(Items.BOWL), new ItemStack(Items.STICK),
                2, "sticks", "bowl");
        addSawmillRecipe(output, Ingredient.of(Items.BOW), new ItemStack(Items.STICK, 3),
                new ItemStack(Items.STRING, 3), "sticks", "bow");
        addSawmillRecipe(output, Ingredient.of(Items.FISHING_ROD), new ItemStack(Items.STICK, 3),
                new ItemStack(Items.STRING, 2), "sticks", "fishing_rod");

        addSawmillRecipe(output, Ingredient.of(Tags.Items.RODS_WOODEN), new ItemStack(EPItems.SAWDUST.get()),
                0, "sawdust", "sticks");
    }

    private void buildPlantGrowthChamberRecipes(Consumer<FinishedRecipe> output) {
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

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.PINK_PETALS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.PINK_PETALS), new double[] {
                        1., 1., 1., .67, .33, .33, .15
                })
        }, 16000, "pink_petals", "pink_petals");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.SWEET_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SWEET_BERRIES), new double[] {
                        1., 1., .33, .17
                })
        }, 16000, "sweet_berries", "sweet_berries");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.GLOW_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.GLOW_BERRIES), new double[] {
                        1., 1., .67, .33, .17, .17
                })
        }, 16000, "glow_berries", "glow_berries");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.WHEAT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT), new double[] {
                        1., .75, .25
                })
        }, 16000, "wheat", "wheat_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.BEETROOT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "beetroots", "beetroot_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.POTATO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.POTATO), new double[] {
                        1., .75, .25, .25
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.POISONOUS_POTATO), new double[] {
                        .125
                })
        }, 16000, "potatoes", "potato");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.CARROT), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.CARROT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "carrots", "carrot");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.MELON_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.MELON_SLICE), new double[] {
                        1., 1., .75, .25, .25
                })
        }, 16000, "melon_slices", "melon_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.PUMPKIN_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.PUMPKIN), new double[] {
                        1.
                })
        }, 16000, "pumpkin", "pumpkin_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.SUGAR_CANE), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SUGAR_CANE), new double[] {
                        1., 1., .67, .67, .33, .17, .17
                })
        }, 16000, "sugar_canes", "sugar_cane");
        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.BAMBOO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BAMBOO), new double[] {
                        1., 1., .67, .17
                })
        }, 16000, "bamboo", "bamboo");
    }

    private void buildPlantGrowthChamberFertilizerRecipes(Consumer<FinishedRecipe> output) {
        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.of(Items.BONE_MEAL),
                1.5, 3., "bone_meal");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.of(EPItems.BASIC_FERTILIZER.get()),
                2.5, 3.5, "basic_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.of(EPItems.GOOD_FERTILIZER.get()),
                3.5, 5., "good_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.of(EPItems.ADVANCED_FERTILIZER.get()),
                5., 6.5, "advanced_fertilizer");
    }

    private void buildMetalPressRecipes(Consumer<FinishedRecipe> output) {
        addGearMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_IRON), new ItemStack(EPItems.IRON_GEAR.get()));

        addRodMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_IRON), new ItemStack(EPItems.IRON_ROD.get()));

        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_TIN), new ItemStack(EPItems.TIN_WIRE.get()));
        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_COPPER), new ItemStack(EPItems.COPPER_WIRE.get()));
        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_GOLD), new ItemStack(EPItems.GOLD_WIRE.get()));

        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER), new ItemStack(EPItems.ENERGIZED_COPPER_WIRE.get()));
        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD), new ItemStack(EPItems.ENERGIZED_GOLD_WIRE.get()));
    }

    private void buildHeatGeneratorRecipes(Consumer<FinishedRecipe> output) {
        addHeatGeneratorRecipe(output, Fluids.FLOWING_LAVA, 15, "flowing_lava");
        addHeatGeneratorRecipe(output, Fluids.LAVA, 25, "still_lava");
    }

    private void buildThermalGeneratorRecipes(Consumer<FinishedRecipe> output) {
        addThermalGeneratorRecipe(output, Fluids.LAVA, 20000, "lava");
    }

    private void buildAssemblingMachineRecipes(Consumer<FinishedRecipe> output) {
        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(EPItems.BASIC_SOLAR_CELL.get()), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_ENERGIZED_COPPER), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_TIN), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY), 1)
        }, new ItemStack(EPItems.ADVANCED_SOLAR_CELL.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(EPItems.ADVANCED_SOLAR_CELL.get()), 2),
                new IngredientWithCount(Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 2),
                new IngredientWithCount(Ingredient.of(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(EPItems.REINFORCED_ADVANCED_SOLAR_CELL.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(EPItems.BASIC_CIRCUIT.get()), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.WIRES_ENERGIZED_COPPER), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 4),
                new IngredientWithCount(Ingredient.of(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(EPItems.ADVANCED_CIRCUIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(EPItems.ADVANCED_CIRCUIT.get()), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.WIRES_ENERGIZED_GOLD), 6),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 6)
        }, new ItemStack(EPItems.PROCESSING_UNIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(EPItems.PROCESSING_UNIT.get()), 4),
                new IngredientWithCount(Ingredient.of(EPItems.TELEPORTER_MATRIX.get()), 4),
                new IngredientWithCount(Ingredient.of(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 2)
        }, new ItemStack(EPItems.TELEPORTER_PROCESSING_UNIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(Tags.Items.GEMS_AMETHYST), 6),
                new IngredientWithCount(Ingredient.of(Tags.Items.GEMS_DIAMOND), 2),
                new IngredientWithCount(Ingredient.of(Tags.Items.GEMS_EMERALD), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY))
        }, new ItemStack(EPItems.CRYSTAL_MATRIX.get()));
    }

    private void buildStoneSolidifierRecipes(Consumer<FinishedRecipe> output) {
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

    private void buildFiltrationPlantRecipes(Consumer<FinishedRecipe> output) {
        addOreFiltrationRecipe(output, new ItemStack(EPItems.RAW_TIN.get()), 0.05, "tin");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_COPPER), 0.05, "copper");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_IRON), 0.05, "iron");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_GOLD), 0.005, "gold");
    }

    private void buildFluidTransposerRecipes(Consumer<FinishedRecipe> output) {
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.WHITE_CONCRETE_POWDER), new ItemStack(Items.WHITE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.ORANGE_CONCRETE_POWDER), new ItemStack(Items.ORANGE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.MAGENTA_CONCRETE_POWDER), new ItemStack(Items.MAGENTA_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.LIGHT_BLUE_CONCRETE_POWDER), new ItemStack(Items.LIGHT_BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.YELLOW_CONCRETE_POWDER), new ItemStack(Items.YELLOW_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.LIME_CONCRETE_POWDER), new ItemStack(Items.LIME_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.PINK_CONCRETE_POWDER), new ItemStack(Items.PINK_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.GRAY_CONCRETE_POWDER), new ItemStack(Items.GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.LIGHT_GRAY_CONCRETE_POWDER), new ItemStack(Items.LIGHT_GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.CYAN_CONCRETE_POWDER), new ItemStack(Items.CYAN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.PURPLE_CONCRETE_POWDER), new ItemStack(Items.PURPLE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.BLUE_CONCRETE_POWDER), new ItemStack(Items.BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.BROWN_CONCRETE_POWDER), new ItemStack(Items.BROWN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.GREEN_CONCRETE_POWDER), new ItemStack(Items.GREEN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.RED_CONCRETE_POWDER), new ItemStack(Items.RED_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.BLACK_CONCRETE_POWDER), new ItemStack(Items.BLACK_CONCRETE));

        addFluidTransposerRecipe(output, Ingredient.of(Items.SPONGE), new ItemStack(Items.WET_SPONGE), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, 1000));
        addFluidTransposerRecipe(output, Ingredient.of(Items.WET_SPONGE), new ItemStack(Items.SPONGE), FluidTransposerBlockEntity.Mode.EMPTYING,
                new FluidStack(Fluids.WATER, 1000));

        addFluidTransposerRecipe(output, Ingredient.of(Items.DIRT), new ItemStack(Items.MUD), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, 250));
    }

    private void buildChargerRecipes(Consumer<FinishedRecipe> output) {
        addChargerRecipe(output, Ingredient.of(Tags.Items.INGOTS_COPPER),
                new ItemStack(EPItems.ENERGIZED_COPPER_INGOT.get()), 4194304);
    }

    private void buildEnergizerRecipes(Consumer<FinishedRecipe> output) {
        addEnergizerRecipe(output, Ingredient.of(Tags.Items.INGOTS_COPPER),
                new ItemStack(EPItems.ENERGIZED_COPPER_INGOT.get()), 32768);
        addEnergizerRecipe(output, Ingredient.of(Tags.Items.INGOTS_GOLD),
                new ItemStack(EPItems.ENERGIZED_GOLD_INGOT.get()), 131072);
        addEnergizerRecipe(output, Ingredient.of(EPItems.CRYSTAL_MATRIX.get()),
                new ItemStack(EPItems.ENERGIZED_CRYSTAL_MATRIX.get()), 524288);
    }

    private void buildCrystalGrowthChamberRecipes(Consumer<FinishedRecipe> output) {
        addCrystalGrowthChamberRecipe(output, Ingredient.of(Tags.Items.GEMS_AMETHYST),
                new OutputItemStackWithPercentages(new ItemStack(Items.AMETHYST_SHARD), new double[] {
                    1., 1., .67, .5, .25, .125
                }), 16000);
        addCrystalGrowthChamberRecipe(output, Ingredient.of(Items.AMETHYST_BLOCK),
                new OutputItemStackWithPercentages(new ItemStack(Items.BUDDING_AMETHYST), .25), 4,
                32000);
    }

    private static void add3x3PackingCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
                                                    Ingredient unpackedInput, RegistryObject<Item> packedItem, CraftingBookCategory category,
                                                    String group, String recipeIdSuffix) {
        add3x3PackingCraftingRecipe(output, hasIngredientTrigger, unpackedInput, packedItem.get(), category, group, recipeIdSuffix);
    }
    private static void add3x3PackingCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
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
    private static void add3x3UnpackingCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
                                                      Ingredient packedInput, RegistryObject<Item> unpackedItem, CraftingBookCategory category,
                                                      String group, String recipeIdSuffix) {
        add3x3UnpackingCraftingRecipe(output, hasIngredientTrigger, packedInput, unpackedItem.get(), category, group, recipeIdSuffix);
    }
    private static void add3x3UnpackingCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
                                                      Ingredient packedInput, ItemLike unpackedItem, CraftingBookCategory category,
                                                      String group, String recipeIdSuffix) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, List.of(
                packedInput
        ), new ItemStack(unpackedItem, 9), category, group, recipeIdSuffix);
    }
    private static void addMetalIngotCraftingRecipes(Consumer<FinishedRecipe> output, TagKey<Item> nuggetInput,
                                                     TagKey<Item> blockInput, RegistryObject<Item> ingotItem, String metalName) {
        addMetalIngotCraftingRecipes(output, nuggetInput, blockInput, ingotItem.get(), metalName);
    }
    private static void addMetalIngotCraftingRecipes(Consumer<FinishedRecipe> output, TagKey<Item> nuggetInput,
                                                     TagKey<Item> blockInput, ItemLike ingotItem, String metalName) {
        add3x3PackingCraftingRecipe(output, has(nuggetInput), Ingredient.of(nuggetInput), ingotItem,
                CraftingBookCategory.MISC, metalName + "_ingot", "_from_nuggets");
        add3x3UnpackingCraftingRecipe(output, has(blockInput), Ingredient.of(blockInput), ingotItem,
                CraftingBookCategory.MISC, metalName + "_ingot", "_from_" + metalName + "_block");
    }
    private static void addMetalNuggetCraftingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> ingotInput, RegistryObject<Item> nuggetItem) {
        addMetalNuggetCraftingRecipe(output, ingotInput, nuggetItem.get());
    }
    private static void addMetalNuggetCraftingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> ingotInput, ItemLike nuggetItem) {
        addShapelessCraftingRecipe(output, has(ingotInput), List.of(
                Ingredient.of(ingotInput)
        ), new ItemStack(nuggetItem, 9), CraftingBookCategory.MISC);
    }
    private static void addMetalPlateCraftingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> ingotInput, RegistryObject<Item> plateItem) {
        addMetalPlateCraftingRecipe(output, ingotInput, plateItem.get());
    }
    private static void addMetalPlateCraftingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> ingotInput, ItemLike plateItem) {
        addShapelessCraftingRecipe(output, has(ingotInput), List.of(
                Ingredient.of(CommonItemTags.TOOLS_HAMMERS),
                Ingredient.of(ingotInput)
        ), new ItemStack(plateItem), CraftingBookCategory.MISC);
    }
    private static void addMetalWireCraftingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> plateInput, RegistryObject<Item> wireItem) {
        addMetalWireCraftingRecipe(output, plateInput, wireItem.get());
    }
    private static void addMetalWireCraftingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> plateInput, ItemLike wireItem) {
        addShapelessCraftingRecipe(output, has(plateInput), List.of(
                Ingredient.of(CommonItemTags.TOOLS_CUTTERS),
                Ingredient.of(plateInput)
        ), new ItemStack(wireItem, 2), CraftingBookCategory.MISC);
    }
    private static void addHammerCraftingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> materialInput, RegistryObject<Item> hammerItem) {
        addHammerCraftingRecipe(output, materialInput, hammerItem.get());
    }
    private static void addHammerCraftingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> materialInput, ItemLike hammerItem) {
        addShapedCraftingRecipe(output, has(materialInput), Map.of(
                'S', Ingredient.of(Tags.Items.RODS_WOODEN),
                'M', Ingredient.of(materialInput)
        ), new String[] {
                " M ",
                " SM",
                "S  "
        }, new ItemStack(hammerItem), CraftingBookCategory.MISC);
    }
    private static void addBasicCableCraftingRecipes(Consumer<FinishedRecipe> output, TagKey<Item> ingotInput, TagKey<Item> wireInput,
                                                     ItemStack cableItem) {
        addCableCraftingRecipe(output, ingotInput, cableItem);
        addCableUsingWireCraftingRecipe(output, wireInput, cableItem);
    }
    private static void addCableUsingWireCraftingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> wireInput,
                                                        ItemStack cableItem) {
        addShapedCraftingRecipe(output, has(wireInput), Map.of(
                'W', Ingredient.of(wireInput),
                'I', Ingredient.of(EPItems.CABLE_INSULATOR.get())
        ), new String[] {
                "IWI",
                "IWI",
                "IWI"
        }, cableItem, CraftingBookCategory.MISC, getItemName(cableItem.getItem()), "_using_wire");
    }
    private static void addCableCraftingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> ingotInput,
                                               ItemStack cableItem) {
        addShapedCraftingRecipe(output, has(ingotInput), Map.of(
                'I', Ingredient.of(ingotInput),
                'i', Ingredient.of(EPItems.CABLE_INSULATOR.get())
        ), new String[] {
                "iIi",
                "iIi",
                "iIi"
        }, cableItem, CraftingBookCategory.MISC, getItemName(cableItem.getItem()));
    }
    private static void addShapedCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingBookCategory category) {
        addShapedCraftingRecipe(output, hasIngredientTrigger, key, pattern, result, category, "");
    }
    private static void addShapedCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingBookCategory category,
                                                String group) {
        addShapedCraftingRecipe(output, hasIngredientTrigger, key, pattern, result, category, group, "");
    }
    private static void addShapedCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingBookCategory category,
                                                String group, String recipeIdSuffix) {
        addShapedCraftingRecipe(output, hasIngredientTrigger, key, pattern, result, category, group, recipeIdSuffix, "");
    }
    private static void addShapedCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
                                                Map<Character, Ingredient> key, String[] pattern,
                                                ItemStack result, CraftingBookCategory category,
                                                String group, String recipeIdSuffix, String recipeIdPrefix) {
        ResourceLocation recipeId = EPAPI.id("crafting/" +
                recipeIdPrefix + getItemName(result.getItem()) + recipeIdSuffix);

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", hasIngredientTrigger)
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        ShapedFinishedRecipe recipe = new ShapedFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                category, key, pattern, result,
                advancementBuilder,
                recipeId.withPrefix("recipes/")
        );
        output.accept(recipe);
    }
    private static void addShapelessCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingBookCategory category) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, inputs, result, category, "");
    }
    private static void addShapelessCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingBookCategory category,
                                                   String group) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, inputs, result, category, group, "");
    }
    private static void addShapelessCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingBookCategory category,
                                                   String group, String recipeIdSuffix) {
        addShapelessCraftingRecipe(output, hasIngredientTrigger, inputs, result, category, group, recipeIdSuffix, "");
    }
    private static void addShapelessCraftingRecipe(Consumer<FinishedRecipe> output, InventoryChangeTrigger.TriggerInstance hasIngredientTrigger,
                                                   List<Ingredient> inputs, ItemStack result, CraftingBookCategory category,
                                                   String group, String recipeIdSuffix, String recipeIdPrefix) {
        ResourceLocation recipeId = EPAPI.id("crafting/" +
                recipeIdPrefix + getItemName(result.getItem()) + recipeIdSuffix);

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", hasIngredientTrigger)
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        ShapelessFinishedRecipe recipe = new ShapelessFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category, result,
                NonNullList.of(Ingredient.EMPTY, inputs.toArray(Ingredient[]::new)),
                advancementBuilder,
                recipeId.withPrefix("recipes/")
        );
        output.accept(recipe);
    }
    private static void addCustomCraftingRecipe(Consumer<FinishedRecipe> output, RecipeSerializer<? extends CustomRecipe> customRecipeSerializer,
                                                CraftingBookCategory category, String recipeIdString) {
        ResourceLocation recipeId = EPAPI.id("crafting/" +
                recipeIdString);

        CustomFinishedRecipe recipe = new CustomFinishedRecipe(
                recipeId,
                category,
                customRecipeSerializer
        );
        output.accept(recipe);
    }

    private static void addBlastingAndSmeltingRecipes(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result,
                                                      CookingBookCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }
    private static void addBlastingAndSmeltingRecipes(Consumer<FinishedRecipe> output, TagKey<Item> ingredient, ItemStack result,
                                                      CookingBookCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group) {
        ResourceLocation recipeId = EPAPI.id("smelting/" +
                getItemName(result.getItem()));

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, ResourceLocation recipeId) {
        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                recipeId.withPrefix("recipes/"),
                RecipeSerializer.SMELTING_RECIPE
        );
        output.accept(recipe);
    }
    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                recipeId.withPrefix("recipes/"),
                RecipeSerializer.SMELTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addBlastingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                recipeId.withPrefix("recipes/"),
                RecipeSerializer.BLASTING_RECIPE
        );
        output.accept(recipe);
    }
    private static void addBlastingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                recipeId.withPrefix("recipes/"),
                RecipeSerializer.BLASTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addNetheriteSmithingUpgradeRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient base, ItemStack output) {
        ResourceLocation recipeId = EPAPI.id("smithing/" +
                getItemName(output.getItem()));

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(Tags.Items.INGOTS_NETHERITE))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        SmithingTransformFinishedRecipe recipe = new SmithingTransformFinishedRecipe(
                recipeId,
                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                base,
                Ingredient.of(Tags.Items.INGOTS_NETHERITE),
                output.getItem(),
                advancementBuilder,
                recipeId.withPrefix("recipes/")
        );
        recipeOutput.accept(recipe);
    }

    private static void addAlloyFurnaceRecipe(Consumer<FinishedRecipe> recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              int ticks) {
        addAlloyFurnaceRecipe(recipeOutput, inputs, output,
                new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0]), ticks);
    }
    private static void addAlloyFurnaceRecipe(Consumer<FinishedRecipe> recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              OutputItemStackWithPercentages secondaryOutput, int ticks) {
        ResourceLocation recipeId = EPAPI.id("alloy_furnace/" +
                getItemName(output.getItem()));

        AlloyFurnaceFinishedRecipe recipe = new AlloyFurnaceFinishedRecipe(
                recipeId,
                output, secondaryOutput, inputs, ticks
        );
        recipeOutput.accept(recipe);
    }

    private static void addPressMoldMakerRecipe(Consumer<FinishedRecipe> recipeOutput, int clayCount, ItemStack output) {
        ResourceLocation recipeId = EPAPI.id("press_mold_maker/" +
                getItemName(output.getItem()));

        PressMoldMakerFinishedRecipe recipe = new PressMoldMakerFinishedRecipe(
                recipeId,
                output, clayCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addPlateCompressorRecipes(Consumer<FinishedRecipe> recipeOutput, Ingredient ingotInput,
                                                  Ingredient blockInput, ItemStack output, String metalName) {
        addPlateCompressorIngotRecipe(recipeOutput, ingotInput, output, metalName);
        addCompressorRecipe(recipeOutput, blockInput, output.copyWithCount(9), metalName + "_block");
    }
    private static void addPlateCompressorIngotRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient ingotInput,
                                                   ItemStack output, String metalName) {
        addCompressorRecipe(recipeOutput, ingotInput, output, metalName + "_ingot");
    }
    private static void addCompressorRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output, String recipeIngredientName) {
        addCompressorRecipe(recipeOutput, input, output, 1, recipeIngredientName);
    }
    private static void addCompressorRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output, int inputCount,
                                            String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("compressing/" +
                getItemName(output.getItem()) + "_from_compressing_" + recipeIngredientName);

        CompressorFinishedRecipe recipe = new CompressorFinishedRecipe(
                recipeId,
                output, input, inputCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addCrusherRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output,
                                         String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("crusher/" +
                getItemName(output.getItem()) + "_from_crushing_" + recipeIngredientName);

        CrusherFinishedRecipe recipe = new CrusherFinishedRecipe(
                recipeId,
                output, input
        );
        recipeOutput.accept(recipe);
    }

    private static void addBasicMetalPulverizerRecipes(Consumer<FinishedRecipe> recipeOutput, Ingredient oreInput,
                                                       Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                       Ingredient ingotInput, ItemStack output, String metalName) {
        addPulverizerRecipe(recipeOutput, oreInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., .25
        }, new double[] {
                1., 1, .5, .25
        }), metalName + "_ores");

        addRawMetalAndIngotPulverizerRecipes(recipeOutput, rawMetalInput, rawMetalBlockInput, ingotInput, output, metalName);
    }
    private static void addRawMetalAndIngotPulverizerRecipes(Consumer<FinishedRecipe> recipeOutput,
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
    private static void addPulverizerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            String recipeIngredientName) {
        addPulverizerRecipe(recipeOutput, input, output,
                new PulverizerRecipe.OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0], new double[0]), recipeIngredientName);
    }
    private static void addPulverizerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            PulverizerRecipe.OutputItemStackWithPercentages secondaryOutput,
                                            String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("pulverizer/" +
                getItemName(output.output().getItem()) + "_from_pulverizer_" + recipeIngredientName);

        PulverizerFinishedRecipe recipe = new PulverizerFinishedRecipe(
                recipeId,
                output, secondaryOutput, input
        );
        recipeOutput.accept(recipe);
    }

    private static void addBasicWoodSawmillRecipe(Consumer<FinishedRecipe> recipeOutput, ItemStack planksItem,
                                                  Ingredient logsInput, Ingredient fenceInput, Ingredient fenceGateInput,
                                                  Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                  Ingredient signInput, Ingredient boatInput, Ingredient chestBoatInput,
                                                  boolean isRaft, String woodName) {
        addSawmillRecipe(recipeOutput, logsInput, planksItem.copyWithCount(6), 1, getItemName(planksItem.getItem()),
                woodName + "_logs");

        addBasicWoodWithoutLogsSawmillRecipe(recipeOutput, planksItem, fenceInput, fenceGateInput, doorInput, trapdoorInput,
                pressurePlateInput, signInput, boatInput, chestBoatInput, isRaft, woodName);
    }
    private static void addBasicWoodWithoutLogsSawmillRecipe(Consumer<FinishedRecipe> recipeOutput, ItemStack planksItem,
                                                             Ingredient fenceInput, Ingredient fenceGateInput,
                                                             Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                             Ingredient signInput, Ingredient boatInput, Ingredient chestBoatInput,
                                                             boolean isRaft, String woodName) {
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(recipeOutput, planksItem, fenceInput, fenceGateInput, doorInput,
                trapdoorInput, pressurePlateInput, signInput, woodName);

        addSawmillRecipe(recipeOutput, boatInput, planksItem.copyWithCount(4), 3, getItemName(planksItem.getItem()),
                woodName + (isRaft?"_raft":"_boat"));
        addSawmillRecipe(recipeOutput, chestBoatInput, planksItem.copyWithCount(5), 7, getItemName(planksItem.getItem()),
                woodName + (isRaft?"_chest_raft":"_chest_boat"));
    }
    private static void addBasicWoodWithoutLogsAndBoatsSawmillRecipe(Consumer<FinishedRecipe> recipeOutput, ItemStack planksItem,
                                                                     Ingredient fenceInput, Ingredient fenceGateInput,
                                                                     Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                                     Ingredient signInput, String woodName) {
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
    }
    private static void addSawmillRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output,
                                         int sawdustAmount, String outputName, String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillFinishedRecipe recipe = new SawmillFinishedRecipe(
                recipeId,
                output, input, sawdustAmount
        );
        recipeOutput.accept(recipe);
    }
    private static void addSawmillRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output,
                                         ItemStack secondaryOutput, String outputName, String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillFinishedRecipe recipe = new SawmillFinishedRecipe(
                recipeId,
                output, secondaryOutput, input
        );
        recipeOutput.accept(recipe);
    }

    private static void addBasicFlowerGrowingRecipe(Consumer<FinishedRecipe> recipeOutput, ItemLike flowerItem,
                                                    String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, Ingredient.of(flowerItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., 1., .33
                })
        }, 16000, outputName, getItemName(flowerItem));
    }
    private static void addBasicMushroomsGrowingRecipe(Consumer<FinishedRecipe> recipeOutput, ItemLike mushroomItem,
                                                       String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, Ingredient.of(mushroomItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(mushroomItem), new double[] {
                        1., 1., .5, .25
                })
        }, 16000, outputName, getItemName(mushroomItem));
    }
    private static void addBasicAncientFlowerGrowingRecipe(Consumer<FinishedRecipe> recipeOutput, ItemLike seedItem,
                                                           ItemLike flowerItem, String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, Ingredient.of(seedItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(seedItem), new double[] {
                        1., .33, .15
                }),
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., .15
                })
        }, 16000, outputName, getItemName(seedItem));
    }
    private static void addPlantGrowthChamberRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input,
                                                    OutputItemStackWithPercentages[] outputs, int ticks,
                                                    String outputName, String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("growing/" +
                outputName + "_from_growing_" + recipeIngredientName);

        PlantGrowthChamberFinishedRecipe recipe = new PlantGrowthChamberFinishedRecipe(
                recipeId,
                outputs, input, ticks
        );
        recipeOutput.accept(recipe);
    }

    private static void addPlantGrowthChamberFertilizerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input,
                                                              double speedMultiplier, double energyConsumptionMultiplier,
                                                              String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("growing/fertilizer/" +
                recipeIngredientName);

        PlantGrowthChamberFertilizerFinishedRecipe recipe = new PlantGrowthChamberFertilizerFinishedRecipe(
                recipeId,
                input, speedMultiplier, energyConsumptionMultiplier
        );
        recipeOutput.accept(recipe);
    }

    private static void addGearMetalPressRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output, new ItemStack(EPItems.GEAR_PRESS_MOLD.get()), 2);
    }
    private static void addRodMetalPressRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output.copyWithCount(2), new ItemStack(EPItems.ROD_PRESS_MOLD.get()));
    }
    private static void addWireMetalPressRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output.copyWithCount(3), new ItemStack(EPItems.WIRE_PRESS_MOLD.get()));
    }
    private static void addMetalPressRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output,
                                            ItemStack pressMold) {
        addMetalPressRecipe(recipeOutput, input, output, pressMold, 1);
    }
    private static void addMetalPressRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output,
                                            ItemStack pressMold, int inputCount) {
        ResourceLocation recipeId = EPAPI.id("metal_press/" +
                getItemName(output.getItem()));

        MetalPressFinishedRecipe recipe = new MetalPressFinishedRecipe(
                recipeId,
                output, pressMold, input, inputCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addHeatGeneratorRecipe(Consumer<FinishedRecipe> recipeOutput, Fluid input, int energyProduction,
                                               String recipeIngredientName) {
        addHeatGeneratorRecipe(recipeOutput, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private static void addHeatGeneratorRecipe(Consumer<FinishedRecipe> recipeOutput, Fluid[] input, int energyProduction,
                                               String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("heat_generator/" +
                "energy_production_from_" + recipeIngredientName);

        HeatGeneratorFinishedRecipe recipe = new HeatGeneratorFinishedRecipe(
                recipeId,
                input, energyProduction
        );
        recipeOutput.accept(recipe);
    }

    private static void addThermalGeneratorRecipe(Consumer<FinishedRecipe> recipeOutput, Fluid input, int energyProduction,
                                                  String recipeIngredientName) {
        addThermalGeneratorRecipe(recipeOutput, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private static void addThermalGeneratorRecipe(Consumer<FinishedRecipe> recipeOutput, Fluid[] input, int energyProduction,
                                                  String recipeIngredientName) {
        ResourceLocation recipeId = EPAPI.id("thermal_generator/" +
                "energy_production_from_" + recipeIngredientName);

        ThermalGeneratorFinishedRecipe recipe = new ThermalGeneratorFinishedRecipe(
                recipeId,
                input, energyProduction
        );
        recipeOutput.accept(recipe);
    }

    private static void addStoneSolidifierRecipe(Consumer<FinishedRecipe> recipeOutput, int waterAmount, int lavaAmount, ItemStack output) {
        ResourceLocation recipeId = EPAPI.id("stone_solidifier/" +
                getItemName(output.getItem()));

        StoneSolidifierFinishedRecipe recipe = new StoneSolidifierFinishedRecipe(
                recipeId,
                output, waterAmount, lavaAmount
        );
        recipeOutput.accept(recipe);
    }

    private static void addAssemblingMachineRecipe(Consumer<FinishedRecipe> recipeOutput, IngredientWithCount[] inputs, ItemStack output) {
        ResourceLocation recipeId = EPAPI.id("assembling/" +
                getItemName(output.getItem()));

        AssemblingMachineFinishedRecipe recipe = new AssemblingMachineFinishedRecipe(
                recipeId,
                output, inputs
        );
        recipeOutput.accept(recipe);
    }

    private static void addOreFiltrationRecipe(Consumer<FinishedRecipe> recipeOutput, ItemStack oreOutput, double oreOutputPercentage,
                                               String oreName) {
        addFiltrationPlantRecipe(recipeOutput, new OutputItemStackWithPercentages(new ItemStack(EPItems.STONE_PEBBLE.get()), .33),
                new OutputItemStackWithPercentages(oreOutput, oreOutputPercentage), ForgeRegistries.ITEMS.getKey(oreOutput.getItem()),
                oreName + "_ore_filtration");
    }
    private static void addFiltrationPlantRecipe(Consumer<FinishedRecipe> recipeOutput, OutputItemStackWithPercentages output,
                                                 ResourceLocation icon, String recipeName) {
        addFiltrationPlantRecipe(recipeOutput, output, OutputItemStackWithPercentages.EMPTY, icon, recipeName);
    }
    private static void addFiltrationPlantRecipe(Consumer<FinishedRecipe> recipeOutput, OutputItemStackWithPercentages output,
                                                 OutputItemStackWithPercentages secondaryOutput, ResourceLocation icon,
                                                 String recipeName) {
        ResourceLocation recipeId = EPAPI.id("filtration_plant/" +
                recipeName);

        FiltrationPlantFinishedRecipe recipe = new FiltrationPlantFinishedRecipe(
                recipeId,
                output, secondaryOutput, icon
        );
        recipeOutput.accept(recipe);
    }

    private static void addConcreteFluidTransposerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output) {
        addFluidTransposerRecipe(recipeOutput, input, output, FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, 1000));
    }
    private static void addFluidTransposerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output,
                                                 FluidTransposerBlockEntity.Mode mode, FluidStack fluid) {
        ResourceLocation recipeId = EPAPI.id("fluid_transposer/" +
                getItemName(output.getItem()));

        FluidTransposerFinishedRecipe recipe = new FluidTransposerFinishedRecipe(
                recipeId,
                mode, output, input, fluid
        );
        recipeOutput.accept(recipe);
    }

    private static void addChargerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        ResourceLocation recipeId = EPAPI.id("charger/" +
                getItemName(output.getItem()));

        ChargerFinishedRecipe recipe = new ChargerFinishedRecipe(
                recipeId,
                output, input, energyConsumption
        );
        recipeOutput.accept(recipe);
    }

    private static void addEnergizerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        ResourceLocation recipeId = EPAPI.id("energizer/" +
                getItemName(output.getItem()));

        EnergizerFinishedRecipe recipe = new EnergizerFinishedRecipe(
                recipeId,
                output, input, energyConsumption
        );
        recipeOutput.accept(recipe);
    }

    private static void addCrystalGrowthChamberRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, OutputItemStackWithPercentages output,
                                                      int ticks) {
        addCrystalGrowthChamberRecipe(recipeOutput, input, output, 1, ticks);
    }
    private static void addCrystalGrowthChamberRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, OutputItemStackWithPercentages output,
                                                      int inputCount, int ticks) {
        ResourceLocation recipeId = EPAPI.id("crystal_growing/" +
                getItemName(output.output().getItem()));

        CrystalGrowthChamberFinishedRecipe recipe = new CrystalGrowthChamberFinishedRecipe(
                recipeId,
                output, input, inputCount, ticks
        );
        recipeOutput.accept(recipe);
    }

    private static InventoryChangeTrigger.TriggerInstance has(RegistryObject<Item> item) {
        return has(item.get());
    }
}
