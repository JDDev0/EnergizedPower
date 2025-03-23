package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AlloyFurnaceScreen extends EnergizedPowerBaseContainerScreen<AlloyFurnaceMenu> {
    private final Identifier TEXTURE;

    public AlloyFurnaceScreen(AlloyFurnaceMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/alloy_furnace.png");
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawContext.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        renderProgressFlame(drawContext, x, y);
        renderProgressArrow(drawContext, x, y);
    }

    private void renderProgressFlame(DrawContext drawContext, int x, int y) {
        if(handler.isBurningFuel()) {
            int pos = handler.getScaledProgressFlameSize();
            drawContext.drawTexture(MACHINE_SPRITES_TEXTURE, x + 36, y + 37 + 14 - pos, 0, 135 - pos, 14, pos);
        }
    }

    private void renderProgressArrow(DrawContext drawContext, int x, int y) {
        if(handler.isCraftingActive())
            drawContext.drawTexture(MACHINE_SPRITES_TEXTURE, x + 79, y + 34, 0, 58, handler.getScaledProgressArrowSize(), 17);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);

        super.render(drawContext, mouseX, mouseY, delta);

        drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }
}
