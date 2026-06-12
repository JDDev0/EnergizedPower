package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PlantGrowthChamberSoilCategory implements DisplayCategory<PlantGrowthChamberSoilDisplay> {
    public static final CategoryIdentifier<PlantGrowthChamberSoilDisplay> CATEGORY = CategoryIdentifier.of(EPAPI.MOD_ID, "plant_growth_chamber_soil");

    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends PlantGrowthChamberSoilDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipes.energizedpower.plant_growth_chamber_soil");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(PlantGrowthChamberSoilDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        ResourceLocation texture = EPAPI.id("textures/gui/container/plant_growth_chamber.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 34, 34, 18, 18));

        widgets.add(Widgets.createSlot(new Point(x + 1, y + 1)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));

        Font font = Minecraft.getInstance().font;

        SoilType soilType = Minecraft.getInstance().level.registryAccess().
                lookupOrThrow(EPRegistries.SOIL_TYPE).getOrThrow(display.recipe().value().getSoilType()).value();

        Component component = Component.translatable("recipes.energizedpower.plant_growth_chamber.soil_type", "");
        int textWidth = font.width(component);

        widgets.add(Widgets.createLabel(new Point(x + 1, y + 22), component).leftAligned());
        widgets.add(Widgets.createLabel(new Point(x + 1 + textWidth, y + 22), soilType.displayName()).leftAligned().noShadow());

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.speed_multiplier",
                display.recipe().value().getSpeedMultiplier());
        widgets.add(Widgets.createLabel(new Point(x + 1, y + 37), component).leftAligned());

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.fluid_consumption_multiplier",
                display.recipe().value().getFluidConsumptionMultiplier());
        widgets.add(Widgets.createLabel(new Point(x + 1, y + 52), component).leftAligned());

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.energy_consumption_multiplier",
                display.recipe().value().getEnergyConsumptionMultiplier());
        widgets.add(Widgets.createLabel(new Point(x + 1, y + 67), component).leftAligned());

        return widgets;
    }

    @Override
    public int getDisplayWidth(PlantGrowthChamberSoilDisplay display) {
        return 144 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 80 + 2*PADDING;
    }
}
