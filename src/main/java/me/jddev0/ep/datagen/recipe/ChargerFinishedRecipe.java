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

public record ChargerFinishedRecipe(
        Identifier id,
        ItemStack output,
        Ingredient input,
        int energyConsumption
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output).
                result().orElseThrow());
        jsonObject.add("ingredient", input.toJson(false));
        jsonObject.addProperty("energy", energyConsumption);
    }

    @Override
    public RecipeSerializer<?> serializer() {
        return ModRecipes.CHARGER_SERIALIZER;
    }

    @Override
    public @Nullable AdvancementEntry advancement() {
        return null;
    }
}