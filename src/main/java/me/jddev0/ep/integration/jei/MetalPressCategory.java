package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.MetalPressRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MetalPressCategory implements IRecipeCategory<MetalPressRecipe> {
    public static final Identifier UID = EPAPI.id("metal_press");
    public static final RecipeType<MetalPressRecipe> TYPE = new RecipeType<>(UID, MetalPressRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public MetalPressCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/container/metal_press.png");
        background = helper.createDrawable(texture, 47, 22, 98, 34);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.METAL_PRESS_ITEM));
    }

    @Override
    public RecipeType<MetalPressRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.metal_press");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, MetalPressRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 13).addItemStacks(
                Arrays.stream(recipe.getInput().getMatchingStacks()).
                        map(itemStack -> itemStack.copyWithCount(recipe.getInputCount())).
                        collect(Collectors.toList()));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.CATALYST, 37, 1).addItemStack(recipe.getPressMold());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 77, 13).addItemStack(recipe.getOutputItem());
    }
}
