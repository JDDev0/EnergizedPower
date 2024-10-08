package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AdvancedPulverizerScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedPulverizerMenu> {
    public AdvancedPulverizerScreen(AdvancedPulverizerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/advanced_pulverizer.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        for(int i = 0;i < 2;i++) {
            renderFluidMeterContent(drawContext, handler.getFluid(i), handler.getTankCapacity(i), x + (i == 0?44:152), y + 17, 16, 52);
            renderFluidMeterOverlay(i, drawContext, x, y);
        }

        renderProgressArrow(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(int tank, DrawContext drawContext, int x, int y) {
        drawContext.drawTexture(TEXTURE, x + (tank == 0?44:152), y + 17, 176, 53, 16, 52);
    }

    private void renderProgressArrow(DrawContext drawContext, int x, int y) {
        if(handler.isCraftingActive())
            drawContext.drawTexture(TEXTURE, x + 90, y + 34, 176, 106, handler.getScaledProgressArrowSize(), 17);
    }

    @Override
    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipNormalView(drawContext, mouseX, mouseY);

        for(int i = 0;i < 2;i++) {
            //Fluid meter

            if(isPointWithinBounds(i == 0?44:152, 17, 16, 52, mouseX, mouseY)) {
                List<Text> components = new ArrayList<>(2);

                boolean fluidEmpty =  handler.getFluid(i).isEmpty();

                long fluidAmount = fluidEmpty?0:handler.getFluid(i).getMilliBucketsAmount();

                Text tooltipComponent = Text.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                        FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(FluidUtils.
                                convertDropletsToMilliBuckets(handler.getTankCapacity(i))));

                if(!fluidEmpty) {
                    tooltipComponent = Text.translatable(handler.getFluid(i).getTranslationKey()).append(" ").
                            append(tooltipComponent);
                }

                components.add(tooltipComponent);

                drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
