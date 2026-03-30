package me.jddev0.ep.screen.base;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeRedstoneModeC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
public abstract class ConfigurableEnergyStorageContainerScreen
        <T extends AbstractContainerMenu & IEnergyStorageMenu & IConfigurableMenu>
        extends EnergyStorageContainerScreen<T> {
    protected final Identifier CONFIGURATION_ICONS_TEXTURE =
            EPAPI.id("textures/gui/machine_configuration/configuration_buttons.png");

    public ConfigurableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent) {
        super(menu, inventory, titleComponent);
    }

    public ConfigurableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                    Identifier texture) {
        super(menu, inventory, titleComponent, texture);
    }

    public ConfigurableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                    String energyIndicatorBarTooltipComponentID,
                                                    Identifier texture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture);
    }

    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    protected boolean mouseClickedConfiguration(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
                //Redstone Mode

                ModMessages.sendClientPacketToServer(new ChangeRedstoneModeC2SPacket(menu.getBlockEntity().getBlockPos()));
                return true;
            }
        }

        return false;
    }

    @Override
    public final boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();

        boolean clicked = mouseClickedNormalView(mouseX, mouseY, mouseButton);

        clicked |= mouseClickedConfiguration(mouseX, mouseY, mouseButton);

        if(clicked)
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));

        return super.mouseClicked(click, doubled);
    }

    protected void extractBackgroundNormalView(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float a) {}

    @Override
    public final void extractBackground(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float a) {
        super.extractBackground(drawContext, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        extractBackgroundNormalView(drawContext, mouseX, mouseY, a);

        renderConfiguration(drawContext, x, y, mouseX, mouseY);
    }

    protected void renderConfiguration(GuiGraphicsExtractor drawContext, int x, int y, int mouseX, int mouseY) {
        RedstoneMode redstoneMode = menu.getRedstoneMode();
        int ordinal = redstoneMode.ordinal();

        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20 * ordinal, 20, 20, 20, 256, 256);
        }else {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20 * ordinal, 0, 20, 20, 256, 256);
        }
    }

    protected void extractLabelsNormalView(GuiGraphicsExtractor drawContext, int mouseX, int mouseY) {}

    protected void extractLabelsConfiguration(GuiGraphicsExtractor drawContext, int mouseX, int mouseY) {
        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            //Redstone Mode

            RedstoneMode redstoneMode = menu.getRedstoneMode();

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.machine_configuration.redstone_mode." + redstoneMode.getSerializedName()));

            drawContext.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected final void extractLabels(GuiGraphicsExtractor drawContext, int mouseX, int mouseY) {
        super.extractLabels(drawContext, mouseX, mouseY);

        extractLabelsNormalView(drawContext, mouseX, mouseY);

        extractLabelsConfiguration(drawContext, mouseX, mouseY);
    }
}
