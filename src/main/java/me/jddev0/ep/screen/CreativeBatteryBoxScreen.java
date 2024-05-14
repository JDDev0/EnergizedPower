package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CreativeBatteryBoxScreen extends HandledScreen<CreativeBatteryBoxMenu> {
    private final Identifier TEXTURE;

    public CreativeBatteryBoxScreen(CreativeBatteryBoxMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/creative_battery_box.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isPointWithinBounds(10, 28, 11, 11, mouseX, mouseY)) {
                //Energy Production checkbox

                ModMessages.sendClientPacketToServer(
                        new SetCheckboxC2SPacket(handler.getBlockEntity().getPos(), 0, !handler.isEnergyProduction()));
                clicked = true;
            }else if(isPointWithinBounds(10, 46, 11, 11, mouseX, mouseY)) {
                //Energy Consumption checkbox

                ModMessages.sendClientPacketToServer(
                        new SetCheckboxC2SPacket(handler.getBlockEntity().getPos(), 1, !handler.isEnergyConsumption()));
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawBackground(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawTexture(poseStack, x, y, 0, 0, backgroundWidth, backgroundHeight);

        renderCheckboxes(poseStack, x, y, mouseX, mouseY);
        renderCheckboxLabels(poseStack, x, y, mouseX, mouseY);
    }

    private void renderCheckboxes(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(handler.isEnergyProduction()) {
            //Energy Production checkbox

            drawTexture(poseStack, x + 10, y + 28, 176, 0, 11, 11);
        }

        if(handler.isEnergyConsumption()) {
            //Energy Consumption checkbox

            drawTexture(poseStack, x + 10, y + 46, 176, 0, 11, 11);
        }
    }

    private void renderCheckboxLabels(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        textRenderer.draw(poseStack, Text.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_production"),
                x + 25, y + 30, 0);

        textRenderer.draw(poseStack, Text.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_consumption"),
                x + 25, y + 48, 0);
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

        if(isPointWithinBounds(10, 28, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_production"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(10, 46, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.creative_battery_box.cbx.energy_consumption"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
