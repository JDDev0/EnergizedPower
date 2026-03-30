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
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity_1_furnace_mode.png"));
    }

    @Override
    protected void extractBackgroundNormalView(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(drawContext, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrows(drawContext, x, y);
    }

    private void renderProgressArrows(GuiGraphicsExtractor drawContext, int x, int y) {
        for(int i = 0;i < 3;i++)
            if(menu.isCraftingActive(i))
                drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 45 + 54 * i, y + 35, 0, 79, 12, menu.getScaledProgressArrowSize(i), 256, 256);
    }
}
