package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.ModRecipes;
import net.minecraft.advancements.AdvancementHolder;
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
        jsonObject.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output).
                result().orElseThrow());

        if(secondaryOutput.isEmpty() || ItemStack.isSameItemSameTags(secondaryOutput, new ItemStack(ModItems.SAWDUST.get())))
            jsonObject.addProperty("sawdustAmount", secondaryOutput.getCount());
        else
            jsonObject.add("secondaryOutput", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, secondaryOutput).
                    result().orElseThrow());

        jsonObject.add("ingredient", input.toJson(false));
    }

    @Override
    public RecipeSerializer<?> type() {
        return ModRecipes.SAWMILL_SERIALIZER.get();
    }

    @Override
    public @Nullable AdvancementHolder advancement() {
        return null;
    }
}
