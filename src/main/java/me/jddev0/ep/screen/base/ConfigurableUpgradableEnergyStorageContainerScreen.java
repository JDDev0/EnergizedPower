package me.jddev0.ep.screen.base;

import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeComparatorModeC2SPacket;
import me.jddev0.ep.networking.packet.ChangeRedstoneModeC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class ConfigurableUpgradableEnergyStorageContainerScreen
        <T extends AbstractContainerMenu & IEnergyStorageMenu & IConfigurableMenu>
        extends UpgradableEnergyStorageContainerScreen<T> {
    public ConfigurableUpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                              Identifier texture,
                                                              Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, texture, upgradeViewTexture);
    }

    public ConfigurableUpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                              String energyIndicatorBarTooltipComponentID,
                                                              Identifier texture,
                                                              Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture,
                upgradeViewTexture);
    }

    public ConfigurableUpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                              String energyIndicatorBarTooltipComponentID,
                                                              Identifier texture,
                                                              Identifier upgradeViewTexture,
                                                              int imageWidth, int imageHeight) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture,
                upgradeViewTexture, imageWidth, imageHeight);
    }

    @Override
    protected boolean mouseClickedConfiguration(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedConfiguration(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
                //Redstone Mode

                ModMessages.sendClientPacketToServer(new ChangeRedstoneModeC2SPacket(menu.getBlockEntity().getBlockPos()));
                return true;
            }else if(isHovering(-22, 50, 20, 20, mouseX, mouseY)) {
                //Comparator Mode

                ModMessages.sendClientPacketToServer(new ChangeComparatorModeC2SPacket(menu.getBlockEntity().getBlockPos()));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderConfiguration(GuiGraphicsExtractor drawContext, int x, int y, int mouseX, int mouseY) {
        super.renderConfiguration(drawContext, x, y, mouseX, mouseY);

        RedstoneMode redstoneMode = menu.getRedstoneMode();
        int ordinal = redstoneMode.ordinal();

        if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 26, 20 * ordinal, 20, 20, 20, 256, 256);
        }else {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 26, 20 * ordinal, 0, 20, 20, 256, 256);
        }

        ComparatorMode comparatorMode = menu.getComparatorMode();
        ordinal = comparatorMode.ordinal();

        if(isHovering(-22, 50, 20, 20, mouseX, mouseY)) {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 50, 20 * ordinal, 60, 20, 20, 256, 256);
        }else {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 50, 20 * ordinal, 40, 20, 20, 256, 256);
        }
    }

    @Override
    protected void extractLabelsConfiguration(GuiGraphicsExtractor drawContext, int mouseX, int mouseY) {
        super.extractLabelsConfiguration(drawContext, mouseX, mouseY);

        if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
            //Redstone Mode

            RedstoneMode redstoneMode = menu.getRedstoneMode();

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.machine_configuration.redstone_mode." + redstoneMode.getSerializedName()));

            drawContext.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(-22, 50, 20, 20, mouseX, mouseY)) {
            //Comparator Mode

            ComparatorMode comparatorMode = menu.getComparatorMode();

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.machine_configuration.comparator_mode." + comparatorMode.getSerializedName()));

            drawContext.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
