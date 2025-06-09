package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.StoneLiquefierRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class StoneLiquefierEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/stone_liquefier_front.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.STONE_LIQUEFIER_ITEM.get());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("stone_liquefier"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public StoneLiquefierEMIRecipe(RecipeHolder<StoneLiquefierRecipe> recipe) {
        this.id = recipe.id();

        FluidStack output = recipe.value().getOutput();

        this.input = List.of(
                EmiIngredient.of(recipe.value().getInput())
        );
        this.output = List.of(
                EmiStack.of(output.getFluid(), output.getComponentsPatch(), output.getAmount())
        );
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
