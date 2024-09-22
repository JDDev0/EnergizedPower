package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public record SawmillFinishedRecipe(
        ResourceLocation id,
        ItemStack output,
        ItemStack secondaryOutput,
        Ingredient input
) implements FinishedRecipe {
    public SawmillFinishedRecipe(ResourceLocation id, ItemStack output, Ingredient input, int sawdustAmount) {
        this(id, output, new ItemStack(ModItems.SAWDUST.get(), sawdustAmount), input);
    }

    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        jsonObject.add("output", ItemStackUtils.toJson(output));

        if(secondaryOutput.isEmpty() || ItemStack.isSameItemSameTags(secondaryOutput, new ItemStack(ModItems.SAWDUST.get())))
            jsonObject.addProperty("sawdustAmount", secondaryOutput.getCount());
        else
            jsonObject.add("secondaryOutput", ItemStackUtils.toJson(secondaryOutput));

        jsonObject.add("ingredient", input.toJson());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return ModRecipes.SAWMILL_SERIALIZER.get();
    }

    @Override
    public @Nullable JsonObject serializeAdvancement() {
        return null;
    }

    @Override
    public @Nullable ResourceLocation getAdvancementId() {
        return null;
    }
}
