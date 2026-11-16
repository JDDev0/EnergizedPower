package me.jddev0.ep.integration.rrv;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class ChanceBasedSlotRenderer {
    private final int x;
    private final int y;

    //TODO support multiple values for multiple ingredients (e.g. Plant Growth Chamber)
    private final int minAmount;
    private final int maxAmount;

    public ChanceBasedSlotRenderer(int x, int y, int minAmount, int maxAmount) {
        this.x = x;
        this.y = y;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(maxAmount > 0) {
            guiGraphics.pose().pushMatrix();

            Font font = Minecraft.getInstance().font;
            Component component = Component.literal(minAmount + (minAmount == maxAmount?"":("-" + maxAmount)));
            int textWidth = font.width(component);

            guiGraphics.pose().translate(this.x + 17, this.y + 11);
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().scale(0.65f, 0.65f);

            guiGraphics.text(Minecraft.getInstance().font, component, -textWidth, 0, 0xFFFFFFFF, true);

            guiGraphics.pose().popMatrix();
            guiGraphics.pose().popMatrix();
        }
    }
}
