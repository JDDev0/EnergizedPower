package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.StoneLiquefierRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class StoneLiquefierCategory implements IRecipeCategory<StoneLiquefierRecipe> {
    public static final ResourceLocation UID = EPAPI.id("stone_liquefier");
    public static final RecipeType<StoneLiquefierRecipe> TYPE = new RecipeType<>(UID, StoneLiquefierRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public StoneLiquefierCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 77, 85, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.STONE_LIQUEFIER_ITEM.get()));
    }

    @Override
    public RecipeType<StoneLiquefierRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.stone_liquefier");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, StoneLiquefierRecipe recipe, IFocusGroup iFocusGroup) {
        FluidStack output = recipe.getOutput();

        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.getInput());

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).addFluidStack(output.getFluid(),
                output.getAmount(), output.getTag());
    }
}
