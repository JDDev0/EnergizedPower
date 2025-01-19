package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AutoStonecutterScreen
        extends SelectableRecipeMachineContainerScreen<StonecuttingRecipe, AutoStonecutterMenu> {
    public AutoStonecutterScreen(AutoStonecutterMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/auto_stonecutter.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected ItemStack getRecipeIcon(RecipeEntry<StonecuttingRecipe> currentRecipe) {
        return currentRecipe.value().getDisplays().get(0).result().
                getFirst(SlotDisplayContexts.createParameters(handler.getBlockEntity().getWorld()));
    }

    @Override
    protected void renderCurrentRecipeTooltip(DrawContext drawContext, int mouseX, int mouseY, RecipeEntry<StonecuttingRecipe> currentRecipe) {
        ItemStack output = currentRecipe.value().getDisplays().get(0).result().
                getFirst(SlotDisplayContexts.createParameters(handler.getBlockEntity().getWorld()));
        if(!output.isEmpty()) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                    output.getName()));

            //TODO display cost

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderProgressArrow(drawContext, x, y);
    }

    private void renderProgressArrow(DrawContext drawContext, int x, int y) {
        if(handler.isCraftingActive())
            drawContext.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x + 84, y + 43, 176, 53, handler.getScaledProgressArrowSize(), 17, 256, 256);
    }

    @Override
    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipNormalView(drawContext, mouseX, mouseY);

        //Missing Pickaxe
        if(isPointWithinBounds(57, 44, 16, 16, mouseX, mouseY) &&
                handler.getSlot(4 * 9 + 1).getStack().isEmpty()) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.auto_stonecutter.pickaxe_missing").
                    formatted(Formatting.RED));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
