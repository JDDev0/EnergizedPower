package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.util.ItemStackUtils;
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
        jsonObject.add("output", ItemStackUtils.toJson(output));

        {
            JsonArray inputsJson = new JsonArray();

            for(IngredientWithCount input:inputs) {
                JsonObject inputJson = new JsonObject();

                inputJson.add("input", input.input().toJson());
                inputJson.addProperty("count", input.count());

                inputsJson.add(inputJson);
            }

            jsonObject.add("inputs", inputsJson);
        }
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return ModRecipes.ASSEMBLING_MACHINE_SERIALIZER.get();
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
