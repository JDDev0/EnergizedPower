package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.api.EPAPI;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    protected void renderBgNormalView(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {}

    @Override
    protected final void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(menu.isInUpgradeModuleView()) {
            RenderSystem.setShaderTexture(0, UPGRADE_VIEW_TEXTURE);
            blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
            RenderSystem.setShaderTexture(0, TEXTURE);
        }else {
            renderBgNormalView(poseStack, partialTick, mouseX, mouseY);
        }

        RenderSystem.setShaderTexture(0, CONFIGURATION_ICONS_TEXTURE);
        renderConfiguration(poseStack, x, y, mouseX, mouseY);
        RenderSystem.setShaderTexture(0, TEXTURE);
    }

    protected void renderConfiguration(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        //Upgrade view
        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            blit(poseStack, x - 22, y + 2, 40, 80, 20, 20);
        }else if(menu.isInUpgradeModuleView()) {
            blit(poseStack, x - 22, y + 2, 20, 80, 20, 20);
        }else {
            blit(poseStack, x - 22, y + 2, 0, 80, 20, 20);
        }
    }

    protected void renderTooltipNormalView(PoseStack poseStack, int mouseX, int mouseY) {}

    protected void renderTooltipConfiguration(PoseStack poseStack, int mouseX, int mouseY) {
        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            //Upgrade view

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.upgrade_view.button." +
                    (menu.isInUpgradeModuleView()?"close":"open")));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected final void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);

        if(!menu.isInUpgradeModuleView())
            renderTooltipNormalView(poseStack, mouseX, mouseY);

        renderTooltipConfiguration(poseStack, mouseX, mouseY);
    }
}
