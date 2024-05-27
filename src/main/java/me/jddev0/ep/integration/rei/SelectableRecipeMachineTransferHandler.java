package me.jddev0.ep.integration.rei;

import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.jddev0.ep.screen.base.ConfigurableMenu;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.display.Display;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;

public class SelectableRecipeMachineTransferHandler
        <M extends ScreenHandler & ConfigurableMenu, R extends Recipe<?>>
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
        if(!(origin instanceof RecipeEntry<?> recipeEntry) || !recipeClass.isAssignableFrom(recipeEntry.value().getClass()))
            return Result.createNotApplicable();

        if(!context.isActuallyCrafting())
            return Result.createSuccessful().blocksFurtherHandling();

        ModMessages.sendClientPacketToServer(new SetCurrentRecipeIdC2SPacket(container.getBlockEntity().getPos(), recipeEntry.id()));

        return Result.createSuccessful().blocksFurtherHandling();
    }
}
