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
public class CoalEngineScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<CoalEngineMenu> {
    public CoalEngineScreen(CoalEngineMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.coal_engine.txt",
                Identifier.of(EnergizedPowerMod.MODID, "textures/gui/container/coal_engine.png"),
                Identifier.of(EnergizedPowerMod.MODID, "textures/gui/container/upgrade_view/1_energy_capacity.png"));
    }

    @Override
    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipNormalView(drawContext, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderProgressFlame(drawContext, x, y);
    }

    private void renderProgressFlame(DrawContext drawContext, int x, int y) {
        if(handler.isProducingActive()) {
            int pos = handler.getScaledProgressFlameSize();
            drawContext.drawTexture(TEXTURE, x + 81, y + 28 + pos, 176, 53 + pos, 14, 14 - pos);
        }
    }
}
