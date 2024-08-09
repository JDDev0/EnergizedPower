package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public record AbstractCookingFinishedRecipe(
        Identifier id,
        String group,
        Ingredient ingredient,
        Item result,
        float experience,
        int cookingTime,
        Advancement.Builder advancement,
        Identifier advancementId,
        RecipeSerializer<? extends AbstractCookingRecipe> serializer
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        if(!group.isEmpty())
            jsonObject.addProperty("group", group);

        jsonObject.add("ingredient", ingredient.toJson());
        jsonObject.addProperty("result", Registry.ITEM.getId(result).toString());
        jsonObject.addProperty("experience", experience);
        jsonObject.addProperty("cookingtime", cookingTime);
    }

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
        return advancement.toJson();
    }

    @Override
    public @Nullable Identifier getAdvancementId() {
        return advancementId;
    }
}