package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class UnchargerScreen extends AbstractContainerScreen<UnchargerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/uncharger.png");

    public UnchargerScreen(UnchargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
        renderEnergyMeter(poseStack, x, y);
        renderEnergyRequirementBar(poseStack, x, y);
    }

    private void renderEnergyMeter(PoseStack poseStack, int x, int y) {
        int pos = menu.getScaledEnergyMeterPos();
        blit(poseStack, x + 8, y + 17 + 52 - pos, 176, 52 - pos, 16, pos);
    }

    private void renderEnergyRequirementBar(PoseStack poseStack, int x, int y) {
        int pos = menu.getEnergyRequirementBarPos();
        if(pos > 0)
            blit(poseStack, x + 8, y + 17 + 52 - pos, 176, 52, 16, 1);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);

        super.render(poseStack, mouseX, mouseY, delta);

        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);

        if(isHovering(8, 17, 16, 52, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.energy_meter.content.txt",
                    EnergyUtils.getEnergyWithPrefix(menu.getEnergy()), EnergyUtils.getEnergyWithPrefix(menu.getCapacity())));
            if(menu.getEnergyProduction() > 0) {
                components.add(Component.translatable("tooltip.energizedpower.uncharger.item_energy_left.txt",
                        EnergyUtils.getEnergyWithPrefix(menu.getEnergyProduction())).withStyle(ChatFormatting.YELLOW));
            }

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
