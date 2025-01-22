package me.jddev0.ep.recipe;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * Used for SyncIngredientsS2CPacket
 */
public interface IngredientPacketUpdate {
    void setIngredients(int index, List<Ingredient> ingredients);
}
