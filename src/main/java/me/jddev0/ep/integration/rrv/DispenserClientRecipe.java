package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class DispenserClientRecipe implements ReliableClientRecipe {
    private final Identifier id;
    private final SlotContent tool, block, output;

    public DispenserClientRecipe(DispenserRecipe recipe) {
        this.id = recipe.id;
        this.tool = SlotContent.of(recipe.tool);
        this.block = SlotContent.of(recipe.block);
        this.output = SlotContent.of(recipe.output);
    }

    @Override
    public ReliableClientRecipeType getType() {
        return DispenserClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.tool);
        slotFillContext.bindSlot(1, this.block);
        slotFillContext.bindSlot(2, this.output);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.tool, this.block);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.output);
    }

    record DispenserRecipe(Identifier id, Ingredient tool, Ingredient block, ItemStack output) {}
}
