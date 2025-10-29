package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.StoneLiquefierRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StoneLiquefierCategory implements IRecipeCategory<RecipeEntry<StoneLiquefierRecipe>> {
    public static final RecipeType<RecipeEntry<StoneLiquefierRecipe>> TYPE = RecipeType.createFromVanilla(StoneLiquefierRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public StoneLiquefierCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 77, 85, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.STONE_LIQUEFIER_ITEM));
    }

    @Override
    public RecipeType<RecipeEntry<StoneLiquefierRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.stone_liquefier");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeEntry<StoneLiquefierRecipe> recipe, IFocusGroup iFocusGroup) {
        FluidStack output = recipe.value().getOutput();

        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.value().getInput());

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).addFluidStack(output.getFluid(),
                output.getDropletsAmount(), output.getFluidVariant().getComponents());
    }
}
