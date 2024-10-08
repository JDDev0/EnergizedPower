package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class StoneSolidifierScreen
        extends SelectableRecipeMachineContainerScreen<StoneSolidifierRecipe, StoneSolidifierMenu> {
    public StoneSolidifierScreen(StoneSolidifierMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/stone_solidifier.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));

        recipeSelectorPosX = 98;

        recipeSelectorTexturePosY = 135;
    }

    @Override
    protected ItemStack getRecipeIcon(StoneSolidifierRecipe currentRecipe) {
        return currentRecipe.getOutput();
    }

    @Override
    protected void renderCurrentRecipeTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, StoneSolidifierRecipe currentRecipe) {
        ItemStack output = currentRecipe.getOutput();
        if(!output.isEmpty()) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                    output.getHoverName()));

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBgNormalView(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        for (int i = 0; i < 2; i++) {
            renderFluidMeterContent(guiGraphics, menu.getFluid(i), menu.getTankCapacity(i), x + (i == 0?44:152), y + 17, 16, 52);
            renderFluidMeterOverlay(i, guiGraphics, x, y);
        }

        renderProgressArrows(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(int tank, GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(TEXTURE, x + (tank == 0?44:152), y + 17, 176, 53, 16, 52);
    }

    private void renderProgressArrows(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCraftingActive()) {
            guiGraphics.blit(TEXTURE, x + 69, y + 45, 176, 106, menu.getScaledProgressArrowSize(), 14);
            guiGraphics.blit(TEXTURE, x + 143 - menu.getScaledProgressArrowSize(), y + 45,
                    196 - menu.getScaledProgressArrowSize(), 120, menu.getScaledProgressArrowSize(), 14);
        }
    }

    @Override
    protected void renderTooltipNormalView(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltipNormalView(guiGraphics, mouseX, mouseY);

        for(int i = 0;i < 2;i++) {
            //Fluid meter

            if(isHovering(i == 0?44:152, 17, 16, 52, mouseX, mouseY)) {
                List<Component> components = new ArrayList<>(2);

                boolean fluidEmpty =  menu.getFluid(i).isEmpty();

                int fluidAmount = fluidEmpty?0:menu.getFluid(i).getAmount();

                Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                        FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(menu.getTankCapacity(i)));

                if(!fluidEmpty) {
                    tooltipComponent = Component.translatable(menu.getFluid(i).getTranslationKey()).append(" ").
                            append(tooltipComponent);
                }

                components.add(tooltipComponent);

                guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
