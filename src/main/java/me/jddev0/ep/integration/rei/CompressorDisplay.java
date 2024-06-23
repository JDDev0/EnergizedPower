package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.CompressorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record CompressorDisplay(RecipeHolder<CompressorRecipe> recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofItemStacks(Arrays.stream(recipe.value().getInput().getItems()).
                        map(itemStack -> itemStack.copyWithCount(recipe.value().getInputCount())).
                        collect(Collectors.toList()))
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getOutput())
        );
    }

    @Override
    public CategoryIdentifier<CompressorDisplay> getCategoryIdentifier() {
        return CompressorCategory.CATEGORY;
    }
}
