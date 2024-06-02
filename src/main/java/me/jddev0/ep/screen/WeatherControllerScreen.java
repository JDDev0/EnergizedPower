package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetWeatherFromWeatherControllerC2SPacket;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class WeatherControllerScreen extends UpgradableEnergyStorageContainerScreen<WeatherControllerMenu> {
    public WeatherControllerScreen(WeatherControllerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/weather_controller.png"),
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/upgrade_view/1_duration.png"));
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
    protected void renderBgNormalView(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderButtons(poseStack, x, y, mouseX, mouseY);
        renderInfoText(poseStack, x, y);
    }

    private void renderButtons(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        int selectedWeatherType = handler.getSelectedWeatherType();

        //Weather clear button
        if(isPointWithinBounds(52, 34, 18, 18, mouseX, mouseY)) {
            drawTexture(poseStack, x + 52, y + 34, 176, 52, 18, 18);
        }else if(selectedWeatherType == 0) {
            drawTexture(poseStack, x + 52, y + 34, 194, 52, 18, 18);
        }

        //Weather rain button
        if(isPointWithinBounds(88, 34, 18, 18, mouseX, mouseY)) {
            drawTexture(poseStack, x + 88, y + 34, 176, 70, 18, 18);
        }else if(selectedWeatherType == 1) {
            drawTexture(poseStack, x + 88, y + 34, 194, 70, 18, 18);
        }

        //Weather thunder button
        if(isPointWithinBounds(124, 34, 18, 18, mouseX, mouseY)) {
            drawTexture(poseStack, x + 124, y + 34, 176, 88, 18, 18);
        }else if(selectedWeatherType == 2) {
            drawTexture(poseStack, x + 124, y + 34, 194, 88, 18, 18);
        }
    }

    private void renderInfoText(MatrixStack poseStack, int x, int y) {
        Text component = handler.hasEnoughEnergy()?
                Text.translatable("tooltip.energizedpower.ready.txt").formatted(Formatting.DARK_GREEN):
                Text.translatable("tooltip.energizedpower.not_enough_energy.txt").formatted(Formatting.RED);

        int componentWidth = textRenderer.getWidth(component);

        textRenderer.draw(poseStack, component, x + 34 + (126 - componentWidth) * .5f, y + 58, 0);
    }

    @Override
    protected void renderTooltipNormalView(MatrixStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        if(isPointWithinBounds(52, 34, 18, 18, mouseX, mouseY)) {
            //Weather clear button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.weather_controller.btn.clear"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(88, 34, 18, 18, mouseX, mouseY)) {
            //Weather rain button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.weather_controller.btn.rain"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(124, 34, 18, 18, mouseX, mouseY)) {
            //Weather thunder button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.weather_controller.btn.thunder"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
