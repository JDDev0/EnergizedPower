package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeCurrentRecipeIndexC2SPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public abstract class SelectableRecipeMachineContainerScreen
        <R extends Recipe<?>, T extends AbstractContainerMenu & IEnergyStorageMenu & IConfigurableMenu &
                ISelectableRecipeMachineMenu<R>>
        extends ConfigurableUpgradableEnergyStorageContainerScreen<T> {
    protected int recipeSelectorPosX = 80;
    protected int recipeSelectorPosY = 17;

    protected int recipeSelectorTexturePosX = 176;
    protected int recipeSelectorTexturePosY = 70;

    public SelectableRecipeMachineContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  ResourceLocation texture,
                                                  ResourceLocation upgradeViewTexture) {
        super(menu, inventory, titleComponent, texture, upgradeViewTexture);
    }

    public SelectableRecipeMachineContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                  String energyIndicatorBarTooltipComponentID,
                                                  ResourceLocation texture,
                                                  ResourceLocation upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture,
                upgradeViewTexture);
    }

    protected abstract ItemStack getRecipeIcon(R currentRecipe);

    protected abstract void renderCurrentRecipeTooltip(PoseStack poseStack, int mouseX, int mouseY, R currentRecipe);

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
    protected void renderBgNormalView(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderCurrentRecipeOutput(poseStack, x, y);

        renderButtons(poseStack, x, y, mouseX, mouseY);
    }

    private void renderCurrentRecipeOutput(PoseStack poseStack, int x, int y) {
        R currentRecipe = menu.getCurrentRecipe();
        if(currentRecipe == null)
            return;

        ItemStack itemStackIcon = getRecipeIcon(currentRecipe);
        if(!itemStackIcon.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.f, 0.f, 100.f);

            itemRenderer.renderAndDecorateItem(itemStackIcon, x + recipeSelectorPosX, y + recipeSelectorPosY,
                    recipeSelectorPosX + recipeSelectorPosY * this.imageWidth);

            poseStack.popPose();

            RenderSystem.setShaderTexture(0, TEXTURE);
        }
    }

    private void renderButtons(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        //Down button
        if(isHovering(recipeSelectorPosX - 13, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            blit(poseStack, x + recipeSelectorPosX - 13, y + recipeSelectorPosY + 2,
                    recipeSelectorTexturePosX, recipeSelectorTexturePosY, 11, 12);
        }

        //Up button
        if(isHovering(recipeSelectorPosX + 18, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            blit(poseStack, x + recipeSelectorPosX + 18, y + recipeSelectorPosY + 2,
                    recipeSelectorTexturePosX + 11, recipeSelectorTexturePosY, 11, 12);
        }
    }

    @Override
    protected void renderTooltipNormalView(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        //Current recipe
        R currentRecipe = menu.getCurrentRecipe();
        if(currentRecipe != null && isHovering(recipeSelectorPosX, recipeSelectorPosY, 16, 16, mouseX, mouseY))
            renderCurrentRecipeTooltip(poseStack, mouseX, mouseY, currentRecipe);

        //Down button
        if(isHovering(recipeSelectorPosX - 13, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.recipe.selector.prev_recipe"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }

        //Up button
        if(isHovering(recipeSelectorPosX + 18, recipeSelectorPosY + 2, 11, 12, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.recipe.selector.next_recipe"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
