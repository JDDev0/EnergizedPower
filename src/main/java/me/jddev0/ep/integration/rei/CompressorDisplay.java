package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.CompressorRecipe;
import me.jddev0.ep.util.ItemStackUtils;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record CompressorDisplay(CompressorRecipe recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofItemStacks(Arrays.stream(recipe.getInput().getItems()).
                        map(itemStack -> ItemStackUtils.copyWithCount(itemStack, recipe.getInputCount())).
                        collect(Collectors.toList()))
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.getOutput())
        );
    }

    @Override
    public CategoryIdentifier<CompressorDisplay> getCategoryIdentifier() {
        return CompressorCategory.CATEGORY;
    }
}
