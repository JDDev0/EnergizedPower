
package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.ModRecipes;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record PlantGrowthChamberFertilizerFinishedRecipe(
        Identifier id,
        Ingredient input,
        double speedMultiplier,
        double energyConsumptionMultiplier
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("ingredient", input.toJson());

        jsonObject.addProperty("speedMultiplier", speedMultiplier);
        jsonObject.addProperty("energyConsumptionMultiplier", energyConsumptionMultiplier);
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_SERIALIZER;
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