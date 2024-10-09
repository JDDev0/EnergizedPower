package me.jddev0.ep.datagen.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.EPRecipes;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record FluidTransposerFinishedRecipe(
        Identifier id,
        FluidTransposerBlockEntity.Mode mode,
        ItemStack output,
        Ingredient input,
        FluidStack fluid
) implements RecipeJsonProvider {
    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty("mode", mode.name());
        jsonObject.add("output", CodecFix.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, output).
                result().orElseThrow());
        jsonObject.add("ingredient", input.toJson(false));
        jsonObject.add("fluid", FluidStack.CODEC_MILLIBUCKETS.encodeStart(JsonOps.INSTANCE, fluid).
                result().orElseThrow());
    }

    @Override
    public RecipeSerializer<?> serializer() {
        return EPRecipes.FLUID_TRANSPOSER_SERIALIZER;
    }

    @Override
    public @Nullable AdvancementEntry advancement() {
        return null;
    }
}