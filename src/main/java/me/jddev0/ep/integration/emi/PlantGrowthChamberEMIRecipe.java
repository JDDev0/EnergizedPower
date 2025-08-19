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
import me.jddev0.ep.block.entity.PlantGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.*;

public class PlantGrowthChamberEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/plant_growth_chamber_front.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("plant_growth_chamber"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final OutputItemStackWithPercentages[] outputsWithPercentages;
    private final int ticks;

    public PlantGrowthChamberEMIRecipe(RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        this.id = recipe.id().location();
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
        return 108;
    }

    @Override
    public int getDisplayHeight() {
        return 48;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation texture = EPAPI.id("textures/gui/container/plant_growth_chamber.png");
        widgets.addTexture(texture, 0, 0, 108, 48, 61, 25);

        widgets.addSlot(input.get(0), 0, 9).drawBack(false);

        List<List<EmiStack>> outputSlotEntries = new ArrayList<>(4);
        for(int i = 0;i < 4;i++)
            outputSlotEntries.add(new ArrayList<>());

        for(int i = 0;i < output.size();i++)
            outputSlotEntries.get(i % 4).add(output.get(i));

        SlotWidget[] outputSlots = new SlotWidget[4];

        outputSlots[0] = widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(0)), 72, 0).drawBack(false).recipeContext(this);
        outputSlots[1] = widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(1)), 90, 0).drawBack(false).recipeContext(this);
        outputSlots[2] = widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(2)), 72, 18).drawBack(false).recipeContext(this);
        outputSlots[3] = widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(3)), 90, 18).drawBack(false).recipeContext(this);

        for(int i = 0;i < outputsWithPercentages.length;i++) {
            SlotWidget outputSlot = outputSlots[i % 4];

            Component oddsText = Component.translatable("recipes.energizedpower.transfer.output_percentages");

            if(i >= 4 || i + 4 < outputsWithPercentages.length) {
                outputSlot.appendTooltip(Component.empty().append(outputsWithPercentages[i].output().getHoverName()).
                        append(Component.literal(": ").append(oddsText)));
            }else {
                outputSlot.appendTooltip(oddsText);
            }

            double[] percentages = outputsWithPercentages[i].percentages();
            for(int j = 0;j < percentages.length;j++)
                outputSlot.appendTooltip(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

            if(i + 4 < outputsWithPercentages.length) {
                outputSlot.appendTooltip(Component.empty());
            }
        }

        Component ticksText = Component.translatable("recipes.energizedpower.info.ticks", ticks);
        widgets.addText(ticksText.getVisualOrderText(),
                widgets.getWidth() - Minecraft.getInstance().font.width(ticksText),
                widgets.getHeight() - Minecraft.getInstance().font.lineHeight, ChatFormatting.WHITE.getColor(), false);
    }
}
