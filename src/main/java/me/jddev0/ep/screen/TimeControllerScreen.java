package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetTimeFromTimeControllerC2SPacket;
import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();

        if(mouseButton == 0) {
            boolean clicked = false;
            if(isPointWithinBounds(34, 34, 18, 18, mouseX, mouseY)) {
                //Day button

                ModMessages.sendClientPacketToServer(new SetTimeFromTimeControllerC2SPacket(handler.getBlockEntity().getPos(), 1000));
                clicked = true;
            }else if(isPointWithinBounds(70, 34, 18, 18, mouseX, mouseY)) {
                //Noon button

                ModMessages.sendClientPacketToServer(new SetTimeFromTimeControllerC2SPacket(handler.getBlockEntity().getPos(), 6000));
                clicked = true;
            }else if(isPointWithinBounds(106, 34, 18, 18, mouseX, mouseY)) {
                //Night button

                ModMessages.sendClientPacketToServer(new SetTimeFromTimeControllerC2SPacket(handler.getBlockEntity().getPos(), 13000));
                clicked = true;
            }else if(isPointWithinBounds(142, 34, 18, 18, mouseX, mouseY)) {
                //Midnight button

                ModMessages.sendClientPacketToServer(new SetTimeFromTimeControllerC2SPacket(handler.getBlockEntity().getPos(), 18000));
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(click, doubled);
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

            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 34, y + 34, 20, 211, 18, 18, 256, 256);
        }else if(isPointWithinBounds(70, 34, 18, 18, mouseX, mouseY)) {
            //Noon button

            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 70, y + 34, 20, 229, 18, 18, 256, 256);
        }else if(isPointWithinBounds(106, 34, 18, 18, mouseX, mouseY)) {
            //Night button

            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 106, y + 34, 38, 211, 18, 18, 256, 256);
        }else if(isPointWithinBounds(142, 34, 18, 18, mouseX, mouseY)) {
            //Midnight button

            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 142, y + 34, 38, 229, 18, 18, 256, 256);
        }
    }

    private void renderInfoText(DrawContext drawContext, int x, int y) {
        Text component = handler.getEnergy() < TimeControllerBlockEntity.CAPACITY?
                Text.translatable("tooltip.energizedpower.not_enough_energy.txt").formatted(Formatting.RED):
                Text.translatable("tooltip.energizedpower.ready.txt").formatted(Formatting.DARK_GREEN);

        int componentWidth = textRenderer.getWidth(component);

        drawContext.drawText(textRenderer, component, (int)(x + 34 + (126 - componentWidth) * .5f), y + 58, 0xFF000000, false);
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
