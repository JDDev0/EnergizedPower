package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableIOUpgradableEnergyStorageContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ElitePoweredFurnaceScreen
        extends ConfigurableIOUpgradableEnergyStorageContainerScreen<ElitePoweredFurnaceMenu> {
    public ElitePoweredFurnaceScreen(ElitePoweredFurnaceMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_powered_furnace.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/elite_powered_furnace.png"),
                EPAPI.id("textures/gui/container/upgrade_view/elite_powered_furnace.png"), 230, 166);

        ioConfigurationViewX = 33;
    }

    @Override
    public void extractBackgroundNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(guiGraphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(menu.hasXPExtractionUpgradeModule()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 205, y + 16, 116, 0, 18, 54, 256, 256);
            renderFluidMeterContent(guiGraphics, menu.getFluid(), menu.getTankCapacity(), x + 206, y + 17, 16, 52);
            renderFluidMeterOverlay(guiGraphics, x, y);
        }

        renderProgressArrows(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 206, y + 17, 16, 0, 16, 52, 256, 256);
    }

    private void renderProgressArrows(GuiGraphicsExtractor guiGraphics, int x, int y) {
        for(int i = 0;i < 7;i++)
            if(menu.isCraftingActive(i))
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 36 + 24 * i, y + 35, 0, 79, 12, menu.getScaledProgressArrowSize(i), 256, 256);
    }

    @Override
    protected void extractTooltipNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltipNormalView(guiGraphics, mouseX, mouseY);

        if(isHovering(206, 17, 16, 52, mouseX, mouseY) && menu.hasXPExtractionUpgradeModule()) {
            renderFluidMeterContentTooltip(guiGraphics, menu.getFluid(), menu.getTankCapacity(), mouseX, mouseY);
        }
    }

    @Override
    protected Rect getTankCords(int tank) {
        if(tank == 0)
            return new Rect(206, 17, 16, 52);

        return super.getTankCords(tank);
    }
}
