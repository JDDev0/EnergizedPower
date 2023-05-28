package me.jddev0.ep.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetWeatherFromWeatherControllerC2SPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class WeatherControllerScreen extends AbstractGenericEnergyStorageContainerScreen<WeatherControllerMenu> {
    public WeatherControllerScreen(WeatherControllerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/weather_controller.png"),
                8, 17);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isHovering(52, 34, 18, 18, mouseX, mouseY)) {
                //Weather clear button

                ModMessages.sendToServer(new SetWeatherFromWeatherControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 0));
                clicked = true;
            }else if(isHovering(88, 34, 18, 18, mouseX, mouseY)) {
                //Weather rain button

                ModMessages.sendToServer(new SetWeatherFromWeatherControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 1));
                clicked = true;
            }else if(isHovering(124, 34, 18, 18, mouseX, mouseY)) {
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
        super.renderBg(poseStack, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderButtons(poseStack, x, y, mouseX, mouseY);
        renderInfoText(poseStack, x, y);
    }

    private void renderButtons(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(isHovering(52, 34, 18, 18, mouseX, mouseY)) {
            //Weather clear button

            blit(poseStack, x + 52, y + 34, 176, 52, 18, 18);
        }else if(isHovering(88, 34, 18, 18, mouseX, mouseY)) {
            //Weather rain button

            blit(poseStack, x + 88, y + 34, 176, 70, 18, 18);
        }else if(isHovering(124, 34, 18, 18, mouseX, mouseY)) {
            //Weather thunder button

            blit(poseStack, x + 124, y + 34, 176, 88, 18, 18);
        }
    }

    private void renderInfoText(PoseStack poseStack, int x, int y) {
        Component component = menu.getEnergy() < TimeControllerBlockEntity.CAPACITY?
                new TranslatableComponent("tooltip.energizedpower.not_enough_energy.txt").withStyle(ChatFormatting.RED):
                new TranslatableComponent("tooltip.energizedpower.ready.txt").withStyle(ChatFormatting.DARK_GREEN);

        int componentWidth = font.width(component);

        font.draw(poseStack, component, x + 34 + (126 - componentWidth) * .5f, y + 58, 0);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);

        if(isHovering(52, 34, 18, 18, mouseX, mouseY)) {
            //Weather clear button

            List<Component> components = new ArrayList<>(2);
            components.add(new TranslatableComponent("tooltip.energizedpower.weather_controller.btn.clear"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(88, 34, 18, 18, mouseX, mouseY)) {
            //Weather rain button

            List<Component> components = new ArrayList<>(2);
            components.add(new TranslatableComponent("tooltip.energizedpower.weather_controller.btn.rain"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(124, 34, 18, 18, mouseX, mouseY)) {
            //Weather thunder button

            List<Component> components = new ArrayList<>(2);
            components.add(new TranslatableComponent("tooltip.energizedpower.weather_controller.btn.thunder"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
