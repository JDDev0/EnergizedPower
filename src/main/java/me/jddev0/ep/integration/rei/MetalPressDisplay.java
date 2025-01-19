package me.jddev0.ep.integration.rei;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.MetalPressRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record MetalPressDisplay(RecipeEntry<MetalPressRecipe> recipe) implements Display {
    public static final CategoryIdentifier<MetalPressDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "metal_press");
    public static final DisplaySerializer<? extends MetalPressDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(Identifier.CODEC.fieldOf("recipeId").forGetter(display -> {
                    return display.recipe.id().getValue();
                }), EPRecipes.METAL_PRESS_SERIALIZER.codec().fieldOf("ingredient").forGetter(display -> {
                    return display.recipe.value();
                })).apply(instance, (recipeId, recipe) -> new MetalPressDisplay(new RecipeEntry<>(
                        RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
                )));
            }),
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC,
                    display -> display.recipe.id().getValue(),
                    EPRecipes.METAL_PRESS_SERIALIZER.packetCodec(),
                    display -> display.recipe.value(),
                    (recipeId, recipe) -> new MetalPressDisplay(new RecipeEntry<>(
                            RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
                    ))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getPressMold()),
                EntryIngredients.ofItemStacks(recipe.value().getInput().input().getMatchingItems().stream().
                        map(RegistryEntry::getKeyOrValue).
                        map(registryKeyItemEither -> registryKeyItemEither.map(
                                l -> new ItemStack(BasicDisplay.registryAccess().getOrThrow(RegistryKeys.ITEM).get(l)),
                                ItemStack::new
                        )).
                        map(itemStack -> itemStack.copyWithCount(recipe.value().getInput().count())).
                        collect(Collectors.toList()))
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getOutput())
        );
    }

    @Override
    public CategoryIdentifier<MetalPressDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(recipe.id().getValue());
    }

    @Override
    public DisplaySerializer<? extends MetalPressDisplay> getSerializer() {
        return SERIALIZER;
    }
}
