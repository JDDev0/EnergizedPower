package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.item.InventoryChargerItem;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InventoryChargerScreen extends EnergizedPowerBaseContainerScreen<InventoryChargerMenu> {
    private final ResourceLocation TEXTURE;

    public InventoryChargerScreen(InventoryChargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/generic_1x" + InventoryChargerItem.SLOT_COUNT + ".png");
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderType::guiTextured, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
