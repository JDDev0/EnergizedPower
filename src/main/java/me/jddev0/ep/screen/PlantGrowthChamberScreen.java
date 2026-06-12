package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class PlantGrowthChamberScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<PlantGrowthChamberMenu> {
    public PlantGrowthChamberScreen(PlantGrowthChamberMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/plant_growth_chamber.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity_1_item_ejector_1_item_pulling.png"));
    }

    @Override
    protected void renderBgNormalView(GuiGraphics drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidMeterContent(drawContext, menu.getFluid(), menu.getTankCapacity(), x + 44, y + 17, 16, 52);
        renderFluidMeterOverlay(drawContext, x, y);

        renderProgressArrow(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 44, y + 17, 16, 0, 16, 52, 256, 256);
    }

    private void renderProgressArrow(GuiGraphics drawContext, int x, int y) {
        if(menu.isCraftingActive())
            drawContext.blit(MACHINE_SPRITES_TEXTURE, x + 103, y + 34, 0, 58, menu.getScaledProgressArrowSize(), 17);
    }

    @Override
    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        if(isHovering(44, 17, 16, 52, mouseX, mouseY)) {
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

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
