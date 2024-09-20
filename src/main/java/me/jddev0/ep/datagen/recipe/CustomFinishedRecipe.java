package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

public record CustomFinishedRecipe(
        ResourceLocation id,
        CraftingBookCategory category,
        RecipeSerializer<? extends CustomRecipe> type
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        jsonObject.addProperty("category", category.getSerializedName());
    }

    @Override
    public @Nullable AdvancementHolder advancement() {
        return null;
    }
}
