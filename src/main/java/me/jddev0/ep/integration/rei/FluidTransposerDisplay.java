package me.jddev0.ep.integration.rei;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public record FluidTransposerDisplay(RecipeHolder<FluidTransposerRecipe> recipe) implements Display {
    public static final CategoryIdentifier<FluidTransposerDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "fluid_transposer");
    public static final DisplaySerializer<? extends FluidTransposerDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(Identifier.CODEC.fieldOf("recipeId").forGetter(display -> {
                    return display.recipe.id().identifier();
                }), EPRecipes.FLUID_TRANSPOSER_SERIALIZER.get().codec().fieldOf("ingredient").forGetter(display -> {
                    return display.recipe.value();
                })).apply(instance, (recipeId, recipe) -> new FluidTransposerDisplay(new RecipeHolder<>(
                        ResourceKey.create(Registries.RECIPE, recipeId), recipe
                )));
            }),
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    display -> display.recipe.id().identifier(),
                    EPRecipes.FLUID_TRANSPOSER_SERIALIZER.get().streamCodec(),
                    display -> display.recipe.value(),
                    (recipeId, recipe) -> new FluidTransposerDisplay(new RecipeHolder<>(
                            ResourceKey.create(Registries.RECIPE, recipeId), recipe
                    ))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        FluidStack fluid = recipe.value().getFluid();

        if(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.EMPTYING) {
            return List.of(
                    EntryIngredients.ofIngredient(recipe.value().getInput())
            );
        }else {
            return List.of(
                    EntryIngredients.ofIngredient(recipe.value().getInput()),
                    EntryIngredients.of(dev.architectury.fluid.FluidStack.create(fluid.getFluid(),
                            fluid.getAmount(), fluid.getComponentsPatch()))
            );
        }
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        FluidStack fluid = recipe.value().getFluid();

        if(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.EMPTYING) {
            return List.of(
                    EntryIngredients.of(recipe.value().getOutput()),
                    EntryIngredients.of(dev.architectury.fluid.FluidStack.create(fluid.getFluid(),
                            fluid.getAmount(), fluid.getComponentsPatch()))
            );
        }else {
            return List.of(
                    EntryIngredients.of(recipe.value().getOutput())
            );
        }
    }

    @Override
    public CategoryIdentifier<FluidTransposerDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(recipe.id().identifier());
    }

    @Override
    public DisplaySerializer<? extends FluidTransposerDisplay> getSerializer() {
        return SERIALIZER;
    }
}
