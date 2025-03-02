package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
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

                ModMessages.sendToServer(new SetCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), 0,
                        menu.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderBgNormalView(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidMeterContent(guiGraphics, menu.getFluid(), menu.getTankCapacity(), x + 152, y + 17, 16, 52);
        renderFluidMeterOverlay(guiGraphics, x, y);

        renderButtons(guiGraphics, x, y, mouseX, mouseY);
        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(RenderType::guiTextured, MACHINE_SPRITES_TEXTURE, x + 152, y + 17, 16, 0, 16, 52, 256, 256);
    }

    private void renderButtons(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(isHovering(114, 47, 20, 20, mouseX, mouseY))
            guiGraphics.blit(RenderType::guiTextured, MACHINE_SPRITES_TEXTURE, x + 114, y + 47, 0, 211, 20, 20, 256, 256);

        ItemStack output = new ItemStack(menu.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?Items.BUCKET:Items.WATER_BUCKET);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.f, 0.f, 100.f);

        guiGraphics.renderItem(output, x + 116, y + 49, 116 + 49 * this.imageWidth);

        guiGraphics.pose().popPose();
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        int arrowPosY = menu.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?58:72;

        guiGraphics.blit(RenderType::guiTextured, MACHINE_SPRITES_TEXTURE, x + 114, y + 19, 52, arrowPosY, 20, 14, 256, 256);

        if(menu.isCraftingActive()) {
            if(menu.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING)
                guiGraphics.blit(RenderType::guiTextured, MACHINE_SPRITES_TEXTURE, x + 114, y + 19, 72, arrowPosY, menu.getScaledProgressArrowSize(), 14, 256, 256);
            else
                guiGraphics.blit(RenderType::guiTextured, MACHINE_SPRITES_TEXTURE, x + 134 - menu.getScaledProgressArrowSize(), y + 19,
                        92 - menu.getScaledProgressArrowSize(), arrowPosY, menu.getScaledProgressArrowSize(), 14, 256, 256);
        }
    }

    @Override
    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        if(isHovering(152, 17, 16, 52, mouseX, mouseY)) {
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

        if(isHovering(114, 47, 20, 20, mouseX, mouseY)) {
            //Mode button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.fluid_transposer.mode." +
                    menu.getMode().getSerializedName()));

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
