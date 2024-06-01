package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class FluidTransposerEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/block/fluid_transposer_front.png");
    public static final EmiStack ITEM = EmiStack.of(ModBlocks.FLUID_TRANSPOSER_ITEM.get());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(new ResourceLocation(EnergizedPowerMod.MODID, "fluid_transposer"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final FluidTransposerBlockEntity.Mode mode;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public FluidTransposerEMIRecipe(FluidTransposerRecipe recipe) {
        this.id = recipe.getId();

        this.mode = recipe.getMode();

        FluidStack fluid = recipe.getFluid();

        if(mode == FluidTransposerBlockEntity.Mode.EMPTYING) {
            this.input = List.of(
                    EmiIngredient.of(recipe.getInput())
            );
            this.output = List.of(
                    EmiStack.of(recipe.getOutput()),
                    EmiStack.of(fluid.getFluid(), fluid.getTag(), fluid.getAmount())
            );
        }else {
            this.input = List.of(
                    EmiIngredient.of(recipe.getInput()),
                    EmiStack.of(fluid.getFluid(), fluid.getTag(), fluid.getAmount())
            );
            this.output = List.of(
                    EmiStack.of(recipe.getOutput())
            );
        }
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
        return 143;
    }

    @Override
    public int getDisplayHeight() {
        return 26;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        if(mode == FluidTransposerBlockEntity.Mode.EMPTYING) {
            widgets.addTexture(new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/recipe/misc_gui.png"),
                    0, 0, 143, 26, 1, 133);

            widgets.addSlot(input.get(0), 0, 4).drawBack(false);

            widgets.addSlot(output.get(0), 63, 4).drawBack(false).recipeContext(this);
            widgets.addSlot(output.get(1), 89, 4).drawBack(false).recipeContext(this);

            widgets.addTexture(new ResourceLocation("minecraft", "textures/item/bucket.png"),
                    120, 5, 16, 16, 0, 0, 16, 16, 16, 16);
        }else {
            widgets.addTexture(new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/recipe/misc_gui.png"),
                    0, 0, 143, 26, 1, 161);

            widgets.addSlot(input.get(0), 0, 4).drawBack(false);
            widgets.addSlot(input.get(1), 18, 4).drawBack(false);

            widgets.addSlot(output.get(0), 89, 4).drawBack(false).recipeContext(this);

            widgets.addTexture(new ResourceLocation("minecraft", "textures/item/water_bucket.png"),
                    120, 5, 16, 16, 0, 0, 16, 16, 16, 16);
        }

        widgets.addTooltipText(List.of(Component.translatable("tooltip.energizedpower.fluid_transposer.mode." + mode.getSerializedName())),
                118, 3, 20, 20);
    }
}
