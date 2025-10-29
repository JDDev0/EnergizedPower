package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.util.FluidUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StoneSolidifierCategory implements IRecipeCategory<StoneSolidifierRecipe> {
    public static final Identifier UID = EPAPI.id("stone_solidifier");
    public static final RecipeType<StoneSolidifierRecipe> TYPE = new RecipeType<>(UID, StoneSolidifierRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public StoneSolidifierCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 1, 103, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.STONE_SOLIDIFIER_ITEM));
    }

    @Override
    public RecipeType<StoneSolidifierRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.stone_solidifier");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, StoneSolidifierRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addFluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(recipe.getWaterAmount()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 19, 5).addFluidStack(Fluids.LAVA, FluidUtils.convertMilliBucketsToDroplets(recipe.getLavaAmount()));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 82, 5).addItemStack(recipe.getOutput());
    }
}
