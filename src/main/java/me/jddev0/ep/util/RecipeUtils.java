package me.jddev0.ep.util;

import me.jddev0.ep.recipe.EnergizedPowerBaseRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.util.Collection;
import java.util.List;

public final class RecipeUtils {
    private RecipeUtils() {}

    public static <C extends RecipeInput, T extends Recipe<C>> Collection<RecipeHolder<T>> getAllRecipesFor(ServerLevel level, RecipeType<T> recipeType) {
        return level.recipeAccess().recipes.byType(recipeType);
    }

    public static <C extends RecipeInput, T extends Recipe<C>> boolean isIngredientOfAny(ServerLevel level, RecipeType<T> recipeType, ItemStack itemStack) {
        Collection<RecipeHolder<T>> recipes = getAllRecipesFor(level, recipeType);

        return recipes.stream().map(RecipeHolder::value).anyMatch(recipe -> {
            if(recipe instanceof EnergizedPowerBaseRecipe<?> epRecipe)
                return epRecipe.isIngredient(itemStack);

            return recipe.placementInfo().ingredients().stream().
                    anyMatch(ingredient -> ingredient.test(itemStack));
        });
    }

    public static boolean isIngredientOfAny(List<Ingredient> ingredientList, ItemStack itemStack) {
        return ingredientList.stream().anyMatch(ingredient -> ingredient.test(itemStack));
    }

    public static <C extends RecipeInput, T extends Recipe<C>> List<Ingredient> getIngredientsOf(ServerLevel level, RecipeType<T> recipeType) {
        Collection<RecipeHolder<T>> recipes = getAllRecipesFor(level, recipeType);

        return recipes.stream().map(RecipeHolder::value).flatMap(recipe -> {
            if(recipe instanceof EnergizedPowerBaseRecipe<?> epRecipe)
                return epRecipe.getIngredients().stream();

            return recipe.placementInfo().ingredients().stream();
        }).toList();
    }

    public static <C extends RecipeInput, T extends Recipe<C>> boolean isResultOfAny(ServerLevel level, RecipeType<T> recipeType, ItemStack itemStack) {
        Collection<RecipeHolder<T>> recipes = getAllRecipesFor(level, recipeType);

        return recipes.stream().map(RecipeHolder::value).anyMatch(recipe -> {
            if(recipe instanceof EnergizedPowerBaseRecipe<?> epRecipe)
                return epRecipe.isResult(itemStack);

            return recipe.display().stream().
                    filter(recipeDisplay -> recipeDisplay.isEnabled(level.enabledFeatures())).
                    map(RecipeDisplay::result).
                    flatMap(result -> result.resolveForStacks(SlotDisplayContext.fromLevel(level)).stream()).
                    anyMatch(stack -> ItemStack.isSameItemSameComponents(stack, itemStack));
        });
    }
}
