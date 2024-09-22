package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.datagen.recipe.*;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
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
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Objects;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataGenerator output) {
        super(output);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> output) {
        buildCookingRecipes(output);
        buildSmithingRecipes(output);
        buildPressMoldMakerRecipes(output);
        buildAlloyFurnaceRecipes(output);
        buildCompressorRecipes(output);
        buildMetalPressRecipes(output);
        buildHeatGeneratorRecipes(output);
        buildThermalGeneratorRecipes(output);
        buildAssemblingMachineRecipes(output);
        buildFiltrationPlantRecipes(output);
        buildFluidTransposerRecipes(output);
        buildChargerRecipes(output);
        buildEnergizerRecipes(output);
        buildCrystalGrowthChamberRecipes(output);
    }

    private void buildCookingRecipes(Consumer<RecipeJsonProvider> output) {
        addBlastingAndSmeltingRecipes(output, CommonItemTags.RAW_TIN_ORES, new ItemStack(ModItems.TIN_INGOT),
                100, .7f, "tin_ingot", "raw_tin");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.TIN_ORES, new ItemStack(ModItems.TIN_INGOT),
                100, .7f, "tin_ingot", "tin_ores");

        addBlastingAndSmeltingRecipes(output, CommonItemTags.TIN_DUSTS, new ItemStack(ModItems.TIN_INGOT),
                100, .7f, "tin_ingot", "tin_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.COPPER_DUSTS, new ItemStack(Items.COPPER_INGOT),
                100, .7f, "copper_ingot", "copper_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.IRON_DUSTS, new ItemStack(Items.IRON_INGOT),
                100, .7f, "iron_ingot", "iron_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.GOLD_DUSTS, new ItemStack(Items.GOLD_INGOT),
                100, .7f, "gold_ingot", "gold_dust");

        addBlastingAndSmeltingRecipes(output, ModItems.IRON_HAMMER, new ItemStack(Items.IRON_NUGGET),
                100, .1f, "iron_nugget", "iron_hammer");
        addBlastingAndSmeltingRecipes(output, ModItems.GOLDEN_HAMMER, new ItemStack(Items.GOLD_NUGGET),
                100, .1f, "gold_nugget", "golden_hammer");

        addBlastingAndSmeltingRecipes(output, ConventionalItemTags.QUARTZ, new ItemStack(ModItems.SILICON),
                250, 4.1f, "silicon", "nether_quartz");

        addSmeltingRecipe(output, ModBlocks.SAWDUST_BLOCK_ITEM, new ItemStack(Items.CHARCOAL),
                200, .15f, null, "sawdust_block");

        addSmeltingRecipe(output, ModItems.RAW_GEAR_PRESS_MOLD, new ItemStack(ModItems.GEAR_PRESS_MOLD),
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_ROD_PRESS_MOLD, new ItemStack(ModItems.ROD_PRESS_MOLD),
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_WIRE_PRESS_MOLD, new ItemStack(ModItems.WIRE_PRESS_MOLD),
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

    private static void addBlastingAndSmeltingRecipes(Consumer<RecipeJsonProvider> output, ItemConvertible ingredient, ItemStack result,
                                                      int time, float xp, String group, String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, 2 * time, xp, group, recipeIngredientName);
    }
    private static void addBlastingAndSmeltingRecipes(Consumer<RecipeJsonProvider> output, TagKey<Item> ingredient, ItemStack result,
                                                      int time, float xp, String group, String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addSmeltingRecipe(Consumer<RecipeJsonProvider> output, ItemConvertible ingredient, ItemStack result,
                                          int time, float xp, String group) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "smelting/" +
                getItemPath(result.getItem()));

        addSmeltingRecipe(output, ingredient, result, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(Consumer<RecipeJsonProvider> output, ItemConvertible ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "smelting/" +
                getItemPath(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(output, ingredient, result, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(Consumer<RecipeJsonProvider> output, ItemConvertible ingredient, ItemStack result,
                                          int time, float xp, String group, Identifier recipeId) {
        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromItem(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.ofItems(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefixedPath(recipeId, "recipes/"),
                RecipeSerializer.SMELTING
        );
        output.accept(recipe);
    }
    private static void addSmeltingRecipe(Consumer<RecipeJsonProvider> output, TagKey<Item> ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "smelting/" +
                getItemPath(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromTag(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.fromTag(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefixedPath(recipeId, "recipes/"),
                RecipeSerializer.SMELTING
        );
        output.accept(recipe);
    }

    private static void addBlastingRecipe(Consumer<RecipeJsonProvider> output, ItemConvertible ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "blasting/" +
                getItemPath(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromItem(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.ofItems(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefixedPath(recipeId, "recipes/"),
                RecipeSerializer.BLASTING
        );
        output.accept(recipe);
    }
    private static void addBlastingRecipe(Consumer<RecipeJsonProvider> output, TagKey<Item> ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "blasting/" +
                getItemPath(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromTag(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.fromTag(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefixedPath(recipeId, "recipes/"),
                RecipeSerializer.BLASTING
        );
        output.accept(recipe);
    }

    private static void addNetheriteSmithingUpgradeRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient base, ItemStack output) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "smithing/" +
                getItemPath(output.getItem()));

        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromTag(ConventionalItemTags.NETHERITE_INGOTS))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        SmithingUpgradeFinishedRecipe recipe = new SmithingUpgradeFinishedRecipe(
                recipeId,
                base,
                Ingredient.fromTag(ConventionalItemTags.NETHERITE_INGOTS),
                output.getItem(),
                advancementBuilder,
                withPrefixedPath(recipeId, "recipes/")
        );
        recipeOutput.accept(recipe);
    }

    private static void addAlloyFurnaceRecipe(Consumer<RecipeJsonProvider> recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              int ticks) {
        addAlloyFurnaceRecipe(recipeOutput, inputs, output, OutputItemStackWithPercentages.EMPTY, ticks);
    }
    private static void addAlloyFurnaceRecipe(Consumer<RecipeJsonProvider> recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              OutputItemStackWithPercentages secondaryOutput, int ticks) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "alloy_furnace/" +
                getItemPath(output.getItem()));

        AlloyFurnaceFinishedRecipe recipe = new AlloyFurnaceFinishedRecipe(
                recipeId,
                output, secondaryOutput, inputs, ticks
        );
        recipeOutput.accept(recipe);
    }

    private static void addPressMoldMakerRecipe(Consumer<RecipeJsonProvider> recipeOutput, int clayCount, ItemStack output) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "press_mold_maker/" +
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
        addCompressorRecipe(recipeOutput, blockInput, ItemStackUtils.copyWithCount(output, 9), metalName + "_block");
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
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "compressing/" +
                getItemPath(output.getItem()) + "_from_compressing_" + recipeIngredientName);

        CompressorFinishedRecipe recipe = new CompressorFinishedRecipe(
                recipeId,
                output, input, inputCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addGearMetalPressRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output, new ItemStack(ModItems.GEAR_PRESS_MOLD), 2);
    }
    private static void addRodMetalPressRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, ItemStackUtils.copyWithCount(output, 2), new ItemStack(ModItems.ROD_PRESS_MOLD));
    }
    private static void addWireMetalPressRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, ItemStackUtils.copyWithCount(output, 3), new ItemStack(ModItems.WIRE_PRESS_MOLD));
    }
    private static void addMetalPressRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output,
                                            ItemStack pressMold) {
        addMetalPressRecipe(recipeOutput, input, output, pressMold, 1);
    }
    private static void addMetalPressRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output,
                                            ItemStack pressMold, int inputCount) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "metal_press/" +
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
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "heat_generator/" +
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
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "thermal_generator/" +
                "energy_production_from_" + recipeIngredientName);

        ThermalGeneratorFinishedRecipe recipe = new ThermalGeneratorFinishedRecipe(
                recipeId,
                input, energyProduction
        );
        recipeOutput.accept(recipe);
    }

    private static void addAssemblingMachineRecipe(Consumer<RecipeJsonProvider> recipeOutput, IngredientWithCount[] inputs, ItemStack output) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "assembling/" +
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
                new OutputItemStackWithPercentages(oreOutput, oreOutputPercentage), Registry.ITEM.getId(oreOutput.getItem()),
                oreName + "_ore_filtration");
    }
    private static void addFiltrationPlantRecipe(Consumer<RecipeJsonProvider> recipeOutput, OutputItemStackWithPercentages output,
                                                 Identifier icon, String recipeName) {
        addFiltrationPlantRecipe(recipeOutput, output, OutputItemStackWithPercentages.EMPTY, icon, recipeName);
    }
    private static void addFiltrationPlantRecipe(Consumer<RecipeJsonProvider> recipeOutput, OutputItemStackWithPercentages output,
                                                 OutputItemStackWithPercentages secondaryOutput, Identifier icon,
                                                 String recipeName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "filtration_plant/" +
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
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "fluid_transposer/" +
                getItemPath(output.getItem()));

        FluidTransposerFinishedRecipe recipe = new FluidTransposerFinishedRecipe(
                recipeId,
                mode, output, input, fluid
        );
        recipeOutput.accept(recipe);
    }

    private static void addChargerRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "charger/" +
                getItemPath(output.getItem()));

        ChargerFinishedRecipe recipe = new ChargerFinishedRecipe(
                recipeId,
                output, input, energyConsumption
        );
        recipeOutput.accept(recipe);
    }

    private static void addEnergizerRecipe(Consumer<RecipeJsonProvider> recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "energizer/" +
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
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "crystal_growing/" +
                getItemPath(output.output().getItem()));

        CrystalGrowthChamberFinishedRecipe recipe = new CrystalGrowthChamberFinishedRecipe(
                recipeId,
                output, input, inputCount, ticks
        );
        recipeOutput.accept(recipe);
    }

    public static Identifier withPrefixedPath(Identifier resourceLocation, String pathPrefix) {
        return new Identifier(resourceLocation.getNamespace(), pathPrefix + resourceLocation.getPath());
    }
}
