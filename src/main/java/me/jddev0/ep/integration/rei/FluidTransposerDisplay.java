package me.jddev0.ep.integration.rei;

import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import java.util.Collections;
import java.util.List;

public record FluidTransposerDisplay(RecipeHolder<FluidTransposerRecipe> recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        if(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.EMPTYING) {
            return List.of(
                    EntryIngredients.ofIngredient(recipe.value().getInput())
            );
        }else {
            return List.of(
                    EntryIngredients.ofIngredient(recipe.value().getInput()),
                    getFluidEntryIngredient()
            );
        }
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        if(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.EMPTYING) {
            return List.of(
                    EntryIngredients.of(recipe.value().getOutput()),
                    getFluidEntryIngredient()
            );
        }else {
            return List.of(
                    EntryIngredients.of(recipe.value().getOutput())
            );
        }
    }

    private EntryIngredient getFluidEntryIngredient() {
        List<EntryStack<dev.architectury.fluid.FluidStack>> entryStacks = recipe.value().getFluid().map(fluid -> {
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

        return EntryIngredient.of(entryStacks.toArray(EntryStack[]::new));
    }

    @Override
    public CategoryIdentifier<FluidTransposerDisplay> getCategoryIdentifier() {
        return FluidTransposerCategory.CATEGORY;
    }
}
