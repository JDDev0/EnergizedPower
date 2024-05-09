package me.jddev0.ep.integration.jei;

import me.jddev0.ep.screen.AbstractGenericEnergyStorageContainerScreen;
import me.jddev0.ep.screen.UpgradeModuleMenu;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public record UpgradeModuleScreenClickArea<T extends AbstractGenericEnergyStorageContainerScreen<? extends UpgradeModuleMenu>>
        (Class<? extends T> containerScreenClass, int xPos, int yPos, int width, int height, RecipeType<?>... recipeTypes)
        implements IGuiContainerHandler<T> {

    public static <T extends AbstractGenericEnergyStorageContainerScreen<? extends UpgradeModuleMenu>> UpgradeModuleScreenClickArea<T>
    createRecipeClickArea(final Class<? extends T> containerScreenClass, int xPos, int yPos, int width, int height, RecipeType<?>... recipeTypes) {
        return new UpgradeModuleScreenClickArea<>(containerScreenClass, xPos, yPos, width, height, recipeTypes);
    }

    @Override
    @NotNull
    public Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull T containerScreen, double mouseX, double mouseY) {
        if(containerScreen.getMenu().isInUpgradeModuleView())
            return List.of();

        return List.of(IGuiClickableArea.createBasic(xPos, yPos, width, height, recipeTypes));
    }
}
