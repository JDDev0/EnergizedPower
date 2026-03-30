package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FluidTransposerScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<FluidTransposerMenu> {
    public FluidTransposerScreen(FluidTransposerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/fluid_transposer.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isHovering(114, 47, 20, 20, mouseX, mouseY)) {
                //Mode button

                ModMessages.sendClientPacketToServer(new SetCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), 0,
                        menu.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void extractBackgroundNormalView(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(drawContext, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidMeterContent(drawContext, menu.getFluid(), menu.getTankCapacity(), x + 152, y + 17, 16, 52);
        renderFluidMeterOverlay(drawContext, x, y);

        renderButtons(drawContext, x, y, mouseX, mouseY);
        renderProgressArrow(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphicsExtractor drawContext, int x, int y) {
        drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 152, y + 17, 16, 0, 16, 52, 256, 256);
    }

    private void renderButtons(GuiGraphicsExtractor drawContext, int x, int y, int mouseX, int mouseY) {
        if(isHovering(114, 47, 20, 20, mouseX, mouseY))
            drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 114, y + 47, 0, 211, 20, 20, 256, 256);

        ItemStack output = new ItemStack(menu.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?Items.BUCKET:Items.WATER_BUCKET);
        drawContext.pose().pushMatrix();

        drawContext.item(output, x + 116, y + 49, 116 + 49 * this.imageWidth);

        drawContext.pose().popMatrix();
    }

    private void renderProgressArrow(GuiGraphicsExtractor drawContext, int x, int y) {
        int arrowPosY = menu.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?58:72;

        drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 114, y + 19, 52, arrowPosY, 20, 14, 256, 256);

        if(menu.isCraftingActive()) {
            if(menu.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING)
                drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 114, y + 19, 72, arrowPosY, menu.getScaledProgressArrowSize(), 14, 256, 256);
            else
                drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 134 - menu.getScaledProgressArrowSize(), y + 19,
                        92 - menu.getScaledProgressArrowSize(), arrowPosY, menu.getScaledProgressArrowSize(), 14, 256, 256);
        }
    }

    @Override
    protected void extractLabelsNormalView(GuiGraphicsExtractor drawContext, int mouseX, int mouseY) {
        super.extractLabelsNormalView(drawContext, mouseX, mouseY);

        if(isHovering(152, 17, 16, 52, mouseX, mouseY)) {
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

        if(isHovering(114, 47, 20, 20, mouseX, mouseY)) {
            //Mode button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.fluid_transposer.mode." +
                    menu.getMode().getSerializedName()));

            drawContext.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
