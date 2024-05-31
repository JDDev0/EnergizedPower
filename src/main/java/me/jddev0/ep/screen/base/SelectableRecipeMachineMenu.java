package me.jddev0.ep.screen.base;

import net.minecraft.world.item.crafting.Recipe;

public interface SelectableRecipeMachineMenu<R extends Recipe<?>> {
    R getCurrentRecipe();
}
