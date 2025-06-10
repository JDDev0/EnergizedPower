package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class AdvancedFluidPumpScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedFluidPumpMenu> {
    public static final boolean SHOW_RELATIVE_COORDINATES = ModConfigs.CLIENT_FLUID_PUMP_RELATIVE_TARGET_COORDINATES.getValue();

    public AdvancedFluidPumpScreen(AdvancedFluidPumpMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.fluid_pump.process_energy_left.txt",
                EPAPI.id("textures/gui/container/advanced_fluid_pump.png"),
                EPAPI.id("textures/gui/container/upgrade_view/advanced_fluid_pump.png"));

        imageWidth = 248;
    }

    @Override
    protected void renderBgNormalView(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        for(int i = 0;i < 4;i++) {
            renderFluidMeterContent(guiGraphics, menu.getFluid(i), menu.getTankCapacity(i), x + 206 + (i%2) * 18, y + 17 + (i/2) * 54, 16, 52);
            renderFluidMeterOverlay(guiGraphics, x, y, i);
        }

        renderInfoText(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphics guiGraphics, int x, int y, int tank) {
        guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 206 + (tank%2) * 18, y + 17 + (tank/2) * 54, 16, 0, 16, 52, 256, 256);
    }

    private void renderInfoText(GuiGraphics guiGraphics, int x, int y) {
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

        guiGraphics.drawString(font, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 22, 0, false);


        if(menu.getSlot(4 * 9).getItem().isEmpty()) {
            component = Component.translatable("tooltip.energizedpower.fluid_pump.cobblestone_missing").
                    withStyle(ChatFormatting.RED);

            componentWidth = font.width(component);

            guiGraphics.drawString(font, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 58, 0, false);
        }else if(menu.isExtractingFluid()) {
            FluidState targetFluidState = menu.getBlockEntity().getLevel().getFluidState(targetPos);
            if(!targetFluidState.isEmpty()) {
                component = Component.translatable("tooltip.energizedpower.fluid_pump.extracting",
                        Component.translatable(new FluidStack(targetFluidState.getType(), 1).getTranslationKey()));

                componentWidth = font.width(component);

                guiGraphics.drawString(font, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 58, 0, false);
            }
        }
    }

    @Override
    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        for(int i = 0;i < 4;i++) {
            if(isHovering(206 + (i%2) * 18, 17 + (i/2) * 54, 16, 52, mouseX, mouseY)) {
                //Fluid meter

                List<Component> components = new ArrayList<>(2);

                boolean fluidEmpty =  menu.getFluid(i).isEmpty();

                int fluidAmount = fluidEmpty?0:menu.getFluid(i).getAmount();

                Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                        FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(menu.getTankCapacity(i)));

                if(!fluidEmpty) {
                    tooltipComponent = Component.translatable(menu.getFluid(i).getTranslationKey()).append(" ").
                            append(tooltipComponent);
                }

                components.add(tooltipComponent);

                guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
