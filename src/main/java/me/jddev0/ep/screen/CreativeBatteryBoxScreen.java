package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
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
public class CreativeBatteryBoxScreen extends EnergizedPowerBaseContainerScreen<CreativeBatteryBoxMenu> {
    private final ResourceLocation TEXTURE;

    public CreativeBatteryBoxScreen(CreativeBatteryBoxMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/creative_battery_box.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isHovering(10, 28, 11, 11, mouseX, mouseY)) {
                //Energy Production checkbox

                ModMessages.sendToServer(new SetCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), 0, !menu.isEnergyProduction()));
                clicked = true;
            }else if(isHovering(10, 46, 11, 11, mouseX, mouseY)) {
                //Energy Consumption checkbox

                ModMessages.sendToServer(new SetCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), 1, !menu.isEnergyConsumption()));
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

        renderCheckboxes(poseStack, x, y, mouseX, mouseY);
        renderCheckboxLabels(poseStack, x, y, mouseX, mouseY);
    }

    private void renderCheckboxes(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(menu.isEnergyProduction()) {
            //Energy Production checkbox

            blit(poseStack, x + 10, y + 28, 176, 0, 11, 11);
        }

        if(menu.isEnergyConsumption()) {
            //Energy Consumption checkbox

            blit(poseStack, x + 10, y + 46, 176, 0, 11, 11);
        }
    }

    private void renderCheckboxLabels(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        font.draw(poseStack, Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_production"),
                x + 25, y + 30, 0);

        font.draw(poseStack, Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_consumption"),
                x + 25, y + 48, 0);
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

        if(isHovering(10, 28, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_production"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(10, 46, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_consumption"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
