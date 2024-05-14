package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetWeatherFromWeatherControllerC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class WeatherControllerScreen extends AbstractGenericEnergyStorageHandledScreen<WeatherControllerMenu> {
    private final Identifier CONFIGURATION_ICONS_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID, "textures/gui/machine_configuration/configuration_buttons.png");
    private final Identifier UPGRADE_VIEW_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/upgrade_view/1_energy_capacity.png");

    public WeatherControllerScreen(WeatherControllerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/weather_controller.png"),
                8, 17);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(!handler.isInUpgradeModuleView()) {
                if(isPointWithinBounds(52, 34, 18, 18, mouseX, mouseY)) {
                    //Weather clear button

                    ModMessages.sendClientPacketToServer(
                            new SetWeatherFromWeatherControllerC2SPacket(handler.getBlockEntity().getPos(), 0));
                    clicked = true;
                }else if(isPointWithinBounds(88, 34, 18, 18, mouseX, mouseY)) {
                    //Weather rain button

                    ModMessages.sendClientPacketToServer(
                            new SetWeatherFromWeatherControllerC2SPacket(handler.getBlockEntity().getPos(), 1));
                    clicked = true;
                }else if(isPointWithinBounds(124, 34, 18, 18, mouseX, mouseY)) {
                    //Weather thunder button

                    ModMessages.sendClientPacketToServer(
                            new SetWeatherFromWeatherControllerC2SPacket(handler.getBlockEntity().getPos(), 2));
                    clicked = true;
                }
            }

            if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
                //Upgrade view

                client.interactionManager.clickButton(handler.syncId, 0);
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawBackground(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(poseStack, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        if(handler.isInUpgradeModuleView()) {
            RenderSystem.setShaderTexture(0, UPGRADE_VIEW_TEXTURE);
            drawTexture(poseStack, x, y, 0, 0, backgroundWidth, backgroundHeight);
            RenderSystem.setShaderTexture(0, TEXTURE);
        }else {
            renderButtons(poseStack, x, y, mouseX, mouseY);
            renderInfoText(poseStack, x, y);
        }

        renderConfiguration(poseStack, x, y, mouseX, mouseY);
    }

    private void renderButtons(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(isPointWithinBounds(52, 34, 18, 18, mouseX, mouseY)) {
            //Weather clear button

            drawTexture(poseStack, x + 52, y + 34, 176, 52, 18, 18);
        }else if(isPointWithinBounds(88, 34, 18, 18, mouseX, mouseY)) {
            //Weather rain button

            drawTexture(poseStack, x + 88, y + 34, 176, 70, 18, 18);
        }else if(isPointWithinBounds(124, 34, 18, 18, mouseX, mouseY)) {
            //Weather thunder button

            drawTexture(poseStack, x + 124, y + 34, 176, 88, 18, 18);
        }
    }

    private void renderInfoText(MatrixStack poseStack, int x, int y) {
        Text component = handler.getEnergy() < TimeControllerBlockEntity.CAPACITY?
                Text.translatable("tooltip.energizedpower.not_enough_energy.txt").formatted(Formatting.RED):
                Text.translatable("tooltip.energizedpower.ready.txt").formatted(Formatting.DARK_GREEN);

        int componentWidth = textRenderer.getWidth(component);

        textRenderer.draw(poseStack, component, x + 34 + (126 - componentWidth) * .5f, y + 58, 0);
    }

    private void renderConfiguration(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, CONFIGURATION_ICONS_TEXTURE);

        //Upgrade view
        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            drawTexture(poseStack, x - 22, y + 2, 40, 80, 20, 20);
        }else if(handler.isInUpgradeModuleView()) {
            drawTexture(poseStack, x - 22, y + 2, 20, 80, 20, 20);
        }else {
            drawTexture(poseStack, x - 22, y + 2, 0, 80, 20, 20);
        }
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack poseStack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(poseStack, mouseX, mouseY);

        if(!handler.isInUpgradeModuleView()) {
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

        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            //Upgrade view

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.upgrade_view.button." +
                    (handler.isInUpgradeModuleView()?"close":"open")));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
