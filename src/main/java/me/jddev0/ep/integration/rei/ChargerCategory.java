package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.ChargerBlockEntity;
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

public class ChargerCategory implements DisplayCategory<ChargerDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends ChargerDisplay> getCategoryIdentifier() {
        return ChargerDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.charger");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.CHARGER_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(ChargerDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        Identifier texture = EPAPI.id("textures/gui/container/charger.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 65, 20, 46, 46));

        widgets.add(Widgets.createArrow(new Point(x + 55, y + 15)));

        widgets.add(Widgets.createResultSlotBackground(new Point(x + 92, y + 15)));

        widgets.add(Widgets.createSlot(new Point(x + 15, y + 15)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 92, y + 15)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0)));

        int energyConsumption = (int)(display.recipe().value().getEnergyConsumption() * ChargerBlockEntity.CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER);
        widgets.add(Widgets.createLabel(new Point(x + bounds.width - 10, y + bounds.height - 17),
                Component.literal(EnergyUtils.getEnergyWithPrefix(energyConsumption)).withStyle(ChatFormatting.YELLOW)).
                noShadow().rightAligned());

        return widgets;
    }

    @Override
    public int getDisplayWidth(ChargerDisplay display) {
        return 113 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 46 + 2*PADDING;
    }
}
