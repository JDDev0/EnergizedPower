package me.jddev0.ep.integration.rei;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
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

public record FluidTransposerDisplay(RecipeEntry<FluidTransposerRecipe> recipe) implements Display {
    public static final CategoryIdentifier<FluidTransposerDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "fluid_transposer");
    public static final DisplaySerializer<? extends FluidTransposerDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(Identifier.CODEC.fieldOf("recipeId").forGetter(display -> {
                    return display.recipe.id().getValue();
                }), EPRecipes.FLUID_TRANSPOSER_SERIALIZER.codec().fieldOf("ingredient").forGetter(display -> {
                    return display.recipe.value();
                })).apply(instance, (recipeId, recipe) -> new FluidTransposerDisplay(new RecipeEntry<>(
                        RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
                )));
            }),
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC,
                    display -> display.recipe.id().getValue(),
                    EPRecipes.FLUID_TRANSPOSER_SERIALIZER.packetCodec(),
                    display -> display.recipe.value(),
                    (recipeId, recipe) -> new FluidTransposerDisplay(new RecipeEntry<>(
                            RegistryKey.of(RegistryKeys.RECIPE, recipeId), recipe
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
                            fluid.getDropletsAmount(), fluid.getFluidVariant().getComponents()))
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
                            fluid.getDropletsAmount(), fluid.getFluidVariant().getComponents()))
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
        return Optional.of(recipe.id().getValue());
    }

    @Override
    public DisplaySerializer<? extends FluidTransposerDisplay> getSerializer() {
        return SERIALIZER;
    }
}
