package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EnergizerBlockEntity;
import me.jddev0.ep.recipe.EnergizerRecipe;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class EnergizerEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/energizer_front.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.ENERGIZER_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("energizer"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final long energyConsumption;

    public EnergizerEMIRecipe(RecipeEntry<EnergizerRecipe> recipe) {
        this.id = recipe.id().getValue();
        this.input = List.of(EmiIngredient.of(recipe.value().getInputItem()));
        this.output = List.of(EmiStack.of(recipe.value().getOutputItem()));
        this.energyConsumption = (long)(recipe.value().getEnergyConsumption() * EnergizerBlockEntity.ENERGY_CONSUMPTION_MULTIPLIER);
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
        return 114;
    }

    @Override
    public int getDisplayHeight() {
        return 50;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        Identifier texture = EPAPI.id("textures/gui/container/energizer.png");
        widgets.addTexture(texture, 0, 0, 114, 50, 31, 18);

        widgets.addSlot(input.get(0), 16, 16).drawBack(false);
        widgets.addSlot(output.get(0), 92, 16).drawBack(false).recipeContext(this);

        Text energyConsumptionText = Text.literal(EnergyUtils.getEnergyWithPrefix(energyConsumption));
        widgets.addText(energyConsumptionText.asOrderedText(),
                widgets.getWidth() - MinecraftClient.getInstance().textRenderer.getWidth(energyConsumptionText),
                widgets.getHeight() - MinecraftClient.getInstance().textRenderer.fontHeight, Formatting.YELLOW.getColorValue(), false);
    }
}
