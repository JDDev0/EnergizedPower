package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AdvancedPulverizerScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedPulverizerMenu> {
    public AdvancedPulverizerScreen(AdvancedPulverizerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/advanced_pulverizer.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected void extractBackgroundNormalView(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(drawContext, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        for(int i = 0;i < 2;i++) {
            renderFluidMeterContent(drawContext, menu.getFluid(i), menu.getTankCapacity(i), x + (i == 0?44:152), y + 17, 16, 52);
            renderFluidMeterOverlay(i, drawContext, x, y);
        }

        renderProgressArrow(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(int tank, GuiGraphicsExtractor drawContext, int x, int y) {
        drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + (tank == 0?44:152), y + 17, 16, 0, 16, 52, 256, 256);
    }

    private void renderProgressArrow(GuiGraphicsExtractor drawContext, int x, int y) {
        if(menu.isCraftingActive())
            drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 90, y + 34, 0, 58, menu.getScaledProgressArrowSize(), 17, 256, 256);
    }

    @Override
    protected void extractLabelsNormalView(GuiGraphicsExtractor drawContext, int mouseX, int mouseY) {
        super.extractLabelsNormalView(drawContext, mouseX, mouseY);

        for(int i = 0;i < 2;i++) {
            //Fluid meter

            if(isHovering(i == 0?44:152, 17, 16, 52, mouseX, mouseY)) {
                List<Component> components = new ArrayList<>(2);

                boolean fluidEmpty =  menu.getFluid(i).isEmpty();

                long fluidAmount = fluidEmpty?0:menu.getFluid(i).getMilliBucketsAmount();

                Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                        FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(FluidUtils.
                                convertDropletsToMilliBuckets(menu.getTankCapacity(i))));

                if(!fluidEmpty) {
                    tooltipComponent = Component.translatable(menu.getFluid(i).getTranslationKey()).append(" ").
                            append(tooltipComponent);
                }

                components.add(tooltipComponent);

                drawContext.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
