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
public class ItemConveyorBeltLoaderScreen extends EnergizedPowerBaseContainerScreen<ItemConveyorBeltLoaderMenu> {
    private final Identifier TEXTURE;

    public ItemConveyorBeltLoaderScreen(ItemConveyorBeltLoaderMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/generic_1x1.png");
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float a) {
        super.extractBackground(drawContext, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        drawContext.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
