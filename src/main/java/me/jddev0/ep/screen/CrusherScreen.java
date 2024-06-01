package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CrusherScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<CrusherMenu> {
    public CrusherScreen(CrusherMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/crusher.png"),
                new Identifier(EnergizedPowerMod.MODID,
                        "textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected void renderTooltipNormalView(MatrixStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderProgressArrow(poseStack, x, y);
    }

    private void renderProgressArrow(MatrixStack poseStack, int x, int y) {
        if(handler.isCraftingActive())
            drawTexture(poseStack, x + 80, y + 34, 176, 53, handler.getScaledProgressArrowSize(), 17);
    }
}
