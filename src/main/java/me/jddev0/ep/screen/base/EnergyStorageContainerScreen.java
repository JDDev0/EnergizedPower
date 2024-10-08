package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public abstract class EnergyStorageContainerScreen<T extends AbstractContainerMenu & IEnergyStorageMenu> extends EnergizedPowerBaseContainerScreen<T> {
    protected final ResourceLocation TEXTURE;

    protected int energyMeterX = 8;
    protected int energyMeterY = 17;
    protected int energyMeterWidth = 16;
    protected int energyMeterHeight = 52;

    protected int energyMeterU = 176;
    protected int energyMeterV = 0;

    protected final String energyIndicatorBarTooltipComponentID;

    public EnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent) {
        this(menu, inventory, titleComponent, (String)null);
    }

    public EnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                        String energyIndicatorBarTooltipComponentID) {
        this(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID,
                EPAPI.id("textures/gui/container/generic_energy.png"));
    }

    public EnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                        ResourceLocation texture) {
        this(menu, inventory, titleComponent, null, texture);
    }

    public EnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                        String energyIndicatorBarTooltipComponentID,
                                        ResourceLocation texture) {
        super(menu, inventory, titleComponent);

        this.TEXTURE = texture;

        this.energyIndicatorBarTooltipComponentID = energyIndicatorBarTooltipComponentID;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(!menu.isInUpgradeModuleView()) {
            blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
            renderEnergyMeter(poseStack, x, y);
            renderEnergyIndicatorBar(poseStack, x, y);
        }
    }

    protected void renderEnergyMeter(PoseStack poseStack, int x, int y) {
        int pos = menu.getScaledEnergyMeterPos(energyMeterHeight);
        blit(poseStack, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, energyMeterU,
                energyMeterV + energyMeterHeight - pos, energyMeterWidth, pos);
    }

    protected void renderEnergyIndicatorBar(PoseStack poseStack, int x, int y) {
        int pos = menu.getScaledEnergyIndicatorBarPos(energyMeterHeight);
        if(pos > 0)
            blit(poseStack, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, energyMeterU,
                    energyMeterV + energyMeterHeight, energyMeterWidth, 1);
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

        if(!menu.isInUpgradeModuleView()) {
            if(isHovering(energyMeterX, energyMeterY, energyMeterWidth, energyMeterHeight, mouseX, mouseY)) {
                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(menu.getEnergy()), EnergyUtils.getEnergyWithPrefix(menu.getCapacity())));
                if(menu.getEnergyIndicatorBarValue() > 0 && energyIndicatorBarTooltipComponentID != null) {
                    components.add(Component.translatable(energyIndicatorBarTooltipComponentID,
                            EnergyUtils.getEnergyWithPrefix(menu.getEnergyIndicatorBarValue())).withStyle(ChatFormatting.YELLOW));
                }

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
