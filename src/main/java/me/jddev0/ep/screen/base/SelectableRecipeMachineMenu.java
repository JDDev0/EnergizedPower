package me.jddev0.ep.screen.base;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;

public interface SelectableRecipeMachineMenu<R extends Recipe<?>> {
    RecipeEntry<R> getCurrentRecipe();
}
