package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.FluidState;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FluidPumpScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<FluidPumpMenu> {
    public static final boolean SHOW_RELATIVE_COORDINATES = ModConfigs.CLIENT_FLUID_PUMP_RELATIVE_TARGET_COORDINATES.getValue();

    public FluidPumpScreen(FluidPumpMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.fluid_pump.process_energy_left.txt",
                EPAPI.id("textures/gui/container/fluid_pump.png"),
                EPAPI.id("textures/gui/container/upgrade_view/fluid_pump.png"), 230, 166);
    }

    @Override
    protected void extractBackgroundNormalView(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(drawContext, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidMeterContent(drawContext, menu.getFluid(), menu.getTankCapacity(), x + 206, y + 17, 16, 52);
        renderFluidMeterOverlay(drawContext, x, y);

        renderInfoText(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphicsExtractor drawContext, int x, int y) {
        drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 206, y + 17, 16, 0, 16, 52, 256, 256);
    }

    private void renderInfoText(GuiGraphicsExtractor drawContext, int x, int y) {
        BlockPos targetPos = menu.getBlockEntity().getBlockPos().offset(menu.getTargetOffset());

        Component component;
        if(SHOW_RELATIVE_COORDINATES) {
            component = Component.translatable("tooltip.energizedpower.fluid_pump.target_relative",
                    String.format(Locale.ENGLISH, "%+d", menu.getTargetOffset().getX()),
                    String.format(Locale.ENGLISH, "%+d", menu.getTargetOffset().getY()),
                    String.format(Locale.ENGLISH, "%+d", menu.getTargetOffset().getZ()));
        }else {
            component = Component.translatable("tooltip.energizedpower.fluid_pump.target",
                    targetPos.getX(), targetPos.getY(), targetPos.getZ());
        }

        int componentWidth = font.width(component);

        drawContext.text(font, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 22, 0xFF000000, false);


        if(menu.getSlot(4 * 9).getItem().isEmpty()) {
            component = Component.translatable("tooltip.energizedpower.fluid_pump.cobblestone_missing").
                    withStyle(ChatFormatting.RED);

            componentWidth = font.width(component);

            drawContext.text(font, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 58, 0xFF000000, false);
        }else if(menu.isExtractingFluid()) {
            FluidState targetFluidState = menu.getBlockEntity().getLevel().getFluidState(targetPos);
            if(!targetFluidState.isEmpty()) {
                component = Component.translatable("tooltip.energizedpower.fluid_pump.extracting",
                        Component.translatable(new FluidStack(targetFluidState.getType(), 1).getTranslationKey()));

                componentWidth = font.width(component);

                drawContext.text(font, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 58, 0xFF000000, false);
            }
        }
    }

    @Override
    protected void extractLabelsNormalView(GuiGraphicsExtractor drawContext, int mouseX, int mouseY) {
        super.extractLabelsNormalView(drawContext, mouseX, mouseY);

        if(isHovering(206, 17, 16, 52, mouseX, mouseY)) {
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

            drawContext.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
