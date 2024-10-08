package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FiltrationPlantEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/filtration_plant_front.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.FILTRATION_PLANT_ITEM.get());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("filtration_plant"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final OutputItemStackWithPercentages outputWithPercentages;
    private final OutputItemStackWithPercentages secondaryOutputWithPercentages;

    public FiltrationPlantEMIRecipe(FiltrationPlantRecipe recipe) {
        this.id = recipe.getId();
        this.input = List.of(EmiStack.of(EPFluids.DIRTY_WATER.get(), FiltrationPlantBlockEntity.DIRTY_WATER_CONSUMPTION_PER_RECIPE));

        this.output = Arrays.stream(recipe.getMaxOutputCounts()).filter(itemStack -> !itemStack.isEmpty()).map(EmiStack::of).toList();
        this.outputWithPercentages = recipe.getOutput();
        this.secondaryOutputWithPercentages = recipe.getSecondaryOutput();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return 112;
    }

    @Override
    public int getDisplayHeight() {
        return 26;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 28, 4);

        widgets.addSlot(input.get(0), 0, 4);

        SlotWidget outputSlot = widgets.addSlot(output.get(0), 63, 4).recipeContext(this);
        {
            outputSlot.appendTooltip(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

            double[] percentages = outputWithPercentages.percentages();
            for(int i = 0;i < percentages.length;i++)
                outputSlot.appendTooltip(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));

        }
        SlotWidget secondaryOutputSlot = widgets.addSlot(output.size() == 2?output.get(1):EmiStack.EMPTY, 91, 4).recipeContext(this);
        if(output.size() == 2) {
            secondaryOutputSlot.appendTooltip(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

            double[] percentages = secondaryOutputWithPercentages.percentages();
            for(int i = 0;i < percentages.length;i++)
                secondaryOutputSlot.appendTooltip(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));

        }
    }
}
