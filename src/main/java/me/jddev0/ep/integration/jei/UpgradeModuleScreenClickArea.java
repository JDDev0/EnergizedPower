package me.jddev0.ep.integration.jei;

import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import me.jddev0.ep.screen.base.IUpgradeModuleMenu;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.types.IRecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public record UpgradeModuleScreenClickArea<T extends EnergyStorageContainerScreen<? extends IUpgradeModuleMenu>>
        (Class<? extends T> containerScreenClass, int xPos, int yPos, int width, int height, IRecipeType<?>... recipeTypes)
        implements IGuiContainerHandler<T> {

    public static <T extends EnergyStorageContainerScreen<? extends IUpgradeModuleMenu>> UpgradeModuleScreenClickArea<T>
    createRecipeClickArea(final Class<? extends T> containerScreenClass, int xPos, int yPos, int width, int height, IRecipeType<?>... recipeTypes) {
        return new UpgradeModuleScreenClickArea<>(containerScreenClass, xPos, yPos, width, height, recipeTypes);
    }

    @Override
    @NotNull
    public Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull T containerScreen, double mouseX, double mouseY) {
        if(containerScreen.getScreenHandler().isInUpgradeModuleView())
            return List.of();

        return List.of(IGuiClickableArea.createBasic(xPos, yPos, width, height, recipeTypes));
    }
}
