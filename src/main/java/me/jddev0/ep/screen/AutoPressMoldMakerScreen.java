package me.jddev0.ep.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
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
        extends SelectableRecipeMachineContainerScreen<PressMoldMakerRecipe, AutoPressMoldMakerMenu> {
    public AutoPressMoldMakerScreen(AutoPressMoldMakerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/auto_press_mold_maker.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected ItemStack getRecipeIcon(PressMoldMakerRecipe currentRecipe) {
        return currentRecipe.getOutput();
    }

    @Override
    protected void renderCurrentRecipeTooltip(PoseStack poseStack, int mouseX, int mouseY, PressMoldMakerRecipe currentRecipe) {
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

    @Override
    protected void renderBgNormalView(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(poseStack, x, y);
    }

    private void renderProgressArrow(PoseStack poseStack, int x, int y) {
        if(menu.isCraftingActive())
            blit(poseStack, x + 84, y + 43, 176, 53, menu.getScaledProgressArrowSize(), 17);
    }

    @Override
    protected void renderTooltipNormalView(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

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
