package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PressMoldMakerCategory implements IRecipeCategory<RecipeEntry<PressMoldMakerRecipe>> {
    public static final IRecipeHolderType<PressMoldMakerRecipe> TYPE = IRecipeHolderType.create(PressMoldMakerRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public PressMoldMakerCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 77, 85, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.PRESS_MOLD_MAKER_ITEM));
    }

    @Override
    public IRecipeType<RecipeEntry<PressMoldMakerRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.press_mold_maker");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeEntry<PressMoldMakerRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 5).add(new ItemStack(Items.CLAY_BALL, recipe.value().getClayCount()));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).add(recipe.value().getOutput());
    }
}
