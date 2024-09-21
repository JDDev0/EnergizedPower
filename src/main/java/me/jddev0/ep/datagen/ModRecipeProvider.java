package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.datagen.recipe.AbstractCookingFinishedRecipe;
import me.jddev0.ep.datagen.recipe.AlloyFurnaceFinishedRecipe;
import me.jddev0.ep.datagen.recipe.AssemblingMachineFinishedRecipe;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter output) {
        buildCookingRecipes(output);
        buildAlloyFurnaceRecipes(output);
        buildAssemblingMachineRecipes(output);
    }

    private void buildCookingRecipes(RecipeExporter output) {
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

    private void buildAlloyFurnaceRecipes(RecipeExporter output) {
        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS), 1),
                new IngredientWithCount(Ingredient.fromTag(ItemTags.COALS), 3)
        }, new ItemStack(ModItems.STEEL_INGOT), 500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.TIN_INGOTS), 1),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON),1),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStack(ModItems.REDSTONE_ALLOY_INGOT), 2500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.STEEL_INGOTS), 3),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS), 3),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.TIN_INGOTS), 3)
        }, new ItemStack(ModItems.ADVANCED_ALLOY_INGOT), 10000);
    }

    private void buildAssemblingMachineRecipes(RecipeExporter output) {
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

    private static void addBlastingAndSmeltingRecipes(RecipeExporter output, ItemConvertible ingredient, ItemStack result,
                                                      CookingRecipeCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addBlastingAndSmeltingRecipes(RecipeExporter output, TagKey<Item> ingredient, ItemStack result,
                                                      CookingRecipeCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addSmeltingRecipe(RecipeExporter output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "smelting/" +
                getItemPath(result.getItem()));

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }

    private static void addSmeltingRecipe(RecipeExporter output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "smelting/" +
                getItemPath(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }

    private static void addSmeltingRecipe(RecipeExporter output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, Identifier recipeId) {
        Advancement.Builder advancementBuilder = output.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromItem(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.ofItems(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefixedPath("recipes/")),
                RecipeSerializer.SMELTING
        );
        output.accept(recipe);
    }

    private static void addSmeltingRecipe(RecipeExporter output, TagKey<Item> ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "smelting/" +
                getItemPath(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromTag(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.fromTag(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefixedPath("recipes/")),
                RecipeSerializer.SMELTING
        );
        output.accept(recipe);
    }

    private static void addBlastingRecipe(RecipeExporter output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "blasting/" +
                getItemPath(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromItem(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.ofItems(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefixedPath("recipes/")),
                RecipeSerializer.BLASTING
        );
        output.accept(recipe);
    }

    private static void addBlastingRecipe(RecipeExporter output, TagKey<Item> ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "blasting/" +
                getItemPath(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromTag(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        RecipeJsonProvider recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.fromTag(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefixedPath("recipes/")),
                RecipeSerializer.BLASTING
        );
        output.accept(recipe);
    }

    private static void addAlloyFurnaceRecipe(RecipeExporter recipeOutput,
                                              IngredientWithCount[] inputs,
                                              ItemStack output,
                                              int ticks) {
        addAlloyFurnaceRecipe(recipeOutput, inputs, output, new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0]), ticks);
    }

    private static void addAlloyFurnaceRecipe(RecipeExporter recipeOutput,
                                              IngredientWithCount[] inputs,
                                              ItemStack output,
                                              OutputItemStackWithPercentages secondaryOutput,
                                              int ticks) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "alloy_furnace/" +
                getItemPath(output.getItem()));

        AlloyFurnaceFinishedRecipe recipe = new AlloyFurnaceFinishedRecipe(
                recipeId,
                output, secondaryOutput, inputs, ticks
        );
        recipeOutput.accept(recipe);
    }

    private static void addAssemblingMachineRecipe(RecipeExporter recipeOutput, IngredientWithCount[] inputs, ItemStack output) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "assembling/" +
                getItemPath(output.getItem()));

        AssemblingMachineFinishedRecipe recipe = new AssemblingMachineFinishedRecipe(
                recipeId,
                output, inputs
        );
        recipeOutput.accept(recipe);
    }
}
