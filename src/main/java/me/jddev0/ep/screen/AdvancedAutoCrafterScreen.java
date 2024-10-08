package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import me.jddev0.ep.networking.packet.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AdvancedAutoCrafterScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedAutoCrafterMenu> {
    public AdvancedAutoCrafterScreen(AdvancedAutoCrafterMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/advanced_auto_crafter.png"),
                EPAPI.id("textures/gui/container/upgrade_view/advanced_auto_crafter.png"));

        backgroundHeight = 224;
        playerInventoryTitleY = backgroundHeight - 94;
    }

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isPointWithinBounds(158, 16, 11, 11, mouseX, mouseY)) {
                //Ignore NBT checkbox

                ModMessages.sendClientPacketToServer(new SetCheckboxC2SPacket(handler.getBlockEntity().getPos(), 0, !handler.isIgnoreNBT()));
                return true;
            }else if(isPointWithinBounds(158, 38, 11, 11, mouseX, mouseY)) {
                //Extract mode checkbox

                ModMessages.sendClientPacketToServer(new SetCheckboxC2SPacket(handler.getBlockEntity().getPos(), 1, !handler.isSecondaryExtractMode()));
                return true;
            }else if(isPointWithinBounds(126, 16, 12, 12, mouseX, mouseY)) {
                //Cycle through recipes

                ModMessages.sendClientPacketToServer(new CycleAdvancedAutoCrafterRecipeOutputC2SPacket(handler.getBlockEntity().getPos()));
                return true;
            }else if(isPointWithinBounds(96, 16, 12, 12, mouseX, mouseY)) {
                //Set recipe index

                ModMessages.sendClientPacketToServer(new SetAdvancedAutoCrafterRecipeIndexC2SPacket(handler.getBlockEntity().getPos(), handler.getRecipeIndex() + 1));
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

        renderProgressArrow(drawContext, x, y);
        renderCheckboxes(drawContext, x, y, mouseX, mouseY);
    }

    private void renderProgressArrow(DrawContext drawContext, int x, int y) {
        if(handler.isCraftingActive())
            drawContext.drawTexture(TEXTURE, x + 89, y + 34, 176, 53, handler.getScaledProgressArrowSize(), 17);
    }

    private void renderCheckboxes(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        if(handler.isIgnoreNBT()) {
            //Ignore NBT checkbox

            drawContext.drawTexture(TEXTURE, x + 158, y + 16, 176, 70, 11, 11);
        }

        if(handler.isSecondaryExtractMode()) {
            //Extract mode checkbox [2]

            drawContext.drawTexture(TEXTURE, x + 158, y + 38, 187, 81, 11, 11);
        }else {
            //Extract mode checkbox [1]

            drawContext.drawTexture(TEXTURE, x + 158, y + 38, 176, 81, 11, 11);
        }

        drawContext.drawTexture(TEXTURE, x + 96, y + 16, 176 + 11 * handler.getRecipeIndex(), 81, 11, 11);
    }

    @Override
    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipNormalView(drawContext, mouseX, mouseY);

        if(isPointWithinBounds(158, 16, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.auto_crafter.cbx.ignore_nbt"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(158, 38, 11, 11, mouseX, mouseY)) {
            //Extract mode

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.auto_crafter.cbx.extract_mode." + (handler.isSecondaryExtractMode()?"2":"1")));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(126, 16, 12, 12, mouseX, mouseY)) {
            //Cycle through recipes

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.auto_crafter.cycle_through_recipes"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(96, 16, 12, 12, mouseX, mouseY)) {
            //Set recipe index

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.auto_crafter.recipe_index", handler.getRecipeIndex() + 1));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
