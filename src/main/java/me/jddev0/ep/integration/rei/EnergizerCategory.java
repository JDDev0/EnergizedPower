package me.jddev0.ep.integration.rei;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.util.EnergyUtils;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class EnergizerCategory implements DisplayCategory<EnergizerDisplay> {
    public static final CategoryIdentifier<EnergizerDisplay> CATEGORY = CategoryIdentifier.of(EnergizedPowerMod.MODID, "energizer");

    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends EnergizerDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.energizer");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.ENERGIZER);
    }

    @Override
    public List<Widget> setupDisplay(EnergizerDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        Identifier texture = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/energizer.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 31, 18, 114, 50));

        widgets.add(Widgets.createSlot(new Point(x + 17, y + 17)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 93, y + 17)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0)));

        widgets.add(Widgets.createLabel(new Point(x + bounds.width - 10, y + bounds.height - 17),
                        Text.literal(EnergyUtils.getEnergyWithPrefix(display.recipe().getEnergyConsumption())).formatted(Formatting.YELLOW)).
                noShadow().rightAligned());

        return widgets;
    }

    @Override
    public int getDisplayWidth(EnergizerDisplay display) {
        return 114 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 50 + 2*PADDING;
    }
}
