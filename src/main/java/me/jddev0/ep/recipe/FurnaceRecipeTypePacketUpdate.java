package me.jddev0.ep.recipe;

import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;

/**
 * Used for SyncFurnaceRecipeTypeS2CPacket
 */
public interface FurnaceRecipeTypePacketUpdate {
    void setRecipeType(RecipeType<? extends AbstractCookingRecipe> recipeType);
}
