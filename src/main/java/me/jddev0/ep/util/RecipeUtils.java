package me.jddev0.ep.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

import java.util.List;

public final class RecipeUtils {
    private RecipeUtils() {}

    public static <C extends Inventory, T extends Recipe<C>> boolean isIngredientOfAny(World level, RecipeType<T> recipeType, ItemStack itemStack) {
        List<T> recipes = level.getRecipeManager().listAllOfType(recipeType);

        return recipes.stream().map(Recipe::getIngredients).
                anyMatch(ingredients -> ingredients.stream().anyMatch(ingredient -> ingredient.test(itemStack)));
    }

    public static <C extends Inventory, T extends Recipe<C>> boolean isResultOfAny(World level, RecipeType<T> recipeType, ItemStack itemStack) {
        List<T> recipes = level.getRecipeManager().listAllOfType(recipeType);

        return recipes.stream().map(Recipe::getOutput).anyMatch(stack -> ItemStack.areEqual(stack, itemStack));
    }

    public static <C extends Inventory, T extends Recipe<C>> boolean isRemainderOfAny(World level, RecipeType<T> recipeType, C container, ItemStack itemStack) {
        List<T> recipes = level.getRecipeManager().listAllOfType(recipeType);

        return recipes.stream().map(recipe -> recipe.getRemainder(container)).
                anyMatch(remainingItems -> remainingItems.stream().anyMatch(item -> ItemStack.areEqual(item, itemStack)));
    }
}
