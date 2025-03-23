package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FiltrationPlantScreen extends SelectableRecipeMachineContainerScreen<FiltrationPlantRecipe, FiltrationPlantMenu> {
    public FiltrationPlantScreen(FiltrationPlantMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/filtration_plant.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));

        recipeSelectorPosX = 98;
    }

    @Override
    protected ItemStack getRecipeIcon(FiltrationPlantRecipe currentRecipe) {
        Identifier icon = currentRecipe.getIcon();
        return new ItemStack(Registries.ITEM.get(icon));
    }

    @Override
    protected void renderCurrentRecipeTooltip(DrawContext guiGraphics, int mouseX, int mouseY, FiltrationPlantRecipe currentRecipe) {
        List<Text> components = new ArrayList<>(2);

        ItemStack[] maxOutputs = currentRecipe.getMaxOutputCounts();
        for(int i = 0;i < maxOutputs.length;i++) {
            ItemStack output = maxOutputs[i];
            if(output.isEmpty())
                continue;

            components.add(Text.empty().
                    append(output.getName()).
                    append(Text.literal(": ")).
                    append(Text.translatable("recipes.energizedpower.transfer.output_percentages"))
            );

            double[] percentages = (i == 0?currentRecipe.getOutput():currentRecipe.getSecondaryOutput()).
                    percentages();
            for(int j = 0;j < percentages.length;j++)
                components.add(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

            components.add(Text.empty());
        }

        //Remove trailing empty line
        if(!components.isEmpty())
            components.remove(components.size() - 1);

        guiGraphics.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
    }

    @Override
    protected void renderBgNormalView(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        for (int i = 0; i < 2; i++) {
            renderFluidMeterContent(drawContext, handler.getFluid(i), handler.getTankCapacity(i), x + (i == 0?44:152), y + 17, 16, 52);
            renderFluidMeterOverlay(i, drawContext, x, y);
        }

        renderProgressArrows(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(int tank, DrawContext drawContext, int x, int y) {
        drawContext.drawTexture(MACHINE_SPRITES_TEXTURE, x + (tank == 0?44:152), y + 17, 16, 0, 16, 52);
    }

    private void renderProgressArrows(DrawContext drawContext, int x, int y) {
        if(handler.isCraftingActive()) {
            for(int i = 0;i < 2;i++) {
                drawContext.drawTexture(MACHINE_SPRITES_TEXTURE, x + 67, y + 34 + 27*i, 0, 108, handler.getScaledProgressArrowSize(), 9);
            }
        }
    }

    @Override
    protected void renderTooltipNormalView(DrawContext drawContext, int mouseX, int mouseY) {
        super.renderTooltipNormalView(drawContext, mouseX, mouseY);

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

                drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
            }
        }

        //Missing Charcoal Filter
        for(int i = 0;i < 2;i++) {
            if(isPointWithinBounds(62 + 72*i, 44, 16, 16, mouseX, mouseY) &&
                    handler.getSlot(4 * 9 + i).getStack().isEmpty()) {
                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.filtration_plant.charcoal_filter_missing").
                        formatted(Formatting.RED));

                drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
