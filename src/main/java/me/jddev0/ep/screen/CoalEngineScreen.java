package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CoalEngineScreen extends HandledScreen<CoalEngineMenu> {
    private static final Identifier TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/coal_engine.png");

    public CoalEngineScreen(CoalEngineMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void drawBackground(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawTexture(poseStack, x, y, 0, 0, backgroundWidth, backgroundHeight);
        renderProgressFlame(poseStack, x, y);
        renderEnergyMeter(poseStack, x, y);
        renderEnergyProductionBar(poseStack, x, y);
    }

    private void renderProgressFlame(MatrixStack poseStack, int x, int y) {
        if(handler.isProducingActive()) {
            int pos = handler.getScaledProgressFlameSize();
            drawTexture(poseStack, x + 81, y + 28 + pos, 176, 53 + pos, 14, 14 - pos);
        }
    }

    private void renderEnergyMeter(MatrixStack poseStack, int x, int y) {
        int pos = handler.getScaledEnergyMeterPos();
        drawTexture(poseStack, x + 8, y + 17 + 52 - pos, 176, 52 - pos, 16, pos);
    }

    private void renderEnergyProductionBar(MatrixStack poseStack, int x, int y) {
        int pos = handler.getEnergyProductionBarPos();
        if(pos > 0)
            drawTexture(poseStack, x + 8, y + 17 + 52 - pos, 176, 52, 16, 1);
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);

        super.render(poseStack, mouseX, mouseY, delta);

        drawMouseoverTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack poseStack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(poseStack, mouseX, mouseY);

        if(isPointWithinBounds(8, 17, 16, 52, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.energy_meter.content.txt",
                    EnergyUtils.getEnergyWithPrefix(handler.getEnergy()), EnergyUtils.getEnergyWithPrefix(handler.getCapacity())));
            if(handler.getEnergyProduction() > 0) {
                components.add(Text.translatable("tooltip.energizedpower.coal_engine.txt",
                        EnergyUtils.getEnergyWithPrefix(handler.getEnergyProduction())).formatted(Formatting.YELLOW));
            }

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
