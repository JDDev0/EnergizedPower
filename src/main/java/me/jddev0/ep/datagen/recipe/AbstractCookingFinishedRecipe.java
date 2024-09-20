package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public record AbstractCookingFinishedRecipe(
        ResourceLocation id,
        String group,
        CookingBookCategory category,
        Ingredient ingredient,
        Item result,
        float experience,
        int cookingTime,
        AdvancementHolder advancement,
        RecipeSerializer<? extends AbstractCookingRecipe> type
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        if(!group.isEmpty())
            jsonObject.addProperty("group", group);

        jsonObject.addProperty("category", category.getSerializedName());
        jsonObject.add("ingredient", ingredient.toJson(false));
        jsonObject.addProperty("result", ForgeRegistries.ITEMS.getKey(result).toString());
        jsonObject.addProperty("experience", experience);
        jsonObject.addProperty("cookingtime", cookingTime);
    }
}
