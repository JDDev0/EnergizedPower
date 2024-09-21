package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import net.minecraft.advancement.AdvancementEntry;
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

            outputJson.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output.output()).
                    result().orElseThrow());

            {
                JsonArray percentagesJson = new JsonArray();

                for(double percentage:output.percentages())
                    percentagesJson.add(percentage);

                outputJson.add("percentages", percentagesJson);
            }

            jsonObject.add("output", outputJson);
        }

        jsonObject.add("ingredient", input.toJson(false));

        jsonObject.addProperty("inputCount", inputCount);
        jsonObject.addProperty("ticks", ticks);
    }

    @Override
    public RecipeSerializer<?> serializer() {
        return ModRecipes.CRYSTAL_GROWTH_CHAMBER_SERIALIZER;
    }

    @Override
    public @Nullable AdvancementEntry advancement() {
        return null;
    }
}