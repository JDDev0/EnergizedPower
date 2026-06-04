package me.jddev0.ep.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class ChanceBasedSlotWidget implements ISlottedRecipeWidget {
    private final int x;
    private final int y;

    //TODO support multiple values for multiple ingredients (e.g. Plant Growth Chamber)
    private final int minAmount;
    private final int maxAmount;

    public ChanceBasedSlotWidget(int x, int y, int minAmount, int maxAmount) {
        this.x = x;
        this.y = y;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    @Override
    public ScreenPosition getPosition() {
        return new ScreenPosition(x, y);
    }

    @Override
    public void drawWidget(GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        if(maxAmount > 0) {
            guiGraphics.pose().pushMatrix();

            Font font = Minecraft.getInstance().font;
            Component component = Component.literal(minAmount + (minAmount == maxAmount?"":("-" + maxAmount)));
            int textWidth = font.width(component);

            guiGraphics.pose().translate(17, 11);
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().scale(0.65f, 0.65f);

            guiGraphics.text(Minecraft.getInstance().font, component, -textWidth, 0, 0xFFFFFFFF, true);

            guiGraphics.pose().popMatrix();
            guiGraphics.pose().popMatrix();
        }
    }

    @Override
    public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
        return Optional.empty();
    }
}
