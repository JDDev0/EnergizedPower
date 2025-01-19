package me.jddev0.ep.integration.rei;

import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.display.Display;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class SelectableRecipeMachineTransferHandler
        <M extends ScreenHandler & IConfigurableMenu, R extends Recipe<?>>
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
        Optional<Identifier> recipeIdOptional = display.getDisplayLocation();
        if(recipeIdOptional.isEmpty())
            return Result.createNotApplicable();

        if(!context.isActuallyCrafting())
            return Result.createSuccessful().blocksFurtherHandling();

        ModMessages.sendClientPacketToServer(new SetCurrentRecipeIdC2SPacket(container.getBlockEntity().getPos(), recipeIdOptional.get()));

        return Result.createSuccessful().blocksFurtherHandling();
    }
}
