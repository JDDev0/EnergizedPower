package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class FluidTransposerEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/fluid_transposer_front.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.FLUID_TRANSPOSER_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("fluid_transposer"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final FluidTransposerBlockEntity.Mode mode;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public FluidTransposerEMIRecipe(RecipeEntry<FluidTransposerRecipe> recipe) {
        this.id = recipe.id().getValue();

        this.mode = recipe.value().getMode();

        FluidStack fluid = recipe.value().getFluid();

        if(mode == FluidTransposerBlockEntity.Mode.EMPTYING) {
            this.input = List.of(
                    EmiIngredient.of(recipe.value().getInput())
            );
            this.output = List.of(
                    EmiStack.of(recipe.value().getOutput()),
                    EmiStack.of(fluid.getFluid(), fluid.getFluidVariant().getComponents(), fluid.getDropletsAmount())
            );
        }else {
            this.input = List.of(
                    EmiIngredient.of(recipe.value().getInput()),
                    EmiStack.of(fluid.getFluid(), fluid.getFluidVariant().getComponents(), fluid.getDropletsAmount())
            );
            this.output = List.of(
                    EmiStack.of(recipe.value().getOutput())
            );
        }
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
        return 143;
    }

    @Override
    public int getDisplayHeight() {
        return 26;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        if(mode == FluidTransposerBlockEntity.Mode.EMPTYING) {
            widgets.addTexture(EPAPI.id("textures/gui/recipe/misc_gui.png"),
                    0, 0, 143, 26, 1, 133);

            widgets.addSlot(input.get(0), 0, 4).drawBack(false);

            widgets.addSlot(output.get(0), 63, 4).drawBack(false).recipeContext(this);
            widgets.addSlot(output.get(1), 89, 4).drawBack(false).recipeContext(this);

            widgets.addTexture(Identifier.of("minecraft", "textures/item/bucket.png"),
                    120, 5, 16, 16, 0, 0, 16, 16, 16, 16);
        }else {
            widgets.addTexture(EPAPI.id("textures/gui/recipe/misc_gui.png"),
                    0, 0, 143, 26, 1, 161);

            widgets.addSlot(input.get(0), 0, 4).drawBack(false);
            widgets.addSlot(input.get(1), 18, 4).drawBack(false);

            widgets.addSlot(output.get(0), 89, 4).drawBack(false).recipeContext(this);

            widgets.addTexture(Identifier.of("minecraft", "textures/item/water_bucket.png"),
                    120, 5, 16, 16, 0, 0, 16, 16, 16, 16);
        }

        widgets.addTooltipText(List.of(Text.translatable("tooltip.energizedpower.fluid_transposer.mode." + mode.asString())),
                118, 3, 20, 20);
    }
}
