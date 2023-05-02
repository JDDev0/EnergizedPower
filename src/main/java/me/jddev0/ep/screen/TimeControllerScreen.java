package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class TimeControllerScreen extends HandledScreen<TimeControllerMenu> {
    private static final Identifier TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/time_controller.png");

    public TimeControllerScreen(TimeControllerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
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
    protected void drawBackground(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawTexture(poseStack, x, y, 0, 0, backgroundWidth, backgroundHeight);
        renderEnergyMeter(poseStack, x, y);
        renderButtons(poseStack, x, y, mouseX, mouseY);
    }

    private void renderEnergyMeter(MatrixStack poseStack, int x, int y) {
        int pos = handler.getScaledEnergyMeterPos();
        drawTexture(poseStack, x + 8, y + 17 + 52 - pos, 176, 52 - pos, 16, pos);
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

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);

        super.render(poseStack, mouseX, mouseY, delta);

        drawMouseoverTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack poseStack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(poseStack, mouseX, mouseY);

        if(isPointWithinBounds(8, 17, 16, 52, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.energy_meter.content.txt",
                    EnergyUtils.getEnergyWithPrefix(handler.getEnergy()), EnergyUtils.getEnergyWithPrefix(handler.getCapacity())));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(34, 34, 18, 18, mouseX, mouseY)) {
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
