package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeComparatorModeC2SPacket;
import me.jddev0.ep.networking.packet.ChangeRedstoneModeC2SPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public abstract class ConfigurableUpgradableEnergyStorageContainerScreen
        <T extends AbstractContainerMenu & EnergyStorageMenu & ConfigurableMenu>
        extends UpgradableEnergyStorageContainerScreen<T> {
    public ConfigurableUpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                              ResourceLocation upgradeViewTexture) {
        super(menu, inventory, titleComponent, upgradeViewTexture);
    }

    public ConfigurableUpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                              String energyIndicatorBarTooltipComponentID,
                                                              ResourceLocation upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, upgradeViewTexture);
    }

    public ConfigurableUpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                              ResourceLocation texture, int energyMeterX, int energyMeterY,
                                                              ResourceLocation upgradeViewTexture) {
        super(menu, inventory, titleComponent, texture, energyMeterX, energyMeterY, upgradeViewTexture);
    }

    public ConfigurableUpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                              String energyIndicatorBarTooltipComponentID,
                                                              ResourceLocation texture, int energyMeterX, int energyMeterY,
                                                              ResourceLocation upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture, energyMeterX, energyMeterY,
                upgradeViewTexture);
    }

    public ConfigurableUpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                              String energyIndicatorBarTooltipComponentID,
                                                              ResourceLocation texture, int energyMeterX, int energyMeterY,
                                                              int energyMeterWidth, int energyMeterHeight,
                                                              ResourceLocation upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture, energyMeterX, energyMeterY,
                energyMeterWidth, energyMeterHeight, upgradeViewTexture);
    }

    @Override
    protected boolean mouseClickedConfiguration(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedConfiguration(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
                //Redstone Mode

                ModMessages.sendToServer(new ChangeRedstoneModeC2SPacket(menu.getBlockEntity().getBlockPos()));
                return true;
            }else if(isHovering(-22, 50, 20, 20, mouseX, mouseY)) {
                //Comparator Mode

                ModMessages.sendToServer(new ChangeComparatorModeC2SPacket(menu.getBlockEntity().getBlockPos()));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderConfiguration(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        super.renderConfiguration(poseStack, x, y, mouseX, mouseY);

        RedstoneMode redstoneMode = menu.getRedstoneMode();
        int ordinal = redstoneMode.ordinal();

        if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
            blit(poseStack, x - 22, y + 26, 20 * ordinal, 20, 20, 20);
        }else {
            blit(poseStack, x - 22, y + 26, 20 * ordinal, 0, 20, 20);
        }

        ComparatorMode comparatorMode = menu.getComparatorMode();
        ordinal = comparatorMode.ordinal();

        if(isHovering(-22, 50, 20, 20, mouseX, mouseY)) {
            blit(poseStack, x - 22, y + 50, 20 * ordinal, 60, 20, 20);
        }else {
            blit(poseStack, x - 22, y + 50, 20 * ordinal, 40, 20, 20);
        }
    }

    @Override
    protected void renderTooltipConfiguration(PoseStack poseStack, int mouseX, int mouseY) {
        if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
            //Redstone Mode

            RedstoneMode redstoneMode = menu.getRedstoneMode();

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.machine_configuration.redstone_mode." + redstoneMode.getSerializedName()));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(-22, 50, 20, 20, mouseX, mouseY)) {
            //Comparator Mode

            ComparatorMode comparatorMode = menu.getComparatorMode();

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.machine_configuration.comparator_mode." + comparatorMode.getSerializedName()));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
