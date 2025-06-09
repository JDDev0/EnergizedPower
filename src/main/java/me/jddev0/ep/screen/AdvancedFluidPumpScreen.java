package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AdvancedFluidPumpScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedFluidPumpMenu> {
    public static final boolean SHOW_RELATIVE_COORDINATES = ModConfigs.CLIENT_FLUID_PUMP_RELATIVE_TARGET_COORDINATES.getValue();

    public AdvancedFluidPumpScreen(AdvancedFluidPumpMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.fluid_pump.process_energy_left.txt",
                EPAPI.id("textures/gui/container/advanced_fluid_pump.png"),
                EPAPI.id("textures/gui/container/upgrade_view/advanced_fluid_pump.png"));

        backgroundWidth = 248;
    }

    @Override
    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        for(int i = 0;i < 4;i++) {
            renderFluidMeterContent(drawContext, handler.getFluid(i), handler.getTankCapacity(i), x + 206 + (i%2) * 18, y + 17 + (i/2) * 54, 16, 52);
            renderFluidMeterOverlay(drawContext, x, y, i);
        }

        renderInfoText(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(DrawContext drawContext, int x, int y, int tank) {
        drawContext.drawTexture(MACHINE_SPRITES_TEXTURE, x + 206 + (tank%2) * 18, y + 17 + (tank/2) * 54, 16, 0, 16, 52, 256, 256);
    }

    private void renderInfoText(DrawContext drawContext, int x, int y) {
        BlockPos targetPos = handler.getBlockEntity().getPos().add(handler.getTargetOffset());

        Text component;
        if(SHOW_RELATIVE_COORDINATES) {
            component = Text.translatable("tooltip.energizedpower.fluid_pump.target_relative",
                    String.format(Locale.ENGLISH, "%+d", handler.getTargetOffset().getX()),
                    String.format(Locale.ENGLISH, "%+d", handler.getTargetOffset().getY()),
                    String.format(Locale.ENGLISH, "%+d", handler.getTargetOffset().getZ()));
        }else {
            component = Text.translatable("tooltip.energizedpower.fluid_pump.target",
                    targetPos.getX(), targetPos.getY(), targetPos.getZ());
        }

        int componentWidth = textRenderer.getWidth(component);

        drawContext.drawText(textRenderer, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 22, 0, false);


        if(handler.getSlot(4 * 9).getStack().isEmpty()) {
            component = Text.translatable("tooltip.energizedpower.fluid_pump.cobblestone_missing").
                    formatted(Formatting.RED);

            componentWidth = textRenderer.getWidth(component);

            drawContext.drawText(textRenderer, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 58, 0, false);
        }else if(handler.isExtractingFluid()) {
            FluidState targetFluidState = handler.getBlockEntity().getWorld().getFluidState(targetPos);
            if(!targetFluidState.isEmpty()) {
                component = Text.translatable("tooltip.energizedpower.fluid_pump.extracting",
                        Text.translatable(new FluidStack(targetFluidState.getFluid(), 1).getTranslationKey()));

                componentWidth = textRenderer.getWidth(component);

                drawContext.drawText(textRenderer, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 58, 0, false);
            }
        }
    }

    @Override
    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipNormalView(drawContext, mouseX, mouseY);

        for(int i = 0;i < 4;i++) {
            if(isPointWithinBounds(206 + (i%2) * 18, 17 + (i/2) * 54, 16, 52, mouseX, mouseY)) {
                //Fluid meter

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
