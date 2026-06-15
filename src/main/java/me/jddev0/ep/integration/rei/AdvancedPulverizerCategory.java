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
import java.util.Locale;

public class AdvancedPulverizerCategory implements DisplayCategory<AdvancedPulverizerDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends AdvancedPulverizerDisplay> getCategoryIdentifier() {
        return AdvancedPulverizerDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.advanced_pulverizer");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.ADVANCED_PULVERIZER_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(AdvancedPulverizerDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        Identifier texture = EPAPI.id("textures/gui/container/pulverizer.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 42, 30, 109, 26));

        widgets.add(Widgets.createSlot(new Point(x + 1, y + 5)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 65, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0).map(stack -> {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = display.recipe().value().getOutput().percentagesAdvanced();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));

                    return stack.tooltip(tooltip);
                })));
        widgets.add(Widgets.createSlot(new Point(x + 92, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().size() == 2?display.getOutputEntries().get(1).map(stack -> {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = display.recipe().value().getSecondaryOutput().percentagesAdvanced();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));

                    return stack.tooltip(tooltip);
                }):new ArrayList<>(0)));

        return widgets;
    }

    @Override
    public int getDisplayWidth(AdvancedPulverizerDisplay display) {
        return 109 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 26 + 2*PADDING;
    }
}
