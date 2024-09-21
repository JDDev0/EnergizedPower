package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record CustomFinishedRecipe(
        Identifier id,
        CraftingRecipeCategory category,
        RecipeSerializer<? extends SpecialCraftingRecipe> serializer
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty("category", category.asString());
    }

    @Override
    public @Nullable AdvancementEntry advancement() {
        return null;
    }
}