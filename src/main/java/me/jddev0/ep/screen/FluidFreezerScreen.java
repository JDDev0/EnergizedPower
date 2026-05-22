package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FluidFreezerScreen
        extends SelectableRecipeMachineContainerScreen<FluidFreezerRecipe, FluidFreezerMenu> {
    public FluidFreezerScreen(FluidFreezerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/fluid_freezer.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity_1_item_ejector.png"));

        recipeSelectorPosX = 89;
    }

    @Override
    protected ItemStack getRecipeIcon(RecipeHolder<FluidFreezerRecipe> currentRecipe) {
        return ItemStackUtils.fromNullableItemStackTemplate(currentRecipe.value().getOutput());
    }

    @Override
    protected void renderCurrentRecipeTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, RecipeHolder<FluidFreezerRecipe> currentRecipe) {
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(currentRecipe.value().getOutput());
        if(!output.isEmpty()) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                    output.getHoverName()));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public void extractBackgroundNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackgroundNormalView(guiGraphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidMeterContent(guiGraphics, menu.getFluid(), menu.getTankCapacity(), x + 44, y + 17, 16, 52);
        renderFluidMeterOverlay(guiGraphics, x, y);

        renderProgressArrows(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 44, y + 17, 16, 0, 16, 52, 256, 256);
    }

    private void renderProgressArrows(GuiGraphicsExtractor guiGraphics, int x, int y) {
        if(menu.isCraftingActive()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 83, y + 43, 0, 58, menu.getScaledProgressArrowSize(), 17, 256, 256);
        }
    }

    @Override
    protected void extractLabelsNormalView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractLabelsNormalView(guiGraphics, mouseX, mouseY);

        if(isHovering(44, 17, 16, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Component> components = new ArrayList<>(2);

            boolean fluidEmpty =  menu.getFluid().isEmpty();

            int fluidAmount = fluidEmpty?0:menu.getFluid().getAmount();

            Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                    FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(menu.getTankCapacity()));

            if(!fluidEmpty) {
                tooltipComponent = Component.translatable(menu.getFluid().getDescriptionId()).append(" ").
                        append(tooltipComponent);
            }

            components.add(tooltipComponent);

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
