package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.ModRecipes;
import net.minecraft.advancements.AdvancementHolder;
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
        jsonObject.add("ingredient", input.toJson(false));

        jsonObject.addProperty("speedMultiplier", speedMultiplier);
        jsonObject.addProperty("energyConsumptionMultiplier", energyConsumptionMultiplier);
    }

    @Override
    public RecipeSerializer<?> type() {
        return ModRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_SERIALIZER.get();
    }

    @Override
    public @Nullable AdvancementHolder advancement() {
        return null;
    }
}
