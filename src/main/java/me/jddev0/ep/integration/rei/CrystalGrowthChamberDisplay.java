package me.jddev0.ep.integration.rei;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.CrystalGrowthChamberRecipe;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record CrystalGrowthChamberDisplay(RecipeHolder<CrystalGrowthChamberRecipe> recipe) implements Display {
    public static final CategoryIdentifier<CrystalGrowthChamberDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "crystal_growth_chamber");
    public static final DisplaySerializer<? extends CrystalGrowthChamberDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(Identifier.CODEC.fieldOf("recipeId").forGetter(display -> {
                    return display.recipe.id().identifier();
                }), EPRecipes.CRYSTAL_GROWTH_CHAMBER_SERIALIZER.get().codec().fieldOf("ingredient").forGetter(display -> {
                    return display.recipe.value();
                })).apply(instance, (recipeId, recipe) -> new CrystalGrowthChamberDisplay(new RecipeHolder<>(
                        ResourceKey.create(Registries.RECIPE, recipeId), recipe
                )));
            }),
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    display -> display.recipe.id().identifier(),
                    EPRecipes.CRYSTAL_GROWTH_CHAMBER_SERIALIZER.get().streamCodec(),
                    display -> display.recipe.value(),
                    (recipeId, recipe) -> new CrystalGrowthChamberDisplay(new RecipeHolder<>(
                            ResourceKey.create(Registries.RECIPE, recipeId), recipe
                    ))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofItemStacks(recipe.value().getInput().input().items().
                        map(Holder::unwrap).
                        map(registryKeyItemEither -> registryKeyItemEither.map(
                                l -> new ItemStack(BasicDisplay.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(l)),
                                ItemStack::new
                        )).
                        map(itemStack -> itemStack.copyWithCount(recipe.value().getInput().count())).
                        collect(Collectors.toList()))
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getMaxOutputCount())
        );
    }

    @Override
    public CategoryIdentifier<CrystalGrowthChamberDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(recipe.id().identifier());
    }

    @Override
    public DisplaySerializer<? extends CrystalGrowthChamberDisplay> getSerializer() {
        return SERIALIZER;
    }
}
