package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.SolarPanelBlock;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SolarPanelScreen extends AbstractGenericEnergyStorageContainerScreen<SolarPanelMenu> {
    private final ResourceLocation CONFIGURATION_ICONS_TEXTURE =
            new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/machine_configuration/configuration_buttons.png");
    private final ResourceLocation UPGRADE_VIEW_TEXTURE =
            new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/upgrade_view/1_moon_light.png");

    public SolarPanelScreen(SolarPanelMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    public SolarPanelBlock.Tier getTier() {
        return menu.getTier();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
                //Upgrade view

                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                clicked = true;
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(menu.isInUpgradeModuleView()) {
            guiGraphics.blit(UPGRADE_VIEW_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        }

        renderConfiguration(guiGraphics, x, y, mouseX, mouseY);
    }

    private void renderConfiguration(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        //Upgrade view
        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            guiGraphics.blit(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 40, 80, 20, 20);
        }else if(menu.isInUpgradeModuleView()) {
            guiGraphics.blit(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20, 80, 20, 20);
        }else {
            guiGraphics.blit(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 0, 80, 20, 20);
        }
    }
}
