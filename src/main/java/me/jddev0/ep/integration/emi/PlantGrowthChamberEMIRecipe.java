package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PlantGrowthChamberEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/block/plant_growth_chamber_front.png");
    public static final EmiStack ITEM = EmiStack.of(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(new Identifier(EnergizedPowerMod.MODID, "plant_growth_chamber"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final int ticks;

    public PlantGrowthChamberEMIRecipe(PlantGrowthChamberRecipe recipe) {
        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(recipe.getInput()));
        this.output = Arrays.stream(recipe.getMaxOutputCounts()).map(EmiStack::of).toList();
        this.ticks = recipe.getTicks();
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

        widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(0)), 72, 0).drawBack(false).recipeContext(this);
        widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(1)), 90, 0).drawBack(false).recipeContext(this);
        widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(2)), 72, 18).drawBack(false).recipeContext(this);
        widgets.addSlot(EmiIngredient.of(outputSlotEntries.get(3)), 90, 18).drawBack(false).recipeContext(this);

        Text ticksText = Text.translatable("recipes.energizedpower.plant_growth_chamber.ticks", ticks);
        widgets.addText(ticksText.asOrderedText(),
                widgets.getWidth() - MinecraftClient.getInstance().textRenderer.getWidth(ticksText),
                widgets.getHeight() - MinecraftClient.getInstance().textRenderer.fontHeight, Formatting.WHITE.getColorValue(), false);
    }
}
