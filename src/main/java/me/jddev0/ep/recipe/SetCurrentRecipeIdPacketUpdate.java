package me.jddev0.ep.recipe;

import net.minecraft.resources.ResourceLocation;

/**
 * Used for SetCurrentRecipeIdC2SPacket
 */
public interface SetCurrentRecipeIdPacketUpdate {
    void setRecipeId(ResourceLocation recipeId);
}
