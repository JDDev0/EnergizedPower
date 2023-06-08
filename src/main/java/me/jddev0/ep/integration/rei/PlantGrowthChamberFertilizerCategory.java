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

public class PlantGrowthChamberFertilizerCategory implements DisplayCategory<PlantGrowthChamberFertilizerDisplay> {
    public static final CategoryIdentifier<PlantGrowthChamberFertilizerDisplay> CATEGORY = CategoryIdentifier.of(EnergizedPowerMod.MODID, "plant_growth_chamber_fertilizer");

    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends PlantGrowthChamberFertilizerDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.PLANT_GROWTH_CHAMBER.get());
    }

    @Override
    public List<Widget> setupDisplay(PlantGrowthChamberFertilizerDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/plant_growth_chamber.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 34, 34, 18, 18));

/* TODO Re-enable
        widgets.add(Widgets.createSlot(new Point(x + 1, y + 1)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));

        widgets.add(Widgets.createLabel(new Point(x + bounds.width - 10, y + bounds.height - 34),
                        Component.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer.speed_multiplier",
                                display.recipe().getSpeedMultiplier())).
                noShadow().rightAligned());
        widgets.add(Widgets.createLabel(new Point(x + bounds.width - 10, y + bounds.height - 17),
                        Component.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer.energy_consumption_multiplier",
                                display.recipe().getEnergyConsumptionMultiplier())).
                noShadow().rightAligned());
 */

        return widgets;
    }

    @Override
    public int getDisplayWidth(PlantGrowthChamberFertilizerDisplay display) {
        return 144 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 30 + 2*PADDING;
    }
}
