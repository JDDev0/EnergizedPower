package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public record StoneSolidifierDisplay(RecipeHolder<StoneSolidifierRecipe> recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.of(Fluids.WATER, recipe.value().getWaterAmount()),
                EntryIngredients.of(Fluids.LAVA, recipe.value().getLavaAmount())
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getOutput())
        );
    }

    @Override
    public CategoryIdentifier<StoneSolidifierDisplay> getCategoryIdentifier() {
        return StoneSolidifierCategory.CATEGORY;
    }
}
