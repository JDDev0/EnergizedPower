package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

import java.util.List;

public class PressMoldMakerEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = Identifier.of(EnergizedPowerMod.MODID, "textures/block/press_mold_maker_front.png");
    public static final EmiStack ITEM = EmiStack.of(ModBlocks.PRESS_MOLD_MAKER_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(Identifier.of(EnergizedPowerMod.MODID, "press_mold_maker"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public PressMoldMakerEMIRecipe(RecipeEntry<PressMoldMakerRecipe> recipe) {
        this.id = recipe.id();
        this.input = List.of(EmiIngredient.of(Ingredient.ofItems(Items.CLAY_BALL), recipe.value().getClayCount()));
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
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return 82;
    }

    @Override
    public int getDisplayHeight() {
        return 25;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 23, 4);

        widgets.addSlot(input.get(0), 0, 4);

        widgets.addSlot(output.get(0), 61, 4).recipeContext(this);
    }
}
