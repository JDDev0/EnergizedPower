package me.jddev0.ep.integration.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
        PoseStack poseStack = guiGraphics.pose();

        if(maxAmount > 0) {
            poseStack.pushPose();

            Font font = Minecraft.getInstance().font;
            Component component = Component.literal(minAmount + (minAmount == maxAmount?"":("-" + maxAmount)));
            int textWidth = font.width(component);

            poseStack.translate(bounds.getX() + 16, bounds.getY() + 11, 200);
            poseStack.pushPose();
            poseStack.scale(0.65f, 0.65f, 0.65f);

            guiGraphics.drawString(Minecraft.getInstance().font, component, -textWidth, 0, 0xFFFFFFFF, true);

            poseStack.popPose();
            poseStack.popPose();
        }
    }
}
