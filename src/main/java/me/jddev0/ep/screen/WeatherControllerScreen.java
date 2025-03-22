package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetWeatherFromWeatherControllerC2SPacket;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class WeatherControllerScreen extends UpgradableEnergyStorageContainerScreen<WeatherControllerMenu> {
    public WeatherControllerScreen(WeatherControllerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                EPAPI.id("textures/gui/container/weather_controller.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_duration.png"));
    }

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isPointWithinBounds(52, 34, 18, 18, mouseX, mouseY)) {
                //Weather clear button

                ModMessages.sendClientPacketToServer(new SetWeatherFromWeatherControllerC2SPacket(handler.getBlockEntity().getPos(), 0));
                return true;
            }else if(isPointWithinBounds(88, 34, 18, 18, mouseX, mouseY)) {
                //Weather rain button

                ModMessages.sendClientPacketToServer(new SetWeatherFromWeatherControllerC2SPacket(handler.getBlockEntity().getPos(), 1));
                return true;
            }else if(isPointWithinBounds(124, 34, 18, 18, mouseX, mouseY)) {
                //Weather thunder button

                ModMessages.sendClientPacketToServer(new SetWeatherFromWeatherControllerC2SPacket(handler.getBlockEntity().getPos(), 2));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderButtons(drawContext, x, y, mouseX, mouseY);
        renderInfoText(drawContext, x, y);
    }

    private void renderButtons(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        int selectedWeatherType = handler.getSelectedWeatherType();

        //Weather clear button
        if(isPointWithinBounds(52, 34, 18, 18, mouseX, mouseY)) {
            drawContext.drawTexture(RenderLayer::getGuiTextured, MACHINE_SPRITES_TEXTURE, x + 52, y + 34, 56, 193, 18, 18, 256, 256);
        }else if(selectedWeatherType == 0) {
            drawContext.drawTexture(RenderLayer::getGuiTextured, MACHINE_SPRITES_TEXTURE, x + 52, y + 34, 74, 193, 18, 18, 256, 256);
        }

        //Weather rain button
        if(isPointWithinBounds(88, 34, 18, 18, mouseX, mouseY)) {
            drawContext.drawTexture(RenderLayer::getGuiTextured, MACHINE_SPRITES_TEXTURE, x + 88, y + 34, 56, 211, 18, 18, 256, 256);
        }else if(selectedWeatherType == 1) {
            drawContext.drawTexture(RenderLayer::getGuiTextured, MACHINE_SPRITES_TEXTURE, x + 88, y + 34, 74, 211, 18, 18, 256, 256);
        }

        //Weather thunder button
        if(isPointWithinBounds(124, 34, 18, 18, mouseX, mouseY)) {
            drawContext.drawTexture(RenderLayer::getGuiTextured, MACHINE_SPRITES_TEXTURE, x + 124, y + 34, 56, 229, 18, 18, 256, 256);
        }else if(selectedWeatherType == 2) {
            drawContext.drawTexture(RenderLayer::getGuiTextured, MACHINE_SPRITES_TEXTURE, x + 124, y + 34, 74, 229, 18, 18, 256, 256);
        }
    }

    private void renderInfoText(DrawContext drawContext, int x, int y) {
        Text component = handler.hasEnoughEnergy()?
                Text.translatable("tooltip.energizedpower.ready.txt").formatted(Formatting.DARK_GREEN):
                Text.translatable("tooltip.energizedpower.not_enough_energy.txt").formatted(Formatting.RED);

        int componentWidth = textRenderer.getWidth(component);

        drawContext.drawText(textRenderer, component, (int)(x + 34 + (126 - componentWidth) * .5f), y + 58, 0, false);
    }

    @Override
    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipNormalView(drawContext, mouseX, mouseY);

        if(isPointWithinBounds(52, 34, 18, 18, mouseX, mouseY)) {
            //Weather clear button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.weather_controller.btn.clear"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(88, 34, 18, 18, mouseX, mouseY)) {
            //Weather rain button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.weather_controller.btn.rain"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(124, 34, 18, 18, mouseX, mouseY)) {
            //Weather thunder button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.weather_controller.btn.thunder"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
