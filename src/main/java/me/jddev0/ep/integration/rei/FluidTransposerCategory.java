package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class FluidTransposerCategory implements DisplayCategory<FluidTransposerDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends FluidTransposerDisplay> getCategoryIdentifier() {
        return FluidTransposerDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.fluid_transposer");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.FLUID_TRANSPOSER_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(FluidTransposerDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        if(display.recipe().value().getMode() == FluidTransposerBlockEntity.Mode.EMPTYING) {
            Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
            widgets.add(Widgets.createTexturedWidget(texture, x, y, 1, 133, 143, 26));

            widgets.add(Widgets.createSlot(new Point(x + 1, y + 5)).disableBackground().markInput().
                    entries(display.getInputEntries().get(0)));

            widgets.add(Widgets.createSlot(new Point(x + 64, y + 5)).disableBackground().markOutput().
                    entries(display.getOutputEntries().get(0)));
            widgets.add(Widgets.createSlot(new Point(x + 90, y + 5)).disableBackground().markOutput().
                    entries(display.getOutputEntries().get(1)));

            widgets.add(Widgets.createTexturedWidget(Identifier.fromNamespaceAndPath("minecraft", "textures/item/bucket.png"),
                    x + 120, y + 5, 16, 16, 16, 16, 16, 16, 16, 16));
        }else {
            Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
            widgets.add(Widgets.createTexturedWidget(texture, x, y, 1, 161, 143, 26));

            widgets.add(Widgets.createSlot(new Point(x + 1, y + 5)).disableBackground().markInput().
                    entries(display.getInputEntries().get(0)));
            widgets.add(Widgets.createSlot(new Point(x + 19, y + 5)).disableBackground().markInput().
                    entries(display.getInputEntries().get(1)));

            widgets.add(Widgets.createSlot(new Point(x + 90, y + 5)).disableBackground().markOutput().
                    entries(display.getOutputEntries().get(0)));

            widgets.add(Widgets.createTexturedWidget(Identifier.fromNamespaceAndPath("minecraft", "textures/item/water_bucket.png"),
                    x + 120, y + 5, 16, 16, 16, 16, 16, 16, 16, 16));
        }

        widgets.add(Widgets.createTooltip(new Rectangle(x + 119, y + 4, 20, 20),
                List.of(Component.translatable("tooltip.energizedpower.fluid_transposer.mode." +
                        display.recipe().value().getMode().getSerializedName()))));

        return widgets;
    }

    @Override
    public int getDisplayWidth(FluidTransposerDisplay display) {
        return 143 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 26 + 2*PADDING;
    }
}
