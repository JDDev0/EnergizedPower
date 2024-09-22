package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.ModRecipes;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public record ThermalGeneratorFinishedRecipe(
        Identifier id,
        Fluid[] input,
        int energyProduction
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        if(input.length == 1) {
            jsonObject.addProperty("input", Registry.FLUID.getId(input[0]).toString());
        }else {
            JsonArray inputJson = new JsonArray();

            for(Fluid fluid:input)
                jsonObject.addProperty("input", Registry.FLUID.getId(fluid).toString());

            jsonObject.add("input", inputJson);
        }
        jsonObject.addProperty("energy", energyProduction);
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.THERMAL_GENERATOR_SERIALIZER;
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