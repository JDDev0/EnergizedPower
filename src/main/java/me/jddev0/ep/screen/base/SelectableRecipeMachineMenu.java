package me.jddev0.ep.screen.base;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface SelectableRecipeMachineMenu<R extends Recipe<?>> {
    RecipeHolder<R> getCurrentRecipe();
}
