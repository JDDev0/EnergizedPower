package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeComparatorModeC2SPacket;
import me.jddev0.ep.networking.packet.ChangeCurrentRecipeIndexC2SPacket;
import me.jddev0.ep.networking.packet.ChangeRedstoneModeC2SPacket;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AutoPressMoldMakerScreen extends AbstractGenericEnergyStorageHandledScreen<AutoPressMoldMakerMenu> {
    private final Identifier CONFIGURATION_ICONS_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID, "textures/gui/machine_configuration/configuration_buttons.png");
    private final Identifier UPGRADE_VIEW_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID,
                    "textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png");

    public AutoPressMoldMakerScreen(AutoPressMoldMakerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/auto_press_mold_maker.png"),
                8, 17);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;

            if(!handler.isInUpgradeModuleView()) {
                int diff = 0;

                //Up button
                if(isPointWithinBounds(67, 19, 11, 12, mouseX, mouseY)) {
                    diff = 1;
                    clicked = true;
                }

                //Down button
                if(isPointWithinBounds(98, 19, 11, 12, mouseX, mouseY)) {
                    diff = -1;
                    clicked = true;
                }

                if(diff != 0) {
                    ModMessages.sendClientPacketToServer(new ChangeCurrentRecipeIndexC2SPacket(handler.getBlockEntity().getPos(),
                            diff == 1));
                }
            }

            if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
                //Upgrade view

                client.interactionManager.clickButton(handler.syncId, 0);
                clicked = true;
            }else if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
                //Redstone Mode

                ModMessages.sendClientPacketToServer(new ChangeRedstoneModeC2SPacket(handler.getBlockEntity().getPos()));
                clicked = true;
            }else if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
                //Comparator Mode
                ModMessages.sendClientPacketToServer(new ChangeComparatorModeC2SPacket(handler.getBlockEntity().getPos()));
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        if(handler.isInUpgradeModuleView()) {
            drawContext.drawTexture(UPGRADE_VIEW_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        }else {
            renderCurrentRecipeOutput(drawContext, x, y);

            renderButtons(drawContext, x, y, mouseX, mouseY);

            renderProgressArrow(drawContext, x, y);
        }

        renderConfiguration(drawContext, x, y, mouseX, mouseY);
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

    private void renderConfiguration(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        //Upgrade view
        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 40, 80, 20, 20);
        }else if(handler.isInUpgradeModuleView()) {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20, 80, 20, 20);
        }else {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 0, 80, 20, 20);
        }

        RedstoneMode redstoneMode = handler.getRedstoneMode();
        int ordinal = redstoneMode.ordinal();

        if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 26, 20 * ordinal, 20, 20, 20);
        }else {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 26, 20 * ordinal, 0, 20, 20);
        }

        ComparatorMode comparatorMode = handler.getComparatorMode();
        ordinal = comparatorMode.ordinal();

        if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 50, 20 * ordinal, 60, 20, 20);
        }else {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 50, 20 * ordinal, 40, 20, 20);
        }
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

        if(!handler.isInUpgradeModuleView()) {
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

        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            //Upgrade view

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.upgrade_view.button." +
                    (handler.isInUpgradeModuleView()?"close":"open")));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
            //Redstone Mode

            RedstoneMode redstoneMode = handler.getRedstoneMode();

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.machine_configuration.redstone_mode." + redstoneMode.asString()));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
            //Comparator Mode

            ComparatorMode comparatorMode = handler.getComparatorMode();

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.machine_configuration.comparator_mode." + comparatorMode.asString()));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
