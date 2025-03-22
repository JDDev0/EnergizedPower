package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class DrainScreen extends EnergizedPowerBaseContainerScreen<DrainMenu> {
    private final Identifier TEXTURE;

    public DrainScreen(DrainMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/generic_fluid.png");
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawContext.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        renderFluidMeterContent(drawContext, handler.getFluid(), handler.getTankCapacity(), x + 80, y + 17, 16, 52);
        renderFluidMeterOverlay(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(DrawContext drawContext, int x, int y) {
        drawContext.drawTexture(RenderLayer::getGuiTextured, MACHINE_SPRITES_TEXTURE, x + 80, y + 17, 16, 0, 16, 52, 256, 256);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

        if(isPointWithinBounds(80, 17, 16, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Text> components = new ArrayList<>(2);

            boolean fluidEmpty =  handler.getFluid().isEmpty();

            long fluidAmount = fluidEmpty?0:handler.getFluid().getMilliBucketsAmount();

            Text tooltipComponent = Text.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                    FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(FluidUtils.
                            convertDropletsToMilliBuckets(handler.getTankCapacity())));

            if(!fluidEmpty) {
                tooltipComponent = Text.translatable(handler.getFluid().getTranslationKey()).append(" ").
                        append(tooltipComponent);
            }

            components.add(tooltipComponent);

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
