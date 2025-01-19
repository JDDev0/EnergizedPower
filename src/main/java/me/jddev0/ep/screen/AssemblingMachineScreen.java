package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class AssemblingMachineScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<AssemblingMachineMenu> {
    public AssemblingMachineScreen(AssemblingMachineMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/assembling_machine.png"),
                EPAPI.id("textures/gui/container/upgrade_view/assembling_machine.png"));

        backgroundHeight = 170;
        playerInventoryTitleY = backgroundHeight - 94;

        energyMeterY = 19;
    }

    @Override
    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderProgressArrow(drawContext, x, y);
    }

    private void renderProgressArrow(DrawContext drawContext, int x, int y) {
        if(handler.isCraftingActive())
            drawContext.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x + 100, y + 36, 176, 53, handler.getScaledProgressArrowSize(), 17, 256, 256);
    }
}
