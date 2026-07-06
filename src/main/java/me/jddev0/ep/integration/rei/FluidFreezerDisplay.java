package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record FluidFreezerDisplay(RecipeHolder<FluidFreezerRecipe> recipe) implements Display {
    public static final CategoryIdentifier<FluidFreezerDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "fluid_freezer");

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryStack<dev.architectury.fluid.FluidStack>> entryStacks = recipe.value().getInput().map(fluid -> {
            return Collections.singletonList(EntryStacks.of(dev.architectury.fluid.FluidStack.create(fluid.getFluid(),
                    fluid.getDropletsAmount(), fluid.getFluidVariant().getComponents())));
        }, f -> {
            long amount = f.dropletsAmount();
            List<Fluid> fluids = f.fluid().getFluid().map(
                    fluid -> fluid,
                    fluid -> BasicDisplay.registryAccess().lookupOrThrow(BuiltInRegistries.FLUID.key()).
                            getOrThrow(fluid).stream().map(Holder::value).toList()
            );

            return fluids.stream().map(fluid -> EntryStacks.of(dev.architectury.fluid.FluidStack.create(fluid, amount))).toList();
        });

        return List.of(EntryIngredient.of(entryStacks.toArray(EntryStack[]::new)));
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
