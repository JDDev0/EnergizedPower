package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CoalEngineScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<CoalEngineMenu> {
    public CoalEngineScreen(CoalEngineMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.coal_engine.txt",
                EPAPI.id("textures/gui/container/coal_engine.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity.png"));

        energyPerTickBarTooltipComponentID = "tooltip.energizedpower.energy_production_per_tick.txt";
    }

    @Override
    public void extractBackgroundNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(guiGraphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressFlame(guiGraphics, x, y);
    }

    private void renderProgressFlame(GuiGraphicsExtractor guiGraphics, int x, int y) {
        if(menu.isProducingActive()) {
            int pos = menu.getScaledProgressFlameSize();
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 81, y + 28 + pos, 0, 121 + pos, 14, 14 - pos, 256, 256);
        }
    }
}
