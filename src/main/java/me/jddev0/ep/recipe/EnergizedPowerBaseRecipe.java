package me.jddev0.ep.recipe;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jspecify.annotations.NonNull;

public interface EnergizedPowerBaseRecipe<T extends RecipeInput> extends Recipe<T> {
    List<Ingredient> getIngredients();

    boolean isIngredient(ItemStack itemStack);

    boolean isResult(ItemStack itemStack);

    @Override
    default boolean showNotification() {
        return true;
    }

    @Override
    default @NonNull String group() {
        return "";
    }
}
