package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class AdvancedPoweredFurnaceScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedPoweredFurnaceMenu> {
    public AdvancedPoweredFurnaceScreen(AdvancedPoweredFurnaceMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_powered_furnace.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/advanced_powered_furnace.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity_1_furnace_mode.png"));
    }

    @Override
    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderProgressArrows(drawContext, x, y);
    }

    private void renderProgressArrows(DrawContext drawContext, int x, int y) {
        for(int i = 0;i < 3;i++)
            if(handler.isCraftingActive(i))
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 45 + 54 * i, y + 35, 0, 79, 12, handler.getScaledProgressArrowSize(i), 256, 256);
    }
}
