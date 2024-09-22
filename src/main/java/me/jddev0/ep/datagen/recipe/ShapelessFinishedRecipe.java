package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public record ShapelessFinishedRecipe(
        Identifier id,
        String group,
        CraftingRecipeCategory category,
        Item result,
        int count,
        DefaultedList<Ingredient> ingredients,
        Advancement.Builder advancement,
        Identifier advancementId
) implements RecipeJsonProvider {
    public ShapelessFinishedRecipe(Identifier id, String group, CraftingRecipeCategory category, ItemStack result,
                                   DefaultedList<Ingredient> ingredients, Advancement.Builder advancement,
                                   Identifier advancementId) {
        this(id, group, category, result.getItem(), result.getCount(), ingredients, advancement, advancementId);
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        if(!group.isEmpty())
            jsonObject.addProperty("group", group);

        jsonObject.addProperty("category", category.asString());

        {
            JsonArray ingredientsJson = new JsonArray();

            for(Ingredient ingredient:ingredients)
                ingredientsJson.add(ingredient.toJson());

            jsonObject.add("ingredients", ingredientsJson);
        }

        {
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("item", Registries.ITEM.getId(result).toString());
            if(count > 1)
                resultJson.addProperty("count", count);

            jsonObject.add("result", resultJson);
        }
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPELESS;
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