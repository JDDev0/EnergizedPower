package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetAutoCrafterPatternInputSlotsC2SPacket;
import me.jddev0.ep.networking.packet.SetWeatherFromWeatherControllerC2SPacket;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class WeatherControllerScreen extends AbstractContainerScreen<WeatherControllerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/weather_controller.png");

    public WeatherControllerScreen(WeatherControllerMenu menu, Inventory inventory, Component component) {
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
            if(isHovering(43, 34, 18, 18, mouseX, mouseY)) {
                //Weather clear button

                ModMessages.sendToServer(new SetWeatherFromWeatherControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 0));
                clicked = true;
            }else if(isHovering(79, 34, 18, 18, mouseX, mouseY)) {
                //Weather rain button

                ModMessages.sendToServer(new SetWeatherFromWeatherControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 1));
                clicked = true;
            }else if(isHovering(115, 34, 18, 18, mouseX, mouseY)) {
                //Weather thunder button

                ModMessages.sendToServer(new SetWeatherFromWeatherControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 2));
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
    }

    private void renderEnergyMeter(PoseStack poseStack, int x, int y) {
        int pos = menu.getScaledEnergyMeterPos();
        blit(poseStack, x + 8, y + 17 + 52 - pos, 176, 52 - pos, 16, pos);
    }

    private void renderButtons(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(isHovering(43, 34, 18, 18, mouseX, mouseY)) {
            //Weather clear button

            blit(poseStack, x + 43, y + 34, 176, 52, 18, 18);
        }else if(isHovering(79, 34, 18, 18, mouseX, mouseY)) {
            //Weather rain button

            blit(poseStack, x + 79, y + 34, 176, 70, 18, 18);
        }else if(isHovering(115, 34, 18, 18, mouseX, mouseY)) {
            //Weather thunder button

            blit(poseStack, x + 115, y + 34, 176, 88, 18, 18);
        }
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
        }else if(isHovering(43, 34, 18, 18, mouseX, mouseY)) {
            //Weather clear button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.weather_controller.btn.clear"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(79, 34, 18, 18, mouseX, mouseY)) {
            //Weather rain button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.weather_controller.btn.rain"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(115, 34, 18, 18, mouseX, mouseY)) {
            //Weather thunder button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.weather_controller.btn.thunder"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
