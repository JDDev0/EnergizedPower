package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.recipe.EPRecipes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public record FluidTransposerFinishedRecipe(
        ResourceLocation id,
        FluidTransposerBlockEntity.Mode mode,
        ItemStack output,
        Ingredient input,
        FluidStack fluid
) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject jsonObject) {
        jsonObject.addProperty("mode", mode.name());
        jsonObject.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output).
                result().orElseThrow());
        jsonObject.add("ingredient", input.toJson(false));
        jsonObject.add("fluid", FluidStack.CODEC.encodeStart(JsonOps.INSTANCE, fluid).
                result().orElseThrow());
    }

    @Override
    public RecipeSerializer<?> type() {
        return EPRecipes.FLUID_TRANSPOSER_SERIALIZER.get();
    }

    @Override
    public @Nullable AdvancementHolder advancement() {
        return null;
    }
}
