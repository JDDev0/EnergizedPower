package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.CompressorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.List;

public record CompressorDisplay(CompressorRecipe recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofIngredient(recipe.getInput())
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
