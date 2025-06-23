package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.CycleAutoCrafterRecipeOutputC2SPacket;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class AutoCrafterScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<AutoCrafterMenu> {
    public AutoCrafterScreen(AutoCrafterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/auto_crafter.png"),
                EPAPI.id("textures/gui/container/upgrade_view/auto_crafter.png"));

        imageHeight = 206;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isHovering(158, 16, 11, 11, mouseX, mouseY)) {
                //Ignore NBT checkbox

                ModMessages.sendToServer(new SetCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), 0, !menu.isIgnoreNBT()));
                return true;
            }else if(isHovering(158, 38, 11, 11, mouseX, mouseY)) {
                //Extract mode checkbox

                ModMessages.sendToServer(new SetCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), 1, !menu.isSecondaryExtractMode()));
                return true;
            }else if(isHovering(126, 16, 12, 12, mouseX, mouseY)) {
                //Cycle through recipes

                ModMessages.sendToServer(new CycleAutoCrafterRecipeOutputC2SPacket(menu.getBlockEntity().getBlockPos()));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderBgNormalView(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(guiGraphics, x, y);
        renderCheckboxes(guiGraphics, x, y, mouseX, mouseY);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCraftingActive())
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 89, y + 34, 0, 58, menu.getScaledProgressArrowSize(), 17, 256, 256);
    }

    private void renderCheckboxes(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(menu.isIgnoreNBT()) {
            //Ignore NBT checkbox

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 158, y + 16, 0, 139, 11, 11, 256, 256);
        }

        if(menu.isSecondaryExtractMode()) {
            //Extract mode checkbox [2]

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 158, y + 38, 22, 139, 11, 11, 256, 256);
        }else {
            //Extract mode checkbox [1]

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 158, y + 38, 11, 139, 11, 11, 256, 256);
        }
    }

    @Override
    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        if(isHovering(158, 16, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_crafter.cbx.ignore_nbt"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(158, 38, 11, 11, mouseX, mouseY)) {
            //Extract mode

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_crafter.cbx.extract_mode." + (menu.isSecondaryExtractMode()?"2":"1")));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(126, 16, 12, 12, mouseX, mouseY)) {
            //Cycle through recipes

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_crafter.cycle_through_recipes"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
