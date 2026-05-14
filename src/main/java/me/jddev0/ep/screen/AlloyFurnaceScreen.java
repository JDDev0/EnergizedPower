package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class AlloyFurnaceScreen extends EnergizedPowerBaseContainerScreen<AlloyFurnaceMenu> {
    private final ResourceLocation TEXTURE;

    public AlloyFurnaceScreen(AlloyFurnaceMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/alloy_furnace.png");
    }

    @Override
    protected void renderBg(GuiGraphics drawContext, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        drawContext.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressFlame(drawContext, x, y);
        renderProgressArrow(drawContext, x, y);
    }

    private void renderProgressFlame(GuiGraphics drawContext, int x, int y) {
        if(menu.isBurningFuel()) {
            int pos = menu.getScaledProgressFlameSize();
            drawContext.blit(MACHINE_SPRITES_TEXTURE, x + 36, y + 37 + 14 - pos, 0, 135 - pos, 14, pos);
        }
    }

    private void renderProgressArrow(GuiGraphics drawContext, int x, int y) {
        if(menu.isCraftingActive())
            drawContext.blit(MACHINE_SPRITES_TEXTURE, x + 79, y + 34, 0, 58, menu.getScaledProgressArrowSize(), 17);
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        renderTooltip(drawContext, mouseX, mouseY);
    }
}
