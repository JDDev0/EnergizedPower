package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.recipe.CrusherRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class CrusherClientRecipe implements ReliableClientRecipe {
    private final Identifier id;
    private final SlotContent input, output;

    public CrusherClientRecipe(RecipeHolder<CrusherRecipe> recipe) {
        this.id = recipe.id().identifier();
        this.input = SlotContent.of(recipe.value().getInput());
        this.output = SlotContent.of(recipe.value().getOutput());
    }

    @Override
    public ReliableClientRecipeType getType() {
        return CrusherClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(1, this.output);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.input);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.output);
    }
}
