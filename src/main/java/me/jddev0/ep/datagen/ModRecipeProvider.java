package me.jddev0.ep.datagen;

import net.minecraft.data.DataGenerator;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.datagen.recipe.AbstractCookingFinishedRecipe;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.Objects;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(DataGenerator output) {
        super(output);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> output) {
        buildCookingRecipes(output);
    }

    private void buildCookingRecipes(Consumer<FinishedRecipe> output) {
        addBlastingAndSmeltingRecipes(output, CommonItemTags.RAW_MATERIALS_TIN, new ItemStack(ModItems.TIN_INGOT.get()),
                100, .7f, "tin_ingot", "raw_tin");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.ORES_TIN, new ItemStack(ModItems.TIN_INGOT.get()),
                100, .7f, "tin_ingot", "tin_ores");

        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_TIN, new ItemStack(ModItems.TIN_INGOT.get()),
                100, .7f, "tin_ingot", "tin_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_COPPER, new ItemStack(Items.COPPER_INGOT),
                100, .7f, "copper_ingot", "copper_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_IRON, new ItemStack(Items.IRON_INGOT),
                100, .7f, "iron_ingot", "iron_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_GOLD, new ItemStack(Items.GOLD_INGOT),
                100, .7f, "gold_ingot", "gold_dust");

        addBlastingAndSmeltingRecipes(output, ModItems.IRON_HAMMER.get(), new ItemStack(Items.IRON_NUGGET),
                100, .1f, "iron_nugget", "iron_hammer");
        addBlastingAndSmeltingRecipes(output, ModItems.GOLDEN_HAMMER.get(), new ItemStack(Items.GOLD_NUGGET),
                100, .1f, "gold_nugget", "golden_hammer");

        addBlastingAndSmeltingRecipes(output, Tags.Items.GEMS_QUARTZ, new ItemStack(ModItems.SILICON.get()),
                250, 4.1f, "silicon", "nether_quartz");

        addSmeltingRecipe(output, ModBlocks.SAWDUST_BLOCK_ITEM.get(), new ItemStack(Items.CHARCOAL),
                200, .15f, null, "sawdust_block");

        addSmeltingRecipe(output, ModItems.RAW_GEAR_PRESS_MOLD.get(), new ItemStack(ModItems.GEAR_PRESS_MOLD.get()),
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_ROD_PRESS_MOLD.get(), new ItemStack(ModItems.ROD_PRESS_MOLD.get()),
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_WIRE_PRESS_MOLD.get(), new ItemStack(ModItems.WIRE_PRESS_MOLD.get()),
                200, .3f, null);
    }

    private static void addBlastingAndSmeltingRecipes(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result,
                                                      int time, float xp, String group, String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addBlastingAndSmeltingRecipes(Consumer<FinishedRecipe> output, TagKey<Item> ingredient, ItemStack result,
                                                      int time, float xp, String group, String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result,
                                          int time, float xp, String group) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()));

        addSmeltingRecipe(output, ingredient, result, time, xp, group, recipeId);
    }

    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(output, ingredient, result, time, xp, group, recipeId);
    }

    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result,
                                          int time, float xp, String group, ResourceLocation recipeId) {
        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefix(recipeId, "recipes/"),
                RecipeSerializer.SMELTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefix(recipeId, "recipes/"),
                RecipeSerializer.SMELTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addBlastingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefix(recipeId, "recipes/"),
                RecipeSerializer.BLASTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addBlastingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefix(recipeId, "recipes/"),
                RecipeSerializer.BLASTING_RECIPE
        );
        output.accept(recipe);
    }

    public static ResourceLocation withPrefix(ResourceLocation resourceLocation, String pathPrefix) {
        return new ResourceLocation(resourceLocation.getNamespace(), pathPrefix + resourceLocation.getPath());
    }
}
