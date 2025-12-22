package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCreativeFluidTankFluidStackC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import me.jddev0.ep.util.CapabilityUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreativeFluidTankScreen extends EnergizedPowerBaseContainerScreen<CreativeFluidTankMenu> {
    private final Identifier TEXTURE;

    public CreativeFluidTankScreen(CreativeFluidTankMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/creative_fluid_tank.png");
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();
        if(mouseButton == 0) {
            boolean clicked = false;

            if(isHovering(48, 17, 80, 52, mouseX, mouseY)) {
                //Fluid Tank

                FluidStack fluidStack = FluidStack.EMPTY;

                ItemStack carriedItemStack = menu.getCarried();

                ResourceHandler<FluidResource> fluidStorage = CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Fluid.ITEM, carriedItemStack);
                if(fluidStorage != null && fluidStorage.size() > 0)
                    fluidStack = fluidStorage.getResource(0).toStack(1);

                ModMessages.sendToServer(new SetCreativeFluidTankFluidStackC2SPacket(menu.getBlockEntity().getBlockPos(), fluidStack));
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

        renderFluidMeterContent(guiGraphics, menu.getFluid(0), menu.getTankCapacity(0), x + 48, y + 17, 80, 52);
        renderFluidMeterOverlay(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 48, y + 17, 36, 0, 80, 52, 256, 256);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isHovering(48, 17, 80, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Component> components = new ArrayList<>(2);

            boolean fluidEmpty = menu.getFluid(0).isEmpty();

            Component tooltipComponent;
            if(fluidEmpty) {
                tooltipComponent = Component.literal("0 / ").append(Component.translatable("tooltip.energizedpower.infinite.txt").
                        withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
            }else {
                tooltipComponent = Component.translatable(menu.getFluid(0).getDescriptionId()).append(" ").
                        append(Component.translatable("tooltip.energizedpower.infinite.txt").
                                withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)).append(" / ").
                        append(Component.translatable("tooltip.energizedpower.infinite.txt").
                                withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
            }

            components.add(tooltipComponent);

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
