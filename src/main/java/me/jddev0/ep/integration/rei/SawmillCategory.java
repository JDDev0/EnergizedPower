package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
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
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SawmillCategory implements DisplayCategory<SawmillDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends SawmillDisplay> getCategoryIdentifier() {
        return SawmillDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.sawmill");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.SAWMILL_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(SawmillDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        Identifier texture = EPAPI.id("textures/gui/container/sawmill.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 42, 30, 109, 26));

        widgets.add(Widgets.createSlot(new Point(x + 1, y + 5)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 65, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 92, y + 5)).disableBackground().markOutput().
                entries(display.recipe().value().getSecondaryOutput().isEmpty()?new ArrayList<>(0):display.getOutputEntries().get(1)));

        return widgets;
    }

    @Override
    public int getDisplayWidth(SawmillDisplay display) {
        return 109 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 26 + 2*PADDING;
    }
}
