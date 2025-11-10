package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FluidTransposerCategory implements IRecipeCategory<RecipeEntry<FluidTransposerRecipe>> {
    public static final IRecipeHolderType<FluidTransposerRecipe> TYPE = IRecipeHolderType.create(FluidTransposerRecipe.Type.INSTANCE);

    private final IDrawable backgroundEmptying;
    private final IDrawable backgroundFilling;
    private final IDrawable icon;

    public FluidTransposerCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        backgroundEmptying = helper.createDrawable(texture, 1, 133, 143, 26);
        backgroundFilling = helper.createDrawable(texture, 1, 161, 143, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.FLUID_TRANSPOSER_ITEM));
    }

    @Override
    public IRecipeType<RecipeEntry<FluidTransposerRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.fluid_transposer");
    }

    @Override
    public IDrawable getBackground() {
        return backgroundEmptying;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeEntry<FluidTransposerRecipe> recipe, IFocusGroup iFocusGroup) {
        FluidStack fluid = recipe.value().getFluid();

        if(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.EMPTYING) {
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).add(recipe.value().getInput());

            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).add(recipe.value().getOutput());
            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 90, 5).add(fluid.getFluid(),
                    fluid.getDropletsAmount(), fluid.getFluidVariant().getComponents());
        }else {
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).add(recipe.value().getInput());
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 19, 5).add(fluid.getFluid(),
                    fluid.getDropletsAmount(), fluid.getFluidVariant().getComponents());

            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 90, 5).add(recipe.value().getOutput());
        }
    }

    @Override
    public void draw(RecipeEntry<FluidTransposerRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        if(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.FILLING) {
            backgroundFilling.draw(guiGraphics, 0, 0);
        }

        ItemStack output = new ItemStack(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?Items.BUCKET:Items.WATER_BUCKET);
        guiGraphics.getMatrices().pushMatrix();
        guiGraphics.getMatrices().translate(0.f, 0.f);

        guiGraphics.drawItem(output, 120, 5, 120 + 5 * 143);

        guiGraphics.getMatrices().popMatrix();
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeEntry<FluidTransposerRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        int tooltipX = 119;
        int tooltipY = 4;
        if(mouseX >= (double)(tooltipX - 1) && mouseX < (double)(tooltipX + 20 + 1) &&
                mouseY >= (double)(tooltipY - 1) && mouseY < (double)(tooltipY + 20 + 1)) {
            tooltip.add(Text.translatable("tooltip.energizedpower.fluid_transposer.mode." +
                    recipe.value().getMode().asString()));
        }
    }
}
