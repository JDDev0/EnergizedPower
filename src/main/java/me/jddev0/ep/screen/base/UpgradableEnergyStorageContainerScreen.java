package me.jddev0.ep.screen.base;

import me.jddev0.ep.api.EPAPI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class UpgradableEnergyStorageContainerScreen<T extends AbstractContainerMenu & IEnergyStorageMenu>
        extends EnergyStorageContainerScreen<T> {
    protected final Identifier CONFIGURATION_ICONS_TEXTURE =
            EPAPI.id("textures/gui/machine_configuration/configuration_buttons.png");
    protected final Identifier UPGRADE_VIEW_TEXTURE;

    public UpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent);

        this.UPGRADE_VIEW_TEXTURE = upgradeViewTexture;
    }

    public UpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  String energyIndicatorBarTooltipComponentID,
                                                  Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID);

        this.UPGRADE_VIEW_TEXTURE = upgradeViewTexture;
    }

    public UpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  Identifier texture,
                                                  Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, texture);

        this.UPGRADE_VIEW_TEXTURE = upgradeViewTexture;
    }

    public UpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  String energyIndicatorBarTooltipComponentID, Identifier texture,
                                                  Identifier upgradeViewTexture) {
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
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();

        boolean clicked = false;
        if(!menu.isInUpgradeModuleView())
            clicked = mouseClickedNormalView(mouseX, mouseY, mouseButton);

        clicked |= mouseClickedConfiguration(mouseX, mouseY, mouseButton);

        if(clicked)
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));

        return super.mouseClicked(click, doubled);
    }

    protected void renderBgNormalView(GuiGraphics drawContext, float partialTick, int mouseX, int mouseY) {}

    @Override
    protected final void renderBg(GuiGraphics drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBg(drawContext, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(menu.isInUpgradeModuleView()) {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, UPGRADE_VIEW_TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
        }else {
            renderBgNormalView(drawContext, partialTick, mouseX, mouseY);
        }

        renderConfiguration(drawContext, x, y, mouseX, mouseY);
    }

    protected void renderConfiguration(GuiGraphics drawContext, int x, int y, int mouseX, int mouseY) {
        //Upgrade view
        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 40, 80, 20, 20, 256, 256);
        }else if(menu.isInUpgradeModuleView()) {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20, 80, 20, 20, 256, 256);
        }else {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 0, 80, 20, 20, 256, 256);
        }
    }

    protected void renderTooltipNormalView(GuiGraphics drawContext, int mouseX, int mouseY) {}

    protected void renderTooltipConfiguration(GuiGraphics drawContext, int mouseX, int mouseY) {
        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            //Upgrade view

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.upgrade_view.button." +
                    (menu.isInUpgradeModuleView()?"close":"open")));

            drawContext.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected final void renderTooltip(GuiGraphics drawContext, int mouseX, int mouseY) {
        super.renderTooltip(drawContext, mouseX, mouseY);

        if(!menu.isInUpgradeModuleView())
            renderTooltipNormalView(drawContext, mouseX, mouseY);

        renderTooltipConfiguration(drawContext, mouseX, mouseY);
    }
}
