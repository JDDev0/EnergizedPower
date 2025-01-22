package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(!menu.isInUpgradeModuleView()) {
            guiGraphics.blit(RenderType::guiTextured, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
            renderEnergyMeter(guiGraphics, x, y);
            renderEnergyIndicatorBar(guiGraphics, x, y);
        }
    }

    protected void renderEnergyMeter(GuiGraphics guiGraphics, int x, int y) {
        int pos = menu.getScaledEnergyMeterPos(energyMeterHeight);
        guiGraphics.blit(RenderType::guiTextured, TEXTURE, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, energyMeterU,
                energyMeterV + energyMeterHeight - pos, energyMeterWidth, pos, 256, 256);
    }

    protected void renderEnergyIndicatorBar(GuiGraphics guiGraphics, int x, int y) {
        int pos = menu.getScaledEnergyIndicatorBarPos(energyMeterHeight);
        if(pos > 0)
            guiGraphics.blit(RenderType::guiTextured, TEXTURE, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, energyMeterU,
                    energyMeterV + energyMeterHeight, energyMeterWidth, 1, 256, 256);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(!menu.isInUpgradeModuleView()) {
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
}
