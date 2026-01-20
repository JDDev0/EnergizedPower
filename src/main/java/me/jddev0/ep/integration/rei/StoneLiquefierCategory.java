package me.jddev0.ep.integration.rei;

import me.jddev0.ep.block.EPBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class StoneLiquefierCategory implements DisplayCategory<StoneLiquefierDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends StoneLiquefierDisplay> getCategoryIdentifier() {
        return StoneLiquefierDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.stone_liquefier");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.STONE_LIQUEFIER_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(StoneLiquefierDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        widgets.add(Widgets.createSlotBackground(new Point(x, y + 5)));

        widgets.add(Widgets.createArrow(new Point(x + 24, y + 5)));

        widgets.add(Widgets.createResultSlotBackground(new Point(x + 62, y + 5)));

        widgets.add(Widgets.createSlot(new Point(x, y + 5)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 62, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0)));

        return widgets;
    }

    @Override
    public int getDisplayWidth(StoneLiquefierDisplay display) {
        return 82 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 25 + 2*PADDING;
    }
}
