package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.MetalPressRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

import java.util.List;

public class MetalPressEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/metal_press_side.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.METAL_PRESS_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("metal_press"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> catalysts;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public MetalPressEMIRecipe(RecipeEntry<MetalPressRecipe> recipe) {
        this.id = recipe.id();
        this.catalysts = List.of(EmiIngredient.of(Ingredient.ofStacks(recipe.value().getPressMold())));
        this.input = List.of(EmiIngredient.of(recipe.value().getInput(), recipe.value().getInputCount()));
        this.output = List.of(EmiStack.of(recipe.value().getOutput()));
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
    public List<EmiIngredient> getCatalysts() {
        return catalysts;
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
        return 34;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        Identifier texture = EPAPI.id("textures/gui/container/metal_press.png");
        widgets.addTexture(texture, 0, 0, 98, 34, 47, 22);

        widgets.addSlot(input.get(0), 0, 12).drawBack(false);
        widgets.addSlot(catalysts.get(0), 36, 0).drawBack(false);
        widgets.addSlot(output.get(0), 76, 12).drawBack(false).recipeContext(this);
    }
}
