package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetTimeFromTimeControllerC2SPacket;
import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TimeControllerScreen extends EnergyStorageContainerScreen<TimeControllerMenu> {
    public TimeControllerScreen(TimeControllerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                EPAPI.id("textures/gui/container/time_controller.png"));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isHovering(34, 34, 18, 18, mouseX, mouseY)) {
                //Day button

                ModMessages.sendToServer(new SetTimeFromTimeControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 1000));
                clicked = true;
            }else if(isHovering(70, 34, 18, 18, mouseX, mouseY)) {
                //Noon button

                ModMessages.sendToServer(new SetTimeFromTimeControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 6000));
                clicked = true;
            }else if(isHovering(106, 34, 18, 18, mouseX, mouseY)) {
                //Night button

                ModMessages.sendToServer(new SetTimeFromTimeControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 13000));
                clicked = true;
            }else if(isHovering(142, 34, 18, 18, mouseX, mouseY)) {
                //Midnight button

                ModMessages.sendToServer(new SetTimeFromTimeControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 18000));
                clicked = true;
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderButtons(guiGraphics, x, y, mouseX, mouseY);
        renderInfoText(guiGraphics, x, y);
    }

    private void renderButtons(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(isHovering(34, 34, 18, 18, mouseX, mouseY)) {
            //Day button

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 34, y + 34, 20, 211, 18, 18, 256, 256);
        }else if(isHovering(70, 34, 18, 18, mouseX, mouseY)) {
            //Noon button

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 70, y + 34, 20, 229, 18, 18, 256, 256);
        }else if(isHovering(106, 34, 18, 18, mouseX, mouseY)) {
            //Night button

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 106, y + 34, 38, 211, 18, 18, 256, 256);
        }else if(isHovering(142, 34, 18, 18, mouseX, mouseY)) {
            //Midnight button

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 142, y + 34, 38, 229, 18, 18, 256, 256);
        }
    }

    private void renderInfoText(GuiGraphics guiGraphics, int x, int y) {
        Component component = menu.getEnergy() < TimeControllerBlockEntity.CAPACITY?
                Component.translatable("tooltip.energizedpower.not_enough_energy.txt").withStyle(ChatFormatting.RED):
                Component.translatable("tooltip.energizedpower.ready.txt").withStyle(ChatFormatting.DARK_GREEN);

        int componentWidth = font.width(component);

        guiGraphics.drawString(font, component, (int)(x + 34 + (126 - componentWidth) * .5f), y + 58, 0xFF000000, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isHovering(34, 34, 18, 18, mouseX, mouseY)) {
            //Day button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.time_controller.btn.day"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(70, 34, 18, 18, mouseX, mouseY)) {
            //Noon button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.time_controller.btn.noon"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(106, 34, 18, 18, mouseX, mouseY)) {
            //Night button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.time_controller.btn.night"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(142, 34, 18, 18, mouseX, mouseY)) {
            //Midnight button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.time_controller.btn.midnight"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
