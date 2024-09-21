package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.ModRecipes;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record ThermalGeneratorFinishedRecipe(
        Identifier id,
        Fluid[] input,
        int energyProduction
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        if(input.length == 1) {
            jsonObject.addProperty("input", Registries.FLUID.getId(input[0]).toString());
        }else {
            JsonArray inputJson = new JsonArray();

            for(Fluid fluid:input)
                jsonObject.addProperty("input", Registries.FLUID.getId(fluid).toString());

            jsonObject.add("input", inputJson);
        }
        jsonObject.addProperty("energy", energyProduction);
    }

    @Override
    public RecipeSerializer<?> serializer() {
        return ModRecipes.THERMAL_GENERATOR_SERIALIZER;
    }

    @Override
    public @Nullable AdvancementEntry advancement() {
        return null;
    }
}