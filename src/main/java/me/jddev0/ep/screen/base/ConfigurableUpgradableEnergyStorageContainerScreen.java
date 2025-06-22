package me.jddev0.ep.screen.base;

import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeComparatorModeC2SPacket;
import me.jddev0.ep.networking.packet.ChangeRedstoneModeC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class ConfigurableUpgradableEnergyStorageContainerScreen
        <T extends ScreenHandler & IEnergyStorageMenu & IConfigurableMenu>
        extends UpgradableEnergyStorageContainerScreen<T> {
    public ConfigurableUpgradableEnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                              Identifier texture,
                                                              Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, texture, upgradeViewTexture);
    }

    public ConfigurableUpgradableEnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                              String energyIndicatorBarTooltipComponentID,
                                                              Identifier texture,
                                                              Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture,
                upgradeViewTexture);
    }

    @Override
    protected boolean mouseClickedConfiguration(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedConfiguration(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
                //Redstone Mode

                ModMessages.sendClientPacketToServer(new ChangeRedstoneModeC2SPacket(handler.getBlockEntity().getPos()));
                return true;
            }else if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
                //Comparator Mode

                ModMessages.sendClientPacketToServer(new ChangeComparatorModeC2SPacket(handler.getBlockEntity().getPos()));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderConfiguration(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        super.renderConfiguration(drawContext, x, y, mouseX, mouseY);

        RedstoneMode redstoneMode = handler.getRedstoneMode();
        int ordinal = redstoneMode.ordinal();

        if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 26, 20 * ordinal, 20, 20, 20, 256, 256);
        }else {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 26, 20 * ordinal, 0, 20, 20, 256, 256);
        }

        ComparatorMode comparatorMode = handler.getComparatorMode();
        ordinal = comparatorMode.ordinal();

        if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 50, 20 * ordinal, 60, 20, 20, 256, 256);
        }else {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 50, 20 * ordinal, 40, 20, 20, 256, 256);
        }
    }

    @Override
    protected void renderTooltipConfiguration(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipConfiguration(drawContext, mouseX, mouseY);

        if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
            //Redstone Mode

            RedstoneMode redstoneMode = handler.getRedstoneMode();

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.machine_configuration.redstone_mode." + redstoneMode.asString()));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
            //Comparator Mode

            ComparatorMode comparatorMode = handler.getComparatorMode();

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.machine_configuration.comparator_mode." + comparatorMode.asString()));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
