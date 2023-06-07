package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class AbstractGenericEnergyStorageHandledScreen<T extends ScreenHandler & EnergyStorageMenu> extends HandledScreen<T> {
    protected final Identifier TEXTURE;
    protected final int energyMeterX;
    protected final int energyMeterY;
    protected final int energyMeterWidth;
    protected final int energyMeterHeight;

    protected final String energyIndicatorBarTooltipComponentID;

    public AbstractGenericEnergyStorageHandledScreen(T menu, PlayerInventory inventory, Text component) {
        this(menu, inventory, component, null);
    }

    public AbstractGenericEnergyStorageHandledScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                     String energyIndicatorBarTooltipComponentID) {
        this(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID,
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/generic_energy.png"),
                80, 17);
    }

    public AbstractGenericEnergyStorageHandledScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                     Identifier texture, int energyMeterX, int energyMeterY) {
        this(menu, inventory, titleComponent, null, texture, energyMeterX, energyMeterY);
    }

    public AbstractGenericEnergyStorageHandledScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                     String energyIndicatorBarTooltipComponentID,
                                                     Identifier texture, int energyMeterX, int energyMeterY) {
        this(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture,
                energyMeterX, energyMeterY, 16, 52);
    }

    public AbstractGenericEnergyStorageHandledScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                     String energyIndicatorBarTooltipComponentID,
                                                     Identifier texture, int energyMeterX, int energyMeterY,
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
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawContext.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        renderEnergyMeter(drawContext, x, y);
        renderEnergyIndicatorBar(drawContext, x, y);
    }

    protected void renderEnergyMeter(DrawContext drawContext, int x, int y) {
        int pos = handler.getScaledEnergyMeterPos(energyMeterHeight);
        drawContext.drawTexture(TEXTURE, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, 176,
                energyMeterHeight - pos, energyMeterWidth, pos);
    }

    protected void renderEnergyIndicatorBar(DrawContext drawContext, int x, int y) {
        int pos = handler.getScaledEnergyIndicatorBarPos(energyMeterHeight);
        if(pos > 0)
            drawContext.drawTexture(TEXTURE, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, 176,
                    energyMeterHeight, energyMeterWidth, 1);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);

        super.render(drawContext, mouseX, mouseY, delta);

        drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

        if(isPointWithinBounds(energyMeterX, energyMeterY, energyMeterWidth, energyMeterHeight, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.energy_meter.content.txt",
                    EnergyUtils.getEnergyWithPrefix(handler.getEnergy()), EnergyUtils.getEnergyWithPrefix(handler.getCapacity())));
            if(handler.getEnergyIndicatorBarValue() > 0 && energyIndicatorBarTooltipComponentID != null) {
                components.add(Text.translatable(energyIndicatorBarTooltipComponentID,
                        EnergyUtils.getEnergyWithPrefix(handler.getEnergyIndicatorBarValue())).formatted(Formatting.YELLOW));
            }

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
