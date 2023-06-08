package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        renderEnergyMeter(guiGraphics, x, y);
        renderEnergyIndicatorBar(guiGraphics, x, y);
    }

    protected void renderEnergyMeter(GuiGraphics guiGraphics, int x, int y) {
        int pos = menu.getScaledEnergyMeterPos(energyMeterHeight);
        guiGraphics.blit(TEXTURE, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, 176,
                energyMeterHeight - pos, energyMeterWidth, pos);
    }

    protected void renderEnergyIndicatorBar(GuiGraphics guiGraphics, int x, int y) {
        int pos = menu.getScaledEnergyIndicatorBarPos(energyMeterHeight);
        if(pos > 0)
            guiGraphics.blit(TEXTURE, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, 176,
                    energyMeterHeight, energyMeterWidth, 1);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, delta);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isHovering(energyMeterX, energyMeterY, energyMeterWidth, energyMeterHeight, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.energy_meter.content.txt",
                    EnergyUtils.getEnergyWithPrefix(menu.getEnergy()), EnergyUtils.getEnergyWithPrefix(menu.getCapacity())));
            if(menu.getEnergyIndicatorBarValue() > 0 && energyIndicatorBarTooltipComponentID != null) {
                components.add(Component.translatable(energyIndicatorBarTooltipComponentID,
                        EnergyUtils.getEnergyWithPrefix(menu.getEnergyIndicatorBarValue())).withStyle(ChatFormatting.YELLOW));
            }

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
