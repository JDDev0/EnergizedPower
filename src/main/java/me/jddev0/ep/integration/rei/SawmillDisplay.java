package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.SawmillRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.RecipeEntry;

import java.util.List;

public record SawmillDisplay(RecipeEntry<SawmillRecipe> recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofIngredient(recipe.value().getInputItem())
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getOutputItem()),
                EntryIngredients.of(recipe.value().getSecondaryOutput())
        );
    }

    @Override
    public CategoryIdentifier<SawmillDisplay> getCategoryIdentifier() {
        return SawmillCategory.CATEGORY;
    }
}
