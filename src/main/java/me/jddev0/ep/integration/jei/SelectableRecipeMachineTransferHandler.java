package me.jddev0.ep.integration.jei;

import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SelectableRecipeMachineTransferHandler
        <M extends ScreenHandler & IConfigurableMenu, R extends Recipe<?>>
        implements IRecipeTransferHandler<M, R> {
    private final IRecipeTransferHandlerHelper helper;

    private final Class<? extends M> menuClass;
    private final ScreenHandlerType<M> menuType;

    public SelectableRecipeMachineTransferHandler(IRecipeTransferHandlerHelper helper, Class<? extends M> menuClass,
                                                  ScreenHandlerType<M> menuType) {
        this.helper = helper;

        this.menuClass = menuClass;
        this.menuType = menuType;
    }

    @Override
    public Class<? extends M> getContainerClass() {
        return menuClass;
    }

    @Override
    public Optional<ScreenHandlerType<M>> getMenuType() {
        return Optional.of(menuType);
    }

    @Override
    public RecipeType<R> getRecipeType() {
        return null;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(M container, R recipe, IRecipeSlotsView recipeSlots, PlayerEntity player,
                                                         boolean maxTransfer, boolean doTransfer) {
        if(!doTransfer)
            return null;

        ModMessages.sendClientPacketToServer(new SetCurrentRecipeIdC2SPacket(container.getBlockEntity().getPos(), recipe.getId()));

        return null;
    }
}
