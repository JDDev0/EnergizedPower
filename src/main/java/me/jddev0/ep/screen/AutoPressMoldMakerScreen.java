package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutoPressMoldMakerScreen
        extends SelectableRecipeMachineContainerScreen<PressMoldMakerRecipe, AutoPressMoldMakerMenu> {
    public AutoPressMoldMakerScreen(AutoPressMoldMakerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/auto_press_mold_maker.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected ItemStack getRecipeIcon(RecipeHolder<PressMoldMakerRecipe> currentRecipe) {
        return ItemStackUtils.fromNullableItemStackTemplate(currentRecipe.value().getOutput());
    }

    @Override
    protected void renderCurrentRecipeTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, RecipeHolder<PressMoldMakerRecipe> currentRecipe) {
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(currentRecipe.value().getOutput());
        if(!output.isEmpty()) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                    output.getHoverName()));
            components.add(Component.translatable("tooltip.energizedpower.press_mold_maker.btn.recipes", currentRecipe.value().getClayCount(),
                    new ItemStack(Items.CLAY_BALL).getItemName()).withStyle(ChatFormatting.ITALIC));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public void extractBackgroundNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(guiGraphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphicsExtractor guiGraphics, int x, int y) {
        if(menu.isCraftingActive())
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 84, y + 43, 0, 58, menu.getScaledProgressArrowSize(), 17, 256, 256);
    }

    @Override
    protected void extractLabelsNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractLabelsNormalView(guiGraphics, mouseX, mouseY);

        //Missing Shovel
        if(isHovering(57, 44, 16, 16, mouseX, mouseY) &&
                menu.getSlot(4 * 9 + 1).getItem().isEmpty()) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_press_mold_maker.shovel_missing").
                    withStyle(ChatFormatting.RED));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
