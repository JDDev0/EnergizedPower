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
import me.jddev0.ep.block.entity.PlantGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;

public class PlantGrowthChamberEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/block/plant_growth_chamber_front.png");
    public static final EmiStack ITEM = EmiStack.of(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(new Identifier(EnergizedPowerMod.MODID, "plant_growth_chamber"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final PlantGrowthChamberRecipe.OutputItemStackWithPercentages[] outputsWithPercentages;
    private final int ticks;

    public PlantGrowthChamberEMIRecipe(RecipeEntry<PlantGrowthChamberRecipe> recipe) {
        this.id = recipe.id();
        this.input = List.of(EmiIngredient.of(recipe.value().getInput()));
        this.output = Arrays.stream(recipe.value().getMaxOutputCounts()).map(EmiStack::of).toList();
        this.outputsWithPercentages = recipe.value().getOutputs();
        this.ticks = (int)(recipe.value().getTicks() * PlantGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
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
        return 108;
    }

    @Override
    public int getDisplayHeight() {
        return 48;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        Identifier texture = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/plant_growth_chamber.png");
        widgets.addTexture(texture, 0, 0, 108, 48, 61, 25);

        widgets.addSlot(input.get(0), 0, 9).drawBack(false);

        List<List<EmiStack>> outputSlotEntries = new ArrayList<>(4);
        for(int i = 0;i < 4;i++)
            outputSlotEntries.add(new LinkedList<>());

        for(int i = 0;i < output.size();i++)
            outputSlotEntries.get(i % 4).add(output.get(i));

        SlotWidget[] outputSlots = new SlotWidget[4];

        outputSlots[0] = widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(0)), 72, 0).drawBack(false).recipeContext(this);
        outputSlots[1] = widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(1)), 90, 0).drawBack(false).recipeContext(this);
        outputSlots[2] = widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(2)), 72, 18).drawBack(false).recipeContext(this);
        outputSlots[3] = widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(3)), 90, 18).drawBack(false).recipeContext(this);

        for(int i = 0;i < outputsWithPercentages.length;i++) {
            SlotWidget outputSlot = outputSlots[i % 4];

            Text oddsText = Text.translatable("recipes.energizedpower.transfer.output_percentages");

            if(i >= 4 || i + 4 < outputsWithPercentages.length) {
                outputSlot.appendTooltip(Text.translatable(outputsWithPercentages[i].output().getTranslationKey()).
                        append(Text.literal(": ").append(oddsText)));
            }else {
                outputSlot.appendTooltip(oddsText);
            }

            double[] percentages = outputsWithPercentages[i].percentages();
            for(int j = 0;j < percentages.length;j++)
                outputSlot.appendTooltip(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

            if(i + 4 < outputsWithPercentages.length) {
                outputSlot.appendTooltip(Text.empty());
            }
        }

        Text ticksText = Text.translatable("recipes.energizedpower.info.ticks", ticks);
        widgets.addText(ticksText.asOrderedText(),
                widgets.getWidth() - MinecraftClient.getInstance().textRenderer.getWidth(ticksText),
                widgets.getHeight() - MinecraftClient.getInstance().textRenderer.fontHeight, Formatting.WHITE.getColorValue(), false);
    }
}
