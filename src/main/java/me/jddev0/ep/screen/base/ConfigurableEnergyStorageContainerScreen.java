package me.jddev0.ep.screen.base;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeRedstoneModeC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class ConfigurableEnergyStorageContainerScreen
        <T extends ScreenHandler & IEnergyStorageMenu & IConfigurableMenu>
        extends EnergyStorageContainerScreen<T> {
    protected final Identifier CONFIGURATION_ICONS_TEXTURE =
            EPAPI.id("textures/gui/machine_configuration/configuration_buttons.png");

    public ConfigurableEnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent) {
        super(menu, inventory, titleComponent);
    }

    public ConfigurableEnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                    Identifier texture) {
        super(menu, inventory, titleComponent, texture);
    }

    public ConfigurableEnergyStorageContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                    String energyIndicatorBarTooltipComponentID,
                                                    Identifier texture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture);
    }

    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    protected boolean mouseClickedConfiguration(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
                //Redstone Mode

                ModMessages.sendClientPacketToServer(new ChangeRedstoneModeC2SPacket(handler.getBlockEntity().getPos()));
                return true;
            }
        }

        return false;
    }

    @Override
    public final boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();

        boolean clicked = mouseClickedNormalView(mouseX, mouseY, mouseButton);

        clicked |= mouseClickedConfiguration(mouseX, mouseY, mouseButton);

        if(clicked)
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));

        return super.mouseClicked(click, doubled);
    }

    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {}

    @Override
    protected final void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        renderConfiguration(drawContext, x, y, mouseX, mouseY);
    }

    protected void renderConfiguration(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        RedstoneMode redstoneMode = handler.getRedstoneMode();
        int ordinal = redstoneMode.ordinal();

        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20 * ordinal, 20, 20, 20, 256, 256);
        }else {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20 * ordinal, 0, 20, 20, 256, 256);
        }
    }

    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {}

    protected void renderTooltipConfiguration(DrawContext drawContext, int mouseX, int mouseY) {
        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            //Redstone Mode

            RedstoneMode redstoneMode = handler.getRedstoneMode();

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.machine_configuration.redstone_mode." + redstoneMode.asString()));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected final void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

        renderTooltipNormalView(drawContext, mouseX, mouseY);

        renderTooltipConfiguration(drawContext, mouseX, mouseY);
    }
}
