package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EnergizerScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<EnergizerMenu> {
    public EnergizerScreen(EnergizerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/energizer.png"),
                8, 17,
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/upgrade_view/1_energy_capacity.png"));
    }

    @Override
    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderProgressArrow(drawContext, x, y);
        renderActiveOverlay(drawContext, x, y);
    }

    private void renderProgressArrow(DrawContext drawContext, int x, int y) {
        if(handler.isCraftingActive())
            drawContext.drawTexture(TEXTURE, x + 89, y + 34, 176, 53, handler.getScaledProgressArrowSize(), 17);
    }

    private void renderActiveOverlay(DrawContext drawContext, int x, int y) {
        if(handler.isCrafting()) {
            drawContext.drawTexture(TEXTURE, x + 31, y + 18, 176, 70, 50, 50);
        }
    }
}
