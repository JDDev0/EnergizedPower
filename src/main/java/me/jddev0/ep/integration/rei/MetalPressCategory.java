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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MetalPressCategory implements DisplayCategory<MetalPressDisplay> {
    public static final CategoryIdentifier<MetalPressDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "metal_press");

    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends MetalPressDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.metal_press");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.METAL_PRESS_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(MetalPressDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        ResourceLocation texture = EPAPI.id("textures/gui/container/metal_press.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 47, 22, 98, 34));

        widgets.add(Widgets.createSlot(new Point(x + 1, y + 13)).disableBackground().markInput().
                entries(display.getInputEntries().get(1)));
        widgets.add(Widgets.createSlot(new Point(x + 37, y + 1)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 77, y + 13)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0)));

        return widgets;
    }

    @Override
    public int getDisplayWidth(MetalPressDisplay display) {
        return 98 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 34 + 2*PADDING;
    }
}
