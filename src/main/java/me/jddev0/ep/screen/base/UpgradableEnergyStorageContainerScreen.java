package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class UpgradableEnergyStorageContainerScreen<T extends ScreenHandler & IEnergyStorageMenu>
        extends EnergyStorageContainerScreen<T> {
    protected final Identifier CONFIGURATION_ICONS_TEXTURE =
            EPAPI.id("textures/gui/machine_configuration/configuration_buttons.png");
    protected final Identifier UPGRADE_VIEW_TEXTURE;

    public UpgradableEnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                  Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent);

        this.UPGRADE_VIEW_TEXTURE = upgradeViewTexture;
    }

    public UpgradableEnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                  String energyIndicatorBarTooltipComponentID,
                                                  Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID);

        this.UPGRADE_VIEW_TEXTURE = upgradeViewTexture;
    }

    public UpgradableEnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                  Identifier texture,
                                                  Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, texture);

        this.UPGRADE_VIEW_TEXTURE = upgradeViewTexture;
    }

    public UpgradableEnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
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
            if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
                //Upgrade view

                client.interactionManager.clickButton(handler.syncId, 0);
                return true;
            }
        }

        return false;
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean clicked = false;
        if(!handler.isInUpgradeModuleView())
            clicked = mouseClickedNormalView(mouseX, mouseY, mouseButton);

        clicked |= mouseClickedConfiguration(mouseX, mouseY, mouseButton);

        if(clicked)
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void renderBgNormalView(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {}

    @Override
    protected final void drawBackground(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(poseStack, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        if(handler.isInUpgradeModuleView()) {
            RenderSystem.setShaderTexture(0, UPGRADE_VIEW_TEXTURE);
            drawTexture(poseStack, x, y, 0, 0, backgroundWidth, backgroundHeight);
            RenderSystem.setShaderTexture(0, TEXTURE);
        }else {
            renderBgNormalView(poseStack, partialTick, mouseX, mouseY);
        }

        RenderSystem.setShaderTexture(0, CONFIGURATION_ICONS_TEXTURE);
        renderConfiguration(poseStack, x, y, mouseX, mouseY);
        RenderSystem.setShaderTexture(0, TEXTURE);
    }

    protected void renderConfiguration(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        //Upgrade view
        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            drawTexture(poseStack, x - 22, y + 2, 40, 80, 20, 20);
        }else if(handler.isInUpgradeModuleView()) {
            drawTexture(poseStack, x - 22, y + 2, 20, 80, 20, 20);
        }else {
            drawTexture(poseStack, x - 22, y + 2, 0, 80, 20, 20);
        }
    }

    protected void renderTooltipNormalView(MatrixStack poseStack, int mouseX, int mouseY) {}

    protected void renderTooltipConfiguration(MatrixStack poseStack, int mouseX, int mouseY) {
        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            //Upgrade view

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.upgrade_view.button." +
                    (handler.isInUpgradeModuleView()?"close":"open")));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected final void drawMouseoverTooltip(MatrixStack poseStack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(poseStack, mouseX, mouseY);

        if(!handler.isInUpgradeModuleView())
            renderTooltipNormalView(poseStack, mouseX, mouseY);

        renderTooltipConfiguration(poseStack, mouseX, mouseY);
    }
}
