package me.jddev0.ep.integration.rei;

import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.jddev0.ep.screen.base.ConfigurableMenu;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.display.Display;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;

public class SelectableRecipeMachineTransferHandler
        <M extends AbstractContainerMenu & ConfigurableMenu, R extends Recipe<?>>
        implements TransferHandler {
    private final Class<? extends M> menuClass;
    private final Class<? extends R> recipeClass;

    public SelectableRecipeMachineTransferHandler(Class<? extends M> menuClass, Class<? extends R> recipeClass) {
        this.menuClass = menuClass;
        this.recipeClass = recipeClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result handle(Context context) {
        if(context.getMenu() == null || !menuClass.isAssignableFrom(context.getMenu().getClass()))
            return Result.createNotApplicable();

        M container = (M)context.getMenu();

        Display display = context.getDisplay();
        Object origin = DisplayRegistry.getInstance().getDisplayOrigin(display);
        if(origin == null || !recipeClass.isAssignableFrom(origin.getClass()))
            return Result.createNotApplicable();

        R recipe = (R)origin;

        if(!context.isActuallyCrafting())
            return Result.createSuccessful().blocksFurtherHandling();

        ModMessages.sendToServer(new SetCurrentRecipeIdC2SPacket(container.getBlockEntity().getBlockPos(), recipe.getId()));

        return Result.createSuccessful().blocksFurtherHandling();
    }
}
