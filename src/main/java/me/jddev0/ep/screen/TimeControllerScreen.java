package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetTimeFromTimeControllerC2SPacket;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class TimeControllerScreen extends AbstractContainerScreen<TimeControllerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/time_controller.png");

    public TimeControllerScreen(TimeControllerMenu menu, Inventory inventory, Component component) {
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

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
        renderEnergyMeter(poseStack, x, y);
        renderButtons(poseStack, x, y, mouseX, mouseY);
        renderInfoText(poseStack, x, y);
    }

    private void renderEnergyMeter(PoseStack poseStack, int x, int y) {
        int pos = menu.getScaledEnergyMeterPos();
        blit(poseStack, x + 8, y + 17 + 52 - pos, 176, 52 - pos, 16, pos);
    }

    private void renderButtons(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(isHovering(34, 34, 18, 18, mouseX, mouseY)) {
            //Day button

            blit(poseStack, x + 34, y + 34, 176, 52, 18, 18);
        }else if(isHovering(70, 34, 18, 18, mouseX, mouseY)) {
            //Noon button

            blit(poseStack, x + 70, y + 34, 176, 70, 18, 18);
        }else if(isHovering(106, 34, 18, 18, mouseX, mouseY)) {
            //Night button

            blit(poseStack, x + 106, y + 34, 176, 88, 18, 18);
        }else if(isHovering(142, 34, 18, 18, mouseX, mouseY)) {
            //Midnight button

            blit(poseStack, x + 142, y + 34, 176, 106, 18, 18);
        }
    }

    private void renderInfoText(PoseStack poseStack, int x, int y) {
        Component component = menu.getEnergy() < TimeControllerBlockEntity.CAPACITY?
                Component.translatable("tooltip.energizedpower.not_enough_energy.txt").withStyle(ChatFormatting.RED):
                Component.translatable("tooltip.energizedpower.ready.txt").withStyle(ChatFormatting.DARK_GREEN);

        int componentWidth = font.width(component);

        font.draw(poseStack, component, x + 34 + (126 - componentWidth) * .5f, y + 58, 0);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);

        super.render(poseStack, mouseX, mouseY, delta);

        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);

        if(isHovering(8, 17, 16, 52, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.energy_meter.content.txt",
                    EnergyUtils.getEnergyWithPrefix(menu.getEnergy()), EnergyUtils.getEnergyWithPrefix(menu.getCapacity())));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(34, 34, 18, 18, mouseX, mouseY)) {
            //Day button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.time_controller.btn.day"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(70, 34, 18, 18, mouseX, mouseY)) {
            //Noon button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.time_controller.btn.noon"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(106, 34, 18, 18, mouseX, mouseY)) {
            //Night button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.time_controller.btn.night"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(142, 34, 18, 18, mouseX, mouseY)) {
            //Midnight button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.time_controller.btn.midnight"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
