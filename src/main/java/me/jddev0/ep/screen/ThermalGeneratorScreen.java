package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class ThermalGeneratorScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<ThermalGeneratorMenu> {
    public ThermalGeneratorScreen(ThermalGeneratorMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.thermal_generator.txt",
                EPAPI.id("textures/gui/container/thermal_generator.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_energy_capacity.png"));

        energyPerTickBarTooltipComponentID = "tooltip.energizedpower.energy_production_per_tick.txt";
    }

    @Override
    protected void renderBgNormalView(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidMeterContent(guiGraphics, menu.getFluid(), menu.getTankCapacity(), x + 80, y + 17, 16, 52);
        renderFluidMeterOverlay(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(RenderType::guiTextured, MACHINE_SPRITES_TEXTURE, x + 80, y + 17, 16, 0, 16, 52, 256, 256);
    }

    @Override
    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        if(isHovering(80, 17, 16, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Component> components = new ArrayList<>(2);

            boolean fluidEmpty =  menu.getFluid().isEmpty();

            int fluidAmount = fluidEmpty?0:menu.getFluid().getAmount();

            Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                    FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(menu.getTankCapacity()));

            if(!fluidEmpty) {
                tooltipComponent = Component.translatable(menu.getFluid().getDescriptionId()).append(" ").
                        append(tooltipComponent);
            }

            components.add(tooltipComponent);

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
