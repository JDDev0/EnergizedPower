package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.networking.packet.SetFluidTankFilterC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class FluidTankScreen extends EnergizedPowerBaseContainerScreen<FluidTankMenu> {
    private final ResourceLocation TEXTURE;

    public FluidTankScreen(FluidTankMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/fluid_tank.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isHovering(158, 16, 11, 11, mouseX, mouseY)) {
                //Ignore NBT checkbox

                ModMessages.sendToServer(new SetCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), 0, !menu.isIgnoreNBT()));
                clicked = true;
            }

            if(isHovering(151, 34, 18, 18, mouseX, mouseY)) {
                //Fluid Filter

                FluidStack fluidFilter = FluidStack.EMPTY;

                ItemStack carriedItemStack = menu.getCarried();

                LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = carriedItemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
                if(fluidStorageLazyOptional.isPresent()) {
                    IFluidHandlerItem fluidStorage = fluidStorageLazyOptional.orElse(null);
                    if(fluidStorage != null && fluidStorage.getTanks() > 0)
                        fluidFilter = fluidStorage.getFluidInTank(0);
                }

                ModMessages.sendToServer(new SetFluidTankFilterC2SPacket(menu.getBlockEntity().getBlockPos(), fluidFilter));
                clicked = true;
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

        for(int i = 0;i < 2;i++) {
            if(i == 0)
                renderFluidMeterContent(poseStack, menu.getFluid(0), menu.getTankCapacity(0), x + 80, y + 17, 16, 52);
            else
                renderFluidMeterContent(poseStack, menu.getFluid(1), -1, x + 152, y + 35, 16, 16);

            renderFluidMeterOverlay(poseStack, x, y, i);
        }

        renderCheckboxes(poseStack, x, y, mouseX, mouseY);
    }

    private void renderFluidMeterOverlay(PoseStack poseStack, int x, int y, int tank) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        if(tank == 0)
            blit(poseStack, x + 80, y + 17, 176, 0, 16, 52);
        else if(tank == 1)
            blit(poseStack, x + 152, y + 35, 176, 64, 16, 16);
    }

    private void renderCheckboxes(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(menu.isIgnoreNBT()) {
            //Ignore NBT checkbox

            blit(poseStack, x + 158, y + 16, 176, 53, 11, 11);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);

        super.render(poseStack, mouseX, mouseY, delta);

        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);

        if(isHovering(80, 17, 16, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Component> components = new ArrayList<>(2);

            boolean fluidEmpty =  menu.getFluid(0).isEmpty();

            int fluidAmount = fluidEmpty?0:menu.getFluid(0).getAmount();

            Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                    FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(menu.getTankCapacity(0)));

            if(!fluidEmpty) {
                tooltipComponent = Component.translatable(menu.getFluid(0).getTranslationKey()).append(" ").
                        append(tooltipComponent);
            }

            components.add(tooltipComponent);

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }

        if(isHovering(158, 16, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.fluid_tanks.cbx.ignore_nbt"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }

        if(isHovering(151, 34, 18, 18, mouseX, mouseY)) {
            //Fluid Filter

            List<Component> components = new ArrayList<>(2);

            FluidStack fluidFilter = menu.getFluid(1);

            if(fluidFilter.isEmpty())
                components.add(Component.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.no_filter_set"));
            else
                components.add(Component.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.filter_set",
                        Component.translatable(fluidFilter.getTranslationKey())));

            components.add(Component.empty());

            components.add(Component.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.txt.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            components.add(Component.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.txt.2").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
