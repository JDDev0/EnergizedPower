package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.EPRecipes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public record AssemblingMachineFinishedRecipe(
        ResourceLocation id,
        ItemStack output,
        IngredientWithCount[] inputs
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        jsonObject.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output).
                result().orElseThrow());

        {
            JsonArray inputsJson = new JsonArray();

            for(IngredientWithCount input:inputs)
                inputsJson.add(IngredientWithCount.CODEC_NONEMPTY.encodeStart(JsonOps.INSTANCE, input).
                        result().orElseThrow());

            jsonObject.add("inputs", inputsJson);
        }
    }

    @Override
    public RecipeSerializer<?> type() {
        return EPRecipes.ASSEMBLING_MACHINE_SERIALIZER.get();
    }

    @Override
    public @Nullable AdvancementHolder advancement() {
        return null;
    }
}
