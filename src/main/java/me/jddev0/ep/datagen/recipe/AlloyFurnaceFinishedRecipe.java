package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public record AlloyFurnaceFinishedRecipe(
        ResourceLocation id,
        ItemStack output,
        OutputItemStackWithPercentages secondaryOutput,
        IngredientWithCount[] inputs,
        int ticks
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        jsonObject.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output).
                result().orElseThrow());

        if(!secondaryOutput.output().isEmpty() && secondaryOutput.percentages().length != 0) {
            JsonObject secondaryOutputJson = new JsonObject();

            secondaryOutputJson.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output).
                    result().orElseThrow());

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

            for(IngredientWithCount input:inputs) {
                JsonObject inputJson = new JsonObject();

                inputJson.add("input", input.input().toJson(false));
                inputJson.addProperty("count", input.count());

                inputsJson.add(inputJson);
            }

            jsonObject.add("inputs", inputsJson);
        }

        jsonObject.addProperty("ticks", ticks);
    }

    @Override
    public RecipeSerializer<?> type() {
        return ModRecipes.ALLOY_FURNACE_SERIALIZER.get();
    }

    @Override
    public @Nullable AdvancementHolder advancement() {
        return null;
    }
}
