package me.jddev0.ep.screen.base;

import net.minecraft.recipe.Recipe;

public interface ISelectableRecipeMachineMenu<R extends Recipe<?>> {
    R getCurrentRecipe();
}
