package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.CrusherRecipe;
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

public class CrusherCategory implements IRecipeCategory<RecipeEntry<CrusherRecipe>> {
    public static final RecipeType<RecipeEntry<CrusherRecipe>> TYPE = RecipeType.createFromVanilla(CrusherRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public CrusherCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/container/crusher.png");
        background = helper.createDrawable(texture, 47, 30, 98, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.CRUSHER_ITEM));
    }

    @Override
    public RecipeType<RecipeEntry<CrusherRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.crusher");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeEntry<CrusherRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.value().getInputItem());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 77, 5).addItemStack(recipe.value().getOutputItem());
    }
}
