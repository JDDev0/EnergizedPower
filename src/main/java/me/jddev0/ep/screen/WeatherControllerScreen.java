package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetWeatherFromWeatherControllerC2SPacket;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageContainerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WeatherControllerScreen
        extends UpgradableEnergyStorageContainerScreen<WeatherControllerMenu> {
    public WeatherControllerScreen(WeatherControllerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                EPAPI.id("textures/gui/container/weather_controller.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_duration.png"));
    }

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isHovering(52, 34, 18, 18, mouseX, mouseY)) {
                //Weather clear button

                ModMessages.sendToServer(new SetWeatherFromWeatherControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 0));
                return true;
            }else if(isHovering(88, 34, 18, 18, mouseX, mouseY)) {
                //Weather rain button

                ModMessages.sendToServer(new SetWeatherFromWeatherControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 1));
                return true;
            }else if(isHovering(124, 34, 18, 18, mouseX, mouseY)) {
                //Weather thunder button

                ModMessages.sendToServer(new SetWeatherFromWeatherControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 2));
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

        renderButtons(guiGraphics, x, y, mouseX, mouseY);
        renderInfoText(guiGraphics, x, y);
    }

    private void renderButtons(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        int selectedWeatherType = menu.getSelectedWeatherType();

        //Weather clear button
        if(isHovering(52, 34, 18, 18, mouseX, mouseY)) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 52, y + 34, 56, 193, 18, 18, 256, 256);
        }else if(selectedWeatherType == 0) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 52, y + 34, 74, 193, 18, 18, 256, 256);
        }

        //Weather rain button
        if(isHovering(88, 34, 18, 18, mouseX, mouseY)) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 88, y + 34, 56, 211, 18, 18, 256, 256);
        }else if(selectedWeatherType == 1) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 88, y + 34, 74, 211, 18, 18, 256, 256);
        }

        //Weather thunder button
        if(isHovering(124, 34, 18, 18, mouseX, mouseY)) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 124, y + 34, 56, 229, 18, 18, 256, 256);
        }else if(selectedWeatherType == 2) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 124, y + 34, 74, 229, 18, 18, 256, 256);
        }
    }

    private void renderInfoText(GuiGraphics guiGraphics, int x, int y) {
        Component component = menu.hasEnoughEnergy()?
                Component.translatable("tooltip.energizedpower.ready.txt").withStyle(ChatFormatting.DARK_GREEN):
                Component.translatable("tooltip.energizedpower.not_enough_energy.txt").withStyle(ChatFormatting.RED);

        int componentWidth = font.width(component);

        guiGraphics.drawString(font, component, (int)(x + 34 + (126 - componentWidth) * .5f), y + 58, 0xFF000000, false);
    }

    @Override
    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        if(isHovering(52, 34, 18, 18, mouseX, mouseY)) {
            //Weather clear button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.weather_controller.btn.clear"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(88, 34, 18, 18, mouseX, mouseY)) {
            //Weather rain button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.weather_controller.btn.rain"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(124, 34, 18, 18, mouseX, mouseY)) {
            //Weather thunder button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.weather_controller.btn.thunder"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
