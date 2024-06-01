package me.jddev0.ep.recipe;

import net.minecraft.util.Identifier;

/**
 * Used for SetCurrentRecipeIdC2SPacket
 */
public interface SetCurrentRecipeIdPacketUpdate {
    void setRecipeId(Identifier recipeId);
}
