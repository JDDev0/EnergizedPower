package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.PlantGrowthChamberFertilizerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public record PlantGrowthChamberFertilizerDisplay(RecipeHolder<PlantGrowthChamberFertilizerRecipe> recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofIngredient(recipe.value().getInput())
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of();
    }

    @Override
    public CategoryIdentifier<PlantGrowthChamberFertilizerDisplay> getCategoryIdentifier() {
        return PlantGrowthChamberFertilizerCategory.CATEGORY;
    }
}
