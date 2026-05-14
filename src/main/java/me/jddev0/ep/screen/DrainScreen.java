package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class DrainScreen extends EnergizedPowerBaseContainerScreen<DrainMenu> {
    private final ResourceLocation TEXTURE;

    public DrainScreen(DrainMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/generic_fluid.png");
    }

    @Override
    protected void renderBg(GuiGraphics drawContext, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        drawContext.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderFluidMeterContent(drawContext, menu.getFluid(), menu.getTankCapacity(), x + 80, y + 17, 16, 52);
        renderFluidMeterOverlay(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphics drawContext, int x, int y) {
        drawContext.blit(MACHINE_SPRITES_TEXTURE, x + 80, y + 17, 16, 0, 16, 52);
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        renderTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics drawContext, int mouseX, int mouseY) {
        super.renderTooltip(drawContext, mouseX, mouseY);

        if(isHovering(80, 17, 16, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Component> components = new ArrayList<>(2);

            boolean fluidEmpty =  menu.getFluid().isEmpty();

            long fluidAmount = fluidEmpty?0:menu.getFluid().getMilliBucketsAmount();

            Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                    FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(FluidUtils.
                            convertDropletsToMilliBuckets(menu.getTankCapacity())));

            if(!fluidEmpty) {
                tooltipComponent = Component.translatable(menu.getFluid().getTranslationKey()).append(" ").
                        append(tooltipComponent);
            }

            components.add(tooltipComponent);

            drawContext.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
