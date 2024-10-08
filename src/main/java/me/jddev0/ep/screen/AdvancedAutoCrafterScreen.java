package me.jddev0.ep.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.CycleAdvancedAutoCrafterRecipeOutputC2SPacket;
import me.jddev0.ep.networking.packet.SetAdvancedAutoCrafterRecipeIndexC2SPacket;
import me.jddev0.ep.networking.packet.*;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class AdvancedAutoCrafterScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedAutoCrafterMenu> {

    public AdvancedAutoCrafterScreen(AdvancedAutoCrafterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/advanced_auto_crafter.png"),
                EPAPI.id("textures/gui/container/upgrade_view/advanced_auto_crafter.png"));

        imageHeight = 224;
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

                ModMessages.sendToServer(new CycleAdvancedAutoCrafterRecipeOutputC2SPacket(menu.getBlockEntity().getBlockPos()));
                return true;
            }else if(isHovering(96, 16, 12, 12, mouseX, mouseY)) {
                //Set recipe index

                ModMessages.sendToServer(new SetAdvancedAutoCrafterRecipeIndexC2SPacket(menu.getBlockEntity().getBlockPos(), menu.getRecipeIndex() + 1));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderBgNormalView(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(poseStack, x, y);
        renderCheckboxes(poseStack, x, y, mouseX, mouseY);
    }

    private void renderProgressArrow(PoseStack poseStack, int x, int y) {
        if(menu.isCraftingActive())
            blit(poseStack, x + 89, y + 34, 176, 53, menu.getScaledProgressArrowSize(), 17);
    }

    private void renderCheckboxes(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(menu.isIgnoreNBT()) {
            //Ignore NBT checkbox

            blit(poseStack, x + 158, y + 16, 176, 70, 11, 11);
        }

        if(menu.isSecondaryExtractMode()) {
            //Extract mode checkbox [2]

            blit(poseStack, x + 158, y + 38, 187, 81, 11, 11);
        }else {
            //Extract mode checkbox [1]

            blit(poseStack, x + 158, y + 38, 176, 81, 11, 11);
        }

        blit(poseStack, x + 96, y + 16, 176 + 11 * menu.getRecipeIndex(), 81, 11, 11);
    }

    @Override
    protected void renderTooltipNormalView(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        if(isHovering(158, 16, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_crafter.cbx.ignore_nbt"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(158, 38, 11, 11, mouseX, mouseY)) {
            //Extract mode

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_crafter.cbx.extract_mode." + (menu.isSecondaryExtractMode()?"2":"1")));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(126, 16, 12, 12, mouseX, mouseY)) {
            //Cycle through recipes

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_crafter.cycle_through_recipes"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(96, 16, 12, 12, mouseX, mouseY)) {
            //Set recipe index

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_crafter.recipe_index", menu.getRecipeIndex() + 1));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
