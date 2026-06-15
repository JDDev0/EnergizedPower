package me.jddev0.ep.integration.rei;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AlloyFurnaceBlockEntity;
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

public class AlloyFurnaceCategory implements DisplayCategory<AlloyFurnaceDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends AlloyFurnaceDisplay> getCategoryIdentifier() {
        return AlloyFurnaceDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.alloy_furnace");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.ALLOY_FURNACE_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(AlloyFurnaceDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        widgets.add(Widgets.createSlotBackground(new Point(x, y + 5)));
        widgets.add(Widgets.createSlotBackground(new Point(x + 18, y + 5)));
        widgets.add(Widgets.createSlotBackground(new Point(x + 36, y + 5)));

        widgets.add(Widgets.createArrow(new Point(x + 60, y + 5)));

        widgets.add(Widgets.createResultSlotBackground(new Point(x + 99, y + 5)));
        widgets.add(Widgets.createResultSlotBackground(new Point(x + 125, y + 5)));

        int len = Math.min(display.getInputEntries().size(), 3);
        for(int i = 0;i < len;i++) {
            widgets.add(Widgets.createSlot(new Point(x + 18 * i, y + 5)).disableBackground().markInput().
                    entries(display.getInputEntries().get(i)));
        }

        widgets.add(Widgets.createSlot(new Point(x + 99, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 125, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().size() == 2?display.getOutputEntries().get(1).map(stack -> {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = display.recipe().value().getSecondaryOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));

                    return stack.tooltip(tooltip);
                }):new ArrayList<>(0)));

        int ticks = (int)(display.recipe().value().getTicks() * AlloyFurnaceBlockEntity.RECIPE_DURATION_MULTIPLIER);
        widgets.add(Widgets.createLabel(new Point(x + bounds.width - 10, y + bounds.height - 17),
                        Component.translatable("recipes.energizedpower.info.ticks",
                                ticks)).
                noShadow().rightAligned());

        return widgets;
    }

    @Override
    public int getDisplayWidth(AlloyFurnaceDisplay display) {
        return 149 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 39 + 2*PADDING;
    }
}
