package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.SawmillRecipe;
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

import java.util.ArrayList;
import java.util.List;

public class SawmillCategory implements IRecipeCategory<RecipeEntry<SawmillRecipe>> {
    public static final RecipeType<RecipeEntry<SawmillRecipe>> TYPE = RecipeType.createFromVanilla(SawmillRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public SawmillCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/container/sawmill.png");
        background = helper.createDrawable(texture, 42, 30, 109, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.SAWMILL_ITEM));
    }

    @Override
    public RecipeType<RecipeEntry<SawmillRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.sawmill");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeEntry<SawmillRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.value().getInputItem());

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 65, 5).addItemStack(recipe.value().getOutputItem());

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 92, 5).
                addItemStacks(recipe.value().getSecondaryOutput().isEmpty()?new ArrayList<>(0):List.of(recipe.value().getSecondaryOutput()));
    }
}
