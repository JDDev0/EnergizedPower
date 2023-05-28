package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class WeatherControllerScreen extends AbstractGenericEnergyStorageHandledScreen<WeatherControllerMenu> {
    public WeatherControllerScreen(WeatherControllerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/weather_controller.png"),
                8, 17);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isPointWithinBounds(52, 34, 18, 18, mouseX, mouseY)) {
                //Weather clear button

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                buf.writeInt(0);
                ClientPlayNetworking.send(ModMessages.SET_WEATHER_FROM_WEATHER_CONTROLLER_ID, buf);
                clicked = true;
            }else if(isPointWithinBounds(88, 34, 18, 18, mouseX, mouseY)) {
                //Weather rain button

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                buf.writeInt(1);
                ClientPlayNetworking.send(ModMessages.SET_WEATHER_FROM_WEATHER_CONTROLLER_ID, buf);
                clicked = true;
            }else if(isPointWithinBounds(124, 34, 18, 18, mouseX, mouseY)) {
                //Weather thunder button

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                buf.writeInt(2);
                ClientPlayNetworking.send(ModMessages.SET_WEATHER_FROM_WEATHER_CONTROLLER_ID, buf);
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
                new TranslatableText("tooltip.energizedpower.not_enough_energy.txt").formatted(Formatting.RED):
                new TranslatableText("tooltip.energizedpower.ready.txt").formatted(Formatting.DARK_GREEN);

        int componentWidth = textRenderer.getWidth(component);

        textRenderer.draw(poseStack, component, x + 34 + (126 - componentWidth) * .5f, y + 58, 0);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack poseStack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(poseStack, mouseX, mouseY);

        if(isPointWithinBounds(52, 34, 18, 18, mouseX, mouseY)) {
            //Weather clear button

            List<Text> components = new ArrayList<>(2);
            components.add(new TranslatableText("tooltip.energizedpower.weather_controller.btn.clear"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(88, 34, 18, 18, mouseX, mouseY)) {
            //Weather rain button

            List<Text> components = new ArrayList<>(2);
            components.add(new TranslatableText("tooltip.energizedpower.weather_controller.btn.rain"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(124, 34, 18, 18, mouseX, mouseY)) {
            //Weather thunder button

            List<Text> components = new ArrayList<>(2);
            components.add(new TranslatableText("tooltip.energizedpower.weather_controller.btn.thunder"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
