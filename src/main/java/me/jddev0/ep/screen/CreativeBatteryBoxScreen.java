package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreativeBatteryBoxScreen extends EnergizedPowerBaseContainerScreen<CreativeBatteryBoxMenu> {
    private final ResourceLocation TEXTURE;

    public CreativeBatteryBoxScreen(CreativeBatteryBoxMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/creative_battery_box.png");
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();
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

        return super.mouseClicked(click, doubled);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        renderCheckboxes(guiGraphics, x, y, mouseX, mouseY);
        renderCheckboxLabels(guiGraphics, x, y, mouseX, mouseY);
    }

    private void renderCheckboxes(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(menu.isEnergyProduction()) {
            //Energy Production checkbox

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 10, y + 28, 0, 139, 11, 11, 256, 256);
        }

        if(menu.isEnergyConsumption()) {
            //Energy Consumption checkbox

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 10, y + 46, 0, 139, 11, 11, 256, 256);
        }
    }

    private void renderCheckboxLabels(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        guiGraphics.drawString(font, Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_production"),
                x + 25, y + 30, 0xFF000000, false);

        guiGraphics.drawString(font, Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_consumption"),
                x + 25, y + 48, 0xFF000000, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isHovering(10, 28, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_production"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(10, 46, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_consumption"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
