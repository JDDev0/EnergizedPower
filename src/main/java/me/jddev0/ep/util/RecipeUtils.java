package me.jddev0.ep.util;

import me.jddev0.ep.recipe.EnergizedPowerBaseRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.server.world.ServerWorld;

import java.util.Collection;
import java.util.List;

public final class RecipeUtils {
    private RecipeUtils() {}

    public static <C extends RecipeInput, T extends Recipe<C>> Collection<RecipeEntry<T>> getAllRecipesFor(ServerWorld level, RecipeType<T> recipeType) {
        return level.getRecipeManager().getAllOfType(recipeType);
    }

    public static <C extends RecipeInput, T extends Recipe<C>> boolean isIngredientOfAny(ServerWorld level, RecipeType<T> recipeType, ItemStack itemStack) {
        Collection<RecipeEntry<T>> recipes = getAllRecipesFor(level, recipeType);

        return recipes.stream().map(RecipeEntry::value).anyMatch(recipe -> {
            if(recipe instanceof EnergizedPowerBaseRecipe<?> epRecipe)
                return epRecipe.isIngredient(itemStack);

            return recipe.getIngredientPlacement().getIngredients().stream().
                    anyMatch(ingredient -> ingredient.test(itemStack));
        });
    }

    public static boolean isIngredientOfAny(List<Ingredient> ingredientList, ItemStack itemStack) {
        return ingredientList.stream().anyMatch(ingredient -> ingredient.test(itemStack));
    }

    public static <C extends RecipeInput, T extends Recipe<C>> List<Ingredient> getIngredientsOf(ServerWorld level, RecipeType<T> recipeType) {
        Collection<RecipeEntry<T>> recipes = getAllRecipesFor(level, recipeType);

        return recipes.stream().map(RecipeEntry::value).flatMap(recipe -> {
            if(recipe instanceof EnergizedPowerBaseRecipe<?> epRecipe)
                return epRecipe.getIngredients().stream();

            return recipe.getIngredientPlacement().getIngredients().stream();
        }).toList();
    }

    public static <C extends RecipeInput, T extends Recipe<C>> boolean isResultOfAny(ServerWorld level, RecipeType<T> recipeType, ItemStack itemStack) {
        Collection<RecipeEntry<T>> recipes = getAllRecipesFor(level, recipeType);

        return recipes.stream().map(RecipeEntry::value).anyMatch(recipe -> {
            if(recipe instanceof EnergizedPowerBaseRecipe<?> epRecipe)
                return epRecipe.isResult(itemStack);

            return recipe.getDisplays().stream().
                    filter(recipeDisplay -> recipeDisplay.isEnabled(level.getEnabledFeatures())).
                    map(RecipeDisplay::result).
                    flatMap(result -> result.getStacks(SlotDisplayContexts.createParameters(level)).stream()).
                    anyMatch(stack -> ItemStack.areItemsAndComponentsEqual(stack, itemStack));
        });
    }
}
