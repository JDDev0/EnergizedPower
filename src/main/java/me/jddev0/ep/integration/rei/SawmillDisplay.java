package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.SawmillRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.List;

public record SawmillDisplay(SawmillRecipe recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofIngredient(recipe.getInput())
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.getOutput()),
                EntryIngredients.of(recipe.getSecondaryOutput())
        );
    }

    @Override
    public CategoryIdentifier<SawmillDisplay> getCategoryIdentifier() {
        return SawmillCategory.CATEGORY;
    }
}
