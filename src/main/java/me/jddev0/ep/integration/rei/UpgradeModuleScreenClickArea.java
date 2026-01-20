package me.jddev0.ep.integration.rei;

import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import me.jddev0.ep.screen.base.IUpgradeModuleMenu;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.registry.screen.ClickArea;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.Arrays;

public record UpgradeModuleScreenClickArea<T extends EnergyStorageContainerScreen<? extends IUpgradeModuleMenu>>
        (Class<? extends T> containerScreenClass, Rectangle area, CategoryIdentifier<?>... recipeTypes)
        implements ClickArea<T> {

    public static <T extends EnergyStorageContainerScreen<? extends IUpgradeModuleMenu>> UpgradeModuleScreenClickArea<T>
    createRecipeClickArea(final Class<? extends T> containerScreenClass, Rectangle area, CategoryIdentifier<?>... recipeTypes) {
        return new UpgradeModuleScreenClickArea<>(containerScreenClass, area, recipeTypes);
    }

    @Override
    public Result handle(ClickAreaContext<T> context) {
        Rectangle areaCopy = area.clone();
        AbstractContainerScreen<? extends IUpgradeModuleMenu> screen = context.getScreen();
        areaCopy.translate(screen.getGuiLeft(), screen.getGuiTop());
        if(screen.getMenu().isInUpgradeModuleView() || !areaCopy.contains(context.getMousePosition()))
            return Result.fail();

        return Result.success().categories(Arrays.asList(recipeTypes));
    }
}
