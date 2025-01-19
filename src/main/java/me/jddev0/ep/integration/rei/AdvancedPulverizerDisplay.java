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
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record AdvancedPulverizerDisplay(RecipeEntry<PulverizerRecipe> recipe) implements Display {
    public static final CategoryIdentifier<AdvancedPulverizerDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "advanced_pulverizer");
    public static final DisplaySerializer<? extends AdvancedPulverizerDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(Identifier.CODEC.fieldOf("recipeId").forGetter(display -> {
                    return display.recipe.id().getValue();
                }), EPRecipes.PULVERIZER_SERIALIZER.codec().fieldOf("ingredient").forGetter(display -> {
                    return display.recipe.value();
                })).apply(instance, (recipeId, recipe) -> new AdvancedPulverizerDisplay(new RecipeEntry<>(
                        RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
                )));
            }),
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC,
                    display -> display.recipe.id().getValue(),
                    EPRecipes.PULVERIZER_SERIALIZER.packetCodec(),
                    display -> display.recipe.value(),
                    (recipeId, recipe) -> new AdvancedPulverizerDisplay(new RecipeEntry<>(
                            RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
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
        return Arrays.stream(recipe.value().getMaxOutputCounts(true)).filter(itemStack -> !itemStack.isEmpty()).map(EntryIngredients::of).toList();
    }

    @Override
    public CategoryIdentifier<AdvancedPulverizerDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(recipe.id().getValue());
    }

    @Override
    public DisplaySerializer<? extends AdvancedPulverizerDisplay> getSerializer() {
        return SERIALIZER;
    }
}
