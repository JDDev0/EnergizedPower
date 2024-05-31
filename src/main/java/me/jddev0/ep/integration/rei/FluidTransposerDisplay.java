package me.jddev0.ep.integration.rei;

import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public record FluidTransposerDisplay(FluidTransposerRecipe recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        FluidStack fluid = recipe.getFluid();

        if(recipe.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING) {
            return List.of(
                    EntryIngredients.ofIngredient(recipe.getInput())
            );
        }else {
            return List.of(
                    EntryIngredients.ofIngredient(recipe.getInput()),
                    EntryIngredients.of(dev.architectury.fluid.FluidStack.create(fluid.getFluid(),
                            fluid.getAmount(), fluid.getTag()))
            );
        }
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        FluidStack fluid = recipe.getFluid();

        if(recipe.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING) {
            return List.of(
                    EntryIngredients.of(recipe.getOutput()),
                    EntryIngredients.of(dev.architectury.fluid.FluidStack.create(fluid.getFluid(),
                            fluid.getAmount(), fluid.getTag()))
            );
        }else {
            return List.of(
                    EntryIngredients.of(recipe.getOutput())
            );
        }
    }

    @Override
    public CategoryIdentifier<FluidTransposerDisplay> getCategoryIdentifier() {
        return FluidTransposerCategory.CATEGORY;
    }
}
