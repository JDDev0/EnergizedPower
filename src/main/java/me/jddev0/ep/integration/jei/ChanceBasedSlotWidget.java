package me.jddev0.ep.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
    public void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        PoseStack poseStack = guiGraphics.pose();

        if(maxAmount > 0) {
            poseStack.pushPose();

            Font font = Minecraft.getInstance().font;
            Component component = Component.literal(minAmount + (minAmount == maxAmount?"":("-" + maxAmount)));
            int textWidth = font.width(component);

            poseStack.translate(17, 11, 200);
            poseStack.pushPose();
            poseStack.scale(0.65f, 0.65f, 0.65f);

            guiGraphics.drawString(Minecraft.getInstance().font, component, -textWidth, 0, 0xFFFFFFFF, true);

            poseStack.popPose();
            poseStack.popPose();
        }
    }

    @Override
    public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
        return Optional.empty();
    }
}
