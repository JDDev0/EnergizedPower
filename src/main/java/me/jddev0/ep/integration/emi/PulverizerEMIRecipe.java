package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.PulverizerRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PulverizerEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/block/pulverizer_side.png");
    public static final EmiStack ITEM = EmiStack.of(ModBlocks.PULVERIZER_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(new Identifier(EnergizedPowerMod.MODID, "pulverizer"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final PulverizerRecipe.OutputItemStackWithPercentages outputWithPercentages;
    private final PulverizerRecipe.OutputItemStackWithPercentages secondaryOutputWithPercentages;

    public PulverizerEMIRecipe(RecipeEntry<PulverizerRecipe> recipe) {
        this.id = recipe.id();
        this.input = List.of(EmiIngredient.of(recipe.value().getInput()));

        this.output = Arrays.stream(recipe.value().getMaxOutputCounts()).filter(itemStack -> !itemStack.isEmpty()).map(EmiStack::of).toList();
        this.outputWithPercentages = recipe.value().getOutput();
        this.secondaryOutputWithPercentages = recipe.value().getSecondaryOutput();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public Identifier getId() {
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
        return 109;
    }

    @Override
    public int getDisplayHeight() {
        return 26;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        Identifier texture = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/pulverizer.png");
        widgets.addTexture(texture, 0, 0, 109, 26, 42, 30);

        widgets.addSlot(input.get(0), 0, 4).drawBack(false);
        SlotWidget outputSlot = widgets.addSlot(output.get(0), 64, 4).drawBack(false).recipeContext(this);
        {
            outputSlot.appendTooltip(Text.translatable("recipes.energizedpower.transfer.output_odds"));

            double[] percentages = outputWithPercentages.percentages();
            for(int i = 0;i < percentages.length;i++)
                outputSlot.appendTooltip(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));

        }
        SlotWidget secondaryOutputSlot = widgets.addSlot(output.size() == 2?output.get(1):EmiStack.EMPTY, 91, 4).drawBack(false).recipeContext(this);
        if(output.size() == 2) {
            secondaryOutputSlot.appendTooltip(Text.translatable("recipes.energizedpower.transfer.output_odds"));

            double[] percentages = secondaryOutputWithPercentages.percentages();
            for(int i = 0;i < percentages.length;i++)
                secondaryOutputSlot.appendTooltip(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));

        }
    }
}
