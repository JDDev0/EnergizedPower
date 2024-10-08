package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
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

import java.util.ArrayList;
import java.util.List;

public class PressMoldMakerCategory implements DisplayCategory<PressMoldMakerDisplay> {
    public static final CategoryIdentifier<PressMoldMakerDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "press_mold_maker");

    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends PressMoldMakerDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.press_mold_maker");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.PRESS_MOLD_MAKER_ITEM);
    }

    @Override
    public List<Widget> setupDisplay(PressMoldMakerDisplay display, Rectangle bounds) {
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
    public int getDisplayWidth(PressMoldMakerDisplay display) {
        return 82 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 25 + 2*PADDING;
    }
}
