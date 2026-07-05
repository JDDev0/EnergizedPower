package me.jddev0.ep.integration.rei;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record FluidFreezerDisplay(RecipeHolder<FluidFreezerRecipe> recipe) implements Display {
    public static final CategoryIdentifier<FluidFreezerDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "fluid_freezer");
    public static final DisplaySerializer<? extends FluidFreezerDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(Identifier.CODEC.fieldOf("recipeId").forGetter(display -> {
                    return display.recipe.id().identifier();
                }), EPRecipes.FLUID_FREEZER_SERIALIZER.codec().fieldOf("ingredient").forGetter(display -> {
                    return display.recipe.value();
                })).apply(instance, (recipeId, recipe) -> new FluidFreezerDisplay(new RecipeHolder<>(
                        ResourceKey.create(Registries.RECIPE, recipeId), recipe
                )));
            }),
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    display -> display.recipe.id().identifier(),
                    EPRecipes.FLUID_FREEZER_SERIALIZER.streamCodec(),
                    display -> display.recipe.value(),
                    (recipeId, recipe) -> new FluidFreezerDisplay(new RecipeHolder<>(
                            ResourceKey.create(Registries.RECIPE, recipeId), recipe
                    ))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryStack<dev.architectury.fluid.FluidStack>> entryStacks = recipe.value().getInput().map(fluid -> {
            return Collections.singletonList(EntryStacks.of(dev.architectury.fluid.FluidStack.create(fluid.getFluid(),
                    fluid.getDropletsAmount(), fluid.getFluidVariant().getComponentsPatch())));
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
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(recipe.id().identifier());
    }

    @Override
    public DisplaySerializer<? extends FluidFreezerDisplay> getSerializer() {
        return SERIALIZER;
    }
}
