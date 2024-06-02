package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AutoStonecutterScreen
        extends SelectableRecipeMachineContainerScreen<StonecuttingRecipe, AutoStonecutterMenu> {
    public AutoStonecutterScreen(AutoStonecutterMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/auto_stonecutter.png"),
                new Identifier(EnergizedPowerMod.MODID,
                        "textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected ItemStack getRecipeIcon(StonecuttingRecipe currentRecipe) {
        return currentRecipe.getOutput();
    }

    @Override
    protected void renderCurrentRecipeTooltip(MatrixStack poseStack, int mouseX, int mouseY, StonecuttingRecipe currentRecipe) {
        ItemStack output = currentRecipe.getOutput();
        if(!output.isEmpty()) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                    output.getName()));

            //TODO display cost

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBgNormalView(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderProgressArrow(poseStack, x, y);
    }

    private void renderProgressArrow(MatrixStack poseStack, int x, int y) {
        if(handler.isCraftingActive())
            drawTexture(poseStack, x + 84, y + 43, 176, 53, handler.getScaledProgressArrowSize(), 17);
    }

    @Override
    protected void renderTooltipNormalView(MatrixStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        //Missing Pickaxe
        if(isPointWithinBounds(57, 44, 16, 16, mouseX, mouseY) &&
                handler.getSlot(4 * 9 + 1).getStack().isEmpty()) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.auto_stonecutter.pickaxe_missing").
                    formatted(Formatting.RED));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
