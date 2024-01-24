package me.jddev0.ep.integration.rei;

import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Arrays;
import java.util.List;

public record FiltrationPlantDisplay(FiltrationPlantRecipe recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.of(ModFluids.DIRTY_WATER, FiltrationPlantBlockEntity.DIRTY_WATER_CONSUMPTION_PER_RECIPE)
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Arrays.stream(recipe.getMaxOutputCounts()).filter(itemStack -> !itemStack.isEmpty()).map(EntryIngredients::of).toList();
    }

    @Override
    public CategoryIdentifier<FiltrationPlantDisplay> getCategoryIdentifier() {
        return FiltrationPlantCategory.CATEGORY;
    }
}
