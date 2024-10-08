package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record AssemblingMachineFinishedRecipe(
        Identifier id,
        ItemStack output,
        IngredientWithCount[] inputs
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("output", ItemStackUtils.toJson(output));

        {
            JsonArray inputsJson = new JsonArray();

            for(IngredientWithCount input:inputs) {
                JsonObject inputJson = new JsonObject();

                inputJson.add("input", input.input().toJson());
                inputJson.addProperty("count", input.count());

                inputsJson.add(inputJson);
            }

            jsonObject.add("inputs", inputsJson);
        }
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EPRecipes.ASSEMBLING_MACHINE_SERIALIZER;
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