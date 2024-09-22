package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record CustomFinishedRecipe(
        Identifier id,
        RecipeSerializer<? extends SpecialCraftingRecipe> serializer
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {}

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
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