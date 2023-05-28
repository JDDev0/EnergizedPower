package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CoalEngineScreen extends AbstractGenericEnergyStorageHandledScreen<CoalEngineMenu> {
    public CoalEngineScreen(CoalEngineMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.coal_engine.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/coal_engine.png"),
                8, 17);
    }

    @Override
    protected void drawBackground(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(poseStack, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderProgressFlame(poseStack, x, y);
    }

    private void renderProgressFlame(MatrixStack poseStack, int x, int y) {
        if(handler.isProducingActive()) {
            int pos = handler.getScaledProgressFlameSize();
            drawTexture(poseStack, x + 81, y + 28 + pos, 176, 53 + pos, 14, 14 - pos);
        }
    }
}
