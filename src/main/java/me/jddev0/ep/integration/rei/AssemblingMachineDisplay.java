package me.jddev0.ep.integration.rei;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record AssemblingMachineDisplay(RecipeHolder<AssemblingMachineRecipe> recipe) implements Display {
    public static final CategoryIdentifier<AssemblingMachineDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "assembling_machine");
    public static final DisplaySerializer<? extends AssemblingMachineDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(Identifier.CODEC.fieldOf("recipeId").forGetter(display -> {
                    return display.recipe.id().identifier();
                }), EPRecipes.ASSEMBLING_MACHINE_SERIALIZER.get().codec().fieldOf("ingredient").forGetter(display -> {
                    return display.recipe.value();
                })).apply(instance, (recipeId, recipe) -> new AssemblingMachineDisplay(new RecipeHolder<>(
                        ResourceKey.create(Registries.RECIPE, recipeId), recipe
                )));
            }),
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    display -> display.recipe.id().identifier(),
                    EPRecipes.ASSEMBLING_MACHINE_SERIALIZER.get().streamCodec(),
                    display -> display.recipe.value(),
                    (recipeId, recipe) -> new AssemblingMachineDisplay(new RecipeHolder<>(
                            ResourceKey.create(Registries.RECIPE, recipeId), recipe
                    ))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return Arrays.stream(recipe.value().getInputs()).map(input ->
                EntryIngredients.ofItemStacks(input.input().items().
                        map(Holder::unwrap).
                        map(registryKeyItemEither -> registryKeyItemEither.map(
                                l -> new ItemStack(BasicDisplay.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(l)),
                                ItemStack::new
                        )).
                        map(itemStack -> itemStack.copyWithCount(input.count())).
                        collect(Collectors.toList()))).collect(Collectors.toList());
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getOutput())
        );
    }

    @Override
    public CategoryIdentifier<AssemblingMachineDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(recipe.id().identifier());
    }

    @Override
    public DisplaySerializer<? extends AssemblingMachineDisplay> getSerializer() {
        return SERIALIZER;
    }
}
