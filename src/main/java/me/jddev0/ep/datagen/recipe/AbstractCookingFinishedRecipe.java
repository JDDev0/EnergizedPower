package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public record AbstractCookingFinishedRecipe(
        ResourceLocation id,
        String group,
        Ingredient ingredient,
        Item result,
        float experience,
        int cookingTime,
        Advancement.Builder advancement,
        ResourceLocation advancementId,
        RecipeSerializer<? extends AbstractCookingRecipe> type
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        if(!group.isEmpty())
            jsonObject.addProperty("group", group);

        jsonObject.add("ingredient", ingredient.toJson());
        jsonObject.addProperty("result", ForgeRegistries.ITEMS.getKey(result).toString());
        jsonObject.addProperty("experience", experience);
        jsonObject.addProperty("cookingtime", cookingTime);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return type;
    }

    @Override
    public @Nullable JsonObject serializeAdvancement() {
        return advancement.serializeToJson();
    }

    @Override
    public @Nullable ResourceLocation getAdvancementId() {
        return advancementId;
    }
}
