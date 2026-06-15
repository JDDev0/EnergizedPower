package me.jddev0.ep.integration.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class ChanceBasedSlotRenderer implements Renderer {
    //TODO support multiple values for multiple ingredients (e.g. Plant Growth Chamber)
    private final int minAmount;
    private final int maxAmount;

    public ChanceBasedSlotRenderer(int minAmount, int maxAmount) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }


    @Override
    public void render(GuiGraphics guiGraphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        if(maxAmount > 0) {
            guiGraphics.pose().pushMatrix();

            Font font = Minecraft.getInstance().font;
            Component component = Component.literal(minAmount + (minAmount == maxAmount?"":("-" + maxAmount)));
            int textWidth = font.width(component);

            guiGraphics.pose().translate(bounds.getX() + 16, bounds.getY() + 11);
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().scale(0.65f, 0.65f);

            guiGraphics.drawString(Minecraft.getInstance().font, component, -textWidth, 0, 0xFFFFFFFF, true);

            guiGraphics.pose().popMatrix();
            guiGraphics.pose().popMatrix();
        }
    }
}