package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.util.FluidStackUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record FluidTransposerFinishedRecipe(
        Identifier id,
        FluidTransposerBlockEntity.Mode mode,
        ItemStack output,
        Ingredient input,
        FluidStack fluid
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty("mode", mode.name());
        jsonObject.add("output", ItemStackUtils.toJson(output));
        jsonObject.add("ingredient", input.toJson());
        jsonObject.add("fluid", FluidStackUtils.toJson(fluid));
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FLUID_TRANSPOSER_SERIALIZER;
    }

    @Override
    public @Nullable JsonObject toAdvancementJson() {
        return null;
    }

    @Override
    public @Nullable Identifier getAdvancementId() {
        return null;
    }
}