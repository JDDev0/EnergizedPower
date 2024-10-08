package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.SawmillRecipe;
import net.minecraft.util.Identifier;

import java.util.List;

public class SawmillEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/sawmill_side.png");
    public static final EmiStack ITEM = EmiStack.of(ModBlocks.SAWMILL_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("sawmill"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public SawmillEMIRecipe(SawmillRecipe recipe) {
        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(recipe.getInputItem()));

        EmiStack emiOutputItem = EmiStack.of(recipe.getOutputItem());

        if(recipe.getSecondaryOutput().isEmpty())
            this.output = List.of(emiOutputItem);
        else
            this.output = List.of(emiOutputItem, EmiStack.of(recipe.getSecondaryOutput()));
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
        Identifier texture = EPAPI.id("textures/gui/container/sawmill.png");
        widgets.addTexture(texture, 0, 0, 109, 26, 42, 30);

        widgets.addSlot(input.get(0), 0, 4).drawBack(false);
        widgets.addSlot(output.get(0), 64, 4).drawBack(false).recipeContext(this);
        widgets.addSlot(output.size() == 2?output.get(1):EmiStack.EMPTY, 91, 4).drawBack(false).recipeContext(this);
    }
}
