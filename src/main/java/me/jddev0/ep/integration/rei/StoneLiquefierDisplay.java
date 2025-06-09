package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.StoneLiquefierRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public record StoneLiquefierDisplay(StoneLiquefierRecipe recipe) implements Display {
    public static final CategoryIdentifier<StoneLiquefierDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "stone_liquefier");

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofIngredient(recipe.getInput())
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        FluidStack output = recipe.getOutput();

        return List.of(
                EntryIngredients.of(dev.architectury.fluid.FluidStack.create(output.getFluid(),
                        output.getAmount(), output.getTag()))
        );
    }

    @Override
    public CategoryIdentifier<StoneLiquefierDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.of(recipe.getId());
    }
}
