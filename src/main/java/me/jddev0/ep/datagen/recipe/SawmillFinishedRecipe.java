package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.recipe.EPRecipes;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record SawmillFinishedRecipe(
        Identifier id,
        ItemStack output,
        ItemStack secondaryOutput,
        Ingredient input
) implements RecipeJsonProvider {
    public SawmillFinishedRecipe(Identifier id, ItemStack output, Ingredient input, int sawdustAmount) {
        this(id, output, new ItemStack(EPItems.SAWDUST, sawdustAmount), input);
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output).
                result().orElseThrow());

        if(secondaryOutput.isEmpty() || ItemStack.canCombine(secondaryOutput, new ItemStack(EPItems.SAWDUST)))
            jsonObject.addProperty("sawdustAmount", secondaryOutput.getCount());
        else
            jsonObject.add("secondaryOutput", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, secondaryOutput).
                    result().orElseThrow());

        jsonObject.add("ingredient", input.toJson(false));
    }

    @Override
    public RecipeSerializer<?> serializer() {
        return EPRecipes.SAWMILL_SERIALIZER;
    }

    @Override
    public @Nullable AdvancementEntry advancement() {
        return null;
    }
}