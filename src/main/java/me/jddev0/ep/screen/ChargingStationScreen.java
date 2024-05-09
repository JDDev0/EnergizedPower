package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ChargingStationScreen extends AbstractGenericEnergyStorageHandledScreen<ChargingStationMenu> {
    private final Identifier CONFIGURATION_ICONS_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID, "textures/gui/machine_configuration/configuration_buttons.png");
    private final Identifier UPGRADE_VIEW_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID,
                    "textures/gui/container/upgrade_view/1_energy_capacity_1_range.png");

    public ChargingStationScreen(ChargingStationMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
                //Upgrade view

                client.interactionManager.clickButton(handler.syncId, 0);
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        if(handler.isInUpgradeModuleView()) {
            drawContext.drawTexture(UPGRADE_VIEW_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        }

        renderConfiguration(drawContext, x, y, mouseX, mouseY);
    }

    private void renderConfiguration(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        //Upgrade view
        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 40, 80, 20, 20);
        }else if(handler.isInUpgradeModuleView()) {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20, 80, 20, 20);
        }else {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 0, 80, 20, 20);
        }
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            //Upgrade view

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.upgrade_view.button." +
                    (handler.isInUpgradeModuleView()?"close":"open")));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
