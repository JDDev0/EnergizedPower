package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.recipe.StoneLiquefierRecipe;
import me.jddev0.ep.util.FluidStackUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class StoneLiquefierClientRecipe implements ReliableClientRecipe {
    private final Identifier id;
    private final SlotContent input, output;

    public StoneLiquefierClientRecipe(RecipeHolder<StoneLiquefierRecipe> recipe) {
        this.id = recipe.id().identifier();
        this.input = SlotContent.of(recipe.value().getInput());
        FluidStack output = FluidStack.fromFluidStack(FluidStackUtils.fromNullableFluidStackTemplate(recipe.value().getOutput()));
        this.output = SlotContent.of(output);
    }

    @Override
    public ReliableClientRecipeType getType() {
        return StoneLiquefierClientRecipeType.INSTANCE;
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
