package me.jddev0.ep.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public interface EnergizedPowerBaseRecipe<T extends RecipeInput> extends Recipe<T> {
    List<Ingredient> getIngredients();

    boolean isIngredient(ItemStack itemStack);

    boolean isResult(ItemStack itemStack);
}
