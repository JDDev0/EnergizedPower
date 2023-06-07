package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class TimeControllerScreen extends AbstractGenericEnergyStorageHandledScreen<TimeControllerMenu> {
    public TimeControllerScreen(TimeControllerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/time_controller.png"),
                8, 17);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isPointWithinBounds(34, 34, 18, 18, mouseX, mouseY)) {
                //Day button

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                buf.writeInt(1000);
                ClientPlayNetworking.send(ModMessages.SET_TIME_FROM_TIME_CONTROLLER_ID, buf);
                clicked = true;
            }else if(isPointWithinBounds(70, 34, 18, 18, mouseX, mouseY)) {
                //Noon button

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                buf.writeInt(6000);
                ClientPlayNetworking.send(ModMessages.SET_TIME_FROM_TIME_CONTROLLER_ID, buf);
                clicked = true;
            }else if(isPointWithinBounds(106, 34, 18, 18, mouseX, mouseY)) {
                //Night button

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                buf.writeInt(13000);
                ClientPlayNetworking.send(ModMessages.SET_TIME_FROM_TIME_CONTROLLER_ID, buf);
                clicked = true;
            }else if(isPointWithinBounds(142, 34, 18, 18, mouseX, mouseY)) {
                //Midnight button

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                buf.writeInt(18000);
                ClientPlayNetworking.send(ModMessages.SET_TIME_FROM_TIME_CONTROLLER_ID, buf);
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderButtons(drawContext, x, y, mouseX, mouseY);
        renderInfoText(drawContext, x, y);
    }

    private void renderButtons(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        if(isPointWithinBounds(34, 34, 18, 18, mouseX, mouseY)) {
            //Day button

            drawContext.drawTexture(TEXTURE, x + 34, y + 34, 176, 52, 18, 18);
        }else if(isPointWithinBounds(70, 34, 18, 18, mouseX, mouseY)) {
            //Noon button

            drawContext.drawTexture(TEXTURE, x + 70, y + 34, 176, 70, 18, 18);
        }else if(isPointWithinBounds(106, 34, 18, 18, mouseX, mouseY)) {
            //Night button

            drawContext.drawTexture(TEXTURE, x + 106, y + 34, 176, 88, 18, 18);
        }else if(isPointWithinBounds(142, 34, 18, 18, mouseX, mouseY)) {
            //Midnight button

            drawContext.drawTexture(TEXTURE, x + 142, y + 34, 176, 106, 18, 18);
        }
    }

    private void renderInfoText(DrawContext drawContext, int x, int y) {
        Text component = handler.getEnergy() < TimeControllerBlockEntity.CAPACITY?
                Text.translatable("tooltip.energizedpower.not_enough_energy.txt").formatted(Formatting.RED):
                Text.translatable("tooltip.energizedpower.ready.txt").formatted(Formatting.DARK_GREEN);

        int componentWidth = textRenderer.getWidth(component);

        drawContext.drawText(textRenderer, component, (int)(x + 34 + (126 - componentWidth) * .5f), y + 58, 0, false);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

        if(isPointWithinBounds(34, 34, 18, 18, mouseX, mouseY)) {
            //Day button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.time_controller.btn.day"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(70, 34, 18, 18, mouseX, mouseY)) {
            //Noon button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.time_controller.btn.noon"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(106, 34, 18, 18, mouseX, mouseY)) {
            //Night button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.time_controller.btn.night"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(142, 34, 18, 18, mouseX, mouseY)) {
            //Midnight button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.time_controller.btn.midnight"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
