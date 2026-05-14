package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Optional;

public record FluidFreezerDisplay(RecipeHolder<FluidFreezerRecipe> recipe) implements Display {
    public static final CategoryIdentifier<FluidFreezerDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "fluid_freezer");

    @Override
    public List<EntryIngredient> getInputEntries() {
        FluidStack fluid = recipe.value().getInput();

        return List.of(
                EntryIngredients.of(dev.architectury.fluid.FluidStack.create(fluid.getFluid(),
                        fluid.getDropletsAmount(), fluid.getFluidVariant().getComponents()))
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getOutput())
        );
    }

    @Override
    public CategoryIdentifier<FluidFreezerDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.of(recipe.id());
    }
}
