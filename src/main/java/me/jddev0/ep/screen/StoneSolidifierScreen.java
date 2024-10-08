package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class StoneSolidifierScreen extends SelectableRecipeMachineContainerScreen<StoneSolidifierRecipe, StoneSolidifierMenu> {
    public StoneSolidifierScreen(StoneSolidifierMenu menu, PlayerInventory inventory, Text component) {
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
    protected void renderCurrentRecipeTooltip(MatrixStack poseStack, int mouseX, int mouseY, StoneSolidifierRecipe currentRecipe) {
        ItemStack output = currentRecipe.getOutput();
        if(!output.isEmpty()) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                    output.getName()));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBgNormalView(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        for (int i = 0; i < 2; i++) {
            renderFluidMeterContent(poseStack, handler.getFluid(i), handler.getTankCapacity(i), x + (i == 0?44:152), y + 17, 16, 52);
            renderFluidMeterOverlay(i, poseStack, x, y);
        }

        renderProgressArrows(poseStack, x, y);
    }

    private void renderFluidMeterOverlay(int tank, MatrixStack poseStack, int x, int y) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        drawTexture(poseStack, x + (tank == 0?44:152), y + 17, 176, 53, 16, 52);
    }

    private void renderProgressArrows(MatrixStack poseStack, int x, int y) {
        if(handler.isCraftingActive()) {
            drawTexture(poseStack, x + 69, y + 45, 176, 106, handler.getScaledProgressArrowSize(), 14);
            drawTexture(poseStack, x + 143 - handler.getScaledProgressArrowSize(), y + 45,
                    196 - handler.getScaledProgressArrowSize(), 120, handler.getScaledProgressArrowSize(), 14);
        }
    }

    @Override
    protected void renderTooltipNormalView(MatrixStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        for(int i = 0;i < 2;i++) {
            //Fluid meter

            if(isPointWithinBounds(i == 0?44:152, 17, 16, 52, mouseX, mouseY)) {
                List<Text> components = new ArrayList<>(2);

                boolean fluidEmpty =  handler.getFluid(i).isEmpty();

                long fluidAmount = fluidEmpty?0:handler.getFluid(i).getMilliBucketsAmount();

                Text tooltipComponent = Text.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                        FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(FluidUtils.
                                convertDropletsToMilliBuckets(handler.getTankCapacity(i))));

                if(!fluidEmpty) {
                    tooltipComponent = Text.translatable(handler.getFluid(i).getTranslationKey()).append(" ").
                            append(tooltipComponent);
                }

                components.add(tooltipComponent);

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
