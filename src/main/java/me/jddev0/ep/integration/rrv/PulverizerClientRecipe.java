package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.recipe.PulverizerRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PulverizerClientRecipe implements EPExtendedReliableClientRecipe {
    private final Identifier id;
    private final SlotContent input, output, secondaryOutput;
    private final PulverizerRecipe.OutputItemStackWithPercentages outputWithPercentages, secondaryOutputWithPercentages;
    private final List<ChanceBasedSlotRenderer> slotRenderers;

    public PulverizerClientRecipe(RecipeHolder<PulverizerRecipe> recipe) {
        this.id = recipe.id().identifier();
        this.input = SlotContent.of(recipe.value().getInput());
        ItemStack[] outputEntries = recipe.value().getMaxOutputCounts(false);
        this.output = SlotContent.of(outputEntries[0].copyWithCount(1));
        this.secondaryOutput = outputEntries[1].isEmpty()?SlotContent.of():SlotContent.of(outputEntries[1].copyWithCount(1));
        this.outputWithPercentages = recipe.value().getOutput();
        this.secondaryOutputWithPercentages = recipe.value().getSecondaryOutput();

        slotRenderers = new ArrayList<>();
        {
            double[] percentages = outputWithPercentages.percentages();
            slotRenderers.add(new ChanceBasedSlotRenderer(68, 8, (int)Arrays.stream(percentages).filter(p -> p >= 1.0).count(), percentages.length));
        }
        if(!outputEntries[1].isEmpty()) {
            double[] percentages = secondaryOutputWithPercentages.percentages();
            slotRenderers.add(new ChanceBasedSlotRenderer(95, 8, (int)Arrays.stream(percentages).filter(p -> p >= 1.0).count(), percentages.length));
        }
    }

    @Override
    public ReliableClientRecipeType getType() {
        return PulverizerClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(1, this.output);
        {
            double[] percentages = outputWithPercentages.percentages();
            slotFillContext.addAdditionalStackModifier(1, ((stack, tooltip) -> {
                tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                for(int i = 0;i < percentages.length;i++)
                    tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
            }));
        }
        slotFillContext.bindSlot(2, this.secondaryOutput);
        {
            double[] percentages = secondaryOutputWithPercentages.percentages();
            slotFillContext.addAdditionalStackModifier(2, ((stack, tooltip) -> {
                tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                for(int i = 0;i < percentages.length;i++)
                    tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
            }));
        }
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.input);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.output, this.secondaryOutput);
    }

    @Override
    public void energizedpower$renderRecipeOverlay(RecipeScreenContext context) {
        for(ChanceBasedSlotRenderer slotRenderer:slotRenderers)
            slotRenderer.extractRenderState(context.guiGraphics(), context.mouseX(), context.mouseY(), context.partialTicks());
    }
}
