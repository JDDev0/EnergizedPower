package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.CompressorRecipe;
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

import java.util.Arrays;
import java.util.stream.Collectors;

public class CompressorCategory implements IRecipeCategory<RecipeEntry<CompressorRecipe>> {
    public static final RecipeType<RecipeEntry<CompressorRecipe>> TYPE = RecipeType.createFromVanilla(CompressorRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public CompressorCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/container/compressor.png");
        background = helper.createDrawable(texture, 47, 30, 98, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.COMPRESSOR_ITEM));
    }

    @Override
    public RecipeType<RecipeEntry<CompressorRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.compressor");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeEntry<CompressorRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addItemStacks(
                Arrays.stream(recipe.value().getInputItem().getMatchingStacks()).
                        map(itemStack -> itemStack.copyWithCount(recipe.value().getInputCount())).
                        collect(Collectors.toList()));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 77, 5).addItemStack(recipe.value().getOutputItem());
    }
}
