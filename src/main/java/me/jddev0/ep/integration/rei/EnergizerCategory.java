package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EnergizerBlockEntity;
import me.jddev0.ep.util.EnergyUtils;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class EnergizerCategory implements DisplayCategory<EnergizerDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends EnergizerDisplay> getCategoryIdentifier() {
        return EnergizerDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.energizer");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.ENERGIZER_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(EnergizerDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        Identifier texture = EPAPI.id("textures/gui/container/energizer.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 31, 18, 114, 50));

        widgets.add(Widgets.createSlot(new Point(x + 17, y + 17)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 93, y + 17)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0)));

        int energyConsumption = (int)(display.recipe().value().getEnergyConsumption() * EnergizerBlockEntity.ENERGY_CONSUMPTION_MULTIPLIER);
        widgets.add(Widgets.createLabel(new Point(x + bounds.width - 10, y + bounds.height - 17),
                        Component.literal(EnergyUtils.getEnergyWithPrefix(energyConsumption)).withStyle(ChatFormatting.YELLOW)).
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
