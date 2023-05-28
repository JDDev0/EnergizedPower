package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGenericEnergyStorageContainerScreen<T extends AbstractContainerMenu & EnergyStorageMenu> extends AbstractContainerScreen<T> {
    protected final ResourceLocation TEXTURE;
    protected final int energyMeterX;
    protected final int energyMeterY;
    protected final int energyMeterWidth;
    protected final int energyMeterHeight;

    protected final String energyIndicatorBarTooltipComponentID;

    public AbstractGenericEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent) {
        this(menu, inventory, titleComponent, null);
    }

    public AbstractGenericEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                       String energyIndicatorBarTooltipComponentID) {
        this(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID,
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/generic_energy.png"),
                80, 17);
    }

    public AbstractGenericEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                       ResourceLocation texture, int energyMeterX, int energyMeterY) {
        this(menu, inventory, titleComponent, null, texture, energyMeterX, energyMeterY);
    }

    public AbstractGenericEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                       String energyIndicatorBarTooltipComponentID,
                                                       ResourceLocation texture, int energyMeterX, int energyMeterY) {
        this(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture,
                energyMeterX, energyMeterY, 16, 52);
    }

    public AbstractGenericEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                       String energyIndicatorBarTooltipComponentID,
                                                       ResourceLocation texture, int energyMeterX, int energyMeterY,
                                                       int energyMeterWidth, int energyMeterHeight) {
        super(menu, inventory, titleComponent);

        this.TEXTURE = texture;

        this.energyMeterX = energyMeterX;
        this.energyMeterY = energyMeterY;
        this.energyMeterWidth = energyMeterWidth;
        this.energyMeterHeight = energyMeterHeight;

        this.energyIndicatorBarTooltipComponentID = energyIndicatorBarTooltipComponentID;
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
        renderEnergyIndicatorBar(poseStack, x, y);
    }

    protected void renderEnergyMeter(PoseStack poseStack, int x, int y) {
        int pos = menu.getScaledEnergyMeterPos(energyMeterHeight);
        blit(poseStack, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, 176,
                energyMeterHeight - pos, energyMeterWidth, pos);
    }

    protected void renderEnergyIndicatorBar(PoseStack poseStack, int x, int y) {
        int pos = menu.getScaledEnergyIndicatorBarPos(energyMeterHeight);
        if(pos > 0)
            blit(poseStack, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, 176,
                    energyMeterHeight, energyMeterWidth, 1);
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

        if(isHovering(energyMeterX, energyMeterY, energyMeterWidth, energyMeterHeight, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(new TranslatableComponent("tooltip.energizedpower.energy_meter.content.txt",
                    EnergyUtils.getEnergyWithPrefix(menu.getEnergy()), EnergyUtils.getEnergyWithPrefix(menu.getCapacity())));
            if(menu.getEnergyIndicatorBarValue() > 0 && energyIndicatorBarTooltipComponentID != null) {
                components.add(new TranslatableComponent(energyIndicatorBarTooltipComponentID,
                        EnergyUtils.getEnergyWithPrefix(menu.getEnergyIndicatorBarValue())).withStyle(ChatFormatting.YELLOW));
            }

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
