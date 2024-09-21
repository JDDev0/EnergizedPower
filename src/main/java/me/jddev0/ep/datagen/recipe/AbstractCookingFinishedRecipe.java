package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public record AbstractCookingFinishedRecipe(
        Identifier id,
        String group,
        CookingRecipeCategory category,
        Ingredient ingredient,
        Item result,
        float experience,
        int cookingTime,
        AdvancementEntry advancement,
        RecipeSerializer<? extends AbstractCookingRecipe> serializer
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        if(!group.isEmpty())
            jsonObject.addProperty("group", group);

        jsonObject.addProperty("category", category.asString());
        jsonObject.add("ingredient", ingredient.toJson(false));
        jsonObject.addProperty("result", Registries.ITEM.getId(result).toString());
        jsonObject.addProperty("experience", experience);
        jsonObject.addProperty("cookingtime", cookingTime);
    }
}