package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.recipe.PulverizerRecipe;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public record PulverizerFinishedRecipe(
        ResourceLocation id,
        PulverizerRecipe.OutputItemStackWithPercentages output,
        PulverizerRecipe.OutputItemStackWithPercentages secondaryOutput,
        Ingredient input
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        {
            JsonObject outputJson = new JsonObject();

            outputJson.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output.output()).
                    result().orElseThrow());

            {
                JsonArray percentagesJson = new JsonArray();

                for(double percentage:output.percentages())
                    percentagesJson.add(percentage);

                outputJson.add("percentages", percentagesJson);
            }

            {
                JsonArray percentagesAdvancedJson = new JsonArray();

                for(double percentage:output.percentagesAdvanced())
                    percentagesAdvancedJson.add(percentage);

                outputJson.add("percentagesAdvanced", percentagesAdvancedJson);
            }

            jsonObject.add("output", outputJson);
        }

        if(!secondaryOutput.output().isEmpty() && secondaryOutput.percentages().length != 0) {
            JsonObject secondaryOutputJson = new JsonObject();

            secondaryOutputJson.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, secondaryOutput.output()).
                    result().orElseThrow());

            {
                JsonArray percentagesJson = new JsonArray();

                for(double percentage:secondaryOutput.percentages())
                    percentagesJson.add(percentage);

                secondaryOutputJson.add("percentages", percentagesJson);
            }

            {
                JsonArray percentagesAdvancedJson = new JsonArray();

                for(double percentage:secondaryOutput.percentagesAdvanced())
                    percentagesAdvancedJson.add(percentage);

                secondaryOutputJson.add("percentagesAdvanced", percentagesAdvancedJson);
            }

            jsonObject.add("secondaryOutput", secondaryOutputJson);
        }

        jsonObject.add("ingredient", input.toJson(false));
    }

    @Override
    public RecipeSerializer<?> type() {
        return ModRecipes.PULVERIZER_SERIALIZER.get();
    }

    @Override
    public @Nullable AdvancementHolder advancement() {
        return null;
    }
}
