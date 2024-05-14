package me.jddev0.ep.recipe;

import net.minecraft.recipe.Recipe;
import org.jetbrains.annotations.Nullable;

/**
 * Used for SyncCurrentRecipeS2CPacket
 */
public interface CurrentRecipePacketUpdate<R extends Recipe<?>> {
    void setCurrentRecipe(@Nullable R currentRecipe);
}
