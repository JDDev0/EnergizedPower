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
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StoneSolidifierCategory implements IRecipeCategory<RecipeEntry<StoneSolidifierRecipe>> {
    public static final IRecipeHolderType<StoneSolidifierRecipe> TYPE = IRecipeHolderType.create(StoneSolidifierRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public StoneSolidifierCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 1, 103, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.STONE_SOLIDIFIER_ITEM));
    }

    @Override
    public IRecipeType<RecipeEntry<StoneSolidifierRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeEntry<StoneSolidifierRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 5).add(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(recipe.value().getWaterAmount()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 19, 5).add(Fluids.LAVA, FluidUtils.convertMilliBucketsToDroplets(recipe.value().getLavaAmount()));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 82, 5).add(recipe.value().getOutput());
    }
}
