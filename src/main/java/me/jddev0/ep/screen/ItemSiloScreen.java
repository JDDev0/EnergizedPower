package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ItemSiloScreen extends EnergizedPowerBaseContainerScreen<ItemSiloMenu> {
    private final Identifier TEXTURE;

    public ItemSiloScreen(ItemSiloMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/generic_1x1.png");
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        renderInfoText(guiGraphics, x, y);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderInfoText(GuiGraphics guiGraphics, int x, int y) {
        Component component = Component.translatable("tooltip.energizedpower.item_silo.amount", menu.getCount(), menu.getMaxCount());
        int componentWidth = font.width(component);

        guiGraphics.drawString(font, component, (int)(x + (176 - componentWidth) * .5f), y + 58, 0xFF000000, false);
    }
}
