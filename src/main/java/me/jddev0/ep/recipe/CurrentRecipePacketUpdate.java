package me.jddev0.ep.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

/**
 * Used for SyncCurrentRecipeS2CPacket
 */
public interface CurrentRecipePacketUpdate<R extends Recipe<?>> {
    void setCurrentRecipe(@Nullable RecipeHolder<R> currentRecipe);
}
