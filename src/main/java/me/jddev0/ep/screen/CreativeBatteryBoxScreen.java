package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CreativeBatteryBoxScreen extends EnergizedPowerBaseContainerScreen<CreativeBatteryBoxMenu> {
    private final Identifier TEXTURE;

    public CreativeBatteryBoxScreen(CreativeBatteryBoxMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/creative_battery_box.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isPointWithinBounds(10, 28, 11, 11, mouseX, mouseY)) {
                //Energy Production checkbox

                ModMessages.sendClientPacketToServer(new SetCheckboxC2SPacket(handler.getBlockEntity().getPos(), 0, !handler.isEnergyProduction()));
                clicked = true;
            }else if(isPointWithinBounds(10, 46, 11, 11, mouseX, mouseY)) {
                //Energy Consumption checkbox

                ModMessages.sendClientPacketToServer(new SetCheckboxC2SPacket(handler.getBlockEntity().getPos(), 1, !handler.isEnergyConsumption()));
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawContext.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        renderCheckboxes(drawContext, x, y, mouseX, mouseY);
        renderCheckboxLabels(drawContext, x, y, mouseX, mouseY);
    }

    private void renderCheckboxes(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        if(handler.isEnergyProduction()) {
            //Energy Production checkbox

            drawContext.drawTexture(TEXTURE, x + 10, y + 28, 176, 0, 11, 11);
        }

        if(handler.isEnergyConsumption()) {
            //Energy Consumption checkbox

            drawContext.drawTexture(TEXTURE, x + 10, y + 46, 176, 0, 11, 11);
        }
    }

    private void renderCheckboxLabels(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        drawContext.drawText(textRenderer, Text.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_production"),
                x + 25, y + 30, 0, false);

        drawContext.drawText(textRenderer, Text.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_consumption"),
                x + 25, y + 48, 0, false);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

        if(isPointWithinBounds(10, 28, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_production"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(10, 46, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_consumption"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
