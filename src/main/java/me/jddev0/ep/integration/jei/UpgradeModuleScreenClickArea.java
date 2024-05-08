package me.jddev0.ep.integration.jei;

import me.jddev0.ep.screen.AbstractGenericEnergyStorageContainerScreen;
import me.jddev0.ep.screen.UpgradeModuleMenu;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
        final Rect2i area = new Rect2i(xPos, yPos, width, height);
        final List<RecipeType<?>> recipeTypesList = Arrays.asList(recipeTypes);
        return List.of(new IGuiClickableArea() {
            public Rect2i getArea() {
                return area;
            }

            public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
                if(containerScreen.getMenu().isInUpgradeModuleView())
                    return;

                recipesGui.showTypes(recipeTypesList);
            }
        });

    }
}
