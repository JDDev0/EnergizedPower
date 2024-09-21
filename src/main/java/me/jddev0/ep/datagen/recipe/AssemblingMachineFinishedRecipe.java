package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.ModRecipes;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record AssemblingMachineFinishedRecipe(
        Identifier id,
        ItemStack output,
        IngredientWithCount[] inputs
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output).
                result().orElseThrow());

        {
            JsonArray inputsJson = new JsonArray();

            for(IngredientWithCount input:inputs)
                inputsJson.add(IngredientWithCount.CODEC_NONEMPTY.encodeStart(JsonOps.INSTANCE, input).
                        result().orElseThrow());

            jsonObject.add("inputs", inputsJson);
        }
    }

    @Override
    public RecipeSerializer<?> serializer() {
        return ModRecipes.ASSEMBLING_MACHINE_SERIALIZER;
    }

    @Override
    public @Nullable AdvancementEntry advancement() {
        return null;
    }
}