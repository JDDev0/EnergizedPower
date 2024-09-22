package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public record SmithingUpgradeFinishedRecipe(
        Identifier id,
        Ingredient base,
        Ingredient addition,
        Item result,
        Advancement.Builder advancement,
        Identifier advancementId
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("base", this.base.toJson());
        jsonObject.add("addition", this.addition.toJson());

        {
            JsonObject resultJson = new JsonObject();

            resultJson.addProperty("item", Registry.ITEM.getId(this.result).toString());

            jsonObject.add("result", resultJson);
        }
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING;
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