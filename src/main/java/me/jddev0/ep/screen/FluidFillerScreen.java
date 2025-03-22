package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FluidFillerScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<FluidFillerMenu> {
    public FluidFillerScreen(FluidFillerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                EPAPI.id("textures/gui/container/fluid_filler.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderFluidMeterContent(drawContext, handler.getFluid(), handler.getTankCapacity(), x + 152, y + 17, 16, 52);
        renderFluidMeterOverlay(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(DrawContext drawContext, int x, int y) {
        drawContext.drawTexture(RenderLayer::getGuiTextured, MACHINE_SPRITES_TEXTURE, x + 152, y + 17, 16, 0, 16, 52, 256, 256);
    }

    @Override
    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipNormalView(drawContext, mouseX, mouseY);

        if(isPointWithinBounds(152, 17, 16, 52, mouseX, mouseY)) {
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
