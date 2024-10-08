package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record CrusherFinishedRecipe(
        Identifier id,
        ItemStack output,
        Ingredient input
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("output", ItemStackUtils.toJson(output));
        jsonObject.add("ingredient", input.toJson());
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EPRecipes.CRUSHER_SERIALIZER;
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