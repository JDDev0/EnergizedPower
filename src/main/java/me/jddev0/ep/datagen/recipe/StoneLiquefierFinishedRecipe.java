package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.util.FluidStackUtils;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record StoneLiquefierFinishedRecipe(
        Identifier id,
        Ingredient ingredient,
        FluidStack result
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("ingredient", ingredient.toJson());
        jsonObject.add("result", FluidStackUtils.toJson(result));
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EPRecipes.STONE_LIQUEFIER_SERIALIZER;
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
