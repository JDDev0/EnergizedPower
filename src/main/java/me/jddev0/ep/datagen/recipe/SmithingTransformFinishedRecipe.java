package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public record SmithingTransformFinishedRecipe(
        ResourceLocation id,
        Ingredient template,
        Ingredient base,
        Ingredient addition,
        Item result,
        Advancement.Builder advancement,
        ResourceLocation advancementId
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        jsonObject.add("template", this.template.toJson());
        jsonObject.add("base", this.base.toJson());
        jsonObject.add("addition", this.addition.toJson());

        {
            JsonObject resultJson = new JsonObject();

            resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());

            jsonObject.add("result", resultJson);
        }
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return RecipeSerializer.SMITHING_TRANSFORM;
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
