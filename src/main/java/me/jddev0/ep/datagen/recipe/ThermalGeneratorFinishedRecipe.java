package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.ModRecipes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public record ThermalGeneratorFinishedRecipe(
        ResourceLocation id,
        Fluid[] input,
        int energyProduction
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        if(input.length == 1) {
            jsonObject.addProperty("input", ForgeRegistries.FLUIDS.getKey(input[0]).toString());
        }else {
            JsonArray inputJson = new JsonArray();

            for(Fluid fluid:input)
                jsonObject.addProperty("input", ForgeRegistries.FLUIDS.getKey(fluid).toString());

            jsonObject.add("input", inputJson);
        }
        jsonObject.addProperty("energy", energyProduction);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return ModRecipes.THERMAL_GENERATOR_SERIALIZER.get();
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
