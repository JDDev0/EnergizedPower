package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public record PlantGrowthChamberFinishedRecipe(
        ResourceLocation id,
        OutputItemStackWithPercentages[] outputs,
        Ingredient input,
        int ticks
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
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
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return EPRecipes.PLANT_GROWTH_CHAMBER_SERIALIZER.get();
    }

    @Override
    public @Nullable JsonObject serializeAdvancement() {
        return null;
    }

    @Override
    public @Nullable ResourceLocation getAdvancementId() {
        return null;
    }
}
