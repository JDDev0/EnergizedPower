package me.jddev0.ep.screen.base;

import net.minecraft.recipe.Recipe;

public interface SelectableRecipeMachineMenu<R extends Recipe<?>> {
    R getCurrentRecipe();
}
