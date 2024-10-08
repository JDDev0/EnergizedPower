package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetTimeFromTimeControllerC2SPacket;
import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
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
public class TimeControllerScreen extends EnergyStorageContainerScreen<TimeControllerMenu> {
    public TimeControllerScreen(TimeControllerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                EPAPI.id("textures/gui/container/time_controller.png"));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isPointWithinBounds(34, 34, 18, 18, mouseX, mouseY)) {
                //Day button

                ModMessages.sendClientPacketToServer(
                        new SetTimeFromTimeControllerC2SPacket(handler.getBlockEntity().getPos(), 1000));
                clicked = true;
            }else if(isPointWithinBounds(70, 34, 18, 18, mouseX, mouseY)) {
                //Noon button

                ModMessages.sendClientPacketToServer(
                        new SetTimeFromTimeControllerC2SPacket(handler.getBlockEntity().getPos(), 6000));
                clicked = true;
            }else if(isPointWithinBounds(106, 34, 18, 18, mouseX, mouseY)) {
                //Night button

                ModMessages.sendClientPacketToServer(
                        new SetTimeFromTimeControllerC2SPacket(handler.getBlockEntity().getPos(), 13000));
                clicked = true;
            }else if(isPointWithinBounds(142, 34, 18, 18, mouseX, mouseY)) {
                //Midnight button

                ModMessages.sendClientPacketToServer(
                        new SetTimeFromTimeControllerC2SPacket(handler.getBlockEntity().getPos(), 18000));
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

        renderButtons(poseStack, x, y, mouseX, mouseY);
        renderInfoText(poseStack, x, y);
    }

    private void renderButtons(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(isPointWithinBounds(34, 34, 18, 18, mouseX, mouseY)) {
            //Day button

            drawTexture(poseStack, x + 34, y + 34, 176, 52, 18, 18);
        }else if(isPointWithinBounds(70, 34, 18, 18, mouseX, mouseY)) {
            //Noon button

            drawTexture(poseStack, x + 70, y + 34, 176, 70, 18, 18);
        }else if(isPointWithinBounds(106, 34, 18, 18, mouseX, mouseY)) {
            //Night button

            drawTexture(poseStack, x + 106, y + 34, 176, 88, 18, 18);
        }else if(isPointWithinBounds(142, 34, 18, 18, mouseX, mouseY)) {
            //Midnight button

            drawTexture(poseStack, x + 142, y + 34, 176, 106, 18, 18);
        }
    }

    private void renderInfoText(MatrixStack poseStack, int x, int y) {
        Text component = handler.getEnergy() < TimeControllerBlockEntity.CAPACITY?
                Text.translatable("tooltip.energizedpower.not_enough_energy.txt").formatted(Formatting.RED):
                Text.translatable("tooltip.energizedpower.ready.txt").formatted(Formatting.DARK_GREEN);

        int componentWidth = textRenderer.getWidth(component);

        textRenderer.draw(poseStack, component, x + 34 + (126 - componentWidth) * .5f, y + 58, 0);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack poseStack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(poseStack, mouseX, mouseY);

        if(isPointWithinBounds(34, 34, 18, 18, mouseX, mouseY)) {
            //Day button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.time_controller.btn.day"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(70, 34, 18, 18, mouseX, mouseY)) {
            //Noon button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.time_controller.btn.noon"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(106, 34, 18, 18, mouseX, mouseY)) {
            //Night button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.time_controller.btn.night"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(142, 34, 18, 18, mouseX, mouseY)) {
            //Midnight button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.time_controller.btn.midnight"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
