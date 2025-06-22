package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class BlockPlacerScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<BlockPlacerMenu> {
    public BlockPlacerScreen(BlockPlacerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.block_placer.block_energy_left.txt",
                EPAPI.id("textures/gui/container/block_placer.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isPointWithinBounds(158, 16, 11, 11, mouseX, mouseY)) {
                //Inverse rotation checkbox

                ModMessages.sendClientPacketToServer(new SetCheckboxC2SPacket(handler.getBlockEntity().getPos(), 0, !handler.isInverseRotation()));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderCheckboxes(drawContext, x, y, mouseX, mouseY);
    }

    private void renderCheckboxes(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        if(handler.isInverseRotation()) {
            //Inverse rotation checkbox

            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 158, y + 16, 0, 139, 11, 11, 256, 256);
        }
    }

    @Override
    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipNormalView(drawContext, mouseX, mouseY);

        if(isPointWithinBounds(158, 16, 11, 11, mouseX, mouseY)) {
            //Inverse rotation checkbox

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.block_placer.cbx.inverse_rotation"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
