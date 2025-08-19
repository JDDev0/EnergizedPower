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
import java.util.Locale;

public class FiltrationPlantCategory implements DisplayCategory<FiltrationPlantDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends FiltrationPlantDisplay> getCategoryIdentifier() {
        return FiltrationPlantDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.filtration_plant");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.FILTRATION_PLANT_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(FiltrationPlantDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        widgets.add(Widgets.createSlotBackground(new Point(x + 1, y + 5)));

        widgets.add(Widgets.createArrow(new Point(x + 29, y + 5)));

        widgets.add(Widgets.createResultSlotBackground(new Point(x + 64, y + 5)));
        widgets.add(Widgets.createResultSlotBackground(new Point(x + 92, y + 5)));

        widgets.add(Widgets.createSlot(new Point(x + 1, y + 5)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 64, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0).map(stack -> {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = display.recipe().value().getOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));

                    return stack.tooltip(tooltip);
                })));
        widgets.add(Widgets.createSlot(new Point(x + 92, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().size() == 2?display.getOutputEntries().get(1).map(stack -> {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = display.recipe().value().getSecondaryOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));

                    return stack.tooltip(tooltip);
                }):new ArrayList<>(0)));

        return widgets;
    }

    @Override
    public int getDisplayWidth(FiltrationPlantDisplay display) {
        return 112 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 26 + 2*PADDING;
    }
}
