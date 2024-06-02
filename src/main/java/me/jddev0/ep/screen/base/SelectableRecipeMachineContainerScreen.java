package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeCurrentRecipeIndexC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
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

    protected abstract ItemStack getRecipeIcon(R currentRecipe);

    protected abstract void renderCurrentRecipeTooltip(MatrixStack poseStack, int mouseX, int mouseY, R currentRecipe);

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
    protected void renderBgNormalView(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderCurrentRecipeOutput(poseStack, x, y);

        renderButtons(poseStack, x, y, mouseX, mouseY);
    }

    private void renderCurrentRecipeOutput(MatrixStack poseStack, int x, int y) {
        R currentRecipe = handler.getCurrentRecipe();
        if(currentRecipe == null)
            return;

        ItemStack itemStackIcon = getRecipeIcon(currentRecipe);
        if(!itemStackIcon.isEmpty()) {
            poseStack.push();
            poseStack.translate(0.f, 0.f, 100.f);

            itemRenderer.renderInGuiWithOverrides(itemStackIcon, x + recipeSelectorPosX, y + recipeSelectorPosY,
                    recipeSelectorPosX + recipeSelectorPosY * this.backgroundWidth);

            poseStack.pop();

            RenderSystem.setShaderTexture(0, TEXTURE);
        }
    }

    private void renderButtons(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        //Down button
        if(isPointWithinBounds(recipeSelectorPosX - 13, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            drawTexture(poseStack, x + recipeSelectorPosX - 13, y + recipeSelectorPosY + 2,
                    recipeSelectorTexturePosX, recipeSelectorTexturePosY, 11, 12);
        }

        //Up button
        if(isPointWithinBounds(recipeSelectorPosX + 18, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            drawTexture(poseStack, x + recipeSelectorPosX + 18, y + recipeSelectorPosY + 2,
                    recipeSelectorTexturePosX + 11, recipeSelectorTexturePosY, 11, 12);
        }
    }

    @Override
    protected void renderTooltipNormalView(MatrixStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        //Current recipe
        R currentRecipe = handler.getCurrentRecipe();
        if(currentRecipe != null && isPointWithinBounds(recipeSelectorPosX, recipeSelectorPosY, 16, 16, mouseX, mouseY))
            renderCurrentRecipeTooltip(poseStack, mouseX, mouseY, currentRecipe);

        //Down button
        if(isPointWithinBounds(recipeSelectorPosX - 13, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.recipe.selector.prev_recipe"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }

        //Up button
        if(isPointWithinBounds(recipeSelectorPosX + 18, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.recipe.selector.next_recipe"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
