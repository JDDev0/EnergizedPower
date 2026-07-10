package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class AdvancedPoweredFurnaceScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedPoweredFurnaceMenu> {
    public AdvancedPoweredFurnaceScreen(AdvancedPoweredFurnaceMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_powered_furnace.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/advanced_powered_furnace.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity_1_furnace_mode_1_item_ejector_1_item_pulling_1_xp_extraction.png"));
    }

    @Override
    protected void extractBackgroundNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(guiGraphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(menu.hasXPExtractionUpgradeModule()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 151, y + 16, 116, 0, 18, 54, 256, 256);
            renderFluidMeterContent(guiGraphics, menu.getFluid(), menu.getTankCapacity(), x + 152, y + 17, 16, 52);
            renderFluidMeterOverlay(guiGraphics, x, y);
        }

        renderProgressArrows(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 152, y + 17, 16, 0, 16, 52, 256, 256);
    }

    private void renderProgressArrows(GuiGraphicsExtractor guiGraphics, int x, int y) {
        for(int i = 0;i < 3;i++)
            if(menu.isCraftingActive(i))
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 45 + 36 * i, y + 35, 0, 79, 12, menu.getScaledProgressArrowSize(i), 256, 256);
    }

    @Override
    protected void extractTooltipNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltipNormalView(guiGraphics, mouseX, mouseY);

        if(isHovering(152, 17, 16, 52, mouseX, mouseY) && menu.hasXPExtractionUpgradeModule()) {
            renderFluidMeterContentTooltip(guiGraphics, menu.getFluid(), menu.getTankCapacity(), mouseX, mouseY);
        }
    }
}
