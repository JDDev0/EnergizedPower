package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import java.util.ArrayList;

public class SelectableRecipeMachineRecipeHandler<M extends ScreenHandler & IConfigurableMenu>
        implements EmiRecipeHandler<M> {
    private final EmiRecipeCategory recipeCategory;

    public SelectableRecipeMachineRecipeHandler(EmiRecipeCategory recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    @Override
    public EmiPlayerInventory getInventory(HandledScreen<M> screen) {
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

        MinecraftClient.getInstance().setScreen(context.getScreen());

        ModMessages.sendClientPacketToServer(new SetCurrentRecipeIdC2SPacket(context.getScreenHandler().getBlockEntity().getPos(), recipe.getId()));

        return true;
    }
}
