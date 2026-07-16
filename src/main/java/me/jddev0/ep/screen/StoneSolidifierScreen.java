package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
import me.jddev0.ep.util.ItemStackUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class StoneSolidifierScreen extends SelectableRecipeMachineContainerScreen<StoneSolidifierRecipe, StoneSolidifierMenu> {
    public StoneSolidifierScreen(StoneSolidifierMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/stone_solidifier.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity_1_item_ejector.png"));

        recipeSelectorPosX = 98;
    }

    @Override
    protected ItemStack getRecipeIcon(RecipeHolder<StoneSolidifierRecipe> currentRecipe) {
        return ItemStackUtils.fromNullableItemStackTemplate(currentRecipe.value().getOutput());
    }

    @Override
    protected void renderCurrentRecipeTooltip(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, RecipeHolder<StoneSolidifierRecipe> currentRecipe) {
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(currentRecipe.value().getOutput());
        if(!output.isEmpty()) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                    output.getHoverName()));

            drawContext.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void extractBackgroundNormalView(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(drawContext, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        for (int i = 0; i < 2; i++) {
            renderFluidMeterContent(drawContext, menu.getFluid(i), menu.getTankCapacity(i), x + (i == 0?44:152), y + 17, 16, 52);
            renderFluidMeterOverlay(i, drawContext, x, y);
        }

        renderProgressArrows(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(int tank, GuiGraphicsExtractor drawContext, int x, int y) {
        drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + (tank == 0?44:152), y + 17, 16, 0, 16, 52, 256, 256);
    }

    private void renderProgressArrows(GuiGraphicsExtractor drawContext, int x, int y) {
        if(menu.isCraftingActive()) {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 69, y + 45, 72, 58, menu.getScaledProgressArrowSize(), 14, 256, 256);
            drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 143 - menu.getScaledProgressArrowSize(), y + 45,
                    92 - menu.getScaledProgressArrowSize(), 72, menu.getScaledProgressArrowSize(), 14, 256, 256);
        }
    }


    @Override
    protected void extractTooltipNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltipNormalView(guiGraphics, mouseX, mouseY);

        for(int i = 0;i < 2;i++) {
            if(isHovering(i == 0?44:152, 17, 16, 52, mouseX, mouseY)) {
                renderFluidMeterContentTooltip(guiGraphics, menu.getFluid(i), menu.getTankCapacity(i), mouseX, mouseY);
            }
        }
    }

    @Override
    protected Rect getTankCords(int tank) {
        if(tank == 0)
            return new Rect(44, 17, 16, 52);
        else if(tank == 1)
            return new Rect(152, 17, 16, 52);

        return super.getTankCords(tank);
    }
}
