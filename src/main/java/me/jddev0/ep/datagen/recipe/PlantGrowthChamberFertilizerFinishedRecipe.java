package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.EPRecipes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public record PlantGrowthChamberFertilizerFinishedRecipe(
        ResourceLocation id,
        Ingredient input,
        double speedMultiplier,
        double energyConsumptionMultiplier
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        jsonObject.add("ingredient", input.toJson());

        jsonObject.addProperty("speedMultiplier", speedMultiplier);
        jsonObject.addProperty("energyConsumptionMultiplier", energyConsumptionMultiplier);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return EPRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_SERIALIZER.get();
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
