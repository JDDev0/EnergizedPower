package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeCurrentRecipeIndexC2SPacket;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AutoPressMoldMakerScreen extends ConfigurableUpgradableEnergyStorageContainerScreen<AutoPressMoldMakerMenu> {
    public AutoPressMoldMakerScreen(AutoPressMoldMakerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/auto_press_mold_maker.png"),
                new Identifier(EnergizedPowerMod.MODID,
                        "textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            int diff = 0;

            //Up button
            if(isPointWithinBounds(67, 19, 11, 12, mouseX, mouseY)) {
                diff = 1;
            }

            //Down button
            if(isPointWithinBounds(98, 19, 11, 12, mouseX, mouseY)) {
                diff = -1;
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

        renderProgressArrow(drawContext, x, y);
    }

    private void renderCurrentRecipeOutput(DrawContext drawContext, int x, int y) {
        RecipeEntry<PressMoldMakerRecipe> currentRecipe = handler.getCurrentRecipe();
        if(currentRecipe == null)
            return;

        ItemStack output = currentRecipe.value().getOutput();
        if(!output.isEmpty()) {
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0.f, 0.f, 100.f);

            drawContext.drawItem(output, x + 80, y + 17, 80 + 17 * this.backgroundWidth);

            drawContext.getMatrices().pop();
        }
    }

    private void renderButtons(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        //Up button
        if(isPointWithinBounds(67, 19, 11, 12, mouseX, mouseY)) {
            drawContext.drawTexture(TEXTURE, x + 67, y + 19, 176, 70, 11, 12);
        }

        //Down button
        if(isPointWithinBounds(98, 19, 11, 12, mouseX, mouseY)) {
            drawContext.drawTexture(TEXTURE, x + 98, y + 19, 187, 70, 11, 12);
        }
    }

    private void renderProgressArrow(DrawContext drawContext, int x, int y) {
        if(handler.isCraftingActive())
            drawContext.drawTexture(TEXTURE, x + 84, y + 43, 176, 53, handler.getScaledProgressArrowSize(), 17);
    }

    @Override
    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipNormalView(drawContext, mouseX, mouseY);

        //Current recipe
        RecipeEntry<PressMoldMakerRecipe> currentRecipe = handler.getCurrentRecipe();
        if(currentRecipe != null && isPointWithinBounds(80, 17, 16, 16, mouseX, mouseY)) {
            ItemStack output = currentRecipe.value().getOutput();
            if(!output.isEmpty()) {
                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                        output.getName()));
                components.add(Text.translatable("tooltip.energizedpower.press_mold_maker.btn.recipes", currentRecipe.value().getClayCount(),
                        Text.translatable(Items.CLAY_BALL.getTranslationKey())).formatted(Formatting.ITALIC));

                drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
            }
        }

        //Up button
        if(isPointWithinBounds(67, 19, 11, 12, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.recipe.selector.next_recipe"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }

        //Down button
        if(isPointWithinBounds(98, 19, 11, 12, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.recipe.selector.prev_recipe"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }

        //Missing Shovel
        if(isPointWithinBounds(57, 44, 16, 16, mouseX, mouseY) &&
                handler.getSlot(4 * 9 + 1).getStack().isEmpty()) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.auto_press_mold_maker.shovel_missing").
                    formatted(Formatting.RED));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
