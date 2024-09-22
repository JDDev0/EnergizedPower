package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record SmithingTransformFinishedRecipe(
        Identifier id,
        Ingredient template,
        Ingredient base,
        Ingredient addition,
        Item result,
        Advancement.Builder advancement,
        Identifier advancementId
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("template", this.template.toJson());
        jsonObject.add("base", this.base.toJson());
        jsonObject.add("addition", this.addition.toJson());

        {
            JsonObject resultJson = new JsonObject();

            resultJson.addProperty("item", Registries.ITEM.getId(this.result).toString());

            jsonObject.add("result", resultJson);
        }
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING_TRANSFORM;
    }

    @Override
    public @Nullable JsonObject toAdvancementJson() {
        return advancement.toJson();
    }

    @Override
    public @Nullable Identifier getAdvancementId() {
        return advancementId;
    }
}