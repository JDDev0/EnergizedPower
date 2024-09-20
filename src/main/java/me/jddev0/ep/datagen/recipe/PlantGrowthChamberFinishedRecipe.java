package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import net.minecraft.advancements.AdvancementHolder;
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
    public RecipeSerializer<?> type() {
        return ModRecipes.PLANT_GROWTH_CHAMBER_SERIALIZER.get();
    }

    @Override
    public @Nullable AdvancementHolder advancement() {
        return null;
    }
}
