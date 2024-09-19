package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public record ShapelessFinishedRecipe(
        ResourceLocation id,
        String group,
        CraftingBookCategory category,
        Item result,
        int count,
        NonNullList<Ingredient> ingredients,
        AdvancementHolder advancement
) implements FinishedRecipe {
    public ShapelessFinishedRecipe(ResourceLocation id, String group, CraftingBookCategory category, ItemStack result,
                                   NonNullList<Ingredient> ingredients, AdvancementHolder advancement) {
        this(id, group, category, result.getItem(), result.getCount(), ingredients, advancement);
    }

    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        if(!group.isEmpty())
            jsonObject.addProperty("group", group);

        jsonObject.addProperty("category", category.getSerializedName());

        {
            JsonArray ingredientsJson = new JsonArray();

            for(Ingredient ingredient:ingredients)
                ingredientsJson.add(ingredient.toJson(false));

            jsonObject.add("ingredients", ingredientsJson);
        }

        {
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("item", BuiltInRegistries.ITEM.getKey(result).toString());
            if(count > 1)
                resultJson.addProperty("count", count);

            jsonObject.add("result", resultJson);
        }
    }

    @Override
    public RecipeSerializer<?> type() {
        return RecipeSerializer.SHAPELESS_RECIPE;
    }
}
