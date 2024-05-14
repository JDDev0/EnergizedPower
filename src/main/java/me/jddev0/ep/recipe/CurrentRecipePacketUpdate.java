package me.jddev0.ep.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

/**
 * Used for SyncCurrentRecipeS2CPacket
 */
public interface CurrentRecipePacketUpdate<R extends Recipe<?>> {
    void setCurrentRecipe(@Nullable RecipeEntry<R> currentRecipe);
}
