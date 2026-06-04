package me.jddev0.ep.integration.emi;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ChanceBasedSlotWidget extends SlotWidget {
    //TODO support multiple values for multiple ingredients (e.g. Plant Growth Chamber)
    private int minAmount;
    private int maxAmount;

    public ChanceBasedSlotWidget(EmiIngredient stack, int x, int y) {
        super(stack, x, y);
    }

    public void setAmount(int amount) {
        this.minAmount = amount;
        this.maxAmount = amount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Override
    public ChanceBasedSlotWidget drawBack(boolean drawBack) {
        super.drawBack(drawBack);

        return this;
    }

    @Override
    public ChanceBasedSlotWidget recipeContext(EmiRecipe recipe) {
        super.recipeContext(recipe);

        return this;
    }

    @Override
    public ChanceBasedSlotWidget large(boolean large) {
        super.large(large);

        return this;
    }

    @Override
    public void drawOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.drawOverlay(guiGraphics, mouseX, mouseY, delta);

        if(maxAmount > 0) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            Font font = Minecraft.getInstance().font;
            Component component = Component.literal(minAmount + (minAmount == maxAmount?"":("-" + maxAmount)));
            int textWidth = font.width(component);

            poseStack.translate(x + 17, y + 12, 200);
            poseStack.pushPose();
            poseStack.scale(0.65f, 0.65f, 0.65f);

            guiGraphics.drawString(Minecraft.getInstance().font, component, -textWidth, 0, 0xFFFFFFFF, true);

            poseStack.popPose();
            poseStack.popPose();
        }
    }
}
