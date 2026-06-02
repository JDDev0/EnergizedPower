package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class EnergizerScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<EnergizerMenu> {
    public EnergizerScreen(EnergizerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/energizer.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity_1_item_ejector_1_item_pulling.png"));
    }

    @Override
    protected void renderBgNormalView(GuiGraphics drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(drawContext, x, y);
        renderActiveOverlay(drawContext, x, y);
    }

    private void renderProgressArrow(GuiGraphics drawContext, int x, int y) {
        if(menu.isCraftingActive())
            drawContext.blit(MACHINE_SPRITES_TEXTURE, x + 89, y + 34, 0, 58, menu.getScaledProgressArrowSize(), 17);
    }

    private void renderActiveOverlay(GuiGraphics drawContext, int x, int y) {
        if(menu.isCrafting()) {
            drawContext.blit(MACHINE_SPRITES_TEXTURE, x + 31, y + 18, 96, 58, 50, 50);
        }
    }
}
