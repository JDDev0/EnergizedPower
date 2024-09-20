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

public record CrystalGrowthChamberFinishedRecipe(
        ResourceLocation id,
        OutputItemStackWithPercentages output,
        Ingredient input,
        int inputCount,
        int ticks
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        {
            JsonObject secondaryOutputJson = new JsonObject();

            secondaryOutputJson.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output.output()).
                    result().orElseThrow());

            {
                JsonArray percentagesJson = new JsonArray();

                for(double percentage:output.percentages())
                    percentagesJson.add(percentage);

                secondaryOutputJson.add("percentages", percentagesJson);
            }

            jsonObject.add("output", secondaryOutputJson);
        }

        jsonObject.add("ingredient", input.toJson(false));

        jsonObject.addProperty("inputCount", inputCount);
        jsonObject.addProperty("ticks", ticks);
    }

    @Override
    public RecipeSerializer<?> type() {
        return ModRecipes.CRYSTAL_GROWTH_CHAMBER_SERIALIZER.get();
    }

    @Override
    public @Nullable AdvancementHolder advancement() {
        return null;
    }
}
