package me.jddev0.ep.screen.base;

import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeCurrentRecipeIndexC2SPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SelectableRecipeMachineContainerScreen
        <R extends Recipe<?>, T extends AbstractContainerMenu & IEnergyStorageMenu & IConfigurableMenu &
                ISelectableRecipeMachineMenu<R>>
        extends ConfigurableUpgradableEnergyStorageContainerScreen<T> {
    protected int recipeSelectorPosX = 80;
    protected int recipeSelectorPosY = 17;

    protected int recipeSelectorTexturePosX = 0;
    protected int recipeSelectorTexturePosY = 187;

    public SelectableRecipeMachineContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  Identifier texture,
                                                  Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, texture, upgradeViewTexture);
    }

    public SelectableRecipeMachineContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  String energyIndicatorBarTooltipComponentID,
                                                  Identifier texture,
                                                  Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture,
                upgradeViewTexture);
    }

    protected abstract ItemStack getRecipeIcon(RecipeHolder<R> currentRecipe);

    protected abstract void renderCurrentRecipeTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, RecipeHolder<R> currentRecipe);

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            int diff = 0;

            //Down button
            if(isHovering(recipeSelectorPosX - 13, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
                diff = -1;
            }

            //Up button
            if(isHovering(recipeSelectorPosX + 18, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
                diff = 1;
            }

            if(diff != 0) {
                ModMessages.sendToServer(new ChangeCurrentRecipeIndexC2SPacket(menu.getBlockEntity().getBlockPos(),
                        diff == 1));

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

        renderCurrentRecipeOutput(guiGraphics, x, y);

        renderButtons(guiGraphics, x, y, mouseX, mouseY);
    }

    private void renderCurrentRecipeOutput(GuiGraphics guiGraphics, int x, int y) {
        RecipeHolder<R> currentRecipe = menu.getCurrentRecipe();
        if(currentRecipe == null)
            return;

        ItemStack itemStackIcon = getRecipeIcon(currentRecipe);
        if(!itemStackIcon.isEmpty()) {
            guiGraphics.pose().pushMatrix();

            guiGraphics.renderItem(itemStackIcon, x + recipeSelectorPosX, y + recipeSelectorPosY,
                    recipeSelectorPosX + recipeSelectorPosY * this.imageWidth);

            guiGraphics.pose().popMatrix();
        }
    }

    private void renderButtons(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        //Down button
        if(isHovering(recipeSelectorPosX - 13, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + recipeSelectorPosX - 13, y + recipeSelectorPosY + 2,
                    recipeSelectorTexturePosX, recipeSelectorTexturePosY, 11, 12, 256, 256);
        }

        //Up button
        if(isHovering(recipeSelectorPosX + 18, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + recipeSelectorPosX + 18, y + recipeSelectorPosY + 2,
                    recipeSelectorTexturePosX + 11, recipeSelectorTexturePosY, 11, 12, 256, 256);
        }
    }

    @Override
    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        //Current recipe
        RecipeHolder<R> currentRecipe = menu.getCurrentRecipe();
        if(currentRecipe != null && isHovering(recipeSelectorPosX, recipeSelectorPosY, 16, 16, mouseX, mouseY))
            renderCurrentRecipeTooltip(guiGraphics, mouseX, mouseY, currentRecipe);

        //Down button
        if(isHovering(recipeSelectorPosX - 13, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.recipe.selector.prev_recipe"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }

        //Up button
        if(isHovering(recipeSelectorPosX + 18, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.recipe.selector.next_recipe"));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
