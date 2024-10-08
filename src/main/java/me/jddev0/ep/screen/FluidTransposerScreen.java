package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    protected void renderBgNormalView(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidMeterContent(poseStack, menu.getFluid(), menu.getTankCapacity(), x + 152, y + 17, 16, 52);
        renderFluidMeterOverlay(poseStack, x, y);

        renderButtons(poseStack, x, y, mouseX, mouseY);
        renderProgressArrow(poseStack, x, y);
    }

    private void renderFluidMeterOverlay(PoseStack poseStack, int x, int y) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, x + 152, y + 17, 176, 53, 16, 52);
    }

    private void renderButtons(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(isHovering(114, 47, 20, 20, mouseX, mouseY))
            blit(poseStack, x + 114, y + 47, 176, 135, 20, 20);

        ItemStack output = new ItemStack(menu.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?Items.BUCKET:Items.WATER_BUCKET);
        poseStack.pushPose();
        poseStack.translate(0.f, 0.f, 100.f);

        itemRenderer.renderAndDecorateItem(output, x + 116, y + 49, 116 + 49 * this.imageWidth);

        poseStack.popPose();

        RenderSystem.setShaderTexture(0, TEXTURE);
    }

    private void renderProgressArrow(PoseStack poseStack, int x, int y) {
        int arrowPosY = menu.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?106:120;

        blit(poseStack, x + 114, y + 19, 176, arrowPosY, 20, 14);

        if(menu.isCraftingActive()) {
            if(menu.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING)
                blit(poseStack, x + 114, y + 19, 196, arrowPosY, menu.getScaledProgressArrowSize(), 14);
            else
                blit(poseStack, x + 134 - menu.getScaledProgressArrowSize(), y + 19,
                        216 - menu.getScaledProgressArrowSize(), arrowPosY, menu.getScaledProgressArrowSize(), 14);
        }
    }

    @Override
    protected void renderTooltipNormalView(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        if(isHovering(152, 17, 16, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Component> components = new ArrayList<>(2);

            boolean fluidEmpty =  menu.getFluid().isEmpty();

            int fluidAmount = fluidEmpty?0:menu.getFluid().getAmount();

            Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                    FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(menu.getTankCapacity()));

            if(!fluidEmpty) {
                tooltipComponent = Component.translatable(menu.getFluid().getTranslationKey()).append(" ").
                        append(tooltipComponent);
            }

            components.add(tooltipComponent);

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }

        if(isHovering(114, 47, 20, 20, mouseX, mouseY)) {
            //Mode button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.fluid_transposer.mode." +
                    menu.getMode().getSerializedName()));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
