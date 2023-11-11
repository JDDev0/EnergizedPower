package me.jddev0.ep.integration.rei;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.CrystalGrowthChamberBlockEntity;
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

public class CrystalGrowthChamberCategory implements DisplayCategory<CrystalGrowthChamberDisplay> {
    public static final CategoryIdentifier<CrystalGrowthChamberDisplay> CATEGORY = CategoryIdentifier.of(EnergizedPowerMod.MODID, "crystal_growth_chamber");

    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends CrystalGrowthChamberDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.crystal_growth_chamber");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM);
    }

    @Override
    public List<Widget> setupDisplay(CrystalGrowthChamberDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        Identifier texture = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/crystal_growth_chamber.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 47, 30, 98, 38));

        widgets.add(Widgets.createSlot(new Point(x + 1, y + 5)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 77, y + 5)).disableBackground().markOutput().
                entries(display.getOutputEntries().get(0)));

        int ticks = (int)(display.recipe().value().getTicks() * CrystalGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
        widgets.add(Widgets.createLabel(new Point(x + bounds.width - 10, y + bounds.height - 17),
                        Text.translatable("recipes.energizedpower.info.ticks", ticks)).
                noShadow().rightAligned());

        return widgets;
    }

    @Override
    public int getDisplayWidth(CrystalGrowthChamberDisplay display) {
        return 98 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 38 + 2*PADDING;
    }
}
