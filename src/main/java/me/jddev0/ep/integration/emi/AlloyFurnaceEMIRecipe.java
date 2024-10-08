package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.AlloyFurnaceBlockEntity;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AlloyFurnaceEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/alloy_furnace_front.png");
    public static final EmiStack ITEM = EmiStack.of(ModBlocks.ALLOY_FURNACE_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("alloy_furnace"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final OutputItemStackWithPercentages secondaryOutputWithPercentages;
    private final int ticks;

    public AlloyFurnaceEMIRecipe(AlloyFurnaceRecipe recipe) {
        this.id = recipe.getId();
        this.input = Arrays.stream(recipe.getInputs()).map(input ->
                EmiIngredient.of(input.input(), input.count())).collect(Collectors.toList());

        this.output = Arrays.stream(recipe.getMaxOutputCounts()).filter(itemStack -> !itemStack.isEmpty()).map(EmiStack::of).toList();
        this.secondaryOutputWithPercentages = recipe.getSecondaryOutputItem();
        this.ticks = (int)(recipe.getTicks() * AlloyFurnaceBlockEntity.RECIPE_DURATION_MULTIPLIER);
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
        return 149;
    }

    @Override
    public int getDisplayHeight() {
        return 39;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 65, 4);

        int len = Math.min(input.size(), 3);
        for(int i = 0;i < len;i++)
            widgets.addSlot(input.get(i), 18 * i, 4);

        for(int i = len;i < 3;i++)
            widgets.addSlot(EmiStack.EMPTY, 18 * i, 4);

        widgets.addSlot(output.get(0), 98, 0).large(true).recipeContext(this);
        SlotWidget secondaryOutputSlot = widgets.addSlot(output.size() == 2?output.get(1):EmiStack.EMPTY, 124, 0).large(true).recipeContext(this);
        if(output.size() == 2) {
            secondaryOutputSlot.appendTooltip(Text.translatable("recipes.energizedpower.transfer.output_percentages"));

            double[] percentages = secondaryOutputWithPercentages.percentages();
            for(int i = 0;i < percentages.length;i++)
                secondaryOutputSlot.appendTooltip(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
        }

        Text ticksText = Text.translatable("recipes.energizedpower.info.ticks", ticks);
        widgets.addText(ticksText.asOrderedText(),
                widgets.getWidth() - MinecraftClient.getInstance().textRenderer.getWidth(ticksText),
                widgets.getHeight() - MinecraftClient.getInstance().textRenderer.fontHeight, Formatting.WHITE.getColorValue(), false);
    }
}
