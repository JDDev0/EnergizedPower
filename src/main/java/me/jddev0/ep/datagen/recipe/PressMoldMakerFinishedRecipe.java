package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.recipe.ModRecipes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public record PressMoldMakerFinishedRecipe(
        ResourceLocation id,
        ItemStack output,
        int clayCount
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        jsonObject.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output).
                result().orElseThrow());
        jsonObject.addProperty("clayCount", clayCount);
    }

    @Override
    public RecipeSerializer<?> type() {
        return ModRecipes.PRESS_MOLD_MAKER_SERIALIZER.get();
    }

    @Override
    public @Nullable AdvancementHolder advancement() {
        return null;
    }
}
