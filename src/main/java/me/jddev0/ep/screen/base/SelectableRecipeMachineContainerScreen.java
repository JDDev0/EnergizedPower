package me.jddev0.ep.screen.base;

import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeCurrentRecipeIndexC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class SelectableRecipeMachineContainerScreen
        <R extends Recipe<?>, T extends ScreenHandler & EnergyStorageMenu & ConfigurableMenu &
                SelectableRecipeMachineMenu<R>>
        extends ConfigurableUpgradableEnergyStorageContainerScreen<T> {
    protected int recipeSelectorPosX = 80;
    protected int recipeSelectorPosY = 17;

    protected int recipeSelectorTexturePosX = 176;
    protected int recipeSelectorTexturePosY = 70;

    public SelectableRecipeMachineContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                  Identifier texture,
                                                  Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, texture, upgradeViewTexture);
    }

    public SelectableRecipeMachineContainerScreen(T menu, PlayerInventory inventory, Text titleComponent,
                                                  String energyIndicatorBarTooltipComponentID,
                                                  Identifier texture,
                                                  Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture,
                upgradeViewTexture);
    }

    protected abstract ItemStack getRecipeIcon(RecipeEntry<R> currentRecipe);

    protected abstract void renderCurrentRecipeTooltip(DrawContext drawContext, int mouseX, int mouseY, RecipeEntry<R> currentRecipe);

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            int diff = 0;

            //Down button
            if(isPointWithinBounds(recipeSelectorPosX - 13, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
                diff = -1;
            }

            //Up button
            if(isPointWithinBounds(recipeSelectorPosX + 18, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
                diff = 1;
            }

            if(diff != 0) {
                ModMessages.sendClientPacketToServer(new ChangeCurrentRecipeIndexC2SPacket(handler.getBlockEntity().getPos(),
                        diff == 1));

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

        renderCurrentRecipeOutput(drawContext, x, y);

        renderButtons(drawContext, x, y, mouseX, mouseY);
    }

    private void renderCurrentRecipeOutput(DrawContext drawContext, int x, int y) {
        RecipeEntry<R> currentRecipe = handler.getCurrentRecipe();
        if(currentRecipe == null)
            return;

        ItemStack itemStackIcon = getRecipeIcon(currentRecipe);
        if(!itemStackIcon.isEmpty()) {
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0.f, 0.f, 100.f);

            drawContext.drawItem(itemStackIcon, x + recipeSelectorPosX, y + recipeSelectorPosY,
                    recipeSelectorPosX + recipeSelectorPosY * this.backgroundWidth);

            drawContext.getMatrices().pop();
        }
    }

    private void renderButtons(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        //Down button
        if(isPointWithinBounds(recipeSelectorPosX - 13, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            drawContext.drawTexture(TEXTURE, x + recipeSelectorPosX - 13, y + recipeSelectorPosY + 2,
                    recipeSelectorTexturePosX, recipeSelectorTexturePosY, 11, 12);
        }

        //Up button
        if(isPointWithinBounds(recipeSelectorPosX + 18, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            drawContext.drawTexture(TEXTURE, x + recipeSelectorPosX + 18, y + recipeSelectorPosY + 2,
                    recipeSelectorTexturePosX + 11, recipeSelectorTexturePosY, 11, 12);
        }
    }

    @Override
    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipNormalView(drawContext, mouseX, mouseY);

        //Current recipe
        RecipeEntry<R> currentRecipe = handler.getCurrentRecipe();
        if(currentRecipe != null && isPointWithinBounds(recipeSelectorPosX, recipeSelectorPosY, 16, 16, mouseX, mouseY))
            renderCurrentRecipeTooltip(drawContext, mouseX, mouseY, currentRecipe);

        //Down button
        if(isPointWithinBounds(recipeSelectorPosX - 13, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.recipe.selector.prev_recipe"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }

        //Up button
        if(isPointWithinBounds(recipeSelectorPosX + 18, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.recipe.selector.next_recipe"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
