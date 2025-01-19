package me.jddev0.ep.integration.rei;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.EnergizerRecipe;
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

import java.util.List;
import java.util.Optional;

public record EnergizerDisplay(RecipeEntry<EnergizerRecipe> recipe) implements Display {
    public static final CategoryIdentifier<EnergizerDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "energizer");
    public static final DisplaySerializer<? extends EnergizerDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(Identifier.CODEC.fieldOf("recipeId").forGetter(display -> {
                    return display.recipe.id().getValue();
                }), EPRecipes.ENERGIZER_SERIALIZER.codec().fieldOf("ingredient").forGetter(display -> {
                    return display.recipe.value();
                })).apply(instance, (recipeId, recipe) -> new EnergizerDisplay(new RecipeEntry<>(
                        RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
                )));
            }),
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC,
                    display -> display.recipe.id().getValue(),
                    EPRecipes.ENERGIZER_SERIALIZER.packetCodec(),
                    display -> display.recipe.value(),
                    (recipeId, recipe) -> new EnergizerDisplay(new RecipeEntry<>(
                            RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
                    ))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofIngredient(recipe.value().getInputItem())
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getOutputItem())
        );
    }

    @Override
    public CategoryIdentifier<EnergizerDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(recipe.id().getValue());
    }

    @Override
    public DisplaySerializer<? extends EnergizerDisplay> getSerializer() {
        return SERIALIZER;
    }
}
