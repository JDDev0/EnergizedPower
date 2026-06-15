package me.jddev0.ep.integration.rei;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.PulverizerRecipe;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record PulverizerDisplay(RecipeHolder<PulverizerRecipe> recipe) implements Display {
    public static final CategoryIdentifier<PulverizerDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "pulverizer");
    public static final DisplaySerializer<? extends PulverizerDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(Identifier.CODEC.fieldOf("recipeId").forGetter(display -> {
                    return display.recipe.id().identifier();
                }), EPRecipes.PULVERIZER_SERIALIZER.get().codec().fieldOf("ingredient").forGetter(display -> {
                    return display.recipe.value();
                })).apply(instance, (recipeId, recipe) -> new PulverizerDisplay(new RecipeHolder<>(
                        ResourceKey.create(Registries.RECIPE, recipeId), recipe
                )));
            }),
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    display -> display.recipe.id().identifier(),
                    EPRecipes.PULVERIZER_SERIALIZER.get().streamCodec(),
                    display -> display.recipe.value(),
                    (recipeId, recipe) -> new PulverizerDisplay(new RecipeHolder<>(
                            ResourceKey.create(Registries.RECIPE, recipeId), recipe
                    ))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofIngredient(recipe.value().getInput())
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Arrays.stream(recipe.value().getMaxOutputCounts(false)).filter(itemStack -> !itemStack.isEmpty()).map(EntryIngredients::of).toList();
    }

    @Override
    public CategoryIdentifier<PulverizerDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(recipe.id().identifier());
    }

    @Override
    public DisplaySerializer<? extends PulverizerDisplay> getSerializer() {
        return SERIALIZER;
    }
}
