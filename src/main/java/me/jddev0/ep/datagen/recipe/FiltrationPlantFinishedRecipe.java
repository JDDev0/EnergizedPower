package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record FiltrationPlantFinishedRecipe(
        Identifier id,
        OutputItemStackWithPercentages output,
        OutputItemStackWithPercentages secondaryOutput,
        Identifier icon
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

        jsonObject.addProperty("icon", icon.toString());
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EPRecipes.FILTRATION_PLANT_SERIALIZER;
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