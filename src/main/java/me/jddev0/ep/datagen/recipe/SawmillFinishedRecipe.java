package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.util.ItemStackUtils;
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
        this(id, output, new ItemStack(ModItems.SAWDUST, sawdustAmount), input);
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add("output", ItemStackUtils.toJson(output));

        if(secondaryOutput.isEmpty() || ItemStack.canCombine(secondaryOutput, new ItemStack(ModItems.SAWDUST)))
            jsonObject.addProperty("sawdustAmount", secondaryOutput.getCount());
        else
            jsonObject.add("secondaryOutput", ItemStackUtils.toJson(secondaryOutput));

        jsonObject.add("ingredient", input.toJson());
    }

    @Override
    public Identifier getRecipeId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SAWMILL_SERIALIZER;
    }

    @Override
    public @Nullable JsonObject toAdvancementJson() {
        return null;
    }

    @Override
    public @Nullable Identifier getAdvancementId() {
        return null;
    }
}