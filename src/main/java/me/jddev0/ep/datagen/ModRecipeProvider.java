package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.datagen.recipe.AbstractCookingFinishedRecipe;
import me.jddev0.ep.datagen.recipe.AlloyFurnaceFinishedRecipe;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataGenerator output) {
        super(output);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> output) {
        buildCookingRecipes(output);
        buildAlloyFurnaceRecipes(output);
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

    private void buildAlloyFurnaceRecipes(Consumer<RecipeJsonProvider> output) {
        addAlloyFurnaceRecipe(output, new AlloyFurnaceRecipe.IngredientWithCount[] {
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS), 1),
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.fromTag(ItemTags.COALS), 3)
        }, new ItemStack(ModItems.STEEL_INGOT), 500);

        addAlloyFurnaceRecipe(output, new AlloyFurnaceRecipe.IngredientWithCount[] {
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.fromTag(CommonItemTags.TIN_INGOTS), 1),
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON),1),
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStack(ModItems.REDSTONE_ALLOY_INGOT), 2500);

        addAlloyFurnaceRecipe(output, new AlloyFurnaceRecipe.IngredientWithCount[] {
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.fromTag(CommonItemTags.STEEL_INGOTS), 3),
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS), 3),
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.fromTag(CommonItemTags.TIN_INGOTS), 3)
        }, new ItemStack(ModItems.ADVANCED_ALLOY_INGOT), 10000);
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

    private static void addAlloyFurnaceRecipe(Consumer<RecipeJsonProvider> recipeOutput,
                                              AlloyFurnaceRecipe.IngredientWithCount[] inputs,
                                              ItemStack output,
                                              int ticks) {
        addAlloyFurnaceRecipe(recipeOutput, inputs, output,
                new AlloyFurnaceRecipe.OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0]), ticks);
    }

    private static void addAlloyFurnaceRecipe(Consumer<RecipeJsonProvider> recipeOutput,
                                              AlloyFurnaceRecipe.IngredientWithCount[] inputs,
                                              ItemStack output,
                                              AlloyFurnaceRecipe.OutputItemStackWithPercentages secondaryOutput,
                                              int ticks) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "alloy_furnace/" +
                getItemPath(output.getItem()));

        AlloyFurnaceFinishedRecipe recipe = new AlloyFurnaceFinishedRecipe(
                recipeId,
                output, secondaryOutput, inputs, ticks
        );
        recipeOutput.accept(recipe);
    }

    public static Identifier withPrefixedPath(Identifier resourceLocation, String pathPrefix) {
        return new Identifier(resourceLocation.getNamespace(), pathPrefix + resourceLocation.getPath());
    }
}
