package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class AlloyFurnaceScreen extends EnergizedPowerBaseContainerScreen<AlloyFurnaceMenu> {
    private final Identifier TEXTURE;

    public AlloyFurnaceScreen(AlloyFurnaceMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/alloy_furnace.png");
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float a) {
        super.extractBackground(drawContext, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        drawContext.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        renderProgressFlame(drawContext, x, y);
        renderProgressArrow(drawContext, x, y);
    }

    private void renderProgressFlame(GuiGraphicsExtractor drawContext, int x, int y) {
        if(menu.isBurningFuel()) {
            int pos = menu.getScaledProgressFlameSize();
            drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 36, y + 37 + 14 - pos, 0, 135 - pos, 14, pos, 256, 256);
        }
    }

    private void renderProgressArrow(GuiGraphicsExtractor drawContext, int x, int y) {
        if(menu.isCraftingActive())
            drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 79, y + 34, 0, 58, menu.getScaledProgressArrowSize(), 17, 256, 256);
    }
}
