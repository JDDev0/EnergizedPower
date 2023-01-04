package me.jddev0.ep.util;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public final class RecipeUtils {
    private RecipeUtils() {}

    public static <C extends Container, T extends Recipe<C>> boolean isIngredientOfAny(Level level, RecipeType<T> recipeType, ItemStack itemStack) {
        List<T> recipes = level.getRecipeManager().getAllRecipesFor(recipeType);

        return recipes.stream().map(Recipe::getIngredients).
                anyMatch(ingredients -> ingredients.stream().anyMatch(ingredient -> ingredient.test(itemStack)));
    }

    public static <C extends Container, T extends Recipe<C>> boolean isResultOfAny(Level level, RecipeType<T> recipeType, ItemStack itemStack) {
        List<T> recipes = level.getRecipeManager().getAllRecipesFor(recipeType);

        return recipes.stream().map(Recipe::getResultItem).anyMatch(stack -> ItemStack.isSameItemSameTags(stack, itemStack));
    }

    public static <C extends Container, T extends Recipe<C>> boolean isRemainderOfAny(Level level, RecipeType<T> recipeType, C container, ItemStack itemStack) {
        List<T> recipes = level.getRecipeManager().getAllRecipesFor(recipeType);

        return recipes.stream().map(recipe -> recipe.getRemainingItems(container)).
                anyMatch(remainingItems -> remainingItems.stream().anyMatch(item -> ItemStack.isSameItemSameTags(item, itemStack)));
    }
}
