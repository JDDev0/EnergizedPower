package me.jddev0.ep.datagen.recipe;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

public record ShapedFinishedRecipe(
        ResourceLocation id,
        String group,
        CraftingBookCategory category,
        Map<Character, Ingredient> key,
        String[] pattern,
        Item result,
        int count,
        AdvancementHolder advancement
) implements FinishedRecipe {
    public ShapedFinishedRecipe(ResourceLocation id, String group, CraftingBookCategory category, Map<Character, Ingredient> key,
                                String[] pattern, ItemStack result, AdvancementHolder advancement) {
        this(id, group, category, key, pattern, result.getItem(), result.getCount(), advancement);
    }

    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        verifyData();

        if(!group.isEmpty())
            jsonObject.addProperty("group", group);

        jsonObject.addProperty("category", category.getSerializedName());

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
            resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(result).toString());
            if(count > 1)
                resultJson.addProperty("count", count);

            jsonObject.add("result", resultJson);
        }
    }

    @Override
    public RecipeSerializer<?> type() {
        return RecipeSerializer.SHAPED_RECIPE;
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
