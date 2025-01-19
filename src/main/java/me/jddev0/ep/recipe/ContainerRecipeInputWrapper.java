package me.jddev0.ep.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public class ContainerRecipeInputWrapper implements RecipeInput {
    private final Inventory inventory;

    public ContainerRecipeInputWrapper(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public int size() {
        return inventory.size();
    }
}
