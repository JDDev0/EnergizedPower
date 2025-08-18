package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemSiloScreen extends EnergizedPowerBaseContainerScreen<ItemSiloMenu> {
    private final Identifier TEXTURE;

    public ItemSiloScreen(ItemSiloMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/generic_1x1.png");
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawContext.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        renderInfoText(drawContext, x, y);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    private void renderInfoText(DrawContext drawContext, int x, int y) {
        Text component = Text.translatable("tooltip.energizedpower.item_silo.amount", handler.getCount(), handler.getMaxCount());
        int componentWidth = textRenderer.getWidth(component);

        drawContext.drawText(textRenderer, component, (int)(x + (176 - componentWidth) * .5f), y + 58, 0xFF000000, false);
    }
}
