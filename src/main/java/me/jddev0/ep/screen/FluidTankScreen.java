package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.networking.packet.SetFluidTankFilterC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FluidTankScreen extends EnergizedPowerBaseContainerScreen<FluidTankMenu> {
    private final ResourceLocation TEXTURE;

    public FluidTankScreen(FluidTankMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/fluid_tank.png");
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();
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

                IFluidHandlerItem fluidStorage = carriedItemStack.getCapability(Capabilities.FluidHandler.ITEM);
                if(fluidStorage != null && fluidStorage.getTanks() > 0)
                    fluidFilter = fluidStorage.getFluidInTank(0);

                ModMessages.sendToServer(new SetFluidTankFilterC2SPacket(menu.getBlockEntity().getBlockPos(), fluidFilter));
                clicked = true;
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        for(int i = 0;i < 2;i++) {
            if(i == 0)
                renderFluidMeterContent(guiGraphics, menu.getFluid(0), menu.getTankCapacity(0), x + 80, y + 17, 16, 52);
            else
                renderFluidMeterContent(guiGraphics, menu.getFluid(1), -1, x + 152, y + 35, 16, 16);

            renderFluidMeterOverlay(guiGraphics, x, y, i);
        }

        renderCheckboxes(guiGraphics, x, y, mouseX, mouseY);
    }

    private void renderFluidMeterOverlay(GuiGraphics guiGraphics, int x, int y, int tank) {
        if(tank == 0)
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 80, y + 17, 16, 0, 16, 52, 256, 256);
        else if(tank == 1)
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 152, y + 35, 0, 167, 16, 16, 256, 256);
    }

    private void renderCheckboxes(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(menu.isIgnoreNBT()) {
            //Ignore NBT checkbox

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 158, y + 16, 0, 139, 11, 11, 256, 256);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isHovering(80, 17, 16, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Component> components = new ArrayList<>(2);

            boolean fluidEmpty =  menu.getFluid(0).isEmpty();

            int fluidAmount = fluidEmpty?0:menu.getFluid(0).getAmount();

            Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                    FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(menu.getTankCapacity(0)));

            if(!fluidEmpty) {
                tooltipComponent = Component.translatable(menu.getFluid(0).getDescriptionId()).append(" ").
                        append(tooltipComponent);
            }

            components.add(tooltipComponent);

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }

        if(isHovering(158, 16, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.fluid_tanks.cbx.ignore_nbt"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }

        if(isHovering(151, 34, 18, 18, mouseX, mouseY)) {
            //Fluid Filter

            List<Component> components = new ArrayList<>(2);

            FluidStack fluidFilter = menu.getFluid(1);

            if(fluidFilter.isEmpty())
                components.add(Component.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.no_filter_set"));
            else
                components.add(Component.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.filter_set",
                        Component.translatable(fluidFilter.getDescriptionId())));

            components.add(Component.empty());

            components.add(Component.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.txt.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            components.add(Component.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.txt.2").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
