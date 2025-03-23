package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class AutoStonecutterScreen
        extends SelectableRecipeMachineContainerScreen<StonecutterRecipe, AutoStonecutterMenu> {
    public AutoStonecutterScreen(AutoStonecutterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/auto_stonecutter.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected ItemStack getRecipeIcon(StonecutterRecipe currentRecipe) {
        return currentRecipe.getResultItem(menu.getBlockEntity().getLevel().registryAccess());
    }

    @Override
    protected void renderCurrentRecipeTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, StonecutterRecipe currentRecipe) {
        ItemStack output = currentRecipe.getResultItem(menu.getBlockEntity().getLevel().registryAccess());
        if(!output.isEmpty()) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                    output.getHoverName()));

            //TODO display cost

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBgNormalView(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCraftingActive())
            guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 84, y + 43, 0, 58, menu.getScaledProgressArrowSize(), 17);
    }

    @Override
    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        //Missing Pickaxe
        if(isHovering(57, 44, 16, 16, mouseX, mouseY) &&
                menu.getSlot(4 * 9 + 1).getItem().isEmpty()) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_stonecutter.pickaxe_missing").
                    withStyle(ChatFormatting.RED));

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
