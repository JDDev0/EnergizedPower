package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public record SmithingTransformFinishedRecipe(
        Identifier id,
        Ingredient template,
        Ingredient base,
        Ingredient addition,
        Item result,
        AdvancementEntry advancement
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("template", this.template.toJson(true));
        jsonObject.add("base", this.base.toJson(true));
        jsonObject.add("addition", this.addition.toJson(true));

        {
            JsonObject resultJson = new JsonObject();

            resultJson.addProperty("item", Registries.ITEM.getId(this.result).toString());

            jsonObject.add("result", resultJson);
        }
    }

    @Override
    public RecipeSerializer<?> serializer() {
        return RecipeSerializer.SMITHING_TRANSFORM;
    }
}