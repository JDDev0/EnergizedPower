package me.jddev0.ep.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetWeatherFromWeatherControllerC2SPacket;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageContainerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class WeatherControllerScreen
        extends UpgradableEnergyStorageContainerScreen<WeatherControllerMenu> {
    public WeatherControllerScreen(WeatherControllerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                EPAPI.id("textures/gui/container/weather_controller.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_duration.png"));
    }

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isHovering(52, 34, 18, 18, mouseX, mouseY)) {
                //Weather clear button

                ModMessages.sendToServer(new SetWeatherFromWeatherControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 0));
                return true;
            }else if(isHovering(88, 34, 18, 18, mouseX, mouseY)) {
                //Weather rain button

                ModMessages.sendToServer(new SetWeatherFromWeatherControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 1));
                return true;
            }else if(isHovering(124, 34, 18, 18, mouseX, mouseY)) {
                //Weather thunder button

                ModMessages.sendToServer(new SetWeatherFromWeatherControllerC2SPacket(menu.getBlockEntity().getBlockPos(), 2));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderBgNormalView(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderButtons(poseStack, x, y, mouseX, mouseY);
        renderInfoText(poseStack, x, y);
    }

    private void renderButtons(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        int selectedWeatherType = menu.getSelectedWeatherType();

        //Weather clear button
        if(isHovering(52, 34, 18, 18, mouseX, mouseY)) {
            blit(poseStack, x + 52, y + 34, 176, 52, 18, 18);
        }else if(selectedWeatherType == 0) {
            blit(poseStack, x + 52, y + 34, 194, 52, 18, 18);
        }

        //Weather rain button
        if(isHovering(88, 34, 18, 18, mouseX, mouseY)) {
            blit(poseStack, x + 88, y + 34, 176, 70, 18, 18);
        }else if(selectedWeatherType == 1) {
            blit(poseStack, x + 88, y + 34, 194, 70, 18, 18);
        }

        //Weather thunder button
        if(isHovering(124, 34, 18, 18, mouseX, mouseY)) {
            blit(poseStack, x + 124, y + 34, 176, 88, 18, 18);
        }else if(selectedWeatherType == 2) {
            blit(poseStack, x + 124, y + 34, 194, 88, 18, 18);
        }
    }

    private void renderInfoText(PoseStack poseStack, int x, int y) {
        Component component = menu.hasEnoughEnergy()?
                Component.translatable("tooltip.energizedpower.ready.txt").withStyle(ChatFormatting.DARK_GREEN):
                Component.translatable("tooltip.energizedpower.not_enough_energy.txt").withStyle(ChatFormatting.RED);

        int componentWidth = font.width(component);

        font.draw(poseStack, component, x + 34 + (126 - componentWidth) * .5f, y + 58, 0);
    }

    @Override
    protected void renderTooltipNormalView(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        if(isHovering(52, 34, 18, 18, mouseX, mouseY)) {
            //Weather clear button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.weather_controller.btn.clear"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(88, 34, 18, 18, mouseX, mouseY)) {
            //Weather rain button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.weather_controller.btn.rain"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(124, 34, 18, 18, mouseX, mouseY)) {
            //Weather thunder button

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.weather_controller.btn.thunder"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
