package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public record EnergizerFinishedRecipe(
        ResourceLocation id,
        ItemStack output,
        Ingredient input,
        int energyConsumption
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        jsonObject.add("output", ItemStackUtils.toJson(output));
        jsonObject.add("ingredient", input.toJson());
        jsonObject.addProperty("energy", energyConsumption);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return EPRecipes.ENERGIZER_SERIALIZER.get();
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
