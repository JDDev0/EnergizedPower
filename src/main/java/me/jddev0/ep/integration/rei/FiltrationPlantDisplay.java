package me.jddev0.ep.integration.rei;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
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

public record FiltrationPlantDisplay(RecipeEntry<FiltrationPlantRecipe> recipe) implements Display {
    public static final CategoryIdentifier<FiltrationPlantDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "filtration_plant");
    public static final DisplaySerializer<? extends FiltrationPlantDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(Identifier.CODEC.fieldOf("recipeId").forGetter(display -> {
                    return display.recipe.id().getValue();
                }), EPRecipes.FILTRATION_PLANT_SERIALIZER.codec().fieldOf("ingredient").forGetter(display -> {
                    return display.recipe.value();
                })).apply(instance, (recipeId, recipe) -> new FiltrationPlantDisplay(new RecipeEntry<>(
                        RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
                )));
            }),
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC,
                    display -> display.recipe.id().getValue(),
                    EPRecipes.FILTRATION_PLANT_SERIALIZER.packetCodec(),
                    display -> display.recipe.value(),
                    (recipeId, recipe) -> new FiltrationPlantDisplay(new RecipeEntry<>(
                            RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
                    ))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.of(EPFluids.DIRTY_WATER, FiltrationPlantBlockEntity.DIRTY_WATER_CONSUMPTION_PER_RECIPE)
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Arrays.stream(recipe.value().getMaxOutputCounts()).filter(itemStack -> !itemStack.isEmpty()).map(EntryIngredients::of).toList();
    }

    @Override
    public CategoryIdentifier<FiltrationPlantDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(recipe.id().getValue());
    }

    @Override
    public DisplaySerializer<? extends FiltrationPlantDisplay> getSerializer() {
        return SERIALIZER;
    }
}
