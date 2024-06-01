package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeCurrentRecipeIndexC2SPacket;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class AutoPressMoldMakerScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<AutoPressMoldMakerMenu> {
    public AutoPressMoldMakerScreen(AutoPressMoldMakerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/auto_press_mold_maker.png"),
                8, 17,
                new ResourceLocation(EnergizedPowerMod.MODID,
                        "textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            int diff = 0;

            //Up button
            if(isHovering(67, 19, 11, 12, mouseX, mouseY)) {
                diff = 1;
            }

            //Down button
            if(isHovering(98, 19, 11, 12, mouseX, mouseY)) {
                diff = -1;
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

        renderProgressArrow(poseStack, x, y);
    }

    private void renderCurrentRecipeOutput(PoseStack poseStack, int x, int y) {
        PressMoldMakerRecipe currentRecipe = menu.getCurrentRecipe();
        if(currentRecipe == null)
            return;

        ItemStack itemStackIcon = currentRecipe.getOutput();
        if(!itemStackIcon.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.f, 0.f, 100.f);

            itemRenderer.renderAndDecorateItem(itemStackIcon, x + 80, y + 17, 80 + 17 * this.imageWidth);

            poseStack.popPose();

            RenderSystem.setShaderTexture(0, TEXTURE);
        }
    }

    private void renderButtons(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        //Up button
        if(isHovering(67, 19, 11, 12, mouseX, mouseY)) {
            blit(poseStack, x + 67, y + 19, 176, 70, 11, 12);
        }

        //Down button
        if(isHovering(98, 19, 11, 12, mouseX, mouseY)) {
            blit(poseStack, x + 98, y + 19, 187, 70, 11, 12);
        }
    }

    private void renderProgressArrow(PoseStack poseStack, int x, int y) {
        if(menu.isCraftingActive())
            blit(poseStack, x + 84, y + 43, 176, 53, menu.getScaledProgressArrowSize(), 17);
    }

    @Override
    protected void renderTooltipNormalView(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        //Current recipe
        PressMoldMakerRecipe currentRecipe = menu.getCurrentRecipe();
        if(currentRecipe != null && isHovering(80, 17, 16, 16, mouseX, mouseY)) {
            ItemStack output = currentRecipe.getOutput();
            if(!output.isEmpty()) {
                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                        output.getHoverName()));
                components.add(Component.translatable("tooltip.energizedpower.press_mold_maker.btn.recipes", currentRecipe.getClayCount(),
                        Component.translatable(Items.CLAY_BALL.getDescriptionId())).withStyle(ChatFormatting.ITALIC));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }
        }

        //Up button
        if(isHovering(67, 19, 11, 12, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.recipe.selector.next_recipe"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }

        //Down button
        if(isHovering(98, 19, 11, 12, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.recipe.selector.prev_recipe"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }

        //Missing Shovel
        if(isHovering(57, 44, 16, 16, mouseX, mouseY) &&
                menu.getSlot(4 * 9 + 1).getItem().isEmpty()) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_press_mold_maker.shovel_missing").
                    withStyle(ChatFormatting.RED));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
