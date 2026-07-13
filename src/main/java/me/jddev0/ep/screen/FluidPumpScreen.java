package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.base.ConfigurableIOUpgradableEnergyStorageContainerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Locale;

public class FluidPumpScreen
        extends ConfigurableIOUpgradableEnergyStorageContainerScreen<FluidPumpMenu> {
    public static final boolean SHOW_RELATIVE_COORDINATES = ModConfigs.CLIENT_FLUID_PUMP_RELATIVE_TARGET_COORDINATES.getValue();

    public FluidPumpScreen(FluidPumpMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.fluid_pump.process_energy_left.txt",
                EPAPI.id("textures/gui/container/fluid_pump.png"),
                EPAPI.id("textures/gui/container/upgrade_view/fluid_pump.png"), 230, 166);

        ioConfigurationViewX = 33;
    }

    @Override
    public void extractBackgroundNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(guiGraphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidMeterContent(guiGraphics, menu.getFluid(), menu.getTankCapacity(), x + 206, y + 17, 16, 52);
        renderFluidMeterOverlay(guiGraphics, x, y);

        renderInfoText(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 206, y + 17, 16, 0, 16, 52, 256, 256);
    }

    private void renderInfoText(GuiGraphicsExtractor guiGraphics, int x, int y) {
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

        guiGraphics.text(font, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 22, 0xFF000000, false);


        if(menu.getSlot(4 * 9).getItem().isEmpty()) {
            component = Component.translatable("tooltip.energizedpower.fluid_pump.cobblestone_missing").
                    withStyle(ChatFormatting.RED);

            componentWidth = font.width(component);

            guiGraphics.text(font, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 58, 0xFF000000, false);
        }else if(menu.isExtractingFluid()) {
            FluidState targetFluidState = menu.getBlockEntity().getLevel().getFluidState(targetPos);
            if(!targetFluidState.isEmpty()) {
                component = Component.translatable("tooltip.energizedpower.fluid_pump.extracting",
                        Component.translatable(new FluidStack(targetFluidState.getType(), 1).getDescriptionId()));

                componentWidth = font.width(component);

                guiGraphics.text(font, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 58, 0xFF000000, false);
            }
        }
    }

    @Override
    protected void extractTooltipNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltipNormalView(guiGraphics, mouseX, mouseY);

        if(isHovering(206, 17, 16, 52, mouseX, mouseY)) {
            renderFluidMeterContentTooltip(guiGraphics, menu.getFluid(), menu.getTankCapacity(), mouseX, mouseY);
        }
    }

    @Override
    protected Rect getTankCords(int tank) {
        if(tank == 0)
            return new Rect(206, 17, 16, 52);

        return super.getTankCords(tank);
    }
}
