
package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import net.minecraft.advancement.AdvancementEntry;
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

                outputJson.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output.output()).
                        result().orElseThrow());

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

        jsonObject.add("ingredient", input.toJson(false));

        jsonObject.addProperty("ticks", ticks);
    }

    @Override
    public RecipeSerializer<?> serializer() {
        return EPRecipes.PLANT_GROWTH_CHAMBER_SERIALIZER;
    }

    @Override
    public @Nullable AdvancementEntry advancement() {
        return null;
    }
}