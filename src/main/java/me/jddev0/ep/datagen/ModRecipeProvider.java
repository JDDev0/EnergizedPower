package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.datagen.recipe.AbstractCookingFinishedRecipe;
import me.jddev0.ep.datagen.recipe.AlloyFurnaceFinishedRecipe;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        buildCookingRecipes(output);
        buildAlloyFurnaceRecipes(output);
    }

    private void buildCookingRecipes(RecipeOutput output) {
        addBlastingAndSmeltingRecipes(output, CommonItemTags.RAW_MATERIALS_TIN, new ItemStack(ModItems.TIN_INGOT.get()), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "raw_tin");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.ORES_TIN, new ItemStack(ModItems.TIN_INGOT.get()), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "tin_ores");

        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_TIN, new ItemStack(ModItems.TIN_INGOT.get()), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "tin_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_COPPER, new ItemStack(Items.COPPER_INGOT), CookingBookCategory.MISC,
                100, .7f, "copper_ingot", "copper_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_IRON, new ItemStack(Items.IRON_INGOT), CookingBookCategory.MISC,
                100, .7f, "iron_ingot", "iron_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_GOLD, new ItemStack(Items.GOLD_INGOT), CookingBookCategory.MISC,
                100, .7f, "gold_ingot", "gold_dust");

        addBlastingAndSmeltingRecipes(output, ModItems.IRON_HAMMER.get(), new ItemStack(Items.IRON_NUGGET), CookingBookCategory.MISC,
                100, .1f, "iron_nugget", "iron_hammer");
        addBlastingAndSmeltingRecipes(output, ModItems.GOLDEN_HAMMER.get(), new ItemStack(Items.GOLD_NUGGET), CookingBookCategory.MISC,
                100, .1f, "gold_nugget", "golden_hammer");

        addBlastingAndSmeltingRecipes(output, Tags.Items.GEMS_QUARTZ, new ItemStack(ModItems.SILICON.get()), CookingBookCategory.MISC,
                250, 4.1f, "silicon", "nether_quartz");

        addSmeltingRecipe(output, ModBlocks.SAWDUST_BLOCK_ITEM.get(), new ItemStack(Items.CHARCOAL), CookingBookCategory.MISC,
                200, .15f, null, "sawdust_block");

        addSmeltingRecipe(output, ModItems.RAW_GEAR_PRESS_MOLD.get(), new ItemStack(ModItems.GEAR_PRESS_MOLD.get()), CookingBookCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_ROD_PRESS_MOLD.get(), new ItemStack(ModItems.ROD_PRESS_MOLD.get()), CookingBookCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_WIRE_PRESS_MOLD.get(), new ItemStack(ModItems.WIRE_PRESS_MOLD.get()), CookingBookCategory.MISC,
                200, .3f, null);
    }

    private void buildAlloyFurnaceRecipes(RecipeOutput output) {
        addAlloyFurnaceRecipe(output, new AlloyFurnaceRecipe.IngredientWithCount[] {
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.of(Tags.Items.INGOTS_IRON), 1),
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.of(ItemTags.COALS), 3)
        }, new ItemStack(ModItems.STEEL_INGOT.get()), 500);

        addAlloyFurnaceRecipe(output, new AlloyFurnaceRecipe.IngredientWithCount[] {
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_TIN), 1),
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.of(CommonItemTags.SILICON),1),
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.of(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(ModItems.REDSTONE_ALLOY_INGOT.get()), 2500);

        addAlloyFurnaceRecipe(output, new AlloyFurnaceRecipe.IngredientWithCount[] {
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_STEEL), 3),
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.of(Tags.Items.INGOTS_COPPER), 3),
                new AlloyFurnaceRecipe.IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_TIN), 3)
        }, new ItemStack(ModItems.ADVANCED_ALLOY_INGOT.get()), 10000);
    }

    private static void addBlastingAndSmeltingRecipes(RecipeOutput output, ItemLike ingredient, ItemStack result,
                                                      CookingBookCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addBlastingAndSmeltingRecipes(RecipeOutput output, TagKey<Item> ingredient, ItemStack result,
                                                      CookingBookCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addSmeltingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()));

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }

    private static void addSmeltingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }

    private static void addSmeltingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, ResourceLocation recipeId) {
        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefix("recipes/")),
                RecipeSerializer.SMELTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addSmeltingRecipe(RecipeOutput output, TagKey<Item> ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefix("recipes/")),
                RecipeSerializer.SMELTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addBlastingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefix("recipes/")),
                RecipeSerializer.BLASTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addBlastingRecipe(RecipeOutput output, TagKey<Item> ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefix("recipes/")),
                RecipeSerializer.BLASTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addAlloyFurnaceRecipe(RecipeOutput recipeOutput,
                                              AlloyFurnaceRecipe.IngredientWithCount[] inputs,
                                              ItemStack output,
                                              int ticks) {
        addAlloyFurnaceRecipe(recipeOutput, inputs, output,
                new AlloyFurnaceRecipe.OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0]), ticks);
    }

    private static void addAlloyFurnaceRecipe(RecipeOutput recipeOutput,
                                              AlloyFurnaceRecipe.IngredientWithCount[] inputs,
                                              ItemStack output,
                                              AlloyFurnaceRecipe.OutputItemStackWithPercentages secondaryOutput,
                                              int ticks) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "alloy_furnace/" +
                getItemName(output.getItem()));

        AlloyFurnaceFinishedRecipe recipe = new AlloyFurnaceFinishedRecipe(
                recipeId,
                output, secondaryOutput, inputs, ticks
        );
        recipeOutput.accept(recipe);
    }
}
