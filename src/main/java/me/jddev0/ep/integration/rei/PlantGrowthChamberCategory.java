package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.PlantGrowthChamberBlockEntity;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlantGrowthChamberCategory implements DisplayCategory<PlantGrowthChamberDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends PlantGrowthChamberDisplay> getCategoryIdentifier() {
        return PlantGrowthChamberDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.plant_growth_chamber");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(PlantGrowthChamberDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        Identifier texture = EPAPI.id("textures/gui/container/plant_growth_chamber.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 61, 25, 108, 48));

        widgets.add(Widgets.createSlot(new Point(x + 1, y + 10)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));

        List<List<EntryStack<?>>> outputSlotEntries = new ArrayList<>(4);
        for(int i = 0;i < 4;i++)
            outputSlotEntries.add(new ArrayList<>());

        List<EntryIngredient> outputEntries = display.getOutputEntries();
        for(int i = 0;i < outputEntries.size();i++) {
            int index = i;
            outputSlotEntries.get(i % 4).addAll(outputEntries.get(i).map(stack -> {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                double[] percentages = display.recipe().value().getOutputs()[index].percentages();
                for(int j = 0;j < percentages.length;j++)
                    tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

                return stack.tooltip(tooltip);
            }));
        }

        widgets.add(Widgets.createSlot(new Point(x + 73, y + 1)).disableBackground().markOutput().
                entries(outputSlotEntries.get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 91, y + 1)).disableBackground().markOutput().
                entries(outputSlotEntries.get(1)));
        widgets.add(Widgets.createSlot(new Point(x + 73, y + 19)).disableBackground().markOutput().
                entries(outputSlotEntries.get(2)));
        widgets.add(Widgets.createSlot(new Point(x + 91, y + 19)).disableBackground().markOutput().
                entries(outputSlotEntries.get(3)));

        int ticks = (int)(display.recipe().value().getTicks() * PlantGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
        widgets.add(Widgets.createLabel(new Point(x + bounds.width - 10, y + bounds.height - 17),
                        Component.translatable("recipes.energizedpower.info.ticks",
                                ticks)).
                noShadow().rightAligned());

        return widgets;
    }

    @Override
    public int getDisplayWidth(PlantGrowthChamberDisplay display) {
        return 108 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 48 + 2*PADDING;
    }
}
