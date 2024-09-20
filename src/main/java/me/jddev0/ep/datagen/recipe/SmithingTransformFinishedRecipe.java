package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public record SmithingTransformFinishedRecipe(
        ResourceLocation id,
        Ingredient template,
        Ingredient base,
        Ingredient addition,
        Item result,
        AdvancementHolder advancement
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        jsonObject.add("template", this.template.toJson(true));
        jsonObject.add("base", this.base.toJson(true));
        jsonObject.add("addition", this.addition.toJson(true));

        {
            JsonObject resultJson = new JsonObject();

            resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());

            jsonObject.add("result", resultJson);
        }
    }

    @Override
    public RecipeSerializer<?> type() {
        return RecipeSerializer.SMITHING_TRANSFORM;
    }
}
