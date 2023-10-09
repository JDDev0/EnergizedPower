package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.MetalPressRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record MetalPressDisplay(MetalPressRecipe recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.of(recipe.getPressMold()),
                EntryIngredients.ofItemStacks(Arrays.stream(recipe.getInput().getMatchingStacks()).
                        map(itemStack -> itemStack.copyWithCount(recipe.getInputCount())).
                        collect(Collectors.toList()))
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.getOutputItem())
        );
    }

    @Override
    public CategoryIdentifier<MetalPressDisplay> getCategoryIdentifier() {
        return MetalPressCategory.CATEGORY;
    }
}
