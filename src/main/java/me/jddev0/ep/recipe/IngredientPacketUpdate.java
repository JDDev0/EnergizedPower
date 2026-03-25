package me.jddev0.ep.recipe;

import java.util.List;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Used for SyncIngredientsS2CPacket
 */
public interface IngredientPacketUpdate {
    void setIngredients(int index, List<Ingredient> ingredients);
}
