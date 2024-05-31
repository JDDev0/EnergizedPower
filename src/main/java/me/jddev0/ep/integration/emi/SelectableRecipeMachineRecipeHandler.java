package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.jddev0.ep.screen.base.ConfigurableMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.ArrayList;

public class SelectableRecipeMachineRecipeHandler<M extends AbstractContainerMenu & ConfigurableMenu>
        implements EmiRecipeHandler<M> {
    private final EmiRecipeCategory recipeCategory;

    public SelectableRecipeMachineRecipeHandler(EmiRecipeCategory recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<M> screen) {
        return new EmiPlayerInventory(new ArrayList<>());
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == recipeCategory;
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<M> context) {
        return true;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<M> context) {
        if(!canCraft(recipe, context))
            return false;

        Minecraft.getInstance().setScreen(context.getScreen());

        ModMessages.sendToServer(new SetCurrentRecipeIdC2SPacket(context.getScreenHandler().getBlockEntity().getBlockPos(), recipe.getId()));

        return true;
    }
}
