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
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record AssemblingMachineDisplay(RecipeEntry<AssemblingMachineRecipe> recipe) implements Display {
    public static final CategoryIdentifier<AssemblingMachineDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "assembling_machine");
    public static final DisplaySerializer<? extends AssemblingMachineDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(Identifier.CODEC.fieldOf("recipeId").forGetter(display -> {
                    return display.recipe.id().getValue();
                }), EPRecipes.ASSEMBLING_MACHINE_SERIALIZER.codec().fieldOf("ingredient").forGetter(display -> {
                    return display.recipe.value();
                })).apply(instance, (recipeId, recipe) -> new AssemblingMachineDisplay(new RecipeEntry<>(
                        RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
                )));
            }),
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC,
                    display -> display.recipe.id().getValue(),
                    EPRecipes.ASSEMBLING_MACHINE_SERIALIZER.packetCodec(),
                    display -> display.recipe.value(),
                    (recipeId, recipe) -> new AssemblingMachineDisplay(new RecipeEntry<>(
                            RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
                    ))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return Arrays.stream(recipe.value().getInputs()).map(input ->
                EntryIngredients.ofItemStacks(input.input().getMatchingItems().stream().
                        map(RegistryEntry::getKeyOrValue).
                        map(registryKeyItemEither -> registryKeyItemEither.map(
                                l -> new ItemStack(BasicDisplay.registryAccess().getOrThrow(RegistryKeys.ITEM).get(l)),
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
        return Optional.of(recipe.id().getValue());
    }

    @Override
    public DisplaySerializer<? extends AssemblingMachineDisplay> getSerializer() {
        return SERIALIZER;
    }
}
