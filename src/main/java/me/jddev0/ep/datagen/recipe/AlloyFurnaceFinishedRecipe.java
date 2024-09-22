package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record AlloyFurnaceFinishedRecipe(
        Identifier id,
        ItemStack output,
        AlloyFurnaceRecipe.OutputItemStackWithPercentages secondaryOutput,
        AlloyFurnaceRecipe.IngredientWithCount[] inputs,
        int ticks
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("output", ItemStackUtils.toJson(output));

        if(!secondaryOutput.output().isEmpty() && secondaryOutput.percentages().length != 0) {
            JsonObject secondaryOutputJson = new JsonObject();

            secondaryOutputJson.add("output", ItemStackUtils.toJson(secondaryOutput.output()));

            {
                JsonArray percentagesJson = new JsonArray();

                for(double percentage:secondaryOutput.percentages())
                    percentagesJson.add(percentage);

                secondaryOutputJson.add("percentages", percentagesJson);
            }

            jsonObject.add("secondaryOutput", secondaryOutputJson);
        }

        {
            JsonArray inputsJson = new JsonArray();

            for(AlloyFurnaceRecipe.IngredientWithCount input:inputs) {
                JsonObject inputJson = new JsonObject();

                inputJson.add("input", input.input().toJson());
                inputJson.addProperty("count", input.count());

                inputsJson.add(inputJson);
            }

            jsonObject.add("inputs", inputsJson);
        }

        jsonObject.addProperty("ticks", ticks);
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALLOY_FURNACE_SERIALIZER;
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