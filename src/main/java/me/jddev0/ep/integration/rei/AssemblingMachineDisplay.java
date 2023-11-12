package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record AssemblingMachineDisplay(AssemblingMachineRecipe recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return Arrays.stream(recipe.getInputs()).map(input ->
                EntryIngredients.ofItemStacks(Arrays.stream(input.input().getItems()).
                        map(itemStack -> itemStack.copyWithCount(input.count())).
                        collect(Collectors.toList()))).collect(Collectors.toList());
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.getOutput())
        );
    }

    @Override
    public CategoryIdentifier<AssemblingMachineDisplay> getCategoryIdentifier() {
        return AssemblingMachineCategory.CATEGORY;
    }
}
