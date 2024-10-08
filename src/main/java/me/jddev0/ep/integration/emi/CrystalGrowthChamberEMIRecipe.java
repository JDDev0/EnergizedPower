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
import me.jddev0.ep.block.entity.CrystalGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.CrystalGrowthChamberRecipe;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Locale;

public class CrystalGrowthChamberEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/crystal_growth_chamber_side.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM.get());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("crystal_growth_chamber"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final OutputItemStackWithPercentages outputWithPercentages;
    private final int ticks;

    public CrystalGrowthChamberEMIRecipe(RecipeHolder<CrystalGrowthChamberRecipe> recipe) {
        this.id = recipe.id();
        this.input = List.of(EmiIngredient.of(recipe.value().getInput(), recipe.value().getInputCount()));
        this.output = List.of(EmiStack.of(recipe.value().getMaxOutputCount()));
        this.outputWithPercentages = recipe.value().getOutput();
        this.ticks = (int)(recipe.value().getTicks() * CrystalGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
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
        return 98;
    }

    @Override
    public int getDisplayHeight() {
        return 38;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation texture = EPAPI.id("textures/gui/container/crystal_growth_chamber.png");
        widgets.addTexture(texture, 0, 0, 98, 38, 47, 30);

        widgets.addSlot(input.get(0), 0, 4).drawBack(false);
        SlotWidget outputSlot = widgets.addSlot(output.get(0), 76, 4).drawBack(false).recipeContext(this);
        {
            outputSlot.appendTooltip(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

            double[] percentages = outputWithPercentages.percentages();
            for(int i = 0;i < percentages.length;i++)
                outputSlot.appendTooltip(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));

        }

        Component ticksText = Component.translatable("recipes.energizedpower.info.ticks", ticks);
        widgets.addText(ticksText.getVisualOrderText(),
                widgets.getWidth() - Minecraft.getInstance().font.width(ticksText),
                widgets.getHeight() - Minecraft.getInstance().font.lineHeight, ChatFormatting.WHITE.getColor(), false);
    }
}
