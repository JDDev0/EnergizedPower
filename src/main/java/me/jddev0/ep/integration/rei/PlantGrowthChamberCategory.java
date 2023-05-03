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
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlantGrowthChamberCategory implements DisplayCategory<PlantGrowthChamberDisplay> {
    public static final CategoryIdentifier<PlantGrowthChamberDisplay> CATEGORY = CategoryIdentifier.of(EnergizedPowerMod.MODID, "plant_growth_chamber");

    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends PlantGrowthChamberDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("container.energizedpower.plant_growth_chamber");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.PLANT_GROWTH_CHAMBER.get());
    }

    @Override
    public List<Widget> setupDisplay(PlantGrowthChamberDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/plant_growth_chamber.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 61, 25, 108, 36));

        widgets.add(Widgets.createSlot(new Point(x + 1, y + 10)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));

        List<List<EntryStack<?>>> outputSlotEntries = new ArrayList<>(4);
        for(int i = 0;i < 4;i++)
            outputSlotEntries.add(new LinkedList<>());

        List<EntryIngredient> outputEntries = display.getOutputEntries();
        for(int i = 0;i < outputEntries.size();i++)
            outputSlotEntries.get(i % 4).addAll(outputEntries.get(i));

        widgets.add(Widgets.createSlot(new Point(x + 73, y + 1)).disableBackground().markOutput().
                entries(outputSlotEntries.get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 91, y + 1)).disableBackground().markOutput().
                entries(outputSlotEntries.get(1)));
        widgets.add(Widgets.createSlot(new Point(x + 73, y + 19)).disableBackground().markOutput().
                entries(outputSlotEntries.get(2)));
        widgets.add(Widgets.createSlot(new Point(x + 91, y + 19)).disableBackground().markOutput().
                entries(outputSlotEntries.get(3)));

        return widgets;
    }

    @Override
    public int getDisplayWidth(PlantGrowthChamberDisplay display) {
        return 108 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 36 + 2*PADDING;
    }
}
