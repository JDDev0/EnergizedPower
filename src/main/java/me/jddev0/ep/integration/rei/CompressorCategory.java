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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CompressorCategory implements DisplayCategory<CompressorDisplay> {
    public static final CategoryIdentifier<CompressorDisplay> CATEGORY = CategoryIdentifier.of(EnergizedPowerMod.MODID, "compressor");

    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends CompressorDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.compressor");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.COMPRESSOR_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(CompressorDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/compressor.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 47, 30, 98, 26));

        widgets.add(Widgets.createSlot(new Point(x + 1, y + 5)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 77, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0)));

        return widgets;
    }

    @Override
    public int getDisplayWidth(CompressorDisplay display) {
        return 98 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 26 + 2*PADDING;
    }
}
