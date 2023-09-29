package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.RecipeEntry;

import java.util.Arrays;
import java.util.List;

public record PlantGrowthChamberDisplay(RecipeEntry<PlantGrowthChamberRecipe> recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofIngredient(recipe.value().getInput())
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Arrays.stream(recipe.value().getMaxOutputCounts()).map(EntryIngredients::of).toList();
    }

    @Override
    public CategoryIdentifier<PlantGrowthChamberDisplay> getCategoryIdentifier() {
        return PlantGrowthChamberCategory.CATEGORY;
    }
}
