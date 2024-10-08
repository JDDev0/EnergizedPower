package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record CrystalGrowthChamberFinishedRecipe(
        Identifier id,
        OutputItemStackWithPercentages output,
        Ingredient input,
        int inputCount,
        int ticks
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        {
            JsonObject outputJson = new JsonObject();

            outputJson.add("output", ItemStackUtils.toJson(output.output()));

            {
                JsonArray percentagesJson = new JsonArray();

                for(double percentage:output.percentages())
                    percentagesJson.add(percentage);

                outputJson.add("percentages", percentagesJson);
            }

            jsonObject.add("output", outputJson);
        }

        jsonObject.add("ingredient", input.toJson());

        jsonObject.addProperty("inputCount", inputCount);
        jsonObject.addProperty("ticks", ticks);
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EPRecipes.CRYSTAL_GROWTH_CHAMBER_SERIALIZER;
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