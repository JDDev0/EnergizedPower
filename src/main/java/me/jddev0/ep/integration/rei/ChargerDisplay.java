package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.ChargerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.RecipeEntry;

import java.util.List;

public record ChargerDisplay(RecipeEntry<ChargerRecipe> recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofIngredient(recipe.value().getInputItem())
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getOutputItem())
        );
    }

    @Override
    public CategoryIdentifier<ChargerDisplay> getCategoryIdentifier() {
        return ChargerCategory.CATEGORY;
    }
}
