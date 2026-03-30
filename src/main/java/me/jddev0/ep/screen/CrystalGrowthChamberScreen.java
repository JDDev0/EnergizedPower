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
public class CrystalGrowthChamberScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<CrystalGrowthChamberMenu> {
    public CrystalGrowthChamberScreen(CrystalGrowthChamberMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/crystal_growth_chamber.png"),
                EPAPI.id("textures/gui/container/upgrade_view/2_speed_2_energy_efficiency_2_energy_capacity.png"));
    }


    @Override
    protected void extractBackgroundNormalView(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(drawContext, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(drawContext, x, y);
    }

    private void renderProgressArrow(GuiGraphicsExtractor drawContext, int x, int y) {
        if(menu.isCraftingActive())
            drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 80, y + 34, 0, 58, menu.getScaledProgressArrowSize(), 17, 256, 256);
    }
}
