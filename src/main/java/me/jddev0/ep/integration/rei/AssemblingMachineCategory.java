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

public class AssemblingMachineCategory implements DisplayCategory<AssemblingMachineDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends AssemblingMachineDisplay> getCategoryIdentifier() {
        return AssemblingMachineDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.assembling_machine");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.ASSEMBLING_MACHINE_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(AssemblingMachineDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        Identifier texture = EPAPI.id("textures/gui/container/assembling_machine.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 43, 18, 115, 54));

        int len = Math.min(display.getInputEntries().size(), 4);
        for(int i = 0;i < len;i++) {
            widgets.add(Widgets.createSlot(new Point(x + (i == 1?1:(i == 2?37:19)), y + (i == 0?1:(i == 3?37:19)))).disableBackground().markInput().
                    entries(display.getInputEntries().get(i)));
        }

        widgets.add(Widgets.createSlot(new Point(x + 91, y + 19)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0)));

        return widgets;
    }

    @Override
    public int getDisplayWidth(AssemblingMachineDisplay display) {
        return 115 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 54 + 2*PADDING;
    }
}
