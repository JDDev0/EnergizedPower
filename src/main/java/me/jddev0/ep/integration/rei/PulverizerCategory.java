package me.jddev0.ep.integration.rei;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PulverizerCategory implements DisplayCategory<PulverizerDisplay> {
    public static final CategoryIdentifier<PulverizerDisplay> CATEGORY = CategoryIdentifier.of(EnergizedPowerMod.MODID, "pulverizer");

    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends PulverizerDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.pulverizer");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.PULVERIZER_ITEM);
    }

    @Override
    public List<Widget> setupDisplay(PulverizerDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        Identifier texture = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/pulverizer.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 42, 30, 109, 26));

        widgets.add(Widgets.createSlot(new Point(x + 1, y + 5)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 65, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 92, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(1)));

        return widgets;
    }

    @Override
    public int getDisplayWidth(PulverizerDisplay display) {
        return 109 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 26 + 2*PADDING;
    }
}
