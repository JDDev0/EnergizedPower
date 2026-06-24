package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
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
    protected void renderBg(GuiGraphics drawContext, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        drawContext.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderCheckboxes(drawContext, x, y, mouseX, mouseY);
        renderCheckboxLabels(drawContext, x, y, mouseX, mouseY);
    }

    private void renderCheckboxes(GuiGraphics drawContext, int x, int y, int mouseX, int mouseY) {
        if(menu.isEnergyProduction()) {
            //Energy Production checkbox

            drawContext.blit(MACHINE_SPRITES_TEXTURE, x + 10, y + 28, 0, 139, 11, 11);
        }

        if(menu.isEnergyConsumption()) {
            //Energy Consumption checkbox

            drawContext.blit(MACHINE_SPRITES_TEXTURE, x + 10, y + 46, 0, 139, 11, 11);
        }
    }

    private void renderCheckboxLabels(GuiGraphics drawContext, int x, int y, int mouseX, int mouseY) {
        drawContext.drawString(font, Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_production"),
                x + 25, y + 30, 0, false);

        drawContext.drawString(font, Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_consumption"),
                x + 25, y + 48, 0, false);
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        renderTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics drawContext, int mouseX, int mouseY) {
        super.renderTooltip(drawContext, mouseX, mouseY);

        if(isHovering(10, 28, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_production"));

            drawContext.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(10, 46, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_consumption"));

            drawContext.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
