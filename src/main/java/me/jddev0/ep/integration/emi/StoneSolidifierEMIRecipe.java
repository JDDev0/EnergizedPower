package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.fluid.Fluids;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

import java.util.List;

public class StoneSolidifierEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/stone_solidifier_front.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.STONE_SOLIDIFIER_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("stone_solidifier"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public StoneSolidifierEMIRecipe(RecipeEntry<StoneSolidifierRecipe> recipe) {
        this.id = recipe.id();
        this.input = List.of(
                EmiStack.of(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(recipe.value().getWaterAmount())),
                EmiStack.of(Fluids.LAVA, FluidUtils.convertMilliBucketsToDroplets(recipe.value().getLavaAmount()))
        );
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
        return 100;
    }

    @Override
    public int getDisplayHeight() {
        return 25;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 41, 4);

        widgets.addSlot(input.get(0), 0, 4);

        widgets.addSlot(input.get(1), 18, 4);

        widgets.addSlot(output.get(0), 79, 4).recipeContext(this);
    }
}
