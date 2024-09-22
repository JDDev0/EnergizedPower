
package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record PlantGrowthChamberFinishedRecipe(
        Identifier id,
        OutputItemStackWithPercentages[] outputs,
        Ingredient input,
        int ticks
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        {
            JsonArray outputsJson = new JsonArray();

            for(OutputItemStackWithPercentages output:outputs) {
                JsonObject outputJson = new JsonObject();

                outputJson.add("output", ItemStackUtils.toJson(output.output()));

                {
                    JsonArray percentagesJson = new JsonArray();

                    for(double percentage:output.percentages())
                        percentagesJson.add(percentage);

                    outputJson.add("percentages", percentagesJson);
                }

                outputsJson.add(outputJson);
            }

            jsonObject.add("outputs", outputsJson);
        }

        jsonObject.add("ingredient", input.toJson());

        jsonObject.addProperty("ticks", ticks);
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PLANT_GROWTH_CHAMBER_SERIALIZER;
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