package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.PlantGrowthChamberFertilizerRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class PlantGrowthChamberFertilizerEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/block/plant_growth_chamber_side.png");
    public static final EmiStack ITEM = EmiStack.of(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(new Identifier(EnergizedPowerMod.MODID, "plant_growth_chamber_fertilizer"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final double speedMultiplier;
    private final double energyConsumptionMultiplier;

    public PlantGrowthChamberFertilizerEMIRecipe(PlantGrowthChamberFertilizerRecipe recipe) {
        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(recipe.getInput()));
        this.speedMultiplier = recipe.getSpeedMultiplier();
        this.energyConsumptionMultiplier = recipe.getEnergyConsumptionMultiplier();
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
        return List.of();
    }

    @Override
    public int getDisplayWidth() {
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        return 30;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        Identifier texture = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/plant_growth_chamber.png");
        widgets.addTexture(texture, 0, 0, 18, 18, 34, 34);

        widgets.addSlot(input.get(0), 0, 0).drawBack(false);

        Text speedMultiplierText = Text.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer.speed_multiplier", speedMultiplier);
        widgets.addText(speedMultiplierText.asOrderedText(),
                widgets.getWidth() - MinecraftClient.getInstance().textRenderer.getWidth(speedMultiplierText),
                widgets.getHeight() - MinecraftClient.getInstance().textRenderer.fontHeight - 17, Formatting.WHITE.getColorValue(), false);

        Text energyConsumptionMultiplierText = Text.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer.energy_consumption_multiplier", energyConsumptionMultiplier);
        widgets.addText(energyConsumptionMultiplierText.asOrderedText(),
                widgets.getWidth() - MinecraftClient.getInstance().textRenderer.getWidth(energyConsumptionMultiplierText),
                widgets.getHeight() - MinecraftClient.getInstance().textRenderer.fontHeight, Formatting.WHITE.getColorValue(), false);
    }
}
