package me.jddev0.ep.screen.base;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public abstract class UpgradableEnergyStorageContainerScreen<T extends AbstractContainerMenu & IEnergyStorageMenu>
        extends EnergyStorageContainerScreen<T> {
    protected final ResourceLocation CONFIGURATION_ICONS_TEXTURE =
            EPAPI.id("textures/gui/machine_configuration/configuration_buttons.png");
    protected final ResourceLocation UPGRADE_VIEW_TEXTURE;

    public UpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  ResourceLocation upgradeViewTexture) {
        super(menu, inventory, titleComponent);

        this.UPGRADE_VIEW_TEXTURE = upgradeViewTexture;
    }

    public UpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  String energyIndicatorBarTooltipComponentID,
                                                  ResourceLocation upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID);

        this.UPGRADE_VIEW_TEXTURE = upgradeViewTexture;
    }

    public UpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  ResourceLocation texture,
                                                  ResourceLocation upgradeViewTexture) {
        super(menu, inventory, titleComponent, texture);

        this.UPGRADE_VIEW_TEXTURE = upgradeViewTexture;
    }

    public UpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  String energyIndicatorBarTooltipComponentID, ResourceLocation texture,
                                                  ResourceLocation upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture);

        this.UPGRADE_VIEW_TEXTURE = upgradeViewTexture;
    }

    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    protected boolean mouseClickedConfiguration(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
                //Upgrade view

                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                return true;
            }
        }

        return false;
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean clicked = false;
        if(!menu.isInUpgradeModuleView())
            clicked = mouseClickedNormalView(mouseX, mouseY, mouseButton);

        clicked |= mouseClickedConfiguration(mouseX, mouseY, mouseButton);

        if(clicked)
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void renderBgNormalView(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {}

    @Override
    protected final void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(menu.isInUpgradeModuleView()) {
            guiGraphics.blit(UPGRADE_VIEW_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        }else {
            renderBgNormalView(guiGraphics, partialTick, mouseX, mouseY);
        }

        renderConfiguration(guiGraphics, x, y, mouseX, mouseY);
    }

    protected void renderConfiguration(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        //Upgrade view
        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            guiGraphics.blit(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 40, 80, 20, 20);
        }else if(menu.isInUpgradeModuleView()) {
            guiGraphics.blit(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20, 80, 20, 20);
        }else {
            guiGraphics.blit(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 0, 80, 20, 20);
        }
    }

    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    protected void renderTooltipConfiguration(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            //Upgrade view

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.upgrade_view.button." +
                    (menu.isInUpgradeModuleView()?"close":"open")));

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected final void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(!menu.isInUpgradeModuleView())
            renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        renderTooltipConfiguration(guiGraphics, mouseX, mouseY);
    }
}
