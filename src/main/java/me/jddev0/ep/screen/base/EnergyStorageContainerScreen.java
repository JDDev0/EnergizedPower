package me.jddev0.ep.screen.base;

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
public abstract class EnergyStorageContainerScreen<T extends ScreenHandler & IEnergyStorageMenu> extends HandledScreen<T> {
    protected final Identifier TEXTURE;

    protected int energyMeterX = 8;
    protected int energyMeterY = 17;
    protected int energyMeterWidth = 16;
    protected int energyMeterHeight = 52;

    protected int energyMeterTextureX = 176;
    protected int energyMeterTextureY = 0;

    protected final String energyIndicatorBarTooltipComponentID;

    public EnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent) {
        this(menu, inventory, titleComponent, (String)null);
    }

    public EnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                        String energyIndicatorBarTooltipComponentID) {
        this(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID,
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/generic_energy.png"));
    }

    public EnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                        Identifier texture) {
        this(menu, inventory, titleComponent, null, texture);
    }

    public EnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                        String energyIndicatorBarTooltipComponentID,
                                        Identifier texture) {
        super(menu, inventory, titleComponent);

        this.TEXTURE = texture;

        this.energyIndicatorBarTooltipComponentID = energyIndicatorBarTooltipComponentID;
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        if(!handler.isInUpgradeModuleView()) {
            drawContext.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
            renderEnergyMeter(drawContext, x, y);
            renderEnergyIndicatorBar(drawContext, x, y);
        }
    }

    protected void renderEnergyMeter(DrawContext drawContext, int x, int y) {
        int pos = handler.getScaledEnergyMeterPos(energyMeterHeight);
        drawContext.drawTexture(TEXTURE, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, energyMeterTextureX,
                energyMeterTextureY + energyMeterHeight - pos, energyMeterWidth, pos);
    }

    protected void renderEnergyIndicatorBar(DrawContext drawContext, int x, int y) {
        int pos = handler.getScaledEnergyIndicatorBarPos(energyMeterHeight);
        if(pos > 0)
            drawContext.drawTexture(TEXTURE, x + energyMeterX, y + energyMeterY + energyMeterHeight - pos, energyMeterTextureX,
                    energyMeterTextureY + energyMeterHeight, energyMeterWidth, 1);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

        if(!handler.isInUpgradeModuleView()) {
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
}