package me.jddev0.ep.datagen.recipe;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;

public record ShapedFinishedRecipe(
        Identifier id,
        String group,
        CraftingRecipeCategory category,
        Map<Character, Ingredient> key,
        String[] pattern,
        Item result,
        int count,
        AdvancementEntry advancement
) implements RecipeJsonProvider {
    public ShapedFinishedRecipe(Identifier id, String group, CraftingRecipeCategory category, Map<Character, Ingredient> key,
                                String[] pattern, ItemStack result, AdvancementEntry advancement) {
        this(id, group, category, key, pattern, result.getItem(), result.getCount(), advancement);
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        verifyData();

        if(!group.isEmpty())
            jsonObject.addProperty("group", group);

        jsonObject.addProperty("category", category.asString());

        {
            JsonArray patternJson = new JsonArray();
            for(String str:pattern)
                patternJson.add(str);

            jsonObject.add("pattern", patternJson);
        }

        {
            JsonObject keyJson = new JsonObject();
            for(Map.Entry<Character, Ingredient> entry:key.entrySet())
                keyJson.add("" + entry.getKey(), entry.getValue().toJson(false));

            jsonObject.add("key", keyJson);
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
    public RecipeSerializer<?> serializer() {
        return RecipeSerializer.SHAPED;
    }

    private void verifyData() {
        if(pattern.length == 0) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        }else {
            Set<Character> set = Sets.newHashSet(key.keySet());
            set.remove(' ');
            for(String str:pattern) {
                for(int i = 0; i < str.length();i++) {
                    char c = str.charAt(i);
                    if(!key.containsKey(c) && c != ' ')
                        throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c + "'");

                    set.remove(c);
                }
            }

            if(!set.isEmpty())
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
            else if(pattern.length == 1 && this.pattern[0].length() == 1)
                throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
        }
    }
}