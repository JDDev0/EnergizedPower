package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FluidPumpScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<FluidPumpMenu> {
    public static final boolean SHOW_RELATIVE_COORDINATES = ModConfigs.CLIENT_FLUID_PUMP_RELATIVE_TARGET_COORDINATES.getValue();

    public FluidPumpScreen(FluidPumpMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.fluid_pump.process_energy_left.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/fluid_pump.png"),
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/upgrade_view/fluid_pump.png"));

        backgroundWidth = 230;
        energyMeterU = 230;
    }

    @Override
    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderFluidMeterContent(drawContext, handler.getFluid(), handler.getTankCapacity(), x + 206, y + 17, 16, 52);
        renderFluidMeterOverlay(drawContext, x, y);

        renderInfoText(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(DrawContext drawContext, int x, int y) {
        drawContext.drawTexture(TEXTURE, x + 206, y + 17, 230, 53, 16, 52);
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

        if(isPointWithinBounds(206, 17, 16, 52, mouseX, mouseY)) {
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
