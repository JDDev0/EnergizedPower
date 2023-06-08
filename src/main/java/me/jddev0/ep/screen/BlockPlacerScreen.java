package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetBlockPlacerCheckboxC2SPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class BlockPlacerScreen extends AbstractGenericEnergyStorageContainerScreen<BlockPlacerMenu> {
    public BlockPlacerScreen(BlockPlacerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.block_placer.block_energy_left.txt",
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/block_placer.png"),
                8, 17);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isHovering(158, 16, 11, 11, mouseX, mouseY)) {
                //Inverse rotation checkbox

                ModMessages.sendToServer(new SetBlockPlacerCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), 0, !menu.isInverseRotation()));
                clicked = true;
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderCheckboxes(guiGraphics, x, y, mouseX, mouseY);
    }

    private void renderCheckboxes(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(menu.isInverseRotation()) {
            //Inverse rotation checkbox

            guiGraphics.blit(TEXTURE, x + 158, y + 16, 176, 53, 11, 11);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isHovering(158, 16, 11, 11, mouseX, mouseY)) {
            //Inverse rotation checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.block_placer.cbx.inverse_rotation"));

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
