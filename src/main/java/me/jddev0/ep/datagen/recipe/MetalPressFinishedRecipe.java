package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.recipe.ModRecipes;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record MetalPressFinishedRecipe(
        Identifier id,
        ItemStack output,
        ItemStack pressMold,
        Ingredient input,
        int inputCount
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output).
                result().orElseThrow());
        jsonObject.add("pressMold", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, pressMold).
                result().orElseThrow());
        jsonObject.add("ingredient", input.toJson(false));
        jsonObject.addProperty("inputCount", inputCount);
    }

    @Override
    public RecipeSerializer<?> serializer() {
        return ModRecipes.METAL_PRESS_SERIALIZER;
    }

    @Override
    public @Nullable AdvancementEntry advancement() {
        return null;
    }
}