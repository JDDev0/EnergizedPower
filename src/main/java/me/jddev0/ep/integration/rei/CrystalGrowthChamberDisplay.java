package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.CrystalGrowthChamberRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record CrystalGrowthChamberDisplay(CrystalGrowthChamberRecipe recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofItemStacks(Arrays.stream(recipe.getInput().getItems()).
                        map(itemStack -> itemStack.copyWithCount(recipe.getInputCount())).
                        collect(Collectors.toList()))
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.getMaxOutputCount())
        );
    }

    @Override
    public CategoryIdentifier<CrystalGrowthChamberDisplay> getCategoryIdentifier() {
        return CrystalGrowthChamberCategory.CATEGORY;
    }
}
